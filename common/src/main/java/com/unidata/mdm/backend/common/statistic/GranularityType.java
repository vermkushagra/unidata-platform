package com.unidata.mdm.backend.common.statistic;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * The Enum Granularity.
 */
public enum GranularityType {

    /** The minute. */
    MINUTE("MINUTE"),
    /** The hour. */
    HOUR("HOUR"),
    /** The day. */
    DAY("DAY"),
    /** Week. */
    WEEK("WEEK"),
    /** Month. */
    MONTH("MONTH");

    /** The value. */
    private final String value;

    /**
     * Instantiates a new granularity.
     *
     * @param v
     *            the v
     */
    GranularityType(String v) {
        value = v;
    }

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return value;
    }

    /**
     * From value.
     *
     * @param v
     *            the v
     * @return the granularity
     */
    public static GranularityType fromValue(String v) {
        for (GranularityType c : GranularityType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Could not parse GranularityType id from string [" + v + "]");
    }

    public static TemporalUnit toTemporalUnit(GranularityType granularityType){
        TemporalUnit result;
        if(granularityType == null){
            result = ChronoUnit.DAYS;
        } else {
            switch (granularityType){
                case MINUTE:
                    result = ChronoUnit.MINUTES;
                    break;
                case HOUR:
                    result = ChronoUnit.HOURS;
                    break;
                case DAY:
                    result = ChronoUnit.DAYS;
                    break;
                case WEEK:
                    result = ChronoUnit.WEEKS;
                    break;
                case MONTH:
                    result = ChronoUnit.MONTHS;
                    break;
                default:
                    result = ChronoUnit.DAYS;
                    break;
            }
        }
        return result;
    }
}
