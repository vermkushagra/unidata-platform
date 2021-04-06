package com.unidata.mdm.backend.dto.job;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author Denis Kostovarov
 */
public enum JobParameterType {

    STRING,
    LONG,
    DOUBLE,
    DATE,
    BOOLEAN;

    @JsonCreator
    public static JobParameterType fromValue(final String v) {
        for (final JobParameterType c : JobParameterType.values()) {
            if (StringUtils.equalsIgnoreCase(v, c.toString())) {
                return c;
            }
        }
        return null;
    }

}
