package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The Enum DQTypeDefinition.
 */
public enum DQTypeDefinition {

/** The validate. */
VALIDATE("VALIDATE"),

/** The enrich. */
ENRICH("ENRICH");

/** The value. */
private final String value;

/**
 * Instantiates a new DQ type definition.
 *
 * @param v the v
 */
DQTypeDefinition(String v) {
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
 * @param v the v
 * @return the DQ type definition
 */
@JsonCreator
public static DQTypeDefinition fromValue(String v) {
    for (DQTypeDefinition c : DQTypeDefinition.values()) {
        if (c.value.equals(v)) {
            return c;
        }
    }
    throw new IllegalArgumentException("Could not parse DQTypeDefinition id from string [" + v + "]");
}
}
