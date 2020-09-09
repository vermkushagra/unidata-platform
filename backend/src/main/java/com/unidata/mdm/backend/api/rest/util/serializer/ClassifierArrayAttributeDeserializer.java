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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.unidata.mdm.backend.api.rest.dto.ArrayDataType;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeArrayAttrRO;
import org.apache.commons.lang3.StringUtils;

/**
 * Deserializer for {@link ClsfNodeArrayAttrRO}
 */
public class ClassifierArrayAttributeDeserializer extends ClassifierAttributeDeserializer<ClsfNodeArrayAttrRO> {

    @Override
    protected ClsfNodeArrayAttrRO objectFactory() {
        return new ClsfNodeArrayAttrRO();
    }

    @Override
    public ClsfNodeArrayAttrRO deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        final ClsfNodeArrayAttrRO clsfNodeArrayAttrRO = deserialize(node);

        if (StringUtils.isBlank(clsfNodeArrayAttrRO.getLookupEntityType())) {
            JsonNode typeNode = node.get("arrayDataType");
            ArrayDataType type = ArrayDataType.fromValue(typeNode.asText());
            clsfNodeArrayAttrRO.setArrayDataType(type);
            if (type == null) {
                clsfNodeArrayAttrRO.setArrayDataType(ArrayDataType.STRING);
            }
        }

        final JsonNode values = node.get("values");
        if (values == null || values.isNull()) {
            clsfNodeArrayAttrRO.setValues(Collections.emptyList());
        }
        else {
            final List<String> vals = StreamSupport.stream(node.spliterator(), false)
                    .map(JsonNode::asText).collect(Collectors.toList());

            clsfNodeArrayAttrRO.setValues(vals);
        }

        return clsfNodeArrayAttrRO;
    }
}
