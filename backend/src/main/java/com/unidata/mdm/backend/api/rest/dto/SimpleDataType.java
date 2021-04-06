package com.unidata.mdm.backend.api.rest.dto;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Supported simple data types.
 */
public enum SimpleDataType {

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
    NUMBER("Number"),
    /**
     * Boolean, as defined in XSD.
     */
    BOOLEAN("Boolean"),
    /**
     * BLOB as defined in XSD.
     */
    BLOB("Blob"),
    /**
     * CLOB as defined in XSD.
     */
    CLOB("Clob"),
    /**
     * CLOB as defined in XSD.
     */
    ANY("Any");
    /**
     * The value, really used for marshaling / unmarshaling.
     */
    private final String value;

    private SimpleDataType(String v) {
        value = v;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static SimpleDataType fromValue(String v) {
        for (SimpleDataType c: SimpleDataType.values()) {
            if (StringUtils.equals(v, c.value())) {
                return c;
            }
        }
        return null;
    }

}
