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
import com.fasterxml.jackson.databind.JsonNode;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeArrayAttrRO;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeSimpleAttrRO;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import org.apache.commons.lang3.StringUtils;

/**
 * Deserializer for {@link ClsfNodeSimpleAttrRO}
 */
public class ClassifierSimpleAttributeDeserializer extends ClassifierAttributeDeserializer<ClsfNodeSimpleAttrRO> {

    @Override
    protected ClsfNodeSimpleAttrRO objectFactory() {
        return new ClsfNodeSimpleAttrRO();
    }

    @Override
    public ClsfNodeSimpleAttrRO deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        final ClsfNodeSimpleAttrRO clsfNodeSimpleAttrRO = deserialize(node);

        if (StringUtils.isBlank(clsfNodeSimpleAttrRO.getLookupEntityType())) {
            JsonNode typeNode = node.get("simpleDataType");
            SimpleDataType type = SimpleDataType.fromValue(typeNode.asText());
            clsfNodeSimpleAttrRO.setSimpleDataType(type);
            if (type == null) {
                clsfNodeSimpleAttrRO.setSimpleDataType(SimpleDataType.STRING);
            }
        }

        if (node.get("enumDataType") != null) {
            clsfNodeSimpleAttrRO.setEnumDataType(node.get("enumDataType").asText());
        }

        JsonNode valueNode = node.get("value");
        if (valueNode == null || valueNode.isNull()) {
            clsfNodeSimpleAttrRO.setValue(null);
            return clsfNodeSimpleAttrRO;
        }
        final String value = valueNode.asText();
        clsfNodeSimpleAttrRO.setValue(value);

        return clsfNodeSimpleAttrRO;
    }
}
