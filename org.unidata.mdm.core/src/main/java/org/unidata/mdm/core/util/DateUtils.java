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

package org.unidata.mdm.core.util;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.util.ConvertUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public final class DateUtils {
    private DateUtils() { }

    public static final DateTimeFormatter DEFAULT_FORMATTER_NO_OFFSET
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * Parses string representation of date according to date format from
     * {@see DEFAULT_TIMESTAMP_NO_OFFSET}.
     *
     * @param dateAsString
     *            string representation of date.
     * @return parsed date.
     */
    public static Date parse(String dateAsString) {

        Date result = null;
        try {
            LocalDateTime ldt = StringUtils.isBlank(dateAsString)
                    ? null
                    : LocalDateTime.parse(dateAsString, DEFAULT_FORMATTER_NO_OFFSET);
            if (ldt != null) {
                result = ConvertUtils.localDateTime2Date(ldt);
            }
        } catch (DateTimeParseException e) {
            throw new PlatformFailureException("Incorrect date format found, unable to parse date string!",
                    CoreExceptionIds.EX_META_VALIDITY_PERIODS_CANNOT_PARSE_DATE, dateAsString);
        }
        return result;
    }
}
