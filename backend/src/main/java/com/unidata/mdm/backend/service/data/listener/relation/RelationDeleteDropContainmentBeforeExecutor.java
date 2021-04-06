package com.unidata.mdm.backend.service.data.listener.relation;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext.DeleteRequestContextBuilder;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * Executor responsible for dropping containment.
 */
public class RelationDeleteDropContainmentBeforeExecutor implements DataRecordBeforeExecutor<DeleteRelationRequestContext> {
    /**
     * Record component. Index updates are processed later.
     */
    @Autowired
    private OriginRecordsComponent originRecordsComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
            if (relation.getRelType() != RelType.CONTAINS) {
                return true;
            }

            RelationKeys keys = ctx.relationKeys();

            WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.RELATIONS_FROM_WF_ASSIGNMENTS);
            ApprovalState state = !ctx.isInactivatePeriod()
                    ? DataRecordUtils.calculateRecordState(ctx, assignment)
                    : DataRecordUtils.calculateVersionState(ctx, keys.getFrom(), assignment);

            DeleteRequestContext dCtx = new DeleteRequestContextBuilder()
                    .etalonKey(keys.getTo().getEtalonKey().getId())
                    .cascade(false)
                    .validFrom(ctx.getValidFrom())
                    .validTo(ctx.getValidTo())
                    .approvalState(state)
                    .inactivatePeriod(ctx.isInactivatePeriod())
                    .inactivateEtalon(ctx.isInactivateEtalon())
                    .workflowAction(ctx.isWorkflowAction() || state == ApprovalState.PENDING)
                    .wipe(ctx.isWipe())
                    .batchUpsert(ctx.isBatchUpsert())
                    .suppressAudit(ctx.isSuppressAudit())
                    .auditLevel(ctx.getAuditLevel())
                    .build();

            dCtx.setOperationId(ctx.getOperationId());
            dCtx.putToStorage(dCtx.keysId(), keys.getTo());
            if (ctx.isBatchUpsert()) {
                dCtx.skipNotification();
            }

            originRecordsComponent.deleteOrigin(dCtx);
            ctx.putToStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT, dCtx);
            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }
}
