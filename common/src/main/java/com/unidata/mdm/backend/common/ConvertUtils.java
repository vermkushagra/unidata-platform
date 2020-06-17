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

package com.unidata.mdm.backend.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author Mikhail Mikhailov
 * Various conversions.
 */
public class ConvertUtils {

    /**
     * Start of epoch as local date.
     */
    private static final LocalDate START_OF_EPOCH = LocalDate.of(1970, 1, 1);

    /**
     * Constructor.
     */
    private ConvertUtils() {
        super();
    }
    /**
     * Date 2 LocalDate.
     * @param d the date
     * @return local date
     */
    public static LocalDate date2LocalDate(Date d) {
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    /**
     * Date 2 LocalTime.
     * @param d the date
     * @return local time
     */
    public static LocalTime date2LocalTime(Date d) {
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }
    /**
     * Date 2 LocalDateTime.
     * @param d the date
     * @return local date time
     */
    public static LocalDateTime date2LocalDateTime(Date d) {
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    /**
     * LocalDate 2 Date.
     * @param d the local date
     * @return date
     */
    public static Date localDate2Date(LocalDate d) {
        return d == null ? null : Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    /**
     * LocalTime 2 Date.
     * @param d the local time
     * @return date
     */
    public static Date localTime2Date(LocalTime d) {
        return d == null ? null : Date.from(d.atDate(START_OF_EPOCH).atZone(ZoneId.systemDefault()).toInstant());
    }
    /**
     * LocalDateTime 2 Date.
     * @param d the local date time
     * @return date
     */
    public static Date localDateTime2Date(LocalDateTime d) {
        return d == null ? null : Date.from(d.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     *
     * @param z the zoned date time
     * @return date
     */
    public static Date zonedDateTime2Date(ZonedDateTime z){
        return z == null ? null : Date.from(z.toInstant());
    }
}
