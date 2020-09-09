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

import static com.unidata.mdm.backend.common.search.FormField.strictValue;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.ReferenceAliasKey;
import com.unidata.mdm.backend.common.search.FacetName;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AbstractSimpleAttributeDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class RelationUpsertModifyBeforeExecutor implements DataRecordBeforeExecutor<UpsertRelationRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationUpsertModifyBeforeExecutor.class);
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /**
     * Search service.
     */
    @Autowired
    private SearchService searchService;

    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;

    @Override
    public boolean execute(UpsertRelationRequestContext uCtx) {

        ReferenceAliasKey referenceResolver = uCtx.getReferenceAliasKey();
        if (referenceResolver == null || referenceResolver.getValue() == null || referenceResolver.getEntityAttributeName() == null) {
            //skip if we doesn't have all necessary information about alias key
            return true;
        }

        RelationDef relationDef = uCtx.getFromStorage(StorageId.RELATIONS_META_DEF);
        if (relationDef == null || (relationDef.getRelType() != RelType.REFERENCES && relationDef.getRelType() != RelType.MANY_TO_MANY)) {
            //skip if relation is not exist or if it is contains
            return true;
        }

        String aliasAttrName = referenceResolver.getEntityAttributeName();
        String entityNameTo = relationDef.getToEntity();
        AbstractAttributeDef aliasAttribute = metaModelService.getEntityAttributeByPath(entityNameTo, aliasAttrName);
        if (!(aliasAttribute instanceof AbstractSimpleAttributeDef)) {
            //skip if alias key use complex attribute as a key
            return true;
        }

        AbstractSimpleAttributeDef simpleAttribute = (AbstractSimpleAttributeDef) aliasAttribute;

        if ((simpleAttribute instanceof SimpleAttributeDef && !((SimpleAttributeDef) simpleAttribute).isUnique())
         || (simpleAttribute instanceof CodeAttributeDef && !((CodeAttributeDef) simpleAttribute).isUnique())) {
            //skip if alias attribute is not unique
            return true;
        }

        FormField formField = strictValue(simpleAttribute.getSimpleDataType(), aliasAttrName, referenceResolver.getValue());

        Date asOf = uCtx.getValidFrom() == null ? uCtx.getValidTo() : uCtx.getValidFrom();

        SearchRequestContext searchContext = SearchRequestContext.forEtalonData(entityNameTo)
                .asOf(asOf)
                .form(FormFieldsGroup.createAndGroup(formField))
                .returnFields(Collections.singletonList(RecordHeaderField.FIELD_ETALON_ID.getField()))
                .facets(Collections.singletonList(FacetName.FACET_NAME_ACTIVE_ONLY))
                .count(10)
                .page(0)
                .build();

        SearchResultDTO searchResultDTO = searchService.search(searchContext);

        String etalonId = searchResultDTO.getHits().stream()
                .map(hit -> hit.getFieldValue(RecordHeaderField.FIELD_ETALON_ID.getField()))
                .filter(Objects::nonNull)
                .filter(SearchResultHitFieldDTO::isNonNullField)
                .filter(SearchResultHitFieldDTO::isSingleValue)
                .map(field-> field.getFirstValue().toString())
                .findAny()
                .orElse(null);

        if (etalonId == null) {
            // Considered supplementary. Just warn and continue.
            LOGGER.warn("Relation reference didn't resolved by reference alias key {}.", referenceResolver);
            return true;
        }

        RecordKeys keys = commonRecordsComponent.identify(EtalonKey.builder().id(etalonId).build());
        if (keys == null) {
            // Considered supplementary. Just warn and continue.
            LOGGER.warn("Relation reference didn't resolved by reference alias key {}.", referenceResolver);
            return true;
        }

        uCtx.putToStorage(uCtx.keysId(), keys);
        return true;
    }
}
