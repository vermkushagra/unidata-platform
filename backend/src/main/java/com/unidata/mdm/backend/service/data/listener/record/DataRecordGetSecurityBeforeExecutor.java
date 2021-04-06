package com.unidata.mdm.backend.service.data.listener.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.GetRequestContext;
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
public class DataRecordGetSecurityBeforeExecutor
    extends AbstractSecurityInfoContextEnricher<GetRequestContext>
    implements DataRecordBeforeExecutor<GetRequestContext> {

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(DataRecordGetSecurityBeforeExecutor.class);

    /**
     * Constructor.
     */
    public DataRecordGetSecurityBeforeExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(GetRequestContext ctx) {

        RecordKeys keys = ctx.keys();

        super.putResourceRights(ctx, StorageId.DATA_GET_RIGHTS, keys.getEntityName());
        super.putWorkflowAssignments(ctx, StorageId.DATA_GET_WF_ASSIGNMENTS, keys.getEntityName(), WorkflowProcessType.RECORD_EDIT);

        Right rights = ctx.getFromStorage(StorageId.DATA_GET_RIGHTS);
        if (!rights.isRead()) {
            final String message = "The user '{}' has no or unsufficient read rights for resource '{}'. Read denied.";
            LOGGER.info(message, SecurityUtils.getCurrentUserName(), keys.getEntityName());
            throw new SystemSecurityException(message,
                    ExceptionId.EX_DATA_GET_NO_RIGHTS, SecurityUtils.getCurrentUserName(), keys.getEntityName());
        }

        return true;
    }

}
