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
