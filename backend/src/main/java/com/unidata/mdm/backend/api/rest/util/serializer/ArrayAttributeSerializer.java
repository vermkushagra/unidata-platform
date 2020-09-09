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
import java.util.Date;
import java.util.Objects;

import com.unidata.mdm.backend.api.rest.dto.data.extended.ExtendedArrayAttributeRO;
import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unidata.mdm.backend.api.rest.dto.data.ArrayAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.ArrayObjectRO;
import com.unidata.mdm.backend.common.ConvertUtils;

public class ArrayAttributeSerializer extends AbstractJsonSerializer<ArrayAttributeRO> {

    private static final FastDateFormat DEFAULT_TIMESTAMP
            = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public void serialize(ArrayAttributeRO value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField("name", value.getName());
        jgen.writeStringField("type", value.getType().value());
        if(value instanceof ExtendedArrayAttributeRO){
            jgen.writeBooleanField("winner", ((ExtendedArrayAttributeRO) value).isWinner());
        }
        jgen.writeArrayFieldStart("value");



        if (Objects.nonNull(value.getValue())) {
            for (ArrayObjectRO val : value.getValue()) {
                jgen.writeStartObject();

                switch (value.getType()) {
                    case DATE:
                        jgen.writeStringField("value", formatLocalDate((LocalDate) val.getValue()));
                        break;
                    case TIMESTAMP:
                        jgen.writeStringField("value", formatLocalDateTime((LocalDateTime) val.getValue()));
                        break;
                    case TIME:
                        jgen.writeStringField("value", formatLocalTime((LocalTime) val.getValue()));
                        break;
                    default:
                        jgen.writeObjectField("value", val.getValue());
                }

                jgen.writeStringField("displayValue", val.getDisplayValue());
                jgen.writeStringField("targetEtalonId", val.getTargetEtalonId());
                jgen.writeEndObject();
            }
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}
