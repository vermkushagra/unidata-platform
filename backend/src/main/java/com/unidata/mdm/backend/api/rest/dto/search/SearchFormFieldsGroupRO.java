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

package com.unidata.mdm.backend.api.rest.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Kopin
 * REST search form fields group.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchFormFieldsGroupRO {
    /**
     * Collection of form fields
     */
    @Nonnull
    private final Collection<SearchFormFieldRO> formFields;

    private List<SearchFormFieldsGroupRO> childGroups;

    /**
     * Type of group
     */
    @Nonnull
    private final SearchFormFieldsGroupRO.GroupType groupType;

    /**
     * Constructor
     *
     * @param joinType
     * @param formFields
     */
    private SearchFormFieldsGroupRO(@Nonnull SearchFormFieldsGroupRO.GroupType joinType, @Nonnull Collection<SearchFormFieldRO> formFields) {
        this.groupType = joinType;
        this.formFields = formFields;
    }

    /**
     * Create group form field which will be compose over logical 'AND'
     *
     * @param formField - logical group of form fields for searching.
     * @return AND group
     */
    @Nonnull
    public static SearchFormFieldsGroupRO createAndGroup(@Nonnull SearchFormFieldRO... formField) {
        Collection<SearchFormFieldRO> fieldList = Arrays.stream(formField)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new SearchFormFieldsGroupRO(SearchFormFieldsGroupRO.GroupType.AND, fieldList);
    }

    /**
     * Create group form field which will be compose over logical 'AND'
     *
     * @param formFields - logical group of form fields for searching.
     * @return AND group
     */
    @Nonnull
    public static SearchFormFieldsGroupRO createAndGroup(@Nonnull Collection<SearchFormFieldRO> formFields) {
        Collection<SearchFormFieldRO> fieldList = formFields.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return new SearchFormFieldsGroupRO(SearchFormFieldsGroupRO.GroupType.AND, fieldList);
    }

    /**
     * Create group form field which will be compose over logical 'AND'
     *
     * @return AND group
     */
    @Nonnull
    public static SearchFormFieldsGroupRO createAndGroup() {
        return new SearchFormFieldsGroupRO(SearchFormFieldsGroupRO.GroupType.AND, new ArrayList<>());
    }

    /**
     * Create group form fields which will be compose over logical 'OR'
     *
     * @param formFields - logical group of form fields for searching.
     */
    public static SearchFormFieldsGroupRO createOrGroup(@Nonnull Collection<SearchFormFieldRO> formFields) {
        Collection<SearchFormFieldRO> fieldList = formFields.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return new SearchFormFieldsGroupRO(SearchFormFieldsGroupRO.GroupType.OR, fieldList);
    }

    /**
     * @return empty OR group
     */
    public static SearchFormFieldsGroupRO createOrGroup() {
        return new SearchFormFieldsGroupRO(SearchFormFieldsGroupRO.GroupType.OR, new ArrayList<>());
    }

    /**
     * @param formField - form field
     * @return - self
     */
    public SearchFormFieldsGroupRO addFormField(SearchFormFieldRO formField) {
        if (formField != null) {
            formFields.add(formField);
        }
        return this;
    }

    public SearchFormFieldsGroupRO addChildGroup(SearchFormFieldsGroupRO childGroup) {
        if (childGroups == null) {
            childGroups = new ArrayList<>();
        }
        childGroups.add(childGroup);
        return this;
    }

    /**
     * @return true if group is empty
     */
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(formFields) && CollectionUtils.isEmpty(childGroups);
    }

    @Nonnull
    public Collection<SearchFormFieldRO> getFormFields() {
        return Collections.unmodifiableCollection(formFields);
    }

    @Nonnull
    public SearchFormFieldsGroupRO.GroupType getGroupType() {
        return groupType;
    }

    public List<SearchFormFieldsGroupRO> getChildGroups() {
        return childGroups;
    }

    public enum GroupType {
        OR, AND;
    }
}
