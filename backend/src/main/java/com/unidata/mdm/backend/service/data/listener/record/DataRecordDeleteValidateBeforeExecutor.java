/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;

/**
 * @author Mikhail Mikhailov
 *         'Delete' pre-check validator.
 */
public class DataRecordDeleteValidateBeforeExecutor implements DataRecordBeforeExecutor<DeleteRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordDeleteValidateBeforeExecutor.class);
    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;
    /**
     * Constructor.
     */
    public DataRecordDeleteValidateBeforeExecutor() {
        super();
    }
    /**
     * Execute.
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {

        RecordKeys keys = commonComponent.identify(ctx);
        if (keys == null) {
            final String message = "Record submitted for (soft) deletion cannot be identified by supplied keys - etalon id: [{}], origin id [{}], external id [{}], source system [{}], name [{}]";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_INVALID_DELETE_INPUT,
                    ctx.getEtalonKey(),
                    ctx.getOriginKey(),
                    ctx.getExternalId(),
                    ctx.getSourceSystem(),
                    ctx.getEntityName());
        }

        if (keys.isPending() && (!ctx.isWorkflowAction() && ctx.getApprovalState() != ApprovalState.PENDING)) {
            throw new DataProcessingException("Record in pending state. Delete disabled.",
                    ExceptionId.EX_DATA_DELETE_PERIOD_NOT_ACCEPTED_HAS_PENDING_RECORD);
        }

        ctx.putToStorage(StorageId.DATA_DELETE_KEYS, keys);
        return true;
    }

}
