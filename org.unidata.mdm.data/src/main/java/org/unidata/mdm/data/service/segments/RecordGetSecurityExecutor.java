package org.unidata.mdm.data.service.segments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.exception.PlatformSecurityException;
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov
 * The former GET SEC executor.
 */
@Component(RecordGetSecurityExecutor.SEGMENT_ID)
public class RecordGetSecurityExecutor extends Point<GetRequestContext> implements RecordIdentityContextSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_GET_SECURITY]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.get.security.description";

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(RecordGetSecurityExecutor.class);

    /**
     * Constructor.
     */
    public RecordGetSecurityExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void point(GetRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        ctx.accessRight(SecurityUtils.getRightsForResourceWithDefault(selectEntityName(ctx)));

        Right rights = ctx.accessRight();
        if (!rights.isRead()) {
            final String message = "The user '{}' has no or unsufficient read rights for resource '{}'. Read denied.";
            LOGGER.info(message, SecurityUtils.getCurrentUserName(), keys.getEntityName());
            throw new PlatformSecurityException(message,
                    DataExceptionIds.EX_DATA_GET_NO_RIGHTS,
                    SecurityUtils.getCurrentUserName(), keys.getEntityName());
        }
    }

    @Override
    public boolean supports(Start<?> start) {
        return GetRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }

}
