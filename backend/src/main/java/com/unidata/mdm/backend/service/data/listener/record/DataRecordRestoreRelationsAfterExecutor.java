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

package com.unidata.mdm.backend.service.data.listener.record;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.RelationHeaderField;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.service.data.RecordsServiceComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static com.unidata.mdm.backend.common.search.fields.RelationHeaderField.FIELD_FROM_ETALON_ID;
import static com.unidata.mdm.backend.common.search.fields.RelationHeaderField.REL_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Data record restore relations
 */
public class DataRecordRestoreRelationsAfterExecutor
        implements DataRecordAfterExecutor<UpsertRequestContext> {
    @Autowired
    private SearchService searchService;

    @Autowired
    RecordsServiceComponent recordsServiceComponent;

    @Autowired
    private MetaModelService metaModelService;


    @Override
    public boolean execute(UpsertRequestContext requestContext) {

        RecordKeys keys = requestContext.keys();

        Map<RelationDef, EntityDef> containsDef = metaModelService.getEntityRelationsByType(keys.getEntityName(), Collections.singletonList(RelType.CONTAINS), false, true);

        if (MapUtils.isEmpty(containsDef)) {
            return true;
        }

        FormField fromEtalon = FormField.strictString(FIELD_FROM_ETALON_ID.getField(), keys.getEtalonKey().getId());
        FormField relType = FormField.strictString(REL_TYPE.getField(), RelationType.CONTAINS.name());

        List<String> searchFields = new ArrayList<>();
        searchFields.add(RelationHeaderField.FIELD_FROM.getField());
        searchFields.add(RelationHeaderField.FIELD_TO.getField());
        searchFields.add(RelationHeaderField.FIELD_TO_ETALON_ID.getField());
        searchFields.add(RelationHeaderField.FIELD_ETALON_ID.getField());
        searchFields.add(RelationHeaderField.REL_NAME.getField());

        SearchRequestContext relationsContext = SearchRequestContext.forEtalonRelation(keys.getEntityName())
                .form(FormFieldsGroup.createAndGroup(fromEtalon, relType))
                .onlyQuery(true)
                .count(10)
                .returnFields(searchFields)
                .build();

        SearchResultDTO relations = searchService.search(relationsContext);
        if (CollectionUtils.isNotEmpty(relations.getHits())) {

            List<UpsertRequestContext> etalonToSideForRestore = new ArrayList<>();

            for (SearchResultHitDTO relationHit : relations.getHits()) {
                String relationToId = relationHit.getFieldValue(RelationHeaderField.FIELD_TO_ETALON_ID.getField()).getFirstValue().toString();

                etalonToSideForRestore.add(new UpsertRequestContext.UpsertRequestContextBuilder()
                        .etalonKey(relationToId)
                        .build());
            }

            if (CollectionUtils.isNotEmpty(etalonToSideForRestore)) {
                etalonToSideForRestore.forEach(rCtx -> recordsServiceComponent.restoreRecord(rCtx, false));
            }
        }

        return true;
    }
}
