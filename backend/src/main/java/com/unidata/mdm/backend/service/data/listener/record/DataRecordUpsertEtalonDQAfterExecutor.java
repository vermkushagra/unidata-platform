/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.common.context.DataQualityContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext.UpsertRequestContextBuilder;
import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.data.ModificationBox;
import com.unidata.mdm.backend.common.dq.DataQualityExecutionMode;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.service.DataQualityService;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl;
import com.unidata.mdm.backend.service.data.batch.RecordUpsertBatchSet;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.meta.DQRuleDef;

/**
 * The Class DQAfterUpsertExecutor.
 */
public class DataRecordUpsertEtalonDQAfterExecutor implements DataRecordAfterExecutor<UpsertRequestContext> {

    /** The dq service. */
    @Autowired
    private DataQualityService dqService;

    /** The metamodel service. */
    @Autowired
    private MetaModelServiceExt metaModelService;

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

            EtalonRecord state = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
            if (state == null) {
                return true;
            }

            // Create box. Temporarily use new one. Use the real one from the parent context after switch.
            List<CalculableHolder<DataRecord>> versions = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_BASE);
            ModificationBox box = ModificationBox.of(versions);
            box.etalonState(state);

            DataQualityContext dqCtx = DataQualityContext.builder(ctx)
                    .modificationBox(box)
                    .rules(selectRules(ctx))
                    .executionMode(DataQualityExecutionMode.MODE_ETALON)
                    .build();

            ContextUtils.storageCopy(ctx, dqCtx, ctx.keysId());

            dqService.apply(dqCtx);

            ctx.getDqErrors().addAll(dqCtx.getErrors());

            if(checkModifications(ctx, box)) {

                EtalonRecord result = new EtalonRecordImpl()
                        .withDataRecord(box.etalonState())
                        .withInfoSection(state.getInfoSection());

                ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_RECORD, result);
                ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_BASE, box.toCalculables());
            }

            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

    // TODO rewrite this! Taken from fromer "recalculate state" executor
    private boolean checkModifications(UpsertRequestContext ctx, ModificationBox box) {

        RecordKeys keys = ctx.keys();
        RecordUpsertBatchSet bs = ctx.getFromStorage(StorageId.DATA_BATCH_RECORDS);

        boolean hasChanges = false;
        Map<String, List<CalculableHolder<DataRecord>>> modifications = box.toModifications();
        for (Entry<String, List<CalculableHolder<DataRecord>>> entry : modifications.entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            RecordKeys enrichmentKeys = null;
            for (int i = 0; i < entry.getValue().size(); i++) {

                CalculableHolder<DataRecord> ch = entry.getValue().get(i);

                OriginRecord enrichmentOrigin = (OriginRecord) ch.getValue();
                UpsertRequestContext enrichmentCtx = new UpsertRequestContextBuilder()
                        .record(ch.getValue())
                        .originKey(enrichmentOrigin.getInfoSection().getOriginKey())
                        .etalonKey(keys.getEtalonKey())
                        .validFrom(ctx.getValidFrom())
                        .validTo(ctx.getValidTo())
                        .enrichment(true)
                        .batchUpsert(ctx.isBatchUpsert())
                        .auditLevel(ctx.getAuditLevel())
                        .build();

                if (Objects.isNull(enrichmentKeys)) {

                    enrichmentKeys = RecordKeys.builder()
                            .entityName(keys.getEntityName())
                            .etalonKey(keys.getEtalonKey())
                            .etalonState(keys.getEtalonState())
                            .etalonStatus(keys.getEtalonStatus())
                            .build();
                }

                enrichmentCtx.putToStorage(StorageId.DATA_UPSERT_KEYS, enrichmentKeys);
                enrichmentCtx.putToStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD, enrichmentOrigin);
                enrichmentCtx.putToStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP, enrichmentOrigin.getInfoSection().getUpdateDate());

                // Create batch set, if needed
                if (ctx.isBatchUpsert()) {
                    enrichmentCtx.putToStorage(StorageId.DATA_BATCH_RECORDS, new RecordUpsertBatchSet(bs.getRecordsAccumulator()));
                }

                // Check origin records only once per orign key.
                if (i == 0) {

                    // UN-6569 handle enrichment keys separately before upsert
                    commonComponent.identify(enrichmentCtx);
                    originComponent.ensureSystemRecords(enrichmentCtx);
                }

                originComponent.putVersion(enrichmentCtx, enrichmentOrigin, DataShift.REVISED);

                // Collect updates
                if (ctx.isBatchUpsert()) {
                    bs.getRecordsAccumulator().accumulateOrigin(enrichmentCtx);
                }

                enrichmentKeys = enrichmentCtx.getFromStorage(StorageId.DATA_UPSERT_KEYS);
                hasChanges = true;
            }
        }

        return hasChanges;
    }

    /**
     * Selects rules to execute.
     * @param ctx the upsert context
     * @return collection of rules
     */
    private List<DQRuleDef> selectRules(UpsertRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        String entityName = keys == null ? ctx.getEntityName() : keys.getEntityName();

        EntityWrapper ew = metaModelService.getValueById(entityName, EntityWrapper.class);
        if (Objects.isNull(ew)) {

            LookupEntityWrapper lew = metaModelService.getValueById(entityName, LookupEntityWrapper.class);
            if (Objects.isNull(lew)) {
                return Collections.emptyList();
            }

            return lew.getEtalonRules();
        }

        return ew.getEtalonRules();
    }
}
