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

package org.unidata.mdm.data.serialization.protostuff;

/**
 * @author Mikhail Mikhailov
 * Schema fields. New enum fields must be added to the end ONLY! Gap is 20.
 */
public enum SchemaFields implements SchemaFieldValues {
    // 10 - Simple attributes.
    /**
     * Data record.
     */
    DATA_RECORD(DATA_RECORD_VAL, DATA_RECORD_TAG),
    /**
     * String simple attribute.
     */
    STRING_SIMPLE_ATTRIBUTE(STRING_SIMPLE_ATTRIBUTE_VAL, STRING_SIMPLE_ATTRIBUTE_TAG),
    /**
     * Integer simple attribute.
     */
    INTEGER_SIMPLE_ATTRIBUTE(INTEGER_SIMPLE_ATTRIBUTE_VAL, INTEGER_SIMPLE_ATTRIBUTE_TAG),
    /**
     * Number simple attribute.
     */
    NUMBER_SIMPLE_ATTRIBUTE(NUMBER_SIMPLE_ATTRIBUTE_VAL, NUMBER_SIMPLE_ATTRIBUTE_TAG),
    /**
     * Boolean simple attribute.
     */
    BOOLEAN_SIMPLE_ATTRIBUTE(BOOLEAN_SIMPLE_ATTRIBUTE_VAL, BOOLEAN_SIMPLE_ATTRIBUTE_TAG),
    /**
     * Blob simple attribute.
     */
    BLOB_SIMPLE_ATTRIBUTE(BLOB_SIMPLE_ATTRIBUTE_VAL, BLOB_SIMPLE_ATTRIBUTE_TAG),
    /**
     * Clob simple attribute.
     */
    CLOB_SIMPLE_ATTRIBUTE(CLOB_SIMPLE_ATTRIBUTE_VAL, CLOB_SIMPLE_ATTRIBUTE_TAG),
    /**
     * Date simple attribute.
     */
    DATE_SIMPLE_ATTRIBUTE(DATE_SIMPLE_ATTRIBUTE_VAL, DATE_SIMPLE_ATTRIBUTE_TAG),
    /**
     * Time simple attribute.
     */
    TIME_SIMPLE_ATTRIBUTE(TIME_SIMPLE_ATTRIBUTE_VAL, TIME_SIMPLE_ATTRIBUTE_TAG),
    /**
     * Timestamp simple attribute.
     */
    TIMESTAMP_SIMPLE_ATTRIBUTE(TIMESTAMP_SIMPLE_ATTRIBUTE_VAL, TIMESTAMP_SIMPLE_ATTRIBUTE_TAG),
    /**
     * Integer simple attribute.
     */
    MEASURED_SIMPLE_ATTRIBUTE(MEASURED_SIMPLE_ATTRIBUTE_VAL, MEASURED_SIMPLE_ATTRIBUTE_TAG),

    // 40 - Code attributes.
    /**
     * String code attribute.
     */
    STRING_CODE_ATTRIBUTE(STRING_CODE_ATTRIBUTE_VAL, STRING_CODE_ATTRIBUTE_TAG),
    /**
     * Integer code attribute.
     */
    INTEGER_CODE_ATTRIBUTE(INTEGER_CODE_ATTRIBUTE_VAL, INTEGER_CODE_ATTRIBUTE_TAG),

    // 60 -Array attributes
    /**
     * String array.
     */
    STRING_ARRAY_ATTRIBUTE(STRING_ARRAY_ATTRIBUTE_VAL, STRING_ARRAY_ATTRIBUTE_TAG),
    /**
     * Integer array.
     */
    INTEGER_ARRAY_ATTRIBUTE(INTEGER_ARRAY_ATTRIBUTE_VAL, INTEGER_ARRAY_ATTRIBUTE_TAG),
    /**
     * Number array.
     */
    NUMBER_ARRAY_ATTRIBUTE(NUMBER_ARRAY_ATTRIBUTE_VAL, NUMBER_ARRAY_ATTRIBUTE_TAG),
    /**
     * Date array.
     */
    DATE_ARRAY_ATTRIBUTE(DATE_ARRAY_ATTRIBUTE_VAL, DATE_ARRAY_ATTRIBUTE_TAG),
    /**
     * Time array.
     */
    TIME_ARRAY_ATTRIBUTE(TIME_ARRAY_ATTRIBUTE_VAL, TIME_ARRAY_ATTRIBUTE_TAG),
    /**
     * String array.
     */
    TIMESTAMP_ARRAY_ATTRIBUTE(TIMESTAMP_ARRAY_ATTRIBUTE_VAL, TIMESTAMP_ARRAY_ATTRIBUTE_TAG),

