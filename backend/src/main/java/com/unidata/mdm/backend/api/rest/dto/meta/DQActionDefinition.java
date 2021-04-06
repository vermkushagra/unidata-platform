package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * The Enum DQActionDefinition.
 */
public enum DQActionDefinition {
    
    /** The create new. */
    CREATE_NEW("CREATE_NEW"),
    
    /** The update current. */
    UPDATE_CURRENT("UPDATE_CURRENT");
    
    /** The value. */
    private final String value;

    /**
     * Instantiates a new DQ action definition.
     *
     * @param v the v
     */
    DQActionDefinition(String v) {
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
     * @return the DQ action definition
     */
    @JsonCreator
    public static DQActionDefinition fromValue(String v) {
        for (DQActionDefinition c : DQActionDefinition.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Could not parse DQActionDefinition id from string [" + v + "]");
    }
}
