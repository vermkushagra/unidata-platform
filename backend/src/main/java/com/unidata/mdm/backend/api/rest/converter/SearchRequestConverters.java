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

package com.unidata.mdm.backend.api.rest.converter;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.unidata.mdm.meta.SimpleDataType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.api.rest.dto.search.SearchComplexRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchRequestDataType;
import com.unidata.mdm.backend.api.rest.dto.search.SearchRequestRO;
import com.unidata.mdm.backend.common.context.NestedSearchRequestContext;
import com.unidata.mdm.backend.common.context.NestedSearchRequestContext.NestedSearchType;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext.SearchRequestContextBuilder;
import com.unidata.mdm.backend.common.search.FacetName;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.ClassifierDataHeaderField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.service.search.util.SearchUtils;

/**
 * Converter for external search request.
 */
public class SearchRequestConverters {
    /**
     * @param request - external request
     * @return search request ctx
     */
    public static SearchRequestContext from(SearchComplexRO request) {

        if (request == null) {
            return null;
        }

        Map<String, List<FormFieldsGroup>> classifierFormFieldMap = new HashMap<>();

        if (SearchRequestDataType.CLASSIFIER.equals(request.getDataType())
                && CollectionUtils.isNotEmpty(request.getFormFields())) {

            Iterator<SearchFormFieldRO> i = request.getFormFields().iterator();
            while (i.hasNext()) {
                SearchFormFieldRO formField = i.next();
                if (!formField.getPath().startsWith(SearchUtils.DOLLAR)) {
                    String attrName = StringUtils.substringAfter(formField.getPath(), SearchUtils.DOT);

                    if (!attrName.startsWith(SearchUtils.DOLLAR)) {
                        String clsName = StringUtils.substringBefore(formField.getPath(), SearchUtils.DOT);
                        StringBuilder attrValuePath = new StringBuilder();

                        attrValuePath.append(clsName)
                                .append(SearchUtils.DOT)
                                .append(ClassifierDataHeaderField.FIELD_CLS_NESTED_ATTRS.getField())
                                .append(SearchUtils.DOT)
                                .append(ClassifierDataHeaderField.FIELD_CLS_ATTR_VALUE_PREFIX.getField());

                        switch (formField.getType()){
                            case STRING:
                            case BLOB:
                            case CLOB:
                            case ANY:
                                attrValuePath.append("string");
                                break;
                            case NUMBER:
                                attrValuePath.append("double");
                                break;
                            case INTEGER:
                                attrValuePath.append("long");
                                break;
                            case DATE:
                            case TIMESTAMP:
                                attrValuePath.append("date");
                                break;
                            case TIME:
                                attrValuePath.append("time");
                                break;
                            case BOOLEAN:
                                attrValuePath.append("boolean");
                                break;
                        }


                        String attrNamePath = clsName +
                                SearchUtils.DOT +
                                ClassifierDataHeaderField.FIELD_CLS_NESTED_ATTRS.getField() +
                                SearchUtils.DOT +
                                ClassifierDataHeaderField.FIELD_CLS_ATTR_NAME.getField();

                        FormField valueField = RestSearchDtoConverter.convertFormField(formField);
                        if (valueField.getFormType() == FormField.FormType.NEGATIVE) {
                            if (valueField.getSearchType() == FormField.SearchType.EXIST) {
                                classifierFormFieldMap.computeIfAbsent(formField.getPath(), k -> new ArrayList<>())
                                        .add(FormFieldsGroup.createAndGroup().addFormField(
                                                FormField.exceptStrictValue(SimpleDataType.STRING, attrNamePath, attrName)
                                        ));
                            } else if (valueField.getSearchType() == FormField.SearchType.EXACT) {
                                classifierFormFieldMap.computeIfAbsent(formField.getPath(), k -> new ArrayList<>())
                                        .add(FormFieldsGroup.createAndGroup().addFormField(
                                                FormField.exceptStrictValue(SimpleDataType.STRING, attrNamePath, attrName)
                                        ));
                                FormFieldsGroup nestedGroup = FormFieldsGroup.createAndGroup();
                                nestedGroup.addFormField(FormField.strictString(attrNamePath, attrName));
                                nestedGroup.addFormField(new FormField(valueField.getType(),
                                        attrValuePath.toString(),
                                        valueField.getFormType(),
                                        valueField.getInitialSingleValue(),
                                        valueField.getInitialValues(),
                                        valueField.getRange(),
                                        valueField.getSearchType()
                                ));
                                classifierFormFieldMap.computeIfAbsent(formField.getPath(), k -> new ArrayList<>())
                                        .add(nestedGroup);
                            }

                        } else {
                            FormFieldsGroup nestedGroup = FormFieldsGroup.createAndGroup();
                            nestedGroup.addFormField(FormField.strictString(attrNamePath, attrName));
                            nestedGroup.addFormField(new FormField(valueField.getType(),
                                    attrValuePath.toString(),
                                    valueField.getFormType(),
                                    valueField.getInitialSingleValue(),
                                    valueField.getInitialValues(),
                                    valueField.getRange(),
                                    valueField.getSearchType()
                            ));
                            classifierFormFieldMap.computeIfAbsent(formField.getPath(), k -> new ArrayList<>())
                                    .add(nestedGroup);
                        }
                        i.remove();
                    }
                }
            }
        }

        Map<String, List<FormFieldsGroup>> formFields = isEmpty(request.getFormFields())
                ?  null
                : RestSearchDtoConverter.convert(request.getFormFields(),
                Collections.singletonList(RecordHeaderField.FIELD_DQ_ERRORS.getField()));

        List<FacetName> facets = FacetName.fromValues(request.getFacets());

        // TODO remove this ugly DQ stuff by
        boolean dqErrorSearch = facets.contains(FacetName.FACET_NAME_ERRORS_ONLY) && !request.isFetchAll();
        SearchRequestContextBuilder builder = getBuilder(request)
                .searchFields(request.getSearchFields())
                .asOf(request.getAsOf())
                .facetsAsStrings(request.getFacets())
                .returnFields(request.getReturnFields() == null
                        ? null :
                        request.getReturnFields().stream().distinct().collect(Collectors.toList()))
                .addSorting(RestSearchDtoConverter.convertSortFields(request))
                .search(request.getQtype())
                .operator(request.getOperator())
                .count(request.getCount())
                .page(request.getPage() > 0 ? request.getPage() - 1 : request.getPage())
                .source(request.isSource())
                .totalCount(request.isTotalCount())
                .countOnly(request.isCountOnly())
                .fetchAll(request.isFetchAll())
                .onlyQuery(request.getDataType() == SearchRequestDataType.CLASSIFIER)
                .runExits(true)
                .searchAfter(request.getSearchAfter());

        if (dqErrorSearch) {
            List<FormFieldsGroup> dqForm = MapUtils.isNotEmpty(formFields)
                    ? formFields.get(RecordHeaderField.FIELD_DQ_ERRORS.getField())
                    : null;
            if (dqForm == null) {
                dqForm = Collections.singletonList(FormFieldsGroup.createAndGroup
                        (FormField.notEmpty(RecordHeaderField.FIELD_DQ_ERRORS.getField())));
            }

            builder.nestedSearch(
                    NestedSearchRequestContext.builder(SearchRequestContext.builder()
                            .nestedPath(RecordHeaderField.FIELD_DQ_ERRORS.getField())
                            .form(dqForm)
                            .count(1000)
                            .source(false)
                            .build())
                            .nestedQueryName(RecordHeaderField.FIELD_DQ_ERRORS.getField())
                            .nestedSearchType(NestedSearchType.NESTED_OBJECTS)
                            .build());
        }

        builder.form(Objects.isNull(formFields) ? null : formFields.get(StringUtils.EMPTY))
                .text(request.getText(), request.isSayt());

        if (!classifierFormFieldMap.isEmpty()) {
            for (Map.Entry<String, List<FormFieldsGroup>> entry : classifierFormFieldMap.entrySet()) {
                FormFieldsGroup orGroup = FormFieldsGroup.createOrGroup();
                for (FormFieldsGroup child : entry.getValue()) {
                    if (child.getFormFields().size() != 1 && CollectionUtils.isEmpty(child.getChildGroups())) {
                        orGroup.addChildGroup(child);
                    } else {
                        orGroup.addFormField(child.getFormFields().iterator().next());
                    }
                }

                builder.nestedSearch(
                        NestedSearchRequestContext.builder(
                                SearchRequestContext.builder()
                                        .nestedPath(
                                                StringUtils.substringBefore(entry.getKey(), SearchUtils.DOT)
                                                        + SearchUtils.DOT + ClassifierDataHeaderField.FIELD_CLS_NESTED_ATTRS.getField())
                                        .form(orGroup)
                                        .count(1000)
                                        .source(false)
                                        .build())
                                .nestedQueryName(entry.getKey())
                                .nestedSearchType(NestedSearchType.NESTED_OBJECTS)
                                .build());
            }
        }

        if(CollectionUtils.isNotEmpty(request.getFormGroups())){
            List<FormFieldsGroup> formFieldsGroups = request.getFormGroups().stream()
                    .map(RestSearchDtoConverter::convertFormFieldsGroup)
                    .collect(Collectors.toList());
            builder.form(formFieldsGroups);
        }


        return builder.build();
    }

    /**
     * @param source source request
     * @return builder for search
     */
    @Nonnull
    private static SearchRequestContextBuilder getBuilder(@Nonnull SearchRequestRO source) {
        SearchRequestDataType type = source.getDataType();
        switch (type) {
            case ETALON:
                return SearchRequestContext.forEtalon(EntitySearchType.ETALON, source.getEntity());
            case ETALON_DATA:
                return SearchRequestContext.forEtalon(EntitySearchType.ETALON_DATA, source.getEntity());
            case ETALON_REL:
                return SearchRequestContext.forEtalon(EntitySearchType.ETALON_RELATION, source.getEntity());
            case CLASSIFIER:
                return SearchRequestContext.builder(EntitySearchType.CLASSIFIER, source.getEntity());
            default:
                throw new RuntimeException();
        }
    }

}
