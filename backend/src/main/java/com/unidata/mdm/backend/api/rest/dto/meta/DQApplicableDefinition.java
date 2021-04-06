package com.unidata.mdm.backend.api.rest.dto.meta;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DQApplicableDefinition {
    
    /** The etalon. */
    ETALON("ETALON"),
    
    /** The origin. */
    ORIGIN("ORIGIN");
    
    /** The value. */
    private final String value;

    /**
     * Instantiates a new DQ action definition.
     *
     * @param v the v
     */
    DQApplicableDefinition(String v) {
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
    public static DQApplicableDefinition fromValue(String v) {
    	if(StringUtils.isEmpty(v)){
    		return null;
    	}
        for (DQApplicableDefinition c : DQApplicableDefinition.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Could not parse DQApplicableDefinition id from string [" + v + "]");
    }
}
