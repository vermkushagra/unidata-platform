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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.unidata.mdm.backend.api.rest.dto.ArrayDataType;
import com.unidata.mdm.backend.api.rest.dto.data.ArrayAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.ArrayObjectRO;

/**
 * @author Michael Yashin. Created on 03.06.2015.
 */
public class ArrayAttributeDeserializer extends AbstractJsonDeserializer<ArrayAttributeRO> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArrayAttributeDeserializer.class);

    @Override
    public ArrayAttributeRO deserialize(JsonParser jsonParser,
                            DeserializationContext deserializationContext) throws IOException {

        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        JsonNode nameNode = node.get("name");
        JsonNode typeNode = node.get("type");
        JsonNode valueNode = node.get("value");

        if (nameNode == null) {
            throw new JsonMappingException("ArrayAttributeRO: Name node is required");
        }

        if (typeNode == null) {
            throw new JsonMappingException("ArrayAttributeRO: Type node is required");
        }

        if (valueNode == null) {
            throw new JsonMappingException("ArrayAttributeRO: Value node is required");
        }

        ArrayDataType type = ArrayDataType.fromValue(typeNode.asText());
        ArrayAttributeRO attr = new ArrayAttributeRO();
        attr.setName(nameNode.asText());
        attr.setType(type);

        if (!valueNode.isNull()) {

            if (!valueNode.isArray()) {
                throw new JsonMappingException("ArrayAttributeRO: Value node must be an array");
            }

            JsonNode innerValue;
            List<ArrayObjectRO> attrVal = new ArrayList<>(valueNode.size());
            switch (type) {
                case DATE:
                    for (JsonNode child : valueNode) {
                        if (child.isNull() || !child.isObject()) {
                            continue;
                        }

                        innerValue = child.get("value");
                        if (innerValue == null || innerValue.isNull()) {
                            continue;
                        }

                        LocalDate result = parseToLocalDate(StringUtils.trim(innerValue.asText()));
                        if (result == null) {
                            LOGGER.warn("Cannot convert attribute {} of type {} [{}].", attr.getName(), attr.getType(), innerValue.asText());
                            continue;
                        }

                        attrVal.add(new ArrayObjectRO(result));
                    }
                    break;
                case TIME:
                    for (JsonNode child : valueNode) {
                        if (child.isNull() || !child.isObject()) {
                            continue;
                        }

                        innerValue = child.get("value");
                        if (innerValue == null || innerValue.isNull()) {
                            continue;
                        }

                        LocalTime result = parseToLocalTime(StringUtils.trim(innerValue.asText()));
                        if (result == null) {
                            LOGGER.warn("Cannot convert attribute {} of type {} [{}].", attr.getName(), attr.getType(), innerValue.asText());
                            continue;
                        }

                        attrVal.add(new ArrayObjectRO(result));
                    }
                    break;
                case TIMESTAMP:
                    for (JsonNode child : valueNode) {
                        if (child.isNull() || !child.isObject()) {
                            continue;
                        }

                        innerValue = child.get("value");
                        if (innerValue == null || innerValue.isNull()) {
                            continue;
                        }

                        LocalDateTime result = parseToLocalDateTime(StringUtils.trim(innerValue.asText()));
                        if (result == null) {
                            LOGGER.warn("Cannot convert attribute {} of type {} [{}].", attr.getName(), attr.getType(), innerValue.asText());
                            continue;
                        }

                        attrVal.add(new ArrayObjectRO(result));
                    }
                    break;
                case INTEGER:
                    for (JsonNode child : valueNode) {
                        if (child.isNull() || !child.isObject()) {
                            continue;
                        }

                        innerValue = child.get("value");
                        if (innerValue == null || innerValue.isNull() || !innerValue.isIntegralNumber()) {
                            continue;
                        }

                        attrVal.add(new ArrayObjectRO(Long.valueOf(innerValue.asLong())));
                    }
                    break;
                case NUMBER:
                    for (JsonNode child : valueNode) {
                        if (child.isNull() || !child.isObject()) {
                            continue;
                        }

                        innerValue = child.get("value");
                        if (innerValue == null || innerValue.isNull() || !innerValue.isNumber()) {
                            continue;
                        }

                        attrVal.add(new ArrayObjectRO(Double.valueOf(innerValue.asDouble())));
                    }
                    break;
                case STRING:
                    for (JsonNode child : valueNode) {
                        if (child.isNull() || !child.isObject()) {
                            continue;
                        }

                        innerValue = child.get("value");
                        if (innerValue == null || innerValue.isNull() || !innerValue.isTextual()) {
                            continue;
                        }

                        attrVal.add(new ArrayObjectRO(innerValue.asText()));
                    }
                    break;
                default:
                    throw new JsonMappingException("Unsupported data type: " + type.name());
            }

            attr.setValue(attrVal);
        }

        return attr;
    }
}
