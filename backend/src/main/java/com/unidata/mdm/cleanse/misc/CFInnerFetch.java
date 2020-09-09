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

package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.backend.common.search.FormField.strict;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT2;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.service.ServiceUtils;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;
import com.unidata.mdm.backend.service.data.util.AttributeUtils;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.meta.AbstractSimpleAttributeDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * Fetch data from inner entities
 */
public class CFInnerFetch extends BasicCleanseFunctionAbstract {

    /**
     * It is a port names. {@see cleanse.properties}
     */
    public static final String ENTITY_NAME_PORT = "entityName";
    public static final String RETURN_NAME_PORT = "returnFieldName";
    public static final String ORDER_NAME_PORT = "orderFieldName";
    public static final String SEARCH_NAME_PORT = "searchFieldName";
    public static final String SEARCH_VALUE_PORT = "searchValue";
    public static final String FETCH_MODE_PORT = "fetchMode";

    /**
     * Index of the first column
     */
    private static final int FIRST_HIT = 0;

    /**
     * Single result count
     */
    private static final long SINGLE_RESULT_COUNT = 1;

    /**
     * Search service.
     */
    private SearchService searchService;

    /**
     * Meta Model service
     */
    private MetaModelService metaModelService;

    /**
     * Instantiates a new cleanse function abstract.
     */
    public CFInnerFetch() {
        super(CFInnerFetch.class);
        this.searchService = ServiceUtils.getSearchService();
        this.metaModelService = ServiceUtils.getMetaModelService();
    }

    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws Exception {
        SearchRequestContext context = createSearchRequest(input);
        SearchResultDTO search = searchService.search(context);

        Attribute fetchedObject = retrieveAttr(search, input);
        result.put(OUTPUT1, fetchedObject);

        long count = search.getTotalCount();
        boolean justOne = count == SINGLE_RESULT_COUNT;
        result.put(OUTPUT2, new BooleanSimpleAttributeImpl(OUTPUT2).withValue(justOne));
        result.put("port3", new IntegerSimpleAttributeImpl("port3").withValue(count));
    }

    @Nonnull
    SearchRequestContext createSearchRequest(Map<String, Object> input) {
        String entityName = (String) super.getValueByPort(ENTITY_NAME_PORT, input);
        String returnFieldName = (String) super.getValueByPort(RETURN_NAME_PORT, input);
        String orderFieldName = (String) super.getValueByPort(ORDER_NAME_PORT, input);
        SimpleAttribute<?> searchValue = createSearchAttr(input);
        FormFieldsGroup group = createAndGroup(strict(searchValue));
        SortField sortField = createSortField(entityName, orderFieldName);
        return SearchRequestContext.forEtalonData(entityName)
                                   .form(group)
                                   .totalCount(true)
                                   .count(Integer.MAX_VALUE)
                                   .addSorting(Collections.singletonList(sortField))
                                   .page(0)
                                   .returnFields(Collections.singletonList(returnFieldName))
                                   .skipEtalonId(true)
                                   .build();
    }

    private SimpleAttribute<?> createSearchAttr(Map<String, Object> input) {
        String entityName = (String) super.getValueByPort(ENTITY_NAME_PORT, input);
        String searchFieldName = (String) super.getValueByPort(SEARCH_NAME_PORT, input);
        AbstractSimpleAttributeDef searchFieldDef = metaModelService.getAttributeByPath(entityName, searchFieldName);

        SimpleAttribute.DataType dataType = searchFieldDef.getSimpleDataType() == SimpleDataType.STRING
                || searchFieldDef.getSimpleDataType() == null ?
                SimpleAttribute.DataType.STRING :
                SimpleAttribute.DataType.INTEGER;

        Attribute attr = (Attribute) input.get(SEARCH_VALUE_PORT);

        if (attr == null) {
            return AbstractSimpleAttribute.of(dataType, searchFieldName);
        }

        SimpleAttribute.DataType valueDataType = null;
        Object value = null;
        switch (attr.getAttributeType()) {
        case SIMPLE:
            SimpleAttribute<?> simpleAttribute = (SimpleAttribute<?>) attr;
            value = simpleAttribute.getValue();
            valueDataType = simpleAttribute.getDataType();

            break;
        case CODE:
            CodeAttribute<?> codeAttribute = (CodeAttribute<?>) attr;
            value = codeAttribute.getValue();
            valueDataType = codeAttribute.getDataType() == CodeAttribute.CodeDataType.STRING ?
                    SimpleAttribute.DataType.STRING :
                    SimpleAttribute.DataType.INTEGER;
            break;
        default:
            throw new RuntimeException("Function supports only code and simple attributes");
        }

        if(dataType != valueDataType && dataType == SimpleAttribute.DataType.STRING && value != null){
            value = value.toString();
        }

        SimpleAttribute<?> searchValue = AbstractSimpleAttribute.of(valueDataType, searchFieldName);
        searchValue.castValue(value);
        return searchValue;
    }

    private SortField createSortField(String entityName, String orderFieldName) {
        AbstractSimpleAttributeDef sortFieldDef = metaModelService.getAttributeByPath(entityName, orderFieldName);
        boolean isAnalyzed = sortFieldDef.getSimpleDataType() == SimpleDataType.STRING || sortFieldDef.getSimpleDataType() == null;
        return new SortField(orderFieldName, SortField.SortOrder.ASC, isAnalyzed);
    }

    @Nullable
    Attribute retrieveAttr(SearchResultDTO search, Map<String, Object> input) {
        String entityName = (String) super.getValueByPort(ENTITY_NAME_PORT, input);
        String returnFieldName = (String) super.getValueByPort(RETURN_NAME_PORT, input);
        AbstractSimpleAttributeDef returnFieldDef = metaModelService.getAttributeByPath(entityName, returnFieldName);
        SimpleAttribute.DataType dataType = getDataType(returnFieldDef);
        SimpleAttribute<?> simpleAttribute = AbstractSimpleAttribute.of(dataType, OUTPUT1);

        if (search.getTotalCount() == 0 || search.getHits().isEmpty()) {
            return simpleAttribute;
        }

        Long fetchMode = (Long) super.getValueByPort(FETCH_MODE_PORT, input);
        SearchResultHitDTO hit =
                fetchMode == 0 ? search.getHits().get(FIRST_HIT) : search.getHits().get(search.getHits().size() - 1);
        SearchResultHitFieldDTO value = hit.getFieldValue(returnFieldName);
        if (value == null || value.getValues().isEmpty()) {
            return simpleAttribute;
        }
        AttributeUtils.processSimpleAttributeValue(simpleAttribute, value.getFirstValue());
        return simpleAttribute;
    }

    private SimpleAttribute.DataType getDataType(AbstractSimpleAttributeDef returnFieldDef) {
        if (returnFieldDef instanceof SimpleAttributeDef) {
            SimpleAttributeDef def = (SimpleAttributeDef) returnFieldDef;
            if (!StringUtils.isBlank(def.getEnumDataType())) {
                return SimpleAttribute.DataType.STRING;
            }
            if (!StringUtils.isBlank(def.getLinkDataType())) {
                return SimpleAttribute.DataType.LINK;
            }
            if(def.getLookupEntityCodeAttributeType() != null){
                return SimpleAttribute.DataType.valueOf(def.getLookupEntityCodeAttributeType().name());
            }
        }
        return SimpleAttribute.DataType.valueOf(returnFieldDef.getSimpleDataType().name());
    }
}
