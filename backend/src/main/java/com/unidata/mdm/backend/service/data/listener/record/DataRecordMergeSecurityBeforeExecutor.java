/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
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
 * Merge request context security enrichment.
 */
public class DataRecordMergeSecurityBeforeExecutor
    extends AbstractSecurityInfoContextEnricher<MergeRequestContext>
    implements DataRecordBeforeExecutor<MergeRequestContext> {

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(DataRecordMergeSecurityBeforeExecutor.class);

    /**
     * Constructor.
     */
    public DataRecordMergeSecurityBeforeExecutor() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.data.listener.DataRecordExecutor#execute(com.unidata.mdm.backend.common.context.CommonRequestContext)
     */
    @Override
    public boolean execute(MergeRequestContext ctx) {

        RecordKeys keys = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS);

        super.putResourceRights(ctx, StorageId.DATA_MERGE_RIGHTS, keys.getEntityName());
        super.putWorkflowAssignments(ctx, StorageId.DATA_MERGE_WF_ASSIGNMENTS, keys.getEntityName(), WorkflowProcessType.RECORD_MERGE);

        Right rights = ctx.getFromStorage(StorageId.DATA_MERGE_RIGHTS);
        if (!rights.isUpdate()) {
            final String message = "The user '{}' has no or unsufficient merge rights for resource '{}'. Merge denied.";
            LOGGER.info(message, SecurityUtils.getCurrentUserName(), keys.getEntityName());
            throw new SystemSecurityException(message,
                    ExceptionId.EX_DATA_MERGE_NO_RIGHTS, SecurityUtils.getCurrentUserName(), keys.getEntityName());
        }

        return true;
    }

}
