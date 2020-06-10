/**
 *
 */
package com.unidata.mdm.backend.common.search;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.meta.SimpleDataType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mikhail Mikhailov
 *         Search form field.
 */
public class FormField {

    /**
     * Value range.
     */
    @Nullable
    private final Range range;
    /**
     * Single value.
     */
    @Nullable
    private final Object single;

    @Nullable
    private final Collection<?> values;

    /**
     * Field path.
     */
    @Nonnull
    private final String path;
    /**
     * Data type.
     */
    @Nonnull
    private final SimpleDataType type;
    /**
     * form type
     */
    @Nonnull
    private final FormType formType;

    /**
     * Search type
     */
    private final SearchType searchType;

    /**
     * Constructor for single.
     */
    private FormField(@Nonnull SimpleDataType type, @Nonnull String path, @Nonnull FormType formType,
                      @Nullable Collection<?> values, @Nonnull SearchType searchType) {
        this.path = path;
        this.type = type;
        this.values = values;
        this.formType = formType;
        this.range = null;
        this.single = null;
        this.searchType = searchType;
    }

    /**
     * Constructor for single.
     */
    private FormField(@Nonnull SimpleDataType type, @Nonnull String path, @Nonnull FormType formType,
            @Nullable Object single, @Nonnull SearchType searchType) {
        this.path = path;
        this.type = type;
        this.single = single;
        this.formType = formType;
        this.range = null;
        this.values = null;
        this.searchType = searchType;
    }

    /**
     * Constructor for ranges.
     */
    private FormField(@Nonnull SimpleDataType type, @Nonnull String path, @Nonnull FormType formType,
            @Nullable Object leftBoundary, @Nullable Object rightBoundary, SearchType searchType) {
        this.path = path;
        this.type = type;
        this.formType = formType;
        this.range = new Range(leftBoundary, rightBoundary);
        this.single = null;
        this.values = null;
        this.searchType = searchType;
    }

    public FormField(@Nonnull SimpleDataType type, @Nonnull String path, @Nonnull FormType formType,
                      @Nullable Object single, @Nullable Collection<?> values, @Nullable Range range, SearchType searchType) {
        this.path = path;
        this.type = type;
        this.formType = formType;
        this.range = range;
        this.single = single;
        this.values = values;
        this.searchType = searchType;
    }

    public static FormField strictValue(@Nonnull SimpleDataType type, @Nonnull String path,
                                        @Nonnull FormType formType, @Nullable Object single, SearchType searchType) {
        Object value = single;
        if (SimpleDataType.STRING == type) {
            value = single == null || single.toString().isEmpty() ? null : single;
        }
        return new FormField(type, path, formType, value, searchType);
    }


    /**
     * @param type     - type of data
     * @param path     -  field name
     * @param single   - value
     * @return form field for strict value
     */
    public static FormField strictValue(@Nonnull SimpleDataType type, @Nonnull String path,
            @Nullable Object single) {
        return strictValue(type, path, FormType.POSITIVE, single, SearchType.EXACT);
    }

    /**
     * @param type   - type of data
     * @param path   -  field name
     * @param single - value
     * @return form field for strict value
     */
    public static FormField exceptStrictValue(@Nonnull SimpleDataType type, @Nonnull String path,
                                              @Nullable Object single) {
        return strictValue(type, path, FormType.NEGATIVE, single, SearchType.EXACT);
    }

    /**
     * @param type   - type of data
     * @param path   -  field name
     * @param values - value list
     * @return form field for strict value
     */
    public static FormField strictValues(@Nonnull SimpleDataType type, @Nonnull String path,
                                              @Nonnull Collection<?> values) {
        Collection<?> transformedValues = values;
        if (SimpleDataType.STRING == type) {
            transformedValues = values.stream().map(o -> o != null ? o.toString() : null).collect(Collectors.toList());
        }
        return new FormField(type, path, FormType.POSITIVE, transformedValues, SearchType.EXACT);
    }

    /**
     * @param type   - type of data
     * @param path   -  field name
     * @param values - value list
     * @return form field for strict value
     */
    public static FormField exceptStrictValues(@Nonnull SimpleDataType type, @Nonnull String path,
                                              @Nonnull List<Object> values) {
        Collection<?> transformedValues = values;
        if (SimpleDataType.STRING == type) {
            transformedValues = values.stream().map(o -> o != null ? o.toString() : null).collect(Collectors.toList());
        }
        return new FormField(type, path, FormType.NEGATIVE, transformedValues, SearchType.EXACT);
    }


