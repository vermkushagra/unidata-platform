package com.unidata.mdm.backend.api.rest.dto;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Supported simple data types.
 */
public enum CodeDataType {
    /**
     * String, as defined in XSD.
     */
    STRING("String"),
    /**
     * Integer, as defined in XSD.
     */
    INTEGER("Integer");
    /**
     * The value, really used for marshaling / unmarshaling.
     */
    private final String value;
    /**
     * Constructor.
     * @param v the value
     */
    private CodeDataType(String v) {
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
    public static CodeDataType fromValue(String v) {
        for (CodeDataType c: CodeDataType.values()) {
            if (StringUtils.equals(v, c.value())) {
                return c;
            }
        }
        return null;
    }
}
