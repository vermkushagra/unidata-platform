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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.common.ConvertUtils;

/**
 * @author Alexey Tsarapkin
 */
public abstract class AbstractJsonSerializer<T> extends JsonSerializer<T> {

    /**
     * Date format without milliseconds.
     * Frontend specific.
     */
    private static final DateTimeFormatter DEFAULT_TIMESTAMP_NO_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private static final String VALUE_FIELD = "value";

    protected void writeValue(SimpleDataType type, Object value, JsonGenerator jgen) throws IOException {

        switch (type) {
            case DATE:
                if (value == null) {
                    jgen.writeNullField(VALUE_FIELD);
                } else {
                    jgen.writeStringField(VALUE_FIELD, formatLocalDate((LocalDate) value));
                }
                break;
            case TIME:
                if (value == null) {
                    jgen.writeNullField(VALUE_FIELD);
                } else {
                    jgen.writeStringField(VALUE_FIELD, formatLocalTime((LocalTime) value));
                }
                break;
            case TIMESTAMP:
                if (value == null) {
                    jgen.writeNullField(VALUE_FIELD);
                } else {
                    jgen.writeStringField(VALUE_FIELD, formatLocalDateTime((LocalDateTime) value));
                }
                break;
            default:
                jgen.writeObjectField(VALUE_FIELD, value);
        }
    }

    /**
     * Format LocalTime to String
     * {@see DEFAULT_TIMESTAMP_NO_MS}.
     *
     * @param localDate
     *
     * @return parsed date.
     */
    protected String formatLocalDate(LocalDate localDate) {
        return ConvertUtils.localDate2LocalDateTime(localDate).format(DEFAULT_TIMESTAMP_NO_MS);
    }

    /**
     * Format LocalTime to String
     * {@see DEFAULT_TIMESTAMP_NO_MS}.
     *
     * @param localTime
     *
     * @return parsed date.
     */
    protected String formatLocalTime(LocalTime localTime) {
        return ConvertUtils.localTime2LocalDateTime(localTime).format(DEFAULT_TIMESTAMP_NO_MS);
    }

    /**
     * Format LocalDateTime to String
     * {@see DEFAULT_TIMESTAMP_NO_MS}.
     *
     * @param localDateTime
     *
     * @return parsed date.
     */
    protected String formatLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(DEFAULT_TIMESTAMP_NO_MS);
    }


    @Override
    abstract public void serialize(T value, JsonGenerator gen, SerializerProvider serializers)throws IOException, JsonProcessingException;

}
