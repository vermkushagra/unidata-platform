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

/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mikhail Mikhailov
 *
 */
public class TimelineDTO {

    /**
     * Etalon id.
     */
    private final String etalonId;

    /**
     * Intervals.
     */
    private final List<TimeIntervalDTO> intervals = new ArrayList<>();

    /**
     * Constructor.
     */
    public TimelineDTO(String etalonId) {
        super();
        this.etalonId = etalonId;
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }


    /**
     * @return the intervals
     */
    public List<TimeIntervalDTO> getIntervals() {
        return intervals;
    }
    /**
     * Selects interval, which includes the given date.
     * @param asOf the date, null is treated as current timestamp.
     * @return
     */
    public TimeIntervalDTO selectAsOf(Date asOf) {
        if (intervals.isEmpty()) {
            return null;
        }

        Date point = asOf == null ? new Date() : asOf;
        for (TimeIntervalDTO interval : intervals) {
            if (interval.isInRange(point)) {
                return interval;
            }
        }

        return null;
    }
}
