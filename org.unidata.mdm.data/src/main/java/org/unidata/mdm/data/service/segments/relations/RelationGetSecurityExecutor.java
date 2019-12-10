package org.unidata.mdm.data.service.segments.relations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.exception.PlatformSecurityException;
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.GetRelationRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * Executor responsible for modifying relations have an alias key.
 */
@Component(RelationGetSecurityExecutor.SEGMENT_ID)
public class RelationGetSecurityExecutor extends Point<GetRelationRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_GET_SECURITY]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relations.get.security.description";
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationGetSecurityExecutor.class);
    /**
     * Constructor.
     */
    public RelationGetSecurityExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(GetRelationRequestContext gCtx) {

        RelationKeys relationKeys = gCtx.relationKeys();
        Right rights = gCtx.accessRight();

        if (!rights.isRead()) {
            final String message = "Read of relation of type {} is denied for user {} due to missing read rights on the {} object (left or right (for containments) side)";
            LOGGER.info(message, relationKeys.getRelationName(), SecurityUtils.getCurrentUserName(), relationKeys.getFromEntityName());
            throw new PlatformSecurityException(message, DataExceptionIds.EX_DATA_RELATIONS_GET_NO_RIGHTS,
                    relationKeys.getRelationName(), SecurityUtils.getCurrentUserName(), relationKeys.getFromEntityName());
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return GetRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
