package org.unidata.mdm.data.service.segments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.impl.CommonRecordsComponent;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov
 *         'Delete' pre-check validator.
 */
@Component(RecordDeleteValidateExecutor.SEGMENT_ID)
public class RecordDeleteValidateExecutor extends Start<DeleteRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordDeleteValidateExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_DELETE_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.delete.start.description";
    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;
    /**
     * Constructor.
     */
    public RecordDeleteValidateExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, DeleteRequestContext.class);
    }

    /**
     * Execute.
     */
    @Override
    public void start(DeleteRequestContext ctx) {

        RecordKeys keys = null;
        if (ctx.isBatchUpsert()) {
            keys = ctx.keys();
        } else {
            keys = commonComponent.identify(ctx);
        }

        if (keys == null) {
            final String message = "Record submitted for (soft) deletion cannot be identified by supplied keys - etalon id: [{}], origin id [{}], external id [{}], source system [{}], name [{}]";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_INVALID_DELETE_INPUT,
                    ctx.getEtalonKey(),
                    ctx.getOriginKey(),
                    ctx.getExternalId(),
                    ctx.getSourceSystem(),
                    ctx.getEntityName());
        }

        if (keys.isPending() && (!ctx.isWorkflowAction() && ctx.getApprovalState() != ApprovalState.PENDING)) {
            if (ctx.isBatchUpsert()) {
                throw new DataProcessingException("Record in pending state. Delete disabled.",
                        DataExceptionIds.EX_DATA_DELETE_PERIOD_NOT_ACCEPTED_HAS_PENDING_RECORD);
            }
        }

        if (!ctx.isBatchUpsert()) {
            ctx.keys(keys);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pipeline select(DeleteRequestContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }
}
