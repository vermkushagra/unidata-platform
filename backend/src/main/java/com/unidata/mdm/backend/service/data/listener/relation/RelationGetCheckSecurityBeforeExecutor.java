package com.unidata.mdm.backend.service.data.listener.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
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
public class RelationGetCheckSecurityBeforeExecutor implements DataRecordBeforeExecutor<GetRelationRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationGetCheckSecurityBeforeExecutor.class);
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(GetRelationRequestContext gCtx) {

        RelationKeys relationKeys = gCtx.relationKeys();
        Right rights = gCtx.getFromStorage(StorageId.RELATIONS_FROM_RIGHTS);

        if (!rights.isRead()) {
            final String message = "Read of relation of type {} is denied for user {} due to missing read rights on the {} object (left side)";
            LOGGER.info(message, relationKeys.getRelationName(), SecurityUtils.getCurrentUserName(), relationKeys.getFrom().getEntityName());
            throw new SystemSecurityException(message, ExceptionId.EX_DATA_RELATIONS_GET_NO_RIGHTS,
                    relationKeys.getRelationName(), SecurityUtils.getCurrentUserName(), relationKeys.getFrom().getEntityName());
        }

        return true;
    }
}
