package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The Enum PhaseDefinition.
 */
public enum PhaseDefinition {

    /** The before upsert. */
    BEFORE_UPSERT("BEFORE_UPSERT"),

    /** The after upsert. */
    AFTER_UPSERT("AFTER_UPSERT"),

    /** The after merge. */
    AFTER_MERGE("AFTER_MERGE");
    private final String value;

    PhaseDefinition(String v) {
        value = v;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static PhaseDefinition fromValue(String v) {
        for (PhaseDefinition c : PhaseDefinition.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Could not parse PhaseDefinition id from string [" + v + "]");
    }
}
