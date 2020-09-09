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

package com.unidata.mdm.backend.api.rest.dto.job;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author Denis Kostovarov
 */
class JobParameterROSerializer extends JsonSerializer<JobParameterRO> {

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_MULTISELECT = "multi_select";

    @Override
    public void serialize(final JobParameterRO value, final JsonGenerator generator, final SerializerProvider serializers) throws IOException {

        generator.writeStartObject();
        if (value.getId() != null) {
            generator.writeNumberField(FIELD_ID, value.getId());
        }

        generator.writeStringField(FIELD_NAME, value.getName());
        generator.writeBooleanField(FIELD_MULTISELECT, value.isMultiSelect());

        if (value.getValueSize() > 1) {
            generator.writeFieldName(FIELD_VALUE);
            generator.writeStartArray();
            switch (value.getType()) {
                case STRING:
                    for (final String s : (String[]) value.getArrayValue()) {
                        generator.writeString(s);
                    }
                    break;
                case DATE:
                    for (final ZonedDateTime dt : (ZonedDateTime[]) value.getArrayValue()) {
                        generator.writeString(dt.truncatedTo(ChronoUnit.SECONDS)
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    }
                    break;
                case LONG:
                    for (final Long l : (Long[]) value.getArrayValue()) {
                        generator.writeNumber(l);
                    }
                    break;
                case DOUBLE:
                    for (final Double d : (Double[]) value.getArrayValue()) {
                        generator.writeNumber(d);
                    }
                    break;
                case BOOLEAN:
                    for (final Boolean b : (Boolean[]) value.getArrayValue()) {
                        generator.writeBoolean(b);
                    }
                    break;
            }
            generator.writeEndArray();
        } else {
            switch (value.getType()) {
                case STRING:
                    generator.writeStringField(FIELD_VALUE, value.getStringValue());
                    break;
                case DATE:
                    generator.writeStringField(FIELD_VALUE, value.getDateValue().truncatedTo(ChronoUnit.SECONDS)
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    break;
                case LONG:
                    generator.writeNumberField(FIELD_VALUE, value.getLongValue());
                    break;
                case DOUBLE:
                    generator.writeNumberField(FIELD_VALUE, value.getDoubleValue());
                    break;
                case BOOLEAN:
                    generator.writeBooleanField(FIELD_VALUE, value.getBooleanValue());
                    break;
            }
        }
        generator.writeObjectField(FIELD_TYPE, value.getType());
        generator.writeEndObject();
    }
}
