/**
 *
 */
package com.unidata.mdm.backend.common.search;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.meta.SimpleDataType;

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
     * is Strict;
     */
    private final boolean isStrict;
    /**
     * Like
     */
    private final boolean like;
    /**
     * Search type
     */
    private final SearchType searchType;
    /**
     * Constructor.
     */
    private FormField(@Nonnull SimpleDataType type, @Nonnull String path, @Nonnull FormType formType,
            @Nullable Object single, boolean strict, boolean like) {
        this.path = path;
        this.type = type;
        this.single = single;
        this.formType = formType;
        this.range = null;
        this.isStrict = strict;
        this.like = like;
        this.searchType = SearchType.DEFAULT;
    }

    /**
     * Constructor.
     */
    private FormField(@Nonnull SimpleDataType type, @Nonnull String path,@Nullable Object value, @Nonnull SearchType searchType) {
        this.path = path;
        this.type = type;
        this.single = value;
        this.formType = FormType.POSITIVE;
        this.range = null;
        this.isStrict = searchType != SearchType.FUZZY && searchType != SearchType.MORPHOLOGICAL;
        this.like = false;
        this.searchType = searchType;
    }

    /**
     * Constructor for ranges.
     */
    private FormField(@Nonnull SimpleDataType type, @Nonnull String path, @Nonnull FormType formType,
            @Nullable Object leftBoundary, @Nullable Object rightBoundary, boolean strict) {
        this.path = path;
        this.type = type;
        this.formType = formType;
        this.range = new Range(leftBoundary, rightBoundary);
        this.single = null;
        //can be use for using gt/lt
        this.isStrict = strict;
        this.like = false;
        this.searchType = SearchType.DEFAULT;
    }

    /**
     * @param type     - type of data
     * @param path     -  field name
     * @param formType - type of field
     * @param single   - value
     * @return form field for strict value
     */
    public static FormField strictValue(@Nonnull SimpleDataType type, @Nonnull String path,
            @Nonnull FormType formType, @Nullable Object single) {
        Object value = single;
        if (SimpleDataType.STRING == type) {
            value = single == null || single.toString().isEmpty() ? null : single;
        }
        return new FormField(type, path, formType, value, true, false);
    }

    /**
     * @param type     - type of data
     * @param path     -  field name
     * @param single   - value
     * @return form field for strict value
     */
    public static FormField strictValue(@Nonnull SimpleDataType type, @Nonnull String path,
            @Nullable Object single) {
        return strictValue(type, path, FormType.POSITIVE, single);
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
        return new FormField(type, path,value, SearchType.FUZZY);
    }

    public static FormField morphologicalValue(@Nonnull String path, @Nullable String single) {
        return new FormField(SimpleDataType.STRING, path, single, SearchType.MORPHOLOGICAL);
    }

    /**
     * @param type   - type of data
     * @param path   -  field name
     * @param single - value
     * @return form field for strict value
     */
    public static FormField exceptStrictValue(@Nonnull SimpleDataType type, @Nonnull String path,
            @Nullable Object single) {
        return strictValue(type, path, FormType.NEGATIVE, single);
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
        return new FormField(type, path, formType, left, right, true);
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
        return new FormField(SimpleDataType.STRING, path, FormType.POSITIVE, value, true, false);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for start with string
     */
    public static FormField startWithString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        return new FormField(SimpleDataType.STRING, path, FormType.POSITIVE, value, false, false);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for not start with string
     */
    public static FormField notStartWithString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        return new FormField(SimpleDataType.STRING, path, FormType.NEGATIVE, value, false, false);
    }

    /**
     * @param path   - field name
     * @param single - value
     * @return form field for like string
     */
    public static FormField likeString(@Nonnull String path, @Nullable Object single) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        //remove all ? and *
        value = value == null ? null : "*" + value.toString().replace("*", "\\*").replace("?", "\\?") + "*";
        return new FormField(SimpleDataType.STRING, path, FormType.POSITIVE, value, false, true);
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
        return new FormField(SimpleDataType.STRING, path, FormType.NEGATIVE, value, false, true);
    }

    /**
     * @param path - field name
     * @return form field for empty results
     */
    public static FormField empty(@Nonnull String path) {
        return new FormField(SimpleDataType.ANY, path, FormType.POSITIVE, null, true, false);
    }

    /**
     * @param path - field name
     * @return form field for not empty results
     */
    public static FormField notEmpty(@Nonnull String path) {
        return new FormField(SimpleDataType.ANY, path, FormType.NEGATIVE, null, true, false);
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


    @Deprecated
    // use strictValue
    public static FormField singleValue(@Nonnull SimpleDataType type, @Nonnull String path, @Nonnull FormType formType,
            @Nullable Object single) {
        return strictValue(type, path, formType, single);
    }

    @Deprecated
    //use strictString/startWithString/likeString
    public static FormField stringValue(@Nonnull String path, @Nonnull FormType formType, @Nullable Object single,
            boolean strict) {
        Object value = single == null || single.toString().isEmpty() ? null : single;
        return new FormField(SimpleDataType.STRING, path, formType, value, strict, false);
    }

    /**
     * Tells whether this form denotes single value or not.
     *
     * @return true if so, false otherwise
     */
    public boolean isSingle() {
        return single != null && range == null;
    }

    /**
     * Tells whether this form denotes range value or not.
     *
     * @return true if so, false otherwise
     */
    public boolean isRange() {
        return single == null && range != null;
    }

    /**
     * Tells whether this form denotes null value.
     *
     * @return true if so, false otherwise
     */
    public boolean isNull() {
        return single == null && range == null;
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

    /**
     * Depends on this field search will be strict or not
     *
     * @return
     */
    public boolean isStrict() {
        return isStrict;
    }

    /**
     * @return true if it single string value in like
     */
    public boolean isLike() {
        return like;
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


    public SearchType getSearchType() {
        return searchType;
    }

    public enum FormType {
        NEGATIVE, POSITIVE
    }

    public enum SearchType{
        DEFAULT,
        STRICT,
        FUZZY,
        MORPHOLOGICAL
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
