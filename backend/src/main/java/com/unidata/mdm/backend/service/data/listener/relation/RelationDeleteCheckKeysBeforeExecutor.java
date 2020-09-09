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

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.relations.CommonRelationsComponent;
import com.unidata.mdm.meta.RelationDef;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class RelationDeleteCheckKeysBeforeExecutor implements DataRecordBeforeExecutor<DeleteRelationRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationDeleteCheckKeysBeforeExecutor.class);
    /**
     * Common relations component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRelationRequestContext dCtx) {

        RelationDef relationDef = dCtx.getFromStorage(StorageId.RELATIONS_META_DEF);
        RelationKeys keys = commonRelationsComponent.ensureAndGetRelationKeys(relationDef.getName(), dCtx);
        if (Objects.isNull(keys)) {

            final String message
                = "Relation delete: relation of type [{}] not found by supplied keys - relation etalon id [{}], relation origin id [{}], "
                + "etalon id: [{}], origin id [{}], external id [{}], source system [{}], name [{}]";
            LOGGER.warn(message,
                    relationDef.getName(),
                    dCtx.getRelationEtalonKey(),
                    dCtx.getRelationOriginKey(),
                    dCtx.getEtalonKey(),
                    dCtx.getOriginKey(),
                    dCtx.getExternalId(),
                    dCtx.getSourceSystem(),
                    dCtx.getEntityName());
            throw new DataProcessingException(message, ExceptionId.EX_DATA_RELATIONS_DELETE_NOT_FOUND,
                    relationDef.getName(),
                    dCtx.getRelationEtalonKey(),
                    dCtx.getRelationOriginKey(),
                    dCtx.getEtalonKey(),
                    dCtx.getOriginKey(),
                    dCtx.getExternalId(),
                    dCtx.getSourceSystem(),
                    dCtx.getEntityName());
        }

        return true;
    }
}
