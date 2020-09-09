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

import static com.unidata.mdm.backend.common.search.FormField.exceptStrictValue;
import static com.unidata.mdm.backend.common.search.FormField.strictValue;
import static com.unidata.mdm.backend.common.search.FormField.FormType.POSITIVE;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_DELETED;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_ETALON_ID;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_FROM;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_PUBLISHED;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_TO;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.meta.SimpleDataType.BOOLEAN;
import static com.unidata.mdm.meta.SimpleDataType.STRING;
import static com.unidata.mdm.meta.SimpleDataType.TIMESTAMP;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.service.ServiceUtils;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Ruslan Trachuk
 */
public class CFAvoidDuplicates extends BasicCleanseFunctionAbstract {
    /**
     * IS_CODE_ATTR parameter.
     */
    private static final String IS_CODE_ATTR_PARAM = "IS_CODE_ATTR";
    /**
     * IS_SEARCH_BY_ALL_PERIODS PARAMETER.
     */
    private static final String IS_SEARCH_BY_ALL_PERIODS_PARAM = "IS_SEARCH_BY_ALL_PERIODS";
    /**
     * Search service.
     */
    private SearchService searchService;
    /**
     * Constructor.
     *
     * @throws Exception
     */
    public CFAvoidDuplicates() {
        super(CFAvoidDuplicates.class);
        this.searchService = ServiceUtils.getSearchService();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        RecordKeys keys = ctx.keys();
        String entityName = Objects.isNull(keys)
                ? ctx.getEntityName()
                : keys.getEntityName();

        // Collect values.
        List<FormField> searchFields = new ArrayList<>();
        Collection<String> portsToCheck = ctx.inputPorts();
        Collection<Attribute> selected = new ArrayList<>(5);
        for (String portName : portsToCheck) {

            if (StringUtils.equalsIgnoreCase(portName, IS_CODE_ATTR_PARAM)
             || StringUtils.equalsIgnoreCase(portName, IS_SEARCH_BY_ALL_PERIODS_PARAM)) {
                continue;
            }

            CleanseFunctionInputParam param = ctx.getInputParamByPortName(portName);
            if (Objects.nonNull(param) && !param.isEmpty() && param.isSingleton()) {

                Attribute attr = param.getSingleton();
                SimpleDataType type = extractDataType(attr);
                Object val = extractDataValue(attr);
                if (type == null || val == null || (type == STRING && isEmpty((String) val))) {
                    continue;
                }

                selected.add(attr);
                searchFields.add(strictValue(type, attr.getName(), val));
            }
        }

        boolean isDuplicate = false;
        if (!searchFields.isEmpty()) {

            FormFieldsGroup searchFieldsGroup = FormFieldsGroup.createAndGroup(searchFields);
            FormFieldsGroup specifiedAndGroup = specifiedAndGroupFields(ctx);
            SearchRequestContext sCtx = SearchRequestContext.forEtalonData(entityName)
                    .form(searchFieldsGroup, specifiedAndGroup)
                    .totalCount(true)
                    .countOnly(true)
                    .onlyQuery(true)
                    .operator(SearchRequestOperator.OP_AND)
                    .build();

            SearchResultDTO searchResult = searchService.search(sCtx);
            isDuplicate = searchResult.getTotalCount() != 0;
        }

        // Output
        ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, !isDuplicate));
        ctx.failedValidations().addAll(selected.stream()
                .map(attr -> new ImmutablePair<>(attr.toLocalPath(), attr))
                .collect(Collectors.toList()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }

    /**
     * Extracts SDT of the attribute
     * @param attr the attribute
     * @return SDT or null
     */
    private SimpleDataType extractDataType(Attribute attr) {

        if (Objects.nonNull(attr)) {
            if (attr.getAttributeType() == AttributeType.CODE) {
                return SimpleDataType.valueOf(((CodeAttribute<?>) attr).getDataType().name());
            } else if (attr.getAttributeType() == AttributeType.SIMPLE) {
                return SimpleDataType.valueOf(((SimpleAttribute<?>) attr).getDataType().name());
            }
        }

        return null;
    }

    /**
     * Extracts value of the attribute
     * @param attr the attribute
     * @return value or null
     */
    private Object extractDataValue(Attribute attr) {

        if (Objects.nonNull(attr)) {
            if (attr.getAttributeType() == AttributeType.CODE) {
                CodeAttribute<?> cast = (CodeAttribute<?>) attr;
                return cast.getDataType() == CodeDataType.STRING && cast.getValue() == null
                        ? StringUtils.EMPTY
                        : cast.getValue();
            } else if (attr.getAttributeType() == AttributeType.SIMPLE) {
                SimpleAttribute<?> cast = (SimpleAttribute<?>) attr;
                return cast.getDataType() == DataType.STRING && cast.getValue() == null
                        ? StringUtils.EMPTY
                        : cast.narrow(SimpleAttribute.NarrowType.ES);
            }
        }

        return null;
    }
    /**
     * Creates AND field group.
     * @param cfc the CF context
     * @return group
     */
    @SuppressWarnings("unchecked")
    private FormFieldsGroup specifiedAndGroupFields(CleanseFunctionContext cfc) {

        RecordKeys keys = cfc.keys();
        String etalonId = Objects.nonNull(keys) && Objects.nonNull(keys.getEtalonKey())
                ? keys.getEtalonKey().getId()
                : cfc.getEtalonKey();

        boolean isExistingRecord = StringUtils.isNotBlank(etalonId);

        Collection<FormField> specifiedFormFields = new ArrayList<>();
        specifiedFormFields.add(strictValue(BOOLEAN, FIELD_DELETED.getField(), Boolean.FALSE));
        specifiedFormFields.add(strictValue(BOOLEAN, FIELD_PUBLISHED.getField(), Boolean.TRUE));
        if (isExistingRecord) {
            //skip self
            specifiedFormFields.add(exceptStrictValue(STRING, FIELD_ETALON_ID.getField(), etalonId));
        }

        CleanseFunctionInputParam searchByAllPeriodsParam = cfc.getInputParamByPortName(IS_SEARCH_BY_ALL_PERIODS_PARAM);
        Boolean searchByAllPeriods = Objects.nonNull(searchByAllPeriodsParam) && !searchByAllPeriodsParam.isEmpty()
                ? ((SimpleAttribute<Boolean>) searchByAllPeriodsParam.getSingleton()).getValue()
                : false;

        if (BooleanUtils.isFalse(searchByAllPeriods)) {
            if (Objects.nonNull(cfc.getValidTo())) {
                specifiedFormFields.add(FormField.range(TIMESTAMP, FIELD_FROM.getField(), POSITIVE, null, cfc.getValidTo()));
            }

            if (Objects.nonNull(cfc.getValidFrom())) {
                specifiedFormFields.add(FormField.range(TIMESTAMP, FIELD_TO.getField(), POSITIVE, cfc.getValidFrom(), null));
            }
        }

        return FormFieldsGroup.createAndGroup(specifiedFormFields);
    }
}
