package com.unidata.mdm.backend.service.data.listener.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemSecurityException;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.listener.AbstractSecurityInfoContextEnricher;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.conf.WorkflowProcessType;


/**
 * @author Mikhail Mikhailov
 *
 */
public class DataRecordDeleteSecurityBeforeExecutor
    extends AbstractSecurityInfoContextEnricher<DeleteRequestContext>
    implements DataRecordBeforeExecutor<DeleteRequestContext> {

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(DataRecordDeleteSecurityBeforeExecutor.class);

    /**
     * Constructor.
     */
    public DataRecordDeleteSecurityBeforeExecutor() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.data.listener.DataRecordExecutor#execute(com.unidata.mdm.backend.common.context.CommonRequestContext)
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {
        RecordKeys keys = ctx.getFromStorage(StorageId.DATA_DELETE_KEYS);

        super.putResourceRights(ctx, StorageId.DATA_DELETE_RIGHTS, keys.getEntityName());
        Right rights = ctx.getFromStorage(StorageId.DATA_DELETE_RIGHTS);
        if (!rights.isDelete()) {
            if (ctx.isInactivatePeriod()) {
                if (!rights.isUpdate()) {
                    final String message = "The user '{}' has no or unsufficient update rights for resource '{}'. Delete denied.";
                    LOGGER.info(message, SecurityUtils.getCurrentUserName(), keys.getEntityName());
                    throw new SystemSecurityException(message,
                            ExceptionId.EX_DATA_UPSERT_UPDATE_NO_RIGHTS, SecurityUtils.getCurrentUserName(), keys.getEntityName());
                }
            } else {
                final String message = "The user '{}' has no or unsufficient delete rights for resource '{}'. Delete denied.";
                LOGGER.info(message, SecurityUtils.getCurrentUserName(), keys.getEntityName());
                throw new SystemSecurityException(message,
                        ExceptionId.EX_DATA_DELETE_NO_RIGHTS, SecurityUtils.getCurrentUserName(), keys.getEntityName());
            }
        }

        // Don't overwrite assignments, since they can be set somewhere else
        if (ctx.getFromStorage(StorageId.DATA_DELETE_WF_ASSIGNMENTS) == null) {
            // Set RECORD_EDIT for period delete
            if (ctx.isInactivatePeriod()) {
                super.putWorkflowAssignments(ctx, StorageId.DATA_DELETE_WF_ASSIGNMENTS,
                    keys.getEntityName(),
                    WorkflowProcessType.RECORD_EDIT);
            // Set RECORD_DELETE otherwise
            } else {
                super.putWorkflowAssignments(ctx, StorageId.DATA_DELETE_WF_ASSIGNMENTS,
                    keys.getEntityName(),
                    WorkflowProcessType.RECORD_DELETE);
            }
        }

        return true;
    }
}