    // 80 - Complex
    /**
     * Complex.
     */
    COMPLEX_ATTRIBUTE(COMPLEX_ATTRIBUTE_VAL, COMPLEX_ATTRIBUTE_TAG),

    // 100 - Fields
    /**
     * Name field.
     */
    FIELD_NAME(FIELD_NAME_VAL, FIELD_NAME_TAG),

    // 120 - Values
    /**
     * Long value.
     */
    INTEGER_VALUE(INTEGER_VALUE_VAL, INTEGER_VALUE_TAG),
    /**
     * Double value.
     */
    NUMBER_VALUE(NUMBER_VALUE_VAL, NUMBER_VALUE_TAG),
    /**
     * Boolean value.
     */
    BOOLEAN_VALUE(BOOLEAN_VALUE_VAL, BOOLEAN_VALUE_TAG),
    /**
     * String value.
     */
    STRING_VALUE(STRING_VALUE_VAL, STRING_VALUE_TAG),
    /**
     * Blob value.
     */
    BLOB_VALUE(BLOB_VALUE_VAL, BLOB_VALUE_TAG),
    /**
     * Clob value.
     */
    CLOB_VALUE(CLOB_VALUE_VAL, CLOB_VALUE_TAG),
    /**
     * Date value.
     */
    DATE_VALUE(DATE_VALUE_VAL, DATE_VALUE_TAG),
    /**
     * Time value.
     */
    TIME_VALUE(TIME_VALUE_VAL, TIME_VALUE_TAG),
    /**
     * Timestamp value.
     */
    TIMESTAMP_VALUE(TIMESTAMP_VALUE_VAL, TIMESTAMP_VALUE_TAG),

    // 150 - Measured
    /**
     * Measured attribute value id.
     */
    MEASURED_VALUE_ID(MEASURED_VALUE_ID_VAL, MEASURED_VALUE_ID_TAG),
    /**
     * Measured attribute unit id.
     */
    MEASURED_UNIT_ID(MEASURED_UNIT_ID_VAL, MEASURED_UNIT_ID_TAG),

    // 170 - LOB value
    /**
     * Large value ID.
     */
    LARGE_VALUE_ID(LARGE_VALUE_ID_VAL, LARGE_VALUE_ID_TAG),
    /**
     * Large value file name.
     */
    LARGE_VALUE_FILENAME(LARGE_VALUE_FILENAME_VAL, LARGE_VALUE_FILENAME_TAG),
    /**
     * Large value size.
     */
    LARGE_VALUE_SIZE(LARGE_VALUE_SIZE_VAL, LARGE_VALUE_SIZE_TAG),
    /**
     * Large value mime type.
     */
    LARGE_VALUE_MIME_TYPE(LARGE_VALUE_MIME_TYPE_VAL, LARGE_VALUE_MIME_TYPE_TAG),

