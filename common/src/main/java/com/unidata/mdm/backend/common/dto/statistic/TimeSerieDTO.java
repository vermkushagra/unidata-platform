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

package com.unidata.mdm.backend.common.dto.statistic;

import java.util.Date;

/**
 * The Class TimeSerie.
 */
public class TimeSerieDTO {
    
    /** The time. */
    private Date time;
    
    /** The value. */
    private int value;
    
    /**
     * Gets the time.
     *
     * @return the time
     */
    public Date getTime() {
        return time;
    }
    
    /**
     * Sets the time.
     *
     * @param time the new time
     */
    public void setTime(Date time) {
        this.time = time;
    }
    
    /**
     * Gets the value.
     *
     * @return the value
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(int value) {
        this.value = value;
    }
}
