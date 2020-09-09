/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
