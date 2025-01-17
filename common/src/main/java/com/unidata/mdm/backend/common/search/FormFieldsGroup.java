package com.unidata.mdm.backend.common.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;

/**
 * Class responsible for grouping form fields by logical operation.
 * It is used for creation research requests.
 */
public class FormFieldsGroup {
    /**
     * Collection of form fields
     */
    @Nonnull
    private final Collection<FormField> formFields;

    private List<FormFieldsGroup> childGroups;

    /**
     * Type of group
     */
    @Nonnull
    private final GroupType groupType;

    /**
     * Constructor
     *
     * @param joinType
     * @param formFields
     */
    private FormFieldsGroup(@Nonnull GroupType joinType, @Nonnull Collection<FormField> formFields) {
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
    public static FormFieldsGroup createAndGroup(@Nonnull FormField... formField) {
        Collection<FormField> fieldList = Arrays.stream(formField)
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toList());
        return new FormFieldsGroup(GroupType.AND, fieldList);
    }

    /**
     * Create group form field which will be compose over logical 'AND'
     *
     * @param formFields - logical group of form fields for searching.
     * @return AND group
     */
    @Nonnull
    public static FormFieldsGroup createAndGroup(@Nonnull Collection<FormField> formFields) {
        Collection<FormField> fieldList = formFields.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return new FormFieldsGroup(GroupType.AND, fieldList);
    }

    /**
     * Create group form field which will be compose over logical 'AND'
     * @return AND group
     */
    @Nonnull
    public static FormFieldsGroup createAndGroup() {
        return new FormFieldsGroup(GroupType.AND, new ArrayList<>());
    }

    /**
     * Create group form fields which will be compose over logical 'OR'
     *
     * @param formFields - logical group of form fields for searching.
     */
    public static FormFieldsGroup createOrGroup(@Nonnull Collection<FormField> formFields) {
        Collection<FormField> fieldList = formFields.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return new FormFieldsGroup(GroupType.OR, fieldList);
    }

    /**
     *
     * @return empty OR group
     */
    public static FormFieldsGroup createOrGroup(){
        return new FormFieldsGroup(GroupType.OR, new ArrayList<>());
    }

    /**
     * @param formField - form field
     * @return - self
     */
    public FormFieldsGroup addFormField(FormField formField) {
        if (formField != null) {
            formFields.add(formField);
        }
        return this;
    }

    public FormFieldsGroup addChildGroup(FormFieldsGroup childGroup) {
        if(childGroups == null){
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
    public Collection<FormField> getFormFields() {
        return Collections.unmodifiableCollection(formFields);
    }

    @Nonnull
    public GroupType getGroupType() {
        return groupType;
    }

    public List<FormFieldsGroup> getChildGroups() {
        return childGroups;
    }

    public enum GroupType {
        OR, AND;
    }
}
