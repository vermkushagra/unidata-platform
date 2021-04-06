package com.unidata.mdm.backend.service.data.listener.relation;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext.UpsertRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.data.batch.BatchKeyReference;
import com.unidata.mdm.backend.service.data.batch.RecordUpsertBatchSet;
import com.unidata.mdm.backend.service.data.batch.RelationBatchSet;
import com.unidata.mdm.backend.service.data.batch.RelationUpsertBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.data.relations.CommonRelationsComponent;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class RelationUpsertContainmentBeforeExecutor implements DataRecordBeforeExecutor<UpsertRelationRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationUpsertContainmentBeforeExecutor.class);
    /**
     * Origin records component.
     */
    @Autowired
    private OriginRecordsComponent originRecordsComponent;
    /**
     * Common relations component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * Audit events writer.
     */
    @Autowired
    private AuditEventsWriter auditEventsWriter;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {
            // 1. Collect prerequisites
            RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
            if (relation.getRelType() != RelType.CONTAINS) {
                return true;
            }

            WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.RELATIONS_FROM_WF_ASSIGNMENTS);

            // 2. Collect assignment state
            RelationKeys relationKeys = commonRelationsComponent.ensureAndGetRelationKeys(relation.getName(), ctx);
            ApprovalState state = relationKeys == null
                    ? DataRecordUtils.calculateRecordState(ctx, assignment)
                    : DataRecordUtils.calculateVersionState(ctx, relationKeys.getFrom(), assignment);

            // 3. Upsert 'TO' record.
            String targetEtalonId = null;
            String targetOriginId = null;
            String targetSourceSystem = null;
            String targetExternalId = null;
            if (relationKeys != null && relationKeys.getTo() != null) {

                if (relationKeys.getTo().getOriginKey() != null) {
                    targetOriginId = relationKeys.getTo().getOriginKey().getId();
                    targetSourceSystem = relationKeys.getTo().getOriginKey().getSourceSystem();
                    targetExternalId = relationKeys.getTo().getOriginKey().getExternalId();
                }

                if (relationKeys.getTo().getEtalonKey() != null) {
                    targetEtalonId = relationKeys.getTo().getEtalonKey().getId();
                }

            } else {
                targetEtalonId = ctx.getEtalonKey();
                targetOriginId = ctx.getOriginKey();
                targetSourceSystem = ctx.getSourceSystem();
                targetExternalId = ctx.getExternalId();
            }

            DataRecord ir = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
            UpsertRequestContext uCtx = new UpsertRequestContextBuilder()
                    .record(ir)
                    .etalonKey(targetEtalonId)
                    .originKey(targetOriginId)
                    .sourceSystem(targetSourceSystem)
                    .externalId(targetExternalId)
                    .entityName(relation.getToEntity())
                    .validFrom(ctx.getValidFrom())
                    .validTo(ctx.getValidTo())
                    .returnEtalon(true)
                    .approvalState(state)
                    .batchUpsert(ctx.isBatchUpsert())
                    .auditLevel(ctx.getAuditLevel())
                    .build();

            uCtx.setOperationId(ctx.getOperationId());

            try {
                // TODO move batch code  to a separate method.
                if (ctx.isBatchUpsert()) {

                    RelationBatchSet rbs = ctx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                    RelationUpsertBatchSetAccumulator rbsa = (RelationUpsertBatchSetAccumulator) rbs.getRelationsAccumulator();

                    uCtx.skipNotification();
                    uCtx.putToStorage(StorageId.DATA_BATCH_RECORDS, new RecordUpsertBatchSet(rbsa.getRecordBatchSetAccumulator()));

                    // If the TO keys have already been resolved withing the commit interval
                    // set it to the target. Check keys cache otherwise.
                    RecordKeys toKeys = null;
                    if (Objects.nonNull(ctx.keys())) {
                        toKeys = ctx.keys();
                    } else if (Objects.nonNull(relationKeys)) {
                        toKeys = relationKeys.getTo();
                    }

                    if (Objects.nonNull(toKeys)) {
                        uCtx.putToStorage(uCtx.keysId(), toKeys);
                    } else {

                        BatchKeyReference<RecordKeys> cachedKeys = rbsa.getRecordBatchSetAccumulator().findCachedKeys(uCtx);
                        toKeys = cachedKeys != null ? cachedKeys.getKeys() : null;
                        if (Objects.nonNull(toKeys)) {
                            uCtx.putToStorage(uCtx.keysId(), toKeys);
                        }
                    }

                    originRecordsComponent.upsertOrigin(uCtx);

                    // Accumulate this containment
                    rbsa.getRecordBatchSetAccumulator().accumulateOrigin(uCtx);

                } else {
                    originRecordsComponent.upsertOrigin(uCtx);
                }
            } catch (Exception exc) {

                if (AuditLevel.AUDIT_ERRORS <= uCtx.getAuditLevel()) {
                    auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT_RELATION, exc, uCtx);
                }

                final String message = "Integral record upsert to '{}' failed.";
                ctx.setDqErrors(uCtx.getDqErrors());
                LOGGER.warn(message, relation.getToEntity());
                throw new DataProcessingException(message, exc, ExceptionId.EX_DATA_RELATIONS_UPSERT_CONTAINS_FAILED, relation.getToEntity());
            }

            // 4. Gather keys
            RecordKeys toRecordKeys = uCtx.keys();
            ctx.putToStorage(ctx.keysId(), toRecordKeys);
            ctx.putToStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT, uCtx);

            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }
}
