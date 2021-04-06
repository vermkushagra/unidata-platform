package com.unidata.mdm.backend.api.rest.dto.meta;

/**
 * The Enum CleanseFunctionType.
 *
 * @author Michael Yashin. Created on 20.05.2015.
 */
public enum CleanseFunctionType {

    /** The simple function. */
    BASIC_FUNCTION("BASIC_FUNCTION"),
    /** Custom cleanse function(loaded from JAR file) */
    CUSTOM_FUNCTION("CUSTOM_FUNCTION"),
    /** The composite function. */
    COMPOSITE_FUNCTION("COMPOSITE_FUNCTION");

    /** The value. */
    private final String value;

    /**
     * Instantiates a new cleanse function type.
     *
     * @param v
     *            the v
     */
    CleanseFunctionType(String v) {
        value = v;
    }

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return value;
    }

    /**
     * From value.
     *
     * @param v
     *            the v
     * @return the cleanse function type
     */
    public static CleanseFunctionType fromValue(String v) {
        for (CleanseFunctionType c : CleanseFunctionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
