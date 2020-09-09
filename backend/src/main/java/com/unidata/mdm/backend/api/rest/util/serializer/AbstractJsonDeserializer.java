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

package com.unidata.mdm.backend.api.rest.util.serializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;

/**
 * @author Alexey Tsarapkin
 */
public abstract class AbstractJsonDeserializer<T> extends JsonDeserializer<T> {
    /**
     * Date format without milliseconds.
     * Frontend specific.
     */
    private static final DateTimeFormatter DEFAULT_TIMESTAMP_NO_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * Parses string representation of date according to LocalDate from
     * {@see DEFAULT_TIMESTAMP_NO_MS}.
     *
     * @param dateAsString string representation of date.
     * @return parsed date.
     */
    protected LocalDate parseToLocalDate(String dateAsString) {
        try {
            return LocalDate.parse(dateAsString, DEFAULT_TIMESTAMP_NO_MS);
        } catch (DateTimeParseException e) {
            throw new SystemRuntimeException("Incorrect date format found, unable to parse date string!",
                    ExceptionId.EX_DATA_CANNOT_PARSE_DATE, dateAsString);
        }
    }

    /**
     * Parses string representation of date according to LocalTime from
     * {@see DEFAULT_TIMESTAMP_NO_MS}.
     *
     * @param dateAsString string representation of date.
     * @return parsed date.
     */
    protected LocalTime parseToLocalTime(String dateAsString) {
        try {
            return LocalTime.parse(dateAsString, DEFAULT_TIMESTAMP_NO_MS);
        } catch (DateTimeParseException e) {
            throw new SystemRuntimeException("Incorrect date format found, unable to parse date string!",
                    ExceptionId.EX_DATA_CANNOT_PARSE_DATE, dateAsString);
        }
    }

    /**
     * Parses string representation of date according to LocalDateTime from
     * {@see DEFAULT_TIMESTAMP_NO_MS}.
     *
     * @param dateAsString string representation of date.
     * @return parsed date.
     */
    protected LocalDateTime parseToLocalDateTime(String dateAsString) {
        try {
            return LocalDateTime.parse(dateAsString, DEFAULT_TIMESTAMP_NO_MS);
        } catch (DateTimeParseException e) {
            throw new SystemRuntimeException("Incorrect date format found, unable to parse date string!",
                    ExceptionId.EX_DATA_CANNOT_PARSE_DATE, dateAsString);
        }
    }

    @Override
    abstract public T deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException;

}
