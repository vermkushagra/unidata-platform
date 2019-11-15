package org.unidata.mdm.search.type.form;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov
 *         Search form field.
 */
public class FormField {
    /**
     * Filtering type - positive or negative.
     */
    public enum FilteringType {
        NEGATIVE,
        POSITIVE
    }
    /**
     * Search type on this field.
     */
    public enum SearchType {
        DEFAULT,    // Match
        EXACT,      // Term
        FUZZY,
        LEVENSHTEIN,
        MORPHOLOGICAL,
        EXIST,
        START_WITH,
        LIKE,
        RANGE,
        NONE_MATCH
    }
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
    /**
     * Multiple values
     */
    @Nullable
    private final Collection<?> values;
    /**
     * Field path.
     */
    @Nonnull
    private final String path;
    /**
     * The field value type.
     */
    private FieldType type;
    /**
     * form type
     */
    @Nonnull
    private final FilteringType formType;
    /**
     * Search type
     */
    private final SearchType searchType;
    /**
     * Constructor for single.
     */
    private FormField(@Nonnull FieldType type, @Nonnull String path, @Nonnull FilteringType formType,
                      @Nullable Collection<?> values, @Nonnull SearchType searchType) {
        this.type = type;
        this.path = path;
        this.values = values;
        this.formType = formType;
        this.range = null;
        this.single = null;
        this.searchType = searchType;
    }
    /**
     * Constructor for single.
     */
    private FormField(@Nonnull FieldType type, @Nonnull String path, @Nonnull FilteringType formType,
            @Nullable Object single, @Nonnull SearchType searchType) {
        this.type = type;
        this.path = path;
        this.single = single;
        this.formType = formType;
        this.range = null;
        this.values = null;
        this.searchType = searchType;
    }
    /**
     * Constructor for ranges.
     */
    private FormField(@Nonnull FieldType type, @Nonnull String path, @Nonnull FilteringType formType,
            @Nullable Object leftBoundary, @Nullable Object rightBoundary, SearchType searchType) {
        this.type = type;
        this.path = path;
        this.formType = formType;
        this.range = new Range(leftBoundary, rightBoundary);
        this.single = null;
        this.values = null;
        this.searchType = searchType;
    }

    public FormField(@Nonnull FieldType type, @Nonnull String path, @Nonnull FilteringType formType,
            @Nullable Object single, @Nullable Collection<?> values, @Nullable Range range, SearchType searchType) {
        this.type = type;
        this.path = path;
        this.formType = formType;
        this.range = range;
        this.single = single;
        this.values = values;
        this.searchType = searchType;
    }

