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

/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldsGroupRO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchRequestRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchSortFieldRO;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.FormFieldsGroup.GroupType;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.meta.SimpleDataType;

import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_CREATED_AT;


/**
 * @author Mikhail Mikhailov
 *
 */
public class RestSearchDtoConverter {

    /**
     * Constructor.
     */
    private RestSearchDtoConverter() {
        super();
    }

    /**
     * Extracts and converts data from rest search request to a list of internal sort fields objects.
     * @param request REST request
     * @return internal
     */
    @Nonnull
    public static Collection<SortField> convertSortFields(@Nonnull SearchRequestRO request) {
        if (request.getSortFields() == null || request.getSortFields().isEmpty()) {
            return request.isDefaultSort() ? Collections.singleton(new SortField(FIELD_CREATED_AT.getField(),
                    SortField.SortOrder.DESC, false)) : Collections.emptyList();
        }

        Collection<SortField> sortFields = new ArrayList<>(request.getSortFields().size());
        for (SearchSortFieldRO sortFieldRO : request.getSortFields()) {
            boolean isString = SimpleDataType.fromValue(sortFieldRO.getType().value()) == SimpleDataType.STRING;
            SortField.SortOrder order = SortField.SortOrder.valueOf(sortFieldRO.getOrder());
            SortField sortField = new SortField(sortFieldRO.getField(), order, isString);
            sortFields.add(sortField);
        }
        return sortFields;
    }

    /**
     * Conversion method.
     * TODO remove facet prefix crap. Do it right with nested context from UI.
     * @param formField - collection of form field ro
     * @param fieldPrefixes facet prefixes. Fields with such a prefix will be put to a separate group
     * @return collection converted form field
     */
    @Nonnull
    public static Map<String, List<FormFieldsGroup>> convert(@Nonnull Collection<SearchFormFieldRO> formField, List<String> fieldPrefixes) {

        Map<String, List<FormFieldsGroup>> result = new HashMap<>();
        Multimap<String, FormField> groupedFields = HashMultimap.create();
        formField.stream()
                 .map(RestSearchDtoConverter::convertFormField)
                 .forEach(form -> groupedFields.put(form.getPath(), form));

        for (String path : groupedFields.keySet()) {

            Collection<FormField> formFields = groupedFields.get(path);
            if (formFields.size() > 1) {

                boolean faceted = false;
                for (String fp : fieldPrefixes) {
                    if (path.startsWith(fp)) {
                        result
                            .computeIfAbsent(fp, key -> new ArrayList<>())
                            .add(FormFieldsGroup.createOrGroup(formFields));
                        faceted = true;
                        break;
                    }
                }

                if (!faceted) {
                    result
                        .computeIfAbsent(StringUtils.EMPTY, key -> new ArrayList<>())
                        .add(FormFieldsGroup.createOrGroup(formFields));
                }
            } else {

                boolean faceted = false;
                for (String fp : fieldPrefixes) {
                    if (path.startsWith(fp)) {

                        List<FormFieldsGroup> currentGroups = result
                            .computeIfAbsent(fp, key -> new ArrayList<>());

                        FormFieldsGroup andGroup = currentGroups.stream()
                                .filter(group -> group.getGroupType() == GroupType.AND)
                                .findFirst()
                                .orElse(null);

                        if (andGroup == null) {
                            andGroup = FormFieldsGroup.createAndGroup();
                            currentGroups.add(andGroup);
                        }

                        formFields.forEach(andGroup::addFormField);
                        faceted = true;
                        break;
                    }
                }

                if (!faceted) {

                    List<FormFieldsGroup> currentGroups = result
                        .computeIfAbsent(StringUtils.EMPTY, key -> new ArrayList<>());

                    FormFieldsGroup andGroup = currentGroups.stream()
                            .filter(group -> group.getGroupType() == GroupType.AND)
                            .findFirst()
                            .orElse(null);

                    if (andGroup == null) {
                        andGroup = FormFieldsGroup.createAndGroup();
                        currentGroups.add(andGroup);
                    }

                    formFields.forEach(andGroup::addFormField);
                }
            }
        }

        return result;
    }

    @Nonnull
    public static FormFieldsGroup convertFormFieldsGroup(@Nonnull SearchFormFieldsGroupRO source) {
        FormFieldsGroup target = source.getGroupType() == SearchFormFieldsGroupRO.GroupType.AND
                ? FormFieldsGroup.createAndGroup()
                : FormFieldsGroup.createOrGroup();

        if(CollectionUtils.isNotEmpty(source.getFormFields())){
            source.getFormFields().forEach(formField -> target.addFormField(convertFormField(formField)));
        }

        if(CollectionUtils.isNotEmpty(source.getChildGroups())){
            source.getChildGroups().forEach(childGroup -> target.addChildGroup(convertFormFieldsGroup(childGroup)));
        }

        return target;
    }

    /**
     * @param source source
     * @return formField
     */
    @Nonnull
    public static FormField convertFormField(@Nonnull SearchFormFieldRO source) {
        FormField.FormType formType = source.isInverted() ? FormField.FormType.NEGATIVE : FormField.FormType.POSITIVE;
        String path = source.getPath();
        SimpleDataType type = SimpleDataType.fromValue(source.getType().value());
        if (source.getRange() != null) {
            Pair<Object, Object> range = source.getRange();
            return FormField.range(type, path, formType, range.getLeft(), range.getRight());
        } else {
            if(source.isFuzzy()){
                return FormField.fuzzyValue(type, path, source.getSingle());
            } else if (source.isLike()) {
                return FormField.likeString(path, source.getSingle());
            } else if (source.isStartWith()) {
                return FormField.startWithString(path, source.getSingle());
            } else if (source.isMorphological()) {
                return FormField.morphologicalValue(path, (String) source.getSingle());
            } else {
                return FormField.strictValue(type, path, formType, source.getSingle(), source.getSearchTypeRO() == null
                        ? FormField.SearchType.EXACT
                        : FormField.SearchType.valueOf(source.getSearchTypeRO().name()));
            }
        }
    }
}
