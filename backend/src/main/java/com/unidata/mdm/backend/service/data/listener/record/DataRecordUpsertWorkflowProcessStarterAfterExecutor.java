/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import com.unidata.mdm.backend.common.context.GetProcessRequestContext;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext.GetProcessRequestContextBuilder;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowProcessDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.listener.AbstractWorkflowProcessStarterAfterExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;


/**
 * @author Mikhail Mikhailov
 * Approve process starting point.
 */
public class DataRecordUpsertWorkflowProcessStarterAfterExecutor
    extends AbstractWorkflowProcessStarterAfterExecutor
    implements DataRecordAfterExecutor<UpsertRequestContext> {



    /**
     * Constructor.
     */
    public DataRecordUpsertWorkflowProcessStarterAfterExecutor() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.data.listener.DataRecordExecutor#execute(com.unidata.mdm.backend.common.context.CommonRequestContext)
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
        EtalonRecord etalon = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
        if (etalon == null || action == UpsertAction.NO_ACTION) {
            return true;
        }

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_UPSERT_WF_ASSIGNMENTS);
            WorkflowTimeIntervalDTO interval = ctx.getFromStorage(StorageId.DATA_UPSERT_WORKFLOW_INTERVAL);
            WorkflowTimelineDTO timeline = ctx.getFromStorage(StorageId.DATA_RECORD_TIMELINE);

            Boolean isPending = timeline != null ? timeline.isPending() : keys.getEtalonState() == ApprovalState.PENDING;
            Boolean isPublished = timeline != null ? timeline.isPublished() :
                action == UpsertAction.INSERT && isPending
                    ? false
                    : true; // <- This branch is expected to be executed for INSERT actions only.

            // Skip other intervals, records not in pending state and
            // pending records having more then one pending version (not the first pending change),
            // to ensure, we're processing the first pending version of the record
            if (ctx.isRecalculateWholeTimeline()
            || assignment == null
            || !isPending
            || interval.getPendings().size() == 0
            || workflowService == null) {
                return true;
            }

            // Skip if a process instance is already running for this etalon id
            GetProcessRequestContext pCtx = new GetProcessRequestContextBuilder()
                    .processKey(keys.getEtalonKey().getId())
                    .skipVariables(true)
                    .build();

            WorkflowProcessDTO current = workflowService.process(pCtx);
            if (current != null) {
                return true;
            }

            return workflowService.start(
                createStartWorkflowContext(assignment, keys, etalon, isPublished, false,
                    ctx.getOperationId(), ctx.getValidFrom(), ctx.getValidTo()));

        } finally {
            MeasurementPoint.stop();
        }
    }

}
