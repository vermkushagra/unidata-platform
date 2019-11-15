package org.unidata.mdm.data.service.segments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.exception.PlatformSecurityException;
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov
 *
 */
@Component(RecordDeleteSecurityExecutor.SEGMENT_ID)
public class RecordDeleteSecurityExecutor extends Point<DeleteRequestContext>
    implements RecordIdentityContextSupport {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordDeleteSecurityExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_DELETE_SECURITY_CHECK]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.delete.security.check.description";
    /**
     * Constructor.
     */
    public RecordDeleteSecurityExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.data.listener.DataRecordExecutor#execute(com.unidata.mdm.backend.common.context.CommonRequestContext)
     */
    @Override
    public void point(DeleteRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        ctx.accessRight(SecurityUtils.getRightsForResourceWithDefault(selectEntityName(ctx)));

        Right rights = ctx.accessRight();
        if (!rights.isDelete()) {

            if (ctx.isInactivatePeriod()) {
                if (!rights.isUpdate()) {
                    final String message = "The user '{}' has no or unsufficient update rights for resource '{}'. Delete denied.";
                    LOGGER.info(message, SecurityUtils.getCurrentUserName(), keys.getEntityName());
                    throw new PlatformSecurityException(message,
                            DataExceptionIds.EX_DATA_UPSERT_UPDATE_NO_RIGHTS, SecurityUtils.getCurrentUserName(), keys.getEntityName());
                }
            } else {
                final String message = "The user '{}' has no or unsufficient delete rights for resource '{}'. Delete denied.";
                LOGGER.info(message, SecurityUtils.getCurrentUserName(), keys.getEntityName());
                throw new PlatformSecurityException(message,
                        DataExceptionIds.EX_DATA_DELETE_NO_RIGHTS, SecurityUtils.getCurrentUserName(), keys.getEntityName());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DeleteRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
