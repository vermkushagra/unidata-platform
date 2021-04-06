/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchRequestRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchSortFieldRO;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.meta.SimpleDataType;

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
        if (request.getSortFields() == null || request.getSortFields().isEmpty()) return Collections.emptyList();
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
     * Conversion method
     * @param formField - collection of form field ro
     * @return collection converted form field
     */
    @Nonnull
    public static List<FormFieldsGroup> convert(@Nonnull Collection<SearchFormFieldRO> formField) {
        Multimap<String, FormField> groupedFields = HashMultimap.create();

        formField.stream()
                 .map(RestSearchDtoConverter::convertFormField)
                 .forEach(form -> groupedFields.put(form.getPath(), form));

        List<FormFieldsGroup> result = new ArrayList<>();
        List<FormField> andFormFields = new ArrayList<>();
        for (String path : groupedFields.keySet()) {
            Collection<FormField> formFields = groupedFields.get(path);
            if (formFields.size() > 1) {
                result.add(FormFieldsGroup.createOrGroup(formFields));
            } else {
                andFormFields.addAll(formFields);
            }
        }
        if (!andFormFields.isEmpty()) {
            result.add(FormFieldsGroup.createAndGroup(andFormFields));
        }

        return result;
    }

    /**
     * @param source source
     * @return formField
     */
    @Nonnull
    private static FormField convertFormField(@Nonnull SearchFormFieldRO source) {
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
                return FormField.strictValue(type, path, formType, source.getSingle());
            }
        }
    }
}
