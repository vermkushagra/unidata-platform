package org.unidata.mdm.data.service.segments.relations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.exception.PlatformSecurityException;
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * Executor responsible for modifying relations have an alias key.
 */
@Component(RelationDeleteSecurityExecutor.SEGMENT_ID)
public class RelationDeleteSecurityExecutor extends Point<DeleteRelationRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationDeleteSecurityExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_DELETE_SECURITY]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.delete.security.description";
    /**
     * Constructor.
     */
    public RelationDeleteSecurityExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(DeleteRelationRequestContext dCtx) {

        RelationKeys relationKeys = dCtx.relationKeys();
        Right rights = dCtx.accessRight();

        if (!rights.isUpdate() && !rights.isDelete()) {
            final String message = "Delete of relation of type {} is denied for user {} due to missign delete rights on the {} object (left side)";
            LOGGER.info(message, relationKeys.getRelationName(), SecurityUtils.getCurrentUserName(), relationKeys.getFromEntityName());
            throw new PlatformSecurityException(message, DataExceptionIds.EX_DATA_RELATIONS_DELETE_NO_RIGHTS,
                    relationKeys.getRelationName(), SecurityUtils.getCurrentUserName(), relationKeys.getFromEntityName());
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DeleteRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
