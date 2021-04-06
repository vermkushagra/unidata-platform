package com.unidata.mdm.backend.service.data.listener.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemSecurityException;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class RelationDeleteCheckSecurityBeforeExecutor implements DataRecordBeforeExecutor<DeleteRelationRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationDeleteCheckSecurityBeforeExecutor.class);
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRelationRequestContext dCtx) {

        RelationKeys relationKeys = dCtx.relationKeys();
        Right rights = dCtx.getFromStorage(StorageId.RELATIONS_FROM_RIGHTS);

        if (!rights.isDelete()) {
            final String message = "Delete of relation of type {} is denied for user {} due to missign delete rights on the {} object (left side)";
            LOGGER.info(message, relationKeys.getRelationName(), SecurityUtils.getCurrentUserName(), relationKeys.getFrom().getEntityName());
            throw new SystemSecurityException(message, ExceptionId.EX_DATA_RELATIONS_DELETE_NO_RIGHTS,
                    relationKeys.getRelationName(), SecurityUtils.getCurrentUserName(), relationKeys.getFrom().getEntityName());
        }

        return true;
    }
}
