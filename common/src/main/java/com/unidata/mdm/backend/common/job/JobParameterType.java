package com.unidata.mdm.backend.common.job;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Denis Kostovarov
 */
public enum JobParameterType {

    STRING,
    LONG,
    DOUBLE,
    DATE,
    BOOLEAN;

    public static JobParameterType fromValue(final String v) {
        for (final JobParameterType c : JobParameterType.values()) {
            if (StringUtils.equalsIgnoreCase(v, c.toString())) {
                return c;
            }
        }
        return null;
    }

}
