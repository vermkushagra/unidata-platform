/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext.UpsertRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;
import com.unidata.mdm.backend.common.types.impl.OriginRecordImpl;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.driver.CalculableHolder;
import com.unidata.mdm.backend.service.data.driver.EtalonComposer;
import com.unidata.mdm.backend.service.data.driver.EtalonCompositionDriverType;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.data.util.DataUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;


/**
 * @author Mikhail Mikhailov
 * Possibly recalculate state after enrichment.
 */
public class DataRecordUpsertEtalonRecalculateStateAfterExecutor
    implements DataRecordAfterExecutor<UpsertRequestContext> {

    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;

    /**
     * Origin component.
     */
    @Autowired
    private OriginRecordsComponent originComponent;

    /**
     * Etalon component.
     */
    @Autowired
    private EtalonRecordsComponent etalonComponent;

    /**
     * Etalon composer.
     */
    @Autowired
    private EtalonComposer etalonComposer;

    /**
     * Constructor.
     */
    public DataRecordUpsertEtalonRecalculateStateAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {
            // 1. Get keys, which must be present at this time
            RecordKeys keys = ctx.keys();
            if (keys == null) {
                return false;
            }

            // 2. Save versions possibly created by DQ
            List<OriginRecord> enriched = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_ENRICHMENT);
            if (CollectionUtils.isEmpty(enriched)) {
                return true;
            }

            Date asOf = ctx.getValidFrom() != null ? ctx.getValidFrom() : ctx.getValidTo();
            Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> result;

            // 2.1. Check for state changes. Skip if data is the same
            List<CalculableHolder<OriginRecord>> base = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_BASE);

            // UN-2327
            boolean hasChanges = false;
            DataRecord prev = etalonComposer.compose(EtalonCompositionDriverType.BVT, base, false, false);

            for (int i = 0; i < enriched.size(); i++) {

                OriginRecord enrichment = enriched.get(i);

                // UN-2329
                DataRecord diff = DataUtils.simpleDataDiff(keys.getEntityName(), enrichment, i == 0 ? prev : enriched.get(i - 1), null);

                // No changes. Skip.
                if (diff == null) {
                    continue;
                }

                Date ts = new Date(System.currentTimeMillis());
                OriginRecord enrichmentOrigin = new OriginRecordImpl()
                        .withDataRecord(diff)
                        .withInfoSection(new OriginRecordInfoSection()
                                .withCreateDate(ts)
                                .withUpdateDate(ts)
                                .withCreatedBy(SecurityUtils.getCurrentUserName())
                                .withShift(DataShift.REVISED)
                                .withValidFrom(ctx.getValidFrom())
                                .withValidTo(ctx.getValidTo())
                                .withOriginKey(enrichment.getInfoSection().getOriginKey()));

                UpsertRequestContext enrichmentCtx = new UpsertRequestContextBuilder()
                        .record(diff)
                        .originKey(enrichment.getInfoSection().getOriginKey())
                        .etalonKey(keys.getEtalonKey())
                        .validFrom(ctx.getValidFrom())
                        .validTo(ctx.getValidTo())
                        .enrichment(true)
                        .batchUpsert(ctx.isBatchUpsert())
                        .auditLevel(ctx.getAuditLevel())
                        .build();

                enrichmentCtx.putToStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD, enrichmentOrigin);
                enrichmentCtx.putToStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP, ts);

                // UN-6569 handle enrichment keys separately before upsert
                commonComponent.identify(enrichmentCtx);
                originComponent.upsertOrigin(enrichmentCtx);
                hasChanges = true;
            }

            // 2.2. Recalculate state, if there were changes
            // TODO 1. use calculation base + enrichment objects to calculate final etalon state
            // to avoid costly versions DB fetch!
            if (hasChanges) {

                boolean isApproverView = false;
                if (ctx.isRestore()) {
                    WorkflowTimelineDTO timeline = ctx.getFromStorage(StorageId.DATA_RECORD_TIMELINE);
                    isApproverView = timeline.isPublished() ? false : true;
                }

                result = etalonComponent.loadEtalonDataFull(keys.getEtalonKey().getId(), asOf, null, null, null, false, isApproverView);
                ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_RECORD, result != null ? result.getKey() : null);
                ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_BASE, result != null ? result.getValue() : null);
            }

            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

}
