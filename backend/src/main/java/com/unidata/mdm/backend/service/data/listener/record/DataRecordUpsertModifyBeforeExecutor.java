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

import static com.unidata.mdm.backend.api.rest.SearchRestService.LOGGER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.meta.SimpleDataType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FacetName;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.CodeAttributeAlias;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.util.AttributeUtils;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleAttributesHolderEntityDef;

/**
 * Executor responsible for modifying records have a links to lookup entities but used for this alias code attributes.
 */
public class DataRecordUpsertModifyBeforeExecutor implements DataRecordBeforeExecutor<UpsertRequestContext> {

    /**
     * Search service
     */
    @Autowired
    private SearchService searchService;

    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;


    @Override
    public boolean execute(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            Collection<CodeAttributeAlias> aliasCodeAttributePointers = ctx.getCodeAttributeAliases();
            DataRecord originRecord = ctx.getRecord();
            if (CollectionUtils.isEmpty(aliasCodeAttributePointers) || originRecord == null) {
                return true;
            }

            RecordKeys keys = ctx.keys();
            String entityName = keys != null && keys.getEntityName() != null
                    ? keys.getEntityName()
                    : ctx.getEntityName();

            Collection<ModifyInstruction> modifyInstructions = createModifyInstructions(entityName, aliasCodeAttributePointers);
            Date asOf = ctx.getValidFrom() == null ? ctx.getValidTo() : ctx.getValidFrom();
            return modifyAliasCodeAttributeInOrigin(originRecord, modifyInstructions, asOf);

        } finally {
            MeasurementPoint.stop();
        }
    }


    /**
     * Method change value of alias code attribute to real code attribute.
     *
     * @param originRecord mo
     * @return true if all available attributes was modified otherwise false.
     */
    private boolean modifyAliasCodeAttributeInOrigin(@Nonnull DataRecord originRecord,
            @Nonnull Collection<ModifyInstruction> modifyInstructions, @Nullable Date asOf) {

        if (modifyInstructions.isEmpty()) {
            return true;
        }
        //todo merge optimization which reduce number of requests to ES

        MeasurementPoint.start();
        try {
            for (ModifyInstruction modifyInstruction : modifyInstructions) {

                Collection<SimpleAttribute<?>> modifiedAttrs
                    = originRecord.getSimpleAttributeRecursive(modifyInstruction.getRecordAttrName());

                List<Object> aliasCodeAttrs = modifiedAttrs.stream()
                        .filter(modifiedAttr -> modifiedAttr.getValue() != null)
                        .map(attr -> (Object) attr.getValue())
                        .collect(Collectors.toList());

                if (aliasCodeAttrs.isEmpty()) {
                    continue;
                }

                //for include all time intervals.
                int multiplier = 3;
                SearchRequestContext searchContext = SearchRequestContext.forEtalonData(modifyInstruction.getLookupEntityName())
                        .asOf(asOf)
                        .form(FormFieldsGroup
                                .createAndGroup()
                                .addFormField(FormField.strictValues(modifyInstruction.getDataType(),
                                    modifyInstruction.getAliasCodeAttrName(),
                                    aliasCodeAttrs)))
                        .returnFields(Arrays.asList(modifyInstruction.getCodeAttrName(), modifyInstruction.getAliasCodeAttrName()))
                        .facets(Collections.singletonList(FacetName.FACET_NAME_ACTIVE_ONLY))
                        .count(aliasCodeAttrs.size() * multiplier)
                        .page(0)
                        .build();

                SearchResultDTO searchResultDTO = searchService.search(searchContext);

                Map<String, SearchResultHitFieldDTO> result = searchResultDTO.getHits().stream()
                                                                             .map(hit -> Pair.of(hit.getFieldValue(modifyInstruction.getAliasCodeAttrName()), hit.getFieldValue(modifyInstruction.getCodeAttrName())))
                                                                             .filter(pair -> pair.getLeft() != null || pair.getLeft().isNonNullField())
                                                                             .collect(Collectors.toMap(pair -> pair.getLeft().getFirstValue().toString(), Pair::getRight));

                modifiedAttrs.stream()
                        .filter(modifiedAttr -> modifiedAttr.getValue() != null)
                        .forEach(modifiedAttr -> this.setRealCodeAttr(modifiedAttr, result));
            }
        } finally {
            MeasurementPoint.stop();
        }
        return true;
    }

    private void setRealCodeAttr(@Nonnull SimpleAttribute<?> simpleAttribute,@Nonnull Map<String, SearchResultHitFieldDTO> result) {
        String aliasCodeAttr = simpleAttribute.castValue().toString();
        SearchResultHitFieldDTO realCodeAttr = result.get(aliasCodeAttr);
        if (realCodeAttr != null && realCodeAttr.isNonNullField()) {
            AttributeUtils.processSimpleAttributeValue(simpleAttribute, realCodeAttr.getFirstValue());
        } else {
            LOGGER.warn("SKIP: Failed to upsert record.");
            throw new DataProcessingException("Alias Code Attribute not found", ExceptionId.EX_DATA_UPSERT_INVALID_ALIAS_CODE_ATTRIBUTE);
        }
    }

    /**
     * Transform alias code attribute pointers to modify instructions , which collect all necessary information about modification.
     *
     * @param entityName                 - from this entity or lookup entity refers to lookup entity
     * @param aliasCodeAttributePointers - collection of pointer which show
     * @return collection of modify instructions.
     */
    private Collection<ModifyInstruction> createModifyInstructions(
            @Nonnull String entityName,
            @Nonnull Collection<CodeAttributeAlias> aliasCodeAttributePointers) {

        if (aliasCodeAttributePointers.isEmpty()) {
            return Collections.emptyList();
        }

        SimpleAttributesHolderEntityDef simpleAttributesHolderEntityDef;
        Collection<NestedEntityDef> nestedEntityDefs = Collections.emptyList();
        if (metaModelService.isEntity(entityName)) {
            GetEntityDTO entityDTO = metaModelService.getEntityById(entityName);
            simpleAttributesHolderEntityDef = entityDTO.getEntity();
            nestedEntityDefs = entityDTO.getRefs();
        } else if (metaModelService.isLookupEntity(entityName)) {
            simpleAttributesHolderEntityDef = metaModelService.getLookupEntityById(entityName);
        } else {
            return Collections.emptyList();
        }

        //todo ask about severity of verifications(bellow)
        Collection<ModifyInstruction> modifyInstructions = new ArrayList<>(aliasCodeAttributePointers.size());
        for (CodeAttributeAlias pointer : aliasCodeAttributePointers) {
            String attributeName = pointer.getRecordAttributeName();
            AbstractAttributeDef simpleAttribute = ModelUtils.findModelAttribute(attributeName, simpleAttributesHolderEntityDef, nestedEntityDefs);


            //attribute not found or it is not a simple attribute
            if (simpleAttribute == null || !(simpleAttribute instanceof SimpleAttributeDef)) {
                continue;
            }

            String lookupEntityName = ((SimpleAttributeDef) simpleAttribute).getLookupEntityType();
            //simple attribute is not linked with lookup entity
            if (lookupEntityName == null) {
                continue;
            }

            LookupEntityDef lookupEntityDef = metaModelService.getLookupEntityById(lookupEntityName);
            //lookup entity doesn't exist
            if (lookupEntityDef == null) {
                continue;
            }

            String refToAttribute = pointer.getAliasAttributeName();
            CodeAttributeDef aliasCodeAttr = lookupEntityDef.getAliasCodeAttributes()
                    .stream()
                    .filter(attr -> refToAttribute.equals(attr.getName()))
                    .findAny()
                    .orElse(null);

            //alias attribute doesn't exist
            if (aliasCodeAttr == null) {
                continue;
            }

            String realCodeAttr = lookupEntityDef.getCodeAttribute().getName();
            modifyInstructions.add(new ModifyInstruction(attributeName, lookupEntityName, refToAttribute, realCodeAttr,
                    aliasCodeAttr.getSimpleDataType()));
        }
        return modifyInstructions;
    }

    private class ModifyInstruction {
        @Nonnull
        private final String recordAttrName;
        @Nonnull
        private final String lookupEntityName;
        @Nonnull
        private final String aliasCodeAttrName;
        @Nonnull
        private final String codeAttrName;
        @Nonnull
        private final SimpleDataType dataType;

        public ModifyInstruction(@Nonnull String recordAttrName,
                                 @Nonnull String lookupEntityName,
                                 @Nonnull String aliasCodeAttrName,
                                 @Nonnull String codeAttrName,
                                 @Nonnull SimpleDataType dataType) {
            this.recordAttrName = recordAttrName;
            this.lookupEntityName = lookupEntityName;
            this.aliasCodeAttrName = aliasCodeAttrName;
            this.codeAttrName = codeAttrName;
            this.dataType = dataType;
        }

        /**
         * @return name of attribute which will be modified in record
         */
        @Nonnull
        public String getRecordAttrName() {
            return recordAttrName;
        }

        /**
         * @return name of alias code attribute in lookup entity
         */
        @Nonnull
        public String getAliasCodeAttrName() {
            return aliasCodeAttrName;
        }

        /**
         * @return name of code attribute in lookup entity
         */
        @Nonnull
        public String getCodeAttrName() {
            return codeAttrName;
        }

        /**
         * @return name of lookup entity
         */
        @Nonnull
        public String getLookupEntityName() {
            return lookupEntityName;
        }

        /**
         * @return data type
         */
        @Nonnull
        public SimpleDataType getDataType() {
            return dataType;
        }
    }

}
