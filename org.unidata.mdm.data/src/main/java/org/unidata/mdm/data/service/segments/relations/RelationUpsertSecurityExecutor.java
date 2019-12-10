package org.unidata.mdm.data.service.segments.relations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.exception.PlatformSecurityException;
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * Executor responsible for modifying relations have an alias key.
 */
@Component(RelationUpsertSecurityExecutor.SEGMENT_ID)
public class RelationUpsertSecurityExecutor
    extends Point<UpsertRelationRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationUpsertSecurityExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_UPSERT_SECURITY]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relations.upsert.security.description";
    /**
     * MMS.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public RelationUpsertSecurityExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRelationRequestContext uCtx) {

        Right rights = uCtx.accessRight();
        UpsertAction action = uCtx.upsertAction();
        RelationDef relationDef = metaModelService.getRelationById(uCtx.relationName());

        if ((!rights.isCreate() && !rights.isUpdate()) && action == UpsertAction.INSERT) {
            final String message = "Insert of relation of type {} is denied for user {} due to missing insert rights on the {} object (left side)";
            LOGGER.info(message, relationDef.getName(), SecurityUtils.getCurrentUserName(), relationDef.getFromEntity());
            throw new PlatformSecurityException(message, DataExceptionIds.EX_DATA_RELATIONS_UPSERT_NO_INSERT_RIGHTS,
                    relationDef.getName(), SecurityUtils.getCurrentUserName(), relationDef.getFromEntity());
        }

        if (!rights.isUpdate() && action == UpsertAction.UPDATE) {
            final String message = "Update of relation of type {} is denied for user {} due to missing update rights on the {} object (left side)";
            LOGGER.info(message, relationDef.getName(), SecurityUtils.getCurrentUserName(), relationDef.getFromEntity());
            throw new PlatformSecurityException(message, DataExceptionIds.EX_DATA_RELATIONS_UPSERT_NO_UPDATE_RIGHTS,
                    relationDef.getName(), SecurityUtils.getCurrentUserName(), relationDef.getFromEntity());
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
