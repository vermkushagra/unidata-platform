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
import java.util.Date;

import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.common.ConvertUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeSimpleAttrRO;

/**
 * Serializer for {@link ClsfNodeSimpleAttrRO}
 */
public class ClassifierSimpleAttributeSerializer extends ClassifierAttributeSerializer<ClsfNodeSimpleAttrRO> {

    /**
     * Value.
     */
    private static final String VALUE_FIELD = "value";
    /**
     * @param value
     * @param jgen
     * @param serializers
     * @throws IOException
     */
    @Override
    public void serialize(ClsfNodeSimpleAttrRO value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
        jgen.writeStartObject();
        super.serialize(value, jgen, serializers);
        serializers.defaultSerializeField(
                "simpleDataType",
                value.getSimpleDataType() == null ? "" : value.getSimpleDataType().value(),
                jgen
        );
        serializers.defaultSerializeField("enumDataType", value.getEnumDataType(), jgen);
        if (value.getSimpleDataType() != null) {
            writeValue(value.getSimpleDataType(), value.getValue(), jgen);
        } else if (value.getLookupEntityCodeAttributeType() != null) {
            jgen.writeObjectField("value", value.getValue());
        } else {
            jgen.writeStringField("value", (String)value.getValue());
        }
        jgen.writeEndObject();
    }

    /**
     * {@inheritDoc}
     * TODO: see UN-9612, classifier node attributes with date types must be saved as localdate
     */
    @Override
    protected void writeValue(SimpleDataType type, Object value, JsonGenerator jgen) throws IOException {

        switch (type) {
            case DATE:
                if (value == null) {
                    jgen.writeNullField(VALUE_FIELD);
                } else {
                    jgen.writeStringField(VALUE_FIELD, formatLocalDate(ConvertUtils.date2LocalDate((Date)value)));
                }
                break;
            case TIME:
                if (value == null) {
                    jgen.writeNullField(VALUE_FIELD);
                } else {
                    jgen.writeStringField(VALUE_FIELD, formatLocalTime(ConvertUtils.date2LocalTime((Date) value)));
                }
                break;
            case TIMESTAMP:
                if (value == null) {
                    jgen.writeNullField(VALUE_FIELD);
                } else {
                    jgen.writeStringField(VALUE_FIELD, formatLocalDateTime(ConvertUtils.date2LocalDateTime((Date) value)));
                }
                break;
            default:
                jgen.writeObjectField(VALUE_FIELD, value);
        }
    }
}
