package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The Enum RelType.
 */
public enum RelType {

    /** The references. */
    REFERENCES("References"),
    /** The contains. */
    CONTAINS("Contains"),
    /** The many to many. */
    MANY_TO_MANY("ManyToMany");

    /** The value. */
    private final String value;

    /**
     * Instantiates a new rel type.
     *
     * @param v
     *            the v
     */
    RelType(String v) {
        value = v;
    }

    /**
     * Value.
     *
     * @return the string
     */
    @JsonValue
    public String value() {
        return value;
    }

    /**
     * From value.
     *
     * @param v
     *            the v
     * @return the rel type
     */
    @JsonCreator
    public static RelType fromValue(String v) {
        for (RelType c : RelType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Could not parse RelType id from string [" + v + "]");
    }
}
