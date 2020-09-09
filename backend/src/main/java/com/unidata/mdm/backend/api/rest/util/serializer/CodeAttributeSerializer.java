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
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unidata.mdm.backend.api.rest.dto.data.CodeAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.extended.ExtendedCodeAttributeRO;

public class CodeAttributeSerializer extends JsonSerializer<CodeAttributeRO> {

    @Override
    public void serialize(CodeAttributeRO value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField("name", value.getName());
        jgen.writeStringField("type", value.getType().value());

        if(value instanceof ExtendedCodeAttributeRO){
            jgen.writeBooleanField("winner", ((ExtendedCodeAttributeRO) value).isWinner());
        }

        jgen.writeFieldName("value");

        if (Objects.nonNull(value.getValue())) {
            jgen.writeObject(value.getValue());
        } else {
            jgen.writeNull();
        }

        jgen.writeArrayFieldStart("supplementary");

        if (Objects.nonNull(value.getSupplementary()) && !value.getSupplementary().isEmpty()) {
            for (Object val : value.getSupplementary()) {
                jgen.writeObject(val);
            }
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}
