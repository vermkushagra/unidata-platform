package com.unidata.mdm.backend.api.rest.dto;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Supported simple data types.
 */
public enum ArrayDataType {
    /**
     * Date, as defined in XSD.
     */
    DATE("Date"),
    /**
     * Time, as defined in XSD.
     */
    TIME("Time"),
    /**
     * Date-time, as defined in XSD.
     */
    TIMESTAMP("Timestamp"),
    /**
     * String, as defined in XSD.
     */
    STRING("String"),
    /**
     * Integer, as defined in XSD.
     */
    INTEGER("Integer"),
    /**
     * FP number, as defined in XSD.
     */
    NUMBER("Number");
    /**
     * The value, really used for marshaling / unmarshaling.
     */
    private final String value;
    /**
     * Constructor.
     * @param v the value
     */
    private ArrayDataType(String v) {
        value = v;
    }
    /**
     * @return the value
     */
    @JsonValue
    public String value() {
        return value;
    }
    /**
     * From value creator.
     * @param v the value
     * @return enum instance
     */
    @JsonCreator
    public static ArrayDataType fromValue(String v) {
        for (ArrayDataType c: ArrayDataType.values()) {
            if (StringUtils.equals(v, c.value())) {
                return c;
            }
        }
        return null;
    }
}