    /**
     * @param type     - type of data
     * @param path     -  field name
     * @param single   - value
     * @return form field for fuzzy search value
     */
    public static FormField fuzzyValue(@Nonnull SimpleDataType type, @Nonnull String path, @Nullable Object single) {
        Object value = single;
        if (SimpleDataType.STRING == type) {
            value = single == null || single.toString().isEmpty() ? null : single;
        }
        return new FormField(type, path, FormType.POSITIVE, value, SearchType.FUZZY);
    }

    public static FormField morphologicalValue(@Nonnull String path, @Nullable String single) {
        return new FormField(SimpleDataType.STRING, path, FormType.POSITIVE, single, SearchType.MORPHOLOGICAL);
    }

    /**
     * @param type          - type of data
     * @param path          - field name
     * @param formType      -  type of field
     * @param leftBoundary  - left boundary value
     * @param rightBoundary - right boundary value
     * @return form field for range
     */
    public static FormField range(@Nonnull SimpleDataType type, @Nonnull String path, @Nonnull FormType formType,
            @Nullable Object leftBoundary, @Nullable Object rightBoundary) {
        Object left = leftBoundary;
        Object right = rightBoundary;
        if (SimpleDataType.STRING == type) {
            left = leftBoundary == null || leftBoundary.toString().isEmpty() ? null : leftBoundary;
            right = rightBoundary == null || rightBoundary.toString().isEmpty() ? null : rightBoundary;
        }
        return new FormField(type, path, formType, left, right,  SearchType.RANGE);
    }

    /**
     * @param type          - type of data
     * @param path          - field name
     * @param leftBoundary  - left boundary value
     * @param rightBoundary - right boundary value
     * @return form field for range
     */
    public static FormField range(@Nonnull SimpleDataType type, @Nonnull String path, @Nullable Object leftBoundary,
            @Nullable Object rightBoundary) {
        return range(type, path, FormType.POSITIVE, leftBoundary, rightBoundary);
    }

    /**
     * @param type          - type of data
     * @param path          - field name
     * @param leftBoundary  - left boundary value
     * @param rightBoundary - right boundary value
     * @return form field for not a range
     */
    public static FormField notRange(@Nonnull SimpleDataType type, @Nonnull String path, @Nullable Object leftBoundary,
            @Nullable Object rightBoundary) {
        return range(type, path, FormType.NEGATIVE, leftBoundary, rightBoundary);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for strict string
     */
    public static FormField strictString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        return new FormField(SimpleDataType.STRING, path, FormType.POSITIVE, value, SearchType.EXACT);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for start with string
     */
    public static FormField startWithString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        return new FormField(SimpleDataType.STRING, path, FormType.POSITIVE, value, SearchType.START_WITH);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for not start with string
     */
    public static FormField notStartWithString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        return new FormField(SimpleDataType.STRING, path, FormType.NEGATIVE, value, SearchType.START_WITH);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for like string
     */
    public static FormField likeString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        value = value == null ? null : value.toString();
        return new FormField(SimpleDataType.STRING, path, FormType.POSITIVE, value, SearchType.LIKE);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for not like string
     */
    public static FormField notLikeString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        //remove all ? and *
        value = value == null ? null : "*" + value.toString().replace("*", "\\*").replace("?", "\\?") + "*";
        return new FormField(SimpleDataType.STRING, path, FormType.NEGATIVE, value, SearchType.LIKE);
    }

    public static FormField booleanValue(@Nonnull String path, @Nonnull Object value) {
        return strictValue(SimpleDataType.BOOLEAN, path, value);
    }

    /**
     * @return form field for empty results
     */
    public static FormField noneMatch() {
        return new FormField(SimpleDataType.ANY, null, FormType.POSITIVE, null, SearchType.NONE_MATCH);
    }


    /**
     * @param path - field name
     * @return form field for empty results
     */
    public static FormField empty(@Nonnull String path) {
        return new FormField(SimpleDataType.ANY, path, FormType.NEGATIVE, null, SearchType.EXIST);
    }

