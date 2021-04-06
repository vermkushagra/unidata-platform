package com.unidata.mdm.backend.service.data.listener.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext.GetProcessRequestContextBuilder;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.service.data.listener.AbstractWorkflowProcessStarterAfterExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;

public class DataRecordDeleteWorkflowTaskDeclinerExecutor
    extends AbstractWorkflowProcessStarterAfterExecutor
    implements DataRecordBeforeExecutor<DeleteRequestContext> {

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(DataRecordDeleteWorkflowTaskDeclinerExecutor.class);

    @Override
    public boolean execute(DeleteRequestContext ctx) {

        RecordKeys keys = ctx.getFromStorage(StorageId.DATA_DELETE_KEYS);
        if (!keys.isPending()) {
            return true;
        }

        boolean cancelProcess = !ctx.isWorkflowAction() && ctx.getApprovalState() != ApprovalState.PENDING
                && ctx.isInactivateEtalon()
                && workflowService != null;

        if (cancelProcess) {
            // Cancel running processes for this etalon id
            GetProcessRequestContext pCtx = new GetProcessRequestContextBuilder()
                    .processKey(keys.getEtalonKey().getId())
                    .skipVariables(true)
                    .build();

            final String message = "Received external DELETE request for etalon id [" + keys.getEntityName()
                + ": " + keys.getEtalonKey().getId() + "] while being in PENDING state. Executing process cancel.";
            LOGGER.info(message);
            workflowService.cancel(pCtx, message);
        }

        return true;
    }
}
