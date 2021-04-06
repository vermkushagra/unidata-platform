package com.unidata.mdm.backend.service.data.listener.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DQContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.impl.OriginRecordImpl;
import com.unidata.mdm.backend.service.cleanse.DataQualityServiceExt;
import com.unidata.mdm.backend.service.data.driver.CalculableHolder;
import com.unidata.mdm.backend.service.data.driver.EtalonComposer;
import com.unidata.mdm.backend.service.data.driver.EtalonCompositionDriverType;
import com.unidata.mdm.backend.service.data.driver.RecordHolder;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.meta.DQRuleClass;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.DQRuleType;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * The Class DQAfterUpsertExecutor.
 */
public class DataRecordUpsertEtalonDQAfterExecutor implements DataRecordAfterExecutor<UpsertRequestContext> {

    /** The dq service. */
    @Autowired
    private DataQualityServiceExt dqService;

    /** The metamodel service. */
    @Autowired
    private MetaModelServiceExt metamodelService;

    /** The etalon composer. */
    @Autowired
    private EtalonComposer etalonComposer;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor
     * #execute(com.unidata.mdm.backend.service.data.DataRequestContext)
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
        if (ctx.isSkipCleanse() || action == UpsertAction.NO_ACTION) {
            return true;
        }

        MeasurementPoint.start();
        try {

            EtalonRecord base = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
            if (base == null) {
                return true;
            }

            EntityDef entityDef = metamodelService.getEntityByIdNoDeps(base.getInfoSection().getEntityName());
            LookupEntityDef lookupEntityDef = metamodelService.getLookupEntityById(base.getInfoSection().getEntityName());
            List<DQRuleDef> rules = entityDef == null ? lookupEntityDef.getDataQualities() : entityDef.getDataQualities();
            rules = dqService.filterForEtalon(rules);

            DQContext<DataRecord> dqContext = new DQContext<>();
            dqContext.withEntityName(base.getInfoSection().getEntityName());
            dqContext.withRecordId(base.getInfoSection().getEtalonKey().getId());
            dqContext.withRecordValidFrom(base.getInfoSection().getValidFrom());
            dqContext.withUserStorage(ctx.getUserStorage());
            dqContext.withRecordValidTo(base.getInfoSection().getValidTo());
            dqContext.withRecord(dqService.copyRecord(base));

            List<CalculableHolder<OriginRecord>> versions = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_BASE);
            List<CalculableHolder<OriginRecord>> enrichments = new ArrayList<>(versions != null ? versions : Collections.emptyList());

            List<OriginRecord> results = new ArrayList<>();
            rules.stream().sorted((r1, r2) -> r1.getOrder().compareTo(r2.getOrder())).forEachOrdered(r -> {
                // UN-6492
                // Consistency checks don't work, as they have to, because entities reindexed in random order.
                if (r.getRClass() == DQRuleClass.SYSTEM && r.isSpecial() && ctx.isSkipConsistencyChecks()) {
                    return;
                }

                dqContext.withRules(Collections.singletonList(r));
                dqService.applyRules(dqContext);

                if (r.getEnrich() != null && r.getEnrich().getSourceSystem() != null && r.getType().contains(DQRuleType.ENRICH)) {

                    OriginRecord record = createEnrichmentOriginRecord(dqContext.getRecord(), r,
                            base.getInfoSection().getEntityName(),
                            base.getInfoSection().getEtalonKey().getId());

                    enrichments.add(new RecordHolder(record));

                    // UN-2327
                    DataRecord composed = etalonComposer.compose(EtalonCompositionDriverType.BVT, enrichments, true, false);
                    dqContext.withRecord(composed);

                    results.add(record);
                }

                ctx.getDqErrors().addAll(dqContext.getErrors());
            });

            if(!results.isEmpty()) {
                ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_ENRICHMENT, results);
            }

            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Create origin record from nested record.
     *
     * @param record
     *            nested record.
     * @param rule
     *            the rule
     * @param entityName
     *            entity name.
     * @param etalonId
     *            the etalon id
     * @return new origin record.
     */
    private OriginRecord createEnrichmentOriginRecord(DataRecord record, DQRuleDef rule, String entityName, String etalonId) {

        String user = SecurityUtils.getCurrentUserName();
        Date now = new Date();
        return new OriginRecordImpl()
            .withDataRecord(SerializableDataRecord.of(record))
            .withInfoSection(new OriginRecordInfoSection()
                    .withOriginKey(OriginKey.builder()
                            .entityName(entityName)
                            .id(IdUtils.v1String())
                            .externalId(String.join("__", etalonId, rule.getName()))
                            .sourceSystem(rule.getEnrich().getSourceSystem())
                            .build())
                    .withStatus(RecordStatus.ACTIVE)
                    .withCreatedBy(user)
                    .withCreateDate(now)
                    .withUpdatedBy(user)
                    .withUpdateDate(now));
    }
}