    public static FormField strictValue(@Nonnull FieldType type, @Nonnull String path,
            @Nonnull FilteringType formType, @Nullable Object single, SearchType searchType) {

        Object value = single;
        if (FieldType.STRING == type) {
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
    public static FormField strictValue(@Nonnull FieldType type, @Nonnull String path,
            @Nullable Object single) {
        return strictValue(type, path, FilteringType.POSITIVE, single, SearchType.EXACT);
    }

    /**
     * @param type   - type of data
     * @param path   -  field name
     * @param single - value
     * @return form field for strict value
     */
    public static FormField exceptStrictValue(@Nonnull FieldType type, @Nonnull String path, @Nullable Object single) {
        return strictValue(type, path, FilteringType.NEGATIVE, single, SearchType.EXACT);
    }

    /**
     * @param type   - type of data
     * @param path   -  field name
     * @param values - value list
     * @return form field for strict value
     */
    public static FormField strictValues(@Nonnull FieldType type, @Nonnull String path, @Nonnull Collection<?> values) {
        Collection<?> transformedValues = values;
        if (FieldType.STRING == type) {
            transformedValues = values.stream().map(o -> o != null ? o.toString() : null).collect(Collectors.toList());
        }

        return new FormField(type, path, FilteringType.POSITIVE, transformedValues, SearchType.EXACT);
    }

    /**
     * @param type     - type of data
     * @param path     -  field name
     * @param single   - value
     * @return form field for fuzzy search value
     */
    public static FormField fuzzyValue(@Nonnull FieldType type, @Nonnull String path, @Nullable Object single) {

        Object value = single;
        if (FieldType.STRING == type) {
            value = single == null || single.toString().isEmpty() ? null : single;
        }

        return new FormField(type, path, FilteringType.POSITIVE, value, SearchType.FUZZY);
    }

    public static FormField levenshteinValue(@Nonnull FieldType type, @Nonnull String path, @Nullable Object single) {

        Object value = single;
        if (FieldType.STRING == type) {
            value = single == null || single.toString().isEmpty() ? null : single;
        }

        return new FormField(type, path, FilteringType.POSITIVE, value, SearchType.LEVENSHTEIN);
    }

    public static FormField morphologicalValue(@Nonnull String path, @Nullable String single) {
        return new FormField(FieldType.STRING, path, FilteringType.POSITIVE, single, SearchType.MORPHOLOGICAL);
    }

    /**
     * @param type          - type of data
     * @param path          - field name
     * @param formType      -  type of field
     * @param leftBoundary  - left boundary value
     * @param rightBoundary - right boundary value
     * @return form field for range
     */
    public static FormField range(@Nonnull FieldType type, @Nonnull String path, @Nonnull FilteringType formType,
            @Nullable Object leftBoundary, @Nullable Object rightBoundary) {

        Object left = leftBoundary;
        Object right = rightBoundary;
        if (FieldType.STRING == type) {
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
    public static FormField range(@Nonnull FieldType type, @Nonnull String path, @Nullable Object leftBoundary,
            @Nullable Object rightBoundary) {
        return range(type, path, FilteringType.POSITIVE, leftBoundary, rightBoundary);
    }

    /**
     * @param type          - type of data
     * @param path          - field name
     * @param leftBoundary  - left boundary value
     * @param rightBoundary - right boundary value
     * @return form field for not a range
     */
    public static FormField notRange(@Nonnull FieldType type, @Nonnull String path, @Nullable Object leftBoundary,
            @Nullable Object rightBoundary) {
        return range(type, path, FilteringType.NEGATIVE, leftBoundary, rightBoundary);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for strict string
     */
    public static FormField strictString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        return new FormField(FieldType.STRING, path, FilteringType.POSITIVE, value, SearchType.EXACT);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for start with string
     */
    public static FormField startWithString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        return new FormField(FieldType.STRING, path, FilteringType.POSITIVE, value, SearchType.START_WITH);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for not start with string
     */
    public static FormField notStartWithString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        return new FormField(FieldType.STRING, path, FilteringType.NEGATIVE, value, SearchType.START_WITH);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for like string
     */
    public static FormField likeString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        value = value == null ? null : value.toString();
        return new FormField(FieldType.STRING, path, FilteringType.POSITIVE, value, SearchType.LIKE);
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
        return new FormField(FieldType.STRING, path, FilteringType.NEGATIVE, value, SearchType.LIKE);
    }

    public static FormField booleanValue(@Nonnull String path, @Nonnull Object value) {
        return strictValue(FieldType.BOOLEAN, path, value);
    }

    /**
     * @return form field for empty results
     */
    public static FormField noneMatch() {
        return new FormField(FieldType.ANY, null, FilteringType.POSITIVE, null, SearchType.NONE_MATCH);
    }


    /**
     * @param path - field name
     * @return form field for empty results
     */
    public static FormField empty(@Nonnull String path) {
        return new FormField(FieldType.ANY, path, FilteringType.NEGATIVE, null, SearchType.EXIST);
    }

    /**
     * @param path - field name
     * @return form field for not empty results
     */
    public static FormField notEmpty(@Nonnull String path) {
        return new FormField(FieldType.ANY, path, FilteringType.POSITIVE, null, SearchType.EXIST);
    }

    /** Create copy for form field with inverter FormType (negative ~ positive)
     * @param forCopy form field for copy
     * @return form field
     */
    public static FormField copyInvertedField(FormField forCopy) {
        return new FormField(forCopy.getType(),
                forCopy.getPath(),
                forCopy.getFormType() == FilteringType.POSITIVE ? FilteringType.NEGATIVE : FilteringType.POSITIVE,
                forCopy.getInitialSingleValue(),
                forCopy.getInitialValues(),
                forCopy.getRange(),
                forCopy.getSearchType());
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
    public FilteringType getFormType() {
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
     * @return the type of the FF.
     */
    public FieldType getType() {
        return type;
    }

    private Object convertToType(Object value) {
        if (value == null) {
            return null;
        }
        if (getPath().startsWith("$")) {
            return value;
        }
        if (getType() == FieldType.TIME) {
            return DateFormatUtils.ISO_TIME_FORMAT.format(value);
        } else if (getType() == FieldType.DATE) {
            return DateFormatUtils.ISO_DATE_FORMAT.format(value);
        } else if (getType() == FieldType.TIMESTAMP) {
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
        /*
        public static Range of(Object left, Object right) {
            return new Range(left, right);
        }
        */
    }
}