    /**
     * @param path - field name
     * @return form field for not empty results
     */
    public static FormField notEmpty(@Nonnull String path) {
        return new FormField(SimpleDataType.ANY, path, FormType.POSITIVE, null, SearchType.EXIST);
    }
    /**
     * @param value - simple attribute
     * @return formField
     */
    public static FormField strict(@Nonnull SimpleAttribute<?> value) {
        Object searchValue = value.narrow(SimpleAttribute.NarrowType.ES);
        String searchField = value.getName();
        SimpleDataType simpleDataType = convert(value.getDataType());
        return strictValue(simpleDataType, searchField, searchValue);
    }

    /**
     * @param value - simple attribute
     * @return formField
     */
    public static FormField fuzzy(@Nonnull SimpleAttribute<?> value) {
        Object searchValue = value.narrow(SimpleAttribute.NarrowType.ES);
        String searchField = value.getName();
        SimpleDataType simpleDataType = convert(value.getDataType());
        return fuzzyValue(simpleDataType, searchField, searchValue);
    }

    /** Create copy for form field with inverter FormType (negative ~ positive)
     * @param forCopy form field for copy
     * @return form field
     */
    public static FormField copyInvertedField(FormField forCopy) {
        return new FormField(forCopy.getType(),
                forCopy.getPath(),
                forCopy.getFormType() == FormType.POSITIVE ? FormType.NEGATIVE : FormType.POSITIVE,
                forCopy.getInitialSingleValue(),
                forCopy.getInitialValues(),
                forCopy.getRange(),
                forCopy.getSearchType());
    }

    private static SimpleDataType convert(SimpleAttribute.DataType dataType) {
        if (dataType == null) {
            return SimpleDataType.ANY;
        }
        if (dataType == SimpleAttribute.DataType.ENUM || dataType == SimpleAttribute.DataType.LINK) {
            return SimpleDataType.STRING;
        } else {
            return SimpleDataType.valueOf(dataType.name());
        }
    }


    /**
     * Tells whether this form denotes null value.
     *
     * @return true if so, false otherwise
     */
    public boolean isNull() {
        return single == null && range == null && values == null;
    }

    /**
     * @return the range
     */
    public Range getRange() {
        return range;
    }

    /**
     * @return the single
     */
    public Object getSingleValue() {
        return convertToType(single);
    }

    /**
     * @return value without transformation fot ES
     */
    public Object getInitialSingleValue() {
        return single;
    }

    /**
     * @return type of form which impact query type.
     */
    @Nonnull
    public FormType getFormType() {
        return formType;
    }

    /**
     * @return the path
     */
    @Nonnull
    public String getPath() {
        return path;
    }

    /**
     * @return the type
     */
    @Nonnull
    public SimpleDataType getType() {
        return type;
    }

    private Object convertToType(Object value) {
        if (value == null) {
            return null;
        }
        if (getPath().startsWith("$")) {
            return value;
        }
        if (getType() == SimpleDataType.TIME) {
            return DateFormatUtils.ISO_TIME_FORMAT.format(value);
        } else if (getType() == SimpleDataType.DATE) {
            return DateFormatUtils.ISO_DATE_FORMAT.format(value);
        } else if (getType() == SimpleDataType.TIMESTAMP) {
            return DateFormatUtils.ISO_DATETIME_FORMAT.format(value);
        }
        return value;
    }

    private List<Object> convertToType(Collection<?> values) {
        if (values == null) {
            return Collections.emptyList();
        }

        return values.stream()
                .map(this::convertToType)
                .collect(Collectors.toList());
    }

    public SearchType getSearchType() {
        return searchType;
    }


    /**
     * List values.
     */
    public Collection<?> getInitialValues() {
        return values;
    }

    /**
     * List values.
     */
    public List<Object> getValues() {
        return convertToType(values);
    }

    public boolean isMultiValues(){
        return values != null;
    }

    public enum FormType {
        NEGATIVE, POSITIVE
    }

    public enum SearchType{
        DEFAULT,
        EXACT,
        FUZZY,
        MORPHOLOGICAL,
        EXIST,
        START_WITH,
        LIKE,
        RANGE,
        NONE_MATCH
    }

    //todo in future class like google guava range for using gt/lt ES abilities.
    public class Range {
        private final Object leftBoundary;
        private final Object rightBoundary;

        public Range(Object leftBoundary, Object rightBoundary) {
            this.leftBoundary = leftBoundary;
            this.rightBoundary = rightBoundary;
        }

        public Object getLeftBoundary() {
            return convertToType(leftBoundary);
        }

        public Object getRightBoundary() {
            return convertToType(rightBoundary);
        }
    }

}
