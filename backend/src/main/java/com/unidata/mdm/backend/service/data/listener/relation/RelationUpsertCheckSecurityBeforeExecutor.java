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

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemSecurityException;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.meta.RelationDef;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class RelationUpsertCheckSecurityBeforeExecutor implements DataRecordBeforeExecutor<UpsertRelationRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationUpsertCheckSecurityBeforeExecutor.class);
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRelationRequestContext uCtx) {

        Right rights = uCtx.getFromStorage(StorageId.RELATIONS_FROM_RIGHTS);
        UpsertAction action = uCtx.getFromStorage(StorageId.RELATIONS_UPSERT_EXACT_ACTION);
        RelationDef relationDef = uCtx.getFromStorage(StorageId.RELATIONS_META_DEF);

        if (!rights.isCreate() && action == UpsertAction.INSERT) {
            final String message = "Insert of relation of type {} is denied for user {} due to missing insert rights on the {} object (left side)";
            LOGGER.info(message, relationDef.getName(), SecurityUtils.getCurrentUserName(), relationDef.getFromEntity());
            throw new SystemSecurityException(message, ExceptionId.EX_DATA_RELATIONS_UPSERT_NO_INSERT_RIGHTS,
                    relationDef.getName(), SecurityUtils.getCurrentUserName(), relationDef.getFromEntity());
        }

        if (!rights.isUpdate() && action == UpsertAction.UPDATE) {
            final String message = "Update of relation of type {} is denied for user {} due to missing update rights on the {} object (left side)";
            LOGGER.info(message, relationDef.getName(), SecurityUtils.getCurrentUserName(), relationDef.getFromEntity());
            throw new SystemSecurityException(message, ExceptionId.EX_DATA_RELATIONS_UPSERT_NO_UPDATE_RIGHTS,
                    relationDef.getName(), SecurityUtils.getCurrentUserName(), relationDef.getFromEntity());
        }

        return true;
    }
}
