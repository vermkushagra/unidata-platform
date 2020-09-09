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

package com.unidata.mdm.backend.converter.classifiers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public final class TypeConverter {
    private TypeConverter() { }

    private static final String SDF_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String SDF_TIME = "HH:mm:ss";
    private static final String SDF_DATE = "yyyy-MM-dd";

    /**
     * Object to boolean, if required.
     * @param o object
     * @return
     */
    public static Boolean toBoolean(Object o) {
        return o == null || StringUtils.isBlank(o.toString())
                ? null
                : Boolean.class.isAssignableFrom(o.getClass())
                ? (Boolean) o
                : Boolean.valueOf(o.toString());
    }

    /**
     * Object to long, if required.
     * @param o object
     * @return
     */
    public static Long toLong(Object o) {
        return o == null || StringUtils.isBlank(o.toString())
                ? null
                : Number.class.isAssignableFrom(o.getClass())
                ? ((Number) o).longValue()
                : Long.valueOf(o.toString());
    }

    /**
     * Object to boolean, if required.
     * @param o object
     * @return
     */
    public static Double toDouble(Object o) {
        return o == null || StringUtils.isBlank(o.toString())
                ? null
                : Number.class.isAssignableFrom(o.getClass())
                ? ((Number) o).doubleValue()
                : Double.valueOf(o.toString());
    }

    /**
     * Object to boolean, if required.
     * @param o object
     * @return
     */
    public static String toString(Object o) {
        return o == null || StringUtils.isBlank(o.toString())
                ? null
                : String.class.isAssignableFrom(o.getClass())
                ? (String) o
                : o.toString();
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the XML gregorian calendar
     */
    public static LocalDate toDate(Object o) {
        if (o == null  || StringUtils.isBlank(o.toString())) {
            return null;
        } else if (Calendar.class.isAssignableFrom(o.getClass())) {
            return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (Date.class.isAssignableFrom(o.getClass())) {
            return ((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (String.class.isAssignableFrom(o.getClass())) {
            return LocalDate.parse(o.toString(), DateTimeFormatter.ofPattern(SDF_DATE));
        }

        return null;
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the XML gregorian calendar
     */
    public static LocalTime toTime(Object o) {
        if (o == null || StringUtils.isBlank(o.toString())) {
            return null;
        } else if (Calendar.class.isAssignableFrom(o.getClass())) {
            return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        } else if (Date.class.isAssignableFrom(o.getClass())) {
            return ((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        } else if (String.class.isAssignableFrom(o.getClass())) {
            return LocalTime.parse(o.toString(), DateTimeFormatter.ofPattern(SDF_TIME));
        }

        return null;
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the XML gregorian calendar
     */
    public static LocalDateTime toTimestamp(Object o) {
        if (o == null || StringUtils.isBlank(o.toString())) {
            return null;
        } else if (Calendar.class.isAssignableFrom(o.getClass())) {
            return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (Date.class.isAssignableFrom(o.getClass())) {
            return ((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (String.class.isAssignableFrom(o.getClass())) {
            return LocalDateTime.parse(o.toString(), DateTimeFormatter.ofPattern(SDF_DATE_TIME));
        }

        return null;
    }

}
