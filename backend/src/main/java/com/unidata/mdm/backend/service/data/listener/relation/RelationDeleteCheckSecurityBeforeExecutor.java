/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
