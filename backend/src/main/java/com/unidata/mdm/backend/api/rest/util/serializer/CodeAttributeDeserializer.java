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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.unidata.mdm.backend.api.rest.dto.CodeDataType;
import com.unidata.mdm.backend.api.rest.dto.data.CodeAttributeRO;

/**
 * @author Michael Yashin. Created on 03.06.2015.
 */
public class CodeAttributeDeserializer extends JsonDeserializer<CodeAttributeRO> {

    @Override
    public CodeAttributeRO deserialize(JsonParser jsonParser,
                            DeserializationContext deserializationContext) throws IOException {

        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        JsonNode nameNode = node.get("name");
        JsonNode typeNode = node.get("type");
        JsonNode valueNode = node.get("value");

        if (nameNode == null) {
            throw new JsonMappingException("CodeAttributeRO: Name node is required");
        }

        if (typeNode == null) {
            throw new JsonMappingException("CodeAttributeRO: Type node is required");
        }

        if (valueNode == null) {
            throw new JsonMappingException("CodeAttributeRO: Value node is required");
        }

        CodeDataType type = CodeDataType.fromValue(typeNode.asText());
        CodeAttributeRO attr = new CodeAttributeRO();
        attr.setName(nameNode.asText());
        attr.setType(type);

        if (!valueNode.isNull()) {

            if (!valueNode.isIntegralNumber() && !valueNode.isTextual()) {
                throw new JsonMappingException("CodeAttributeRO: Value node must be either an integer or string");
            }

            switch (type) {
                case INTEGER:
                    attr.setValue(Long.valueOf(valueNode.asLong()));
                    break;
                case STRING:
                    attr.setValue(valueNode.asText());
                    break;
                default:
                    throw new JsonMappingException("Unsupported data type: " + type.name());
            }
        }

        return attr;
    }
}