    // 190 - Array values
    /**
     * Long array value.
     */
    INTEGER_ARRAY_VALUE(INTEGER_ARRAY_VALUE_VAL, INTEGER_ARRAY_VALUE_TAG),
    /**
     * Double array value.
     */
    NUMBER_ARRAY_VALUE(NUMBER_ARRAY_VALUE_VAL, NUMBER_ARRAY_VALUE_TAG),
    /**
     * String array value.
     */
    STRING_ARRAY_VALUE(STRING_ARRAY_VALUE_VAL, STRING_ARRAY_VALUE_TAG),
    /**
     * Date array value.
     */
    DATE_ARRAY_VALUE(DATE_ARRAY_VALUE_VAL, DATE_ARRAY_VALUE_TAG),
    /**
     * Time array value.
     */
    TIME_ARRAY_VALUE(TIME_ARRAY_VALUE_VAL, TIME_ARRAY_VALUE_TAG),
    /**
     * Timestamp array value.
     */
    TIMESTAMP_ARRAY_VALUE(TIMESTAMP_ARRAY_VALUE_VAL, TIMESTAMP_ARRAY_VALUE_TAG);

    /**
     * Constructor.
     * @param field name of the field
     */
    private SchemaFields(int value, String field) {
        this.field = field;
        this.value = value;
    }
    /**
     * Name of the field.
     */
    private final String field;
    /**
     * The numeric
     */
    private final int value;
    /**
     * @return the field
     */
    public String getField() {
        return field;
    }
    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }
    /**
     * String to int method.
     * @param s the string
     * @return integer
     */
    public static int stringToInt(String s) {

        switch (s) {
        // 1 - 49. Record.
        case DATA_RECORD_TAG:
            return DATA_RECORD.getValue();
        // 50 - Simple attributes.
        case STRING_SIMPLE_ATTRIBUTE_TAG:
            return STRING_SIMPLE_ATTRIBUTE.getValue();
        case INTEGER_SIMPLE_ATTRIBUTE_TAG:
            return INTEGER_SIMPLE_ATTRIBUTE.getValue();
        case NUMBER_SIMPLE_ATTRIBUTE_TAG:
            return NUMBER_SIMPLE_ATTRIBUTE.getValue();
        case BOOLEAN_SIMPLE_ATTRIBUTE_TAG:
            return BOOLEAN_SIMPLE_ATTRIBUTE.getValue();
        case BLOB_SIMPLE_ATTRIBUTE_TAG:
            return BLOB_SIMPLE_ATTRIBUTE.getValue();
        case CLOB_SIMPLE_ATTRIBUTE_TAG:
            return CLOB_SIMPLE_ATTRIBUTE.getValue();
        case DATE_SIMPLE_ATTRIBUTE_TAG:
            return DATE_SIMPLE_ATTRIBUTE.getValue();
        case TIME_SIMPLE_ATTRIBUTE_TAG:
            return TIME_SIMPLE_ATTRIBUTE.getValue();
        case TIMESTAMP_SIMPLE_ATTRIBUTE_TAG:
            return TIMESTAMP_SIMPLE_ATTRIBUTE.getValue();
        case MEASURED_SIMPLE_ATTRIBUTE_TAG:
            return MEASURED_SIMPLE_ATTRIBUTE.getValue();

        // 100 - Code attributes.
        case STRING_CODE_ATTRIBUTE_TAG:
            return STRING_CODE_ATTRIBUTE.getValue();
        case INTEGER_CODE_ATTRIBUTE_TAG:
            return INTEGER_CODE_ATTRIBUTE.getValue();

        // 150 -Array attributes
        case STRING_ARRAY_ATTRIBUTE_TAG:
            return STRING_ARRAY_ATTRIBUTE.getValue();
        case INTEGER_ARRAY_ATTRIBUTE_TAG:
            return INTEGER_ARRAY_ATTRIBUTE.getValue();
        case NUMBER_ARRAY_ATTRIBUTE_TAG:
            return NUMBER_ARRAY_ATTRIBUTE.getValue();
        case DATE_ARRAY_ATTRIBUTE_TAG:
            return DATE_ARRAY_ATTRIBUTE.getValue();
        case TIME_ARRAY_ATTRIBUTE_TAG:
            return TIME_ARRAY_ATTRIBUTE.getValue();
        case TIMESTAMP_ARRAY_ATTRIBUTE_TAG:
            return TIMESTAMP_ARRAY_ATTRIBUTE.getValue();

        // 200 - Complex
        case COMPLEX_ATTRIBUTE_TAG:
            return COMPLEX_ATTRIBUTE.getValue();

        // 250 - Fields
        case FIELD_NAME_TAG:
            return FIELD_NAME.getValue();

        // 300 - Values
        case INTEGER_VALUE_TAG:
            return INTEGER_VALUE.getValue();
        case NUMBER_VALUE_TAG:
            return NUMBER_VALUE.getValue();
        case BOOLEAN_VALUE_TAG:
            return BOOLEAN_VALUE.getValue();
        case STRING_VALUE_TAG:
            return STRING_VALUE.getValue();
        case BLOB_VALUE_TAG:
            return BLOB_VALUE.getValue();
        case CLOB_VALUE_TAG:
            return CLOB_VALUE.getValue();
        case DATE_VALUE_TAG:
            return DATE_VALUE.getValue();
        case TIME_VALUE_TAG:
            return TIME_VALUE.getValue();
        case TIMESTAMP_VALUE_TAG:
            return TIMESTAMP_VALUE.getValue();

        // 350 - Measured
        case MEASURED_VALUE_ID_TAG:
            return MEASURED_VALUE_ID.getValue();
        case MEASURED_UNIT_ID_TAG:
            return MEASURED_UNIT_ID.getValue();

        // 400 - LOB value
        case LARGE_VALUE_ID_TAG:
            return LARGE_VALUE_ID.getValue();
        case LARGE_VALUE_FILENAME_TAG:
            return LARGE_VALUE_FILENAME.getValue();
        case LARGE_VALUE_SIZE_TAG:
            return LARGE_VALUE_SIZE.getValue();
        case LARGE_VALUE_MIME_TYPE_TAG:
            return LARGE_VALUE_MIME_TYPE.getValue();

        // 450 - Array values
        case INTEGER_ARRAY_VALUE_TAG:
            return INTEGER_ARRAY_VALUE.getValue();
        case NUMBER_ARRAY_VALUE_TAG:
            return NUMBER_ARRAY_VALUE.getValue();
        case STRING_ARRAY_VALUE_TAG:
            return STRING_ARRAY_VALUE.getValue();
        case DATE_ARRAY_VALUE_TAG:
            return DATE_ARRAY_VALUE.getValue();
        case TIME_ARRAY_VALUE_TAG:
            return TIME_ARRAY_VALUE.getValue();
        case TIMESTAMP_ARRAY_VALUE_TAG:
            return TIMESTAMP_ARRAY_VALUE.getValue();
        default:
            break;
        }

        return 0;
    }

    /**
     * Int to string method.
     * @param s the it value
     * @return string
     */
    public static String intToString(int i) {

        switch (i) {
        // 1 - 49 - Record.
        case DATA_RECORD_VAL:
            return DATA_RECORD.getField();
        // 50 - Simple attributes.
        case STRING_SIMPLE_ATTRIBUTE_VAL:
            return STRING_SIMPLE_ATTRIBUTE.getField();
        case INTEGER_SIMPLE_ATTRIBUTE_VAL:
            return INTEGER_SIMPLE_ATTRIBUTE.getField();
        case NUMBER_SIMPLE_ATTRIBUTE_VAL:
            return NUMBER_SIMPLE_ATTRIBUTE.getField();
        case BOOLEAN_SIMPLE_ATTRIBUTE_VAL:
            return BOOLEAN_SIMPLE_ATTRIBUTE.getField();
        case BLOB_SIMPLE_ATTRIBUTE_VAL:
            return BLOB_SIMPLE_ATTRIBUTE.getField();
        case CLOB_SIMPLE_ATTRIBUTE_VAL:
            return CLOB_SIMPLE_ATTRIBUTE.getField();
        case DATE_SIMPLE_ATTRIBUTE_VAL:
            return DATE_SIMPLE_ATTRIBUTE.getField();
        case TIME_SIMPLE_ATTRIBUTE_VAL:
            return TIME_SIMPLE_ATTRIBUTE.getField();
        case TIMESTAMP_SIMPLE_ATTRIBUTE_VAL:
            return TIMESTAMP_SIMPLE_ATTRIBUTE.getField();
        case MEASURED_SIMPLE_ATTRIBUTE_VAL:
            return MEASURED_SIMPLE_ATTRIBUTE.getField();

        // 100 - Code attributes.
        case STRING_CODE_ATTRIBUTE_VAL:
            return STRING_CODE_ATTRIBUTE.getField();
        case INTEGER_CODE_ATTRIBUTE_VAL:
            return INTEGER_CODE_ATTRIBUTE.getField();

        // 150 -Array attributes
        case STRING_ARRAY_ATTRIBUTE_VAL:
            return STRING_ARRAY_ATTRIBUTE.getField();
        case INTEGER_ARRAY_ATTRIBUTE_VAL:
            return INTEGER_ARRAY_ATTRIBUTE.getField();
        case NUMBER_ARRAY_ATTRIBUTE_VAL:
            return NUMBER_ARRAY_ATTRIBUTE.getField();
        case DATE_ARRAY_ATTRIBUTE_VAL:
            return DATE_ARRAY_ATTRIBUTE.getField();
        case TIME_ARRAY_ATTRIBUTE_VAL:
            return TIME_ARRAY_ATTRIBUTE.getField();
        case TIMESTAMP_ARRAY_ATTRIBUTE_VAL:
            return TIMESTAMP_ARRAY_ATTRIBUTE.getField();

        // 200 - Complex
        case COMPLEX_ATTRIBUTE_VAL:
            return COMPLEX_ATTRIBUTE.getField();

        // 250 - Fields
        case FIELD_NAME_VAL:
            return FIELD_NAME.getField();

        // 300 - Values
        case INTEGER_VALUE_VAL:
            return INTEGER_VALUE.getField();
        case NUMBER_VALUE_VAL:
            return NUMBER_VALUE.getField();
        case BOOLEAN_VALUE_VAL:
            return BOOLEAN_VALUE.getField();
        case STRING_VALUE_VAL:
            return STRING_VALUE.getField();
        case BLOB_VALUE_VAL:
            return BLOB_VALUE.getField();
        case CLOB_VALUE_VAL:
            return CLOB_VALUE.getField();
        case DATE_VALUE_VAL:
            return DATE_VALUE.getField();
        case TIME_VALUE_VAL:
            return TIME_VALUE.getField();
        case TIMESTAMP_VALUE_VAL:
            return TIMESTAMP_VALUE.getField();

        // 350 - Measured
        case MEASURED_VALUE_ID_VAL:
            return MEASURED_VALUE_ID.getField();
        case MEASURED_UNIT_ID_VAL:
            return MEASURED_UNIT_ID.getField();

        // 400 - LOB value
        case LARGE_VALUE_ID_VAL:
            return LARGE_VALUE_ID.getField();
        case LARGE_VALUE_FILENAME_VAL:
            return LARGE_VALUE_FILENAME.getField();
        case LARGE_VALUE_SIZE_VAL:
            return LARGE_VALUE_SIZE.getField();
        case LARGE_VALUE_MIME_TYPE_VAL:
            return LARGE_VALUE_MIME_TYPE.getField();

        // 450 - Array values
        case INTEGER_ARRAY_VALUE_VAL:
            return INTEGER_ARRAY_VALUE.getField();
        case NUMBER_ARRAY_VALUE_VAL:
            return NUMBER_ARRAY_VALUE.getField();
        case STRING_ARRAY_VALUE_VAL:
            return STRING_ARRAY_VALUE.getField();
        case DATE_ARRAY_VALUE_VAL:
            return DATE_ARRAY_VALUE.getField();
        case TIME_ARRAY_VALUE_VAL:
            return TIME_ARRAY_VALUE.getField();
        case TIMESTAMP_ARRAY_VALUE_VAL:
            return TIMESTAMP_ARRAY_VALUE.getField();
        default:
            break;
        }

        return null;
    }
}
