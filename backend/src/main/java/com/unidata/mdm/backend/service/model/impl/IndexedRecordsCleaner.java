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

import static com.unidata.mdm.backend.common.search.fields.RelationHeaderField.REL_NAME;
import static java.util.Collections.singletonList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.service.model.RecordsCleaner;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.RelationDef;

@Component
public class IndexedRecordsCleaner implements RecordsCleaner {

    /**
     * Meta model
     */
    @Autowired
    private MetaModelService metaModelService;

    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt searchService;

    @Override
    public void cleanRelatedRecords(DeleteModelRequestContext context) {
        String storageId = context.getStorageId();
        context.getEntitiesIds().forEach(id -> searchService.dropIndex(id, storageId));
        context.getLookupEntitiesIds().forEach(id -> searchService.dropIndex(id, storageId));
        Collection<RelationDef> relationDefs = context.getRelationIds()
                .stream()
                .map(id -> metaModelService.getRelationById(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (RelationDef rel : relationDefs) {
            EntityDef from = metaModelService.getEntityByIdNoDeps(rel.getFromEntity());
            if (from != null && !context.getEntitiesIds().contains(from.getName())) {
                dropAllIndexedRels(rel.getName(), from.getName(), storageId);
            }
            EntityDef to = metaModelService.getEntityByIdNoDeps(rel.getToEntity());
            if (to != null && !context.getEntitiesIds().contains(to.getName())) {
                dropAllIndexedRels(rel.getName(), to.getName(), storageId);
            }
        }
    }

    private void dropAllIndexedRels(String relName, String entityName, String storageId) {
        SearchRequestContext context = SearchRequestContext.forEtalonRelation(entityName)
                .storageId(storageId)
                .form(FormFieldsGroup.createAndGroup()
                        .addFormField(FormField.strictString(REL_NAME.getField(), relName)))
                .onlyQuery(true)
                .build();
        searchService.deleteFoundResult(context);
    }
}
