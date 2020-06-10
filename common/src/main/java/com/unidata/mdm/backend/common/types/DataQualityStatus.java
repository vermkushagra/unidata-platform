package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Data quality status type.
 */
public enum DataQualityStatus {
    /**
     * DQ error new.
     */
    NEW,
    /**
     * DQ error resolved.
     */
    RESOLVED;
    /**
     * Convenient value method.
     * @return value / name.
     */
    public String value() {
        return name();
    }
    /**
     * Covenient from value wmethod.
     * @param v value / name
     * @return parsed value
     */
    public static DataQualityStatus fromValue(String v) {

        if (v == null) {
            return null;
        }

        return valueOf(v);
    }
}
