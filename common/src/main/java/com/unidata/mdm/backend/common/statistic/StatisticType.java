package com.unidata.mdm.backend.common.statistic;

/**
 * The Enum StatisticType.
 */
public enum StatisticType {

    /**
     * The total.
     */
    TOTAL("TOTAL", false),
    /**
     * The new.
     */
    NEW("NEW", true),
    /**
     * The updated.
     */
    UPDATED("UPDATED", true),
    /**
     * The merged.
     */
    MERGED("MERGED", true),
    /**
     * The errors.
     */
    ERRORS("ERRORS", false),
    /**
     * The duplicates.
     */
    DUPLICATES("DUPLICATES", false),
    /**
     * The clusters.
     */
    @Deprecated
    CLUSTERS("CLUSTERS", false);

    /**
     * The value.
     */
    private final String value;
    private final boolean isAggregate;

    /**
     * Instantiates a new statistic type.
     *
     * @param value       the value
     * @param isAggregate is aggregate flag
     */
    StatisticType(String value, boolean isAggregate) {
        this.value = value;
        this.isAggregate = isAggregate;
    }

    /**
     * From value.
     *
     * @param v the v
     * @return the statistic type
     */
    public static StatisticType fromValue(String v) {
        for (StatisticType c : StatisticType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Could not parse StatisticType id from string [" + v + "]");
    }

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return value;
    }

    public boolean isAggregate() {
        return isAggregate;
    }
}
