package com.unidata.mdm.backend.common.types;

/**
 * @author Michael Yashin. Created on 29.05.2015.
 */
public enum SeverityType {

    CRITICAL("CRITICAL"),
    HIGH("HIGH"),
    NORMAL("NORMAL"),
    LOW("LOW");
    private final String value;

    SeverityType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SeverityType fromValue(String v) {

        if (v == null) {
            return null;
        }

        for (SeverityType c: SeverityType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException("Could not parse SeverityType id from string [" + v + "]");
    }
}
