/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext.GetProcessRequestContextBuilder;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowProcessDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.service.data.listener.AbstractWorkflowProcessStarterAfterExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.wf.WorkflowService;


/**
 * @author Mikhail Mikhailov
 * Workflow starter on delete operations.
 */
public class DataRecordDeleteWorkflowProcessStarterAfterExecutor
        extends AbstractWorkflowProcessStarterAfterExecutor
        implements DataRecordAfterExecutor<DeleteRequestContext> {

    /**
     * Workflow service.
     */
    @Autowired(required = false)
    private WorkflowService workflowService;
    /**
     * Constructor.
     */
    public DataRecordDeleteWorkflowProcessStarterAfterExecutor() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.data.listener.DataRecordExecutor#execute(com.unidata.mdm.backend.common.context.CommonRequestContext)
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {

        // Skip on origin deactivate.
        if (ctx.isWorkflowAction() || ctx.isInactivateOrigin() || ctx.isBatchUpsert()) {
            return true;
        }

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.getFromStorage(StorageId.DATA_DELETE_KEYS);
            if (keys.isPending()) {

                // This may be either RECORD_EDIT for periods or RECORD_DELETE for delete operations
                WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_DELETE_WF_ASSIGNMENTS);
                if (assignment == null || workflowService == null) {
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

                ctx.skipNotification();

                EtalonRecord etalon = ctx.getFromStorage(StorageId.DATA_DELETE_ETALON_RECORD);
                return workflowService.start(
                    createStartWorkflowContext(assignment, keys, etalon, true, true,
                        ctx.getOperationId(), ctx.getValidFrom(), ctx.getValidTo()));
            }

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

}
