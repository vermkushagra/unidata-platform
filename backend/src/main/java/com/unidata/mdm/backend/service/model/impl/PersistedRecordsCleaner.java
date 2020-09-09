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

package com.unidata.mdm.backend.service.model.impl;

import com.unidata.mdm.backend.dao.MetaModelDao;
import com.unidata.mdm.backend.service.model.ModelType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.model.RecordsCleaner;

@Component
public class PersistedRecordsCleaner implements RecordsCleaner {

    /**
     * Ralation component
     */
    @Autowired
    private RelationsServiceComponent relationsServiceComponent;

    /**
     * Meta model DAO.
     */
    @Autowired
    private MetaModelDao metaModelDao;

    @Override

    public void cleanRelatedRecords(DeleteModelRequestContext context) {
        //UN-4757 После удалении связей из реестра невозможно удалить запись
        //perhaps we need also deactivate for entity and lookup entity
        context.getRelationIds().forEach(id -> relationsServiceComponent.deactiveteRelationsByName(id));

        if(CollectionUtils.isNotEmpty(context.getNestedEntitiesIds())){
            metaModelDao.deleteRecords(context.getStorageId(), ModelType.NESTED_ENTITY, context.getNestedEntitiesIds());
        }
    }
}
