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
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.data.LargeObjectRO;
import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;

/**
 * @author Michael Yashin. Created on 03.06.2015.
 */
public class SimpleAttributeDeserializer extends AbstractJsonDeserializer<SimpleAttributeRO> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAttributeDeserializer.class);

    @Override
    public SimpleAttributeRO deserialize(JsonParser jsonParser,
                            DeserializationContext deserializationContext) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        JsonNode nameNode = node.get("name");
        JsonNode typeNode = node.get("type");
        JsonNode valueNode = node.get("value");
        JsonNode unitIdNode = node.get("unitId");
        JsonNode valueIdNode = node.get("valueId");
        if (nameNode == null) {
            throw new JsonMappingException("SimpleAttributeRO: Name node is required");
        }
        if (typeNode == null) {
            throw new JsonMappingException("SimpleAttributeRO: Type node is required");
        }
        if (valueNode == null) {
            throw new JsonMappingException("SimpleAttributeRO: Value node is required");
        }
        if ((unitIdNode == null && valueIdNode != null) || (unitIdNode != null && valueIdNode == null)) {
            throw new JsonMappingException("SimpleAttributeRO: Measured options should be define completely");
        }
        SimpleDataType type = SimpleDataType.fromValue(typeNode.asText());
        SimpleAttributeRO attr = new SimpleAttributeRO();
        attr.setValueId(valueIdNode == null || valueIdNode.isNull() ? null : valueIdNode.asText());
        attr.setUnitId(unitIdNode == null || unitIdNode.isNull() ? null : unitIdNode.asText());
        attr.setName(nameNode.asText());
        attr.setType(type);
        if (!valueNode.isNull()) {
            switch (type) {
                case BOOLEAN:
                    attr.setValue(valueNode.asBoolean());
                    break;
                case DATE:
                    try {
                        attr.setValue(parseToLocalDate(valueNode.asText()));
                    } catch (SystemRuntimeException e) {
                        LOGGER.warn("Cannot convert attribute {} of type {} [{}]. Exception {}.",
                                attr.getName(),
                                attr.getType(),
                                valueNode.asText(),
                                e);
                    }
                    break;
                case TIME:
                    try {
                        attr.setValue(parseToLocalTime(valueNode.asText()));
                    } catch (SystemRuntimeException e) {
                        LOGGER.warn("Cannot convert attribute {} of type {} [{}]. Exception {}.",
                                attr.getName(),
                                attr.getType(),
                                valueNode.asText(),
                                e);
                    }
                    break;
                case TIMESTAMP:
                    try {
                        attr.setValue(parseToLocalDateTime(valueNode.asText()));
                    } catch (SystemRuntimeException e) {
                        LOGGER.warn("Cannot convert attribute {} of type {} [{}]. Exception {}.",
                                attr.getName(),
                                attr.getType(),
                                valueNode.asText(),
                                e);
                    }
                    break;
                case INTEGER:
                    attr.setValue(valueNode.asLong());
                    break;
                case NUMBER:
                    attr.setValue(valueNode.asDouble());
                    break;
                case STRING:
                    attr.setValue(valueNode.asText());
                    break;
                case BLOB:
                case CLOB:
                    if (valueNode.getNodeType() != JsonNodeType.OBJECT) {
                        throw new DataProcessingException("Invalid LOB object received from front end [%s].",
                                ExceptionId.EX_DATA_INVALID_LOB_OBJECT, valueNode.toString());
                    }

                    Iterator<String> i = valueNode.fieldNames();
                    if (i.hasNext()) {
                        LargeObjectRO obj = new LargeObjectRO();
                        while (i.hasNext()) {
                            String field = i.next();
                            JsonNode lobNode = valueNode.get(field);

                            switch (field) {
                                case "id":
                                    obj.setId(lobNode.textValue());
                                    break;
                                case "fileName":
                                    obj.setFileName(lobNode.textValue());
                                    break;
                                case "mimeType":
                                    obj.setMimeType(lobNode.textValue());
                                    break;
                                case "size":
                                    obj.setSize(lobNode.longValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        attr.setValue(obj);
                    }
                    break;
                default:
                    throw new RuntimeException("Unsupported data type: " + type.name());
            }
        }
        return attr;
    }
	/**
	 * Parses string representation of date according to date format from
	 * {@see DEFAULT_TIMESTAMP}.
	 * 
	 * @param dateAsString
	 *            string representation of date.
	 * @return parsed date.
	 */
	private static Date parse(String dateAsString, DateParser dateParser) {
		Date result;
		try {
			result = dateAsString != null ? dateParser.parse(dateAsString) : null;
		} catch (ParseException e) {
			throw new SystemRuntimeException("Incorrect date format found, unable to parse date string!",
					ExceptionId.EX_DATA_CANNOT_PARSE_DATE, dateAsString);
		}
		return result;
	}
}
