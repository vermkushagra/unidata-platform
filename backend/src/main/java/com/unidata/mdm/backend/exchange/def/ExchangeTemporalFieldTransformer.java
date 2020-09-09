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
package com.unidata.mdm.backend.exchange.def;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ExchangeTemporalFieldTransformer extends ExchangeFieldTransformer {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 4171819032393072033L;
    /**
     * ISO8601 output format.
     */
    public static final FastDateFormat  ISO8601_TZ_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssXXX");
    /**
     * ISO8601 output format (no time zone).
     */
    public static final FastDateFormat ISO8601_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
    /**
     * ISO8601 output format.
     */
    public static final FastDateFormat ISO8601_MILLIS_TZ_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        /**
     * ISO8601 output format (no time zone).
     */
    public static final FastDateFormat ISO8601_MILLIS_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS");
    /**
     * File name capable format.
     */
    public static final FastDateFormat FILE_NAME_TIMESTAMP_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH-mm-ss.SSSZ");

    /**
     * Parsing pattern for {@link SimpleDateFormat}.
     */
    private String pattern;

    /**
     * Lenient. True is the default.
     */
    private boolean lenient = true;

    /**
     * Time zone.
     */
    private String timeZone;

    /**
     * Input format.
     */
    private FastDateFormat inFmt = FastDateFormat.getInstance();

    /**
     * Output format.
     */
    private FastDateFormat outFmt = ISO8601_MILLIS_TZ_FORMAT;


    /**
     * Ctor.
     */
    public ExchangeTemporalFieldTransformer() {
        super();
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
        try {
            inFmt = FastDateFormat.getInstance(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String transform(String input) {
        if (input != null) {
            try {
                return outFmt.format(inFmt.parse(input.toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @return the lenient
     */
    public boolean isLenient() {
        return lenient;
    }

    /**
     * @param lenient the lenient to set
     */
    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    /**
     * @return the timeZone
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * @param timeZone the timeZone to set
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Converts input to {@link Date}.
     * @param input the input string
     * @return date instance or null
     */
    public Date toDate(String input) {
        if (input != null) {
            try {
                return inFmt.parse(input.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Converts input to {@link Calendar}.
     * @param input the input string
     * @return date instance or null
     */
    public Calendar toCalendar(String input) {
        if (input != null) {
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(inFmt.parse(input.toString()));
                return c;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Converts input to {@link Date}.
     * @param input the input string
     * @return date instance or null
     */
    public static Date ISO801TZStringToDate(String input) {

        if (input != null) {
            try {
                return ISO8601_TZ_FORMAT.parse(input.toString());
            } catch (ParseException e) {}
        }

        return null;
    }

    /**
     * Converts input to {@link Date}.
     * @param input the input string
     * @return date instance or null
     */
    public static Date ISO801StringToDate(String input) {

        if (input != null) {
            try {
                return ISO8601_FORMAT.parse(input.toString());
            } catch (ParseException e) {}
        }

        return null;
    }

    /**
     * Converts input to {@link Date}.
     * @param input the input string
     * @return date instance or null
     */
    public static Date ISO801TZMillisStringToDate(String input) {

        if (input != null) {
            try {
                return ISO8601_MILLIS_TZ_FORMAT.parse(input.toString());
            } catch (ParseException e) {}
        }

        return null;
    }

    /**
     * Converts input to {@link Date}.
     * @param input the input string
     * @return date instance or null
     */
    public static Date ISO801MillisStringToDate(String input) {

        if (input != null) {
            try {
                return ISO8601_MILLIS_FORMAT.parse(input.toString());
            } catch (ParseException e) {}
        }

        return null;
    }

    /**
     * Converts input to {@link Calendar}.
     * @param input the input string
     * @return date instance or null
     */
    public static Calendar ISO801TZStringToCalendar(String input) {
        if (input != null) {
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(ISO8601_TZ_FORMAT.parse(input.toString()));
                return c;
            } catch (ParseException e) {}
        }

        return null;
    }

    /**
     * Converts input to {@link Calendar}.
     * @param input the input string
     * @return date instance or null
     */
    public static Calendar ISO801StringToCalendar(String input) {
        if (input != null) {
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(ISO8601_FORMAT.parse(input.toString()));
                return c;
            } catch (ParseException e) {}
        }

        return null;
    }

    /**
     * Converts input to {@link Calendar}.
     * @param input the input string
     * @return date instance or null
     */
    public static Calendar ISO801TZMillisStringToCalendar(String input) {
        if (input != null) {
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(ISO8601_MILLIS_TZ_FORMAT.parse(input.toString()));
                return c;
            } catch (ParseException e) {}
        }

        return null;
    }

    /**
     * Converts input to {@link Calendar}.
     * @param input the input string
     * @return date instance or null
     */
    public static Calendar ISO801MillisStringToCalendar(String input) {
        if (input != null) {
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(ISO8601_MILLIS_FORMAT.parse(input.toString()));
                return c;
            } catch (ParseException e) {}
        }

        return null;
    }
}
