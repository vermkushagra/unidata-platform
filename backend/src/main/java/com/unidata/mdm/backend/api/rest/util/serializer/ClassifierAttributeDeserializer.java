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

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.unidata.mdm.backend.api.rest.dto.CodeDataType;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeAttrRO;
import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class ClassifierAttributeDeserializer<T extends ClsfNodeAttrRO> extends JsonDeserializer<T> {

    protected abstract T objectFactory();

    /**
     * @param node       - json node
     * @return - demoralized classifier attr
     */
    protected T deserialize(JsonNode node) {
    	final T attr = objectFactory();
        attr.setDescription(node.get("description").asText(""));
        attr.setDisplayName(node.get("displayName").asText(""));
        attr.setHidden(node.get("hidden").asBoolean(false));
        attr.setReadOnly(node.get("readOnly").asBoolean(false));
        attr.setSearchable(node.get("searchable").asBoolean(false));
        attr.setNullable(node.get("nullable").asBoolean(false));
        attr.setName(node.get("name").asText());
        attr.setLookupEntityType(node.get("lookupEntityType").asText());
        final JsonNode orderNode = node.get("order");
        if (orderNode != null && !orderNode.isNull()) {
            attr.setOrder(orderNode.intValue());
        }
        attr.setUnique(false);
        if (node.get("lookupEntityCodeAttributeType") != null
                && StringUtils.isNoneBlank(node.get("lookupEntityCodeAttributeType").asText())) {
            final CodeDataType lookupEntityCodeAttributeType =
                    CodeDataType.fromValue(node.get("lookupEntityCodeAttributeType").asText());
            attr.setLookupEntityCodeAttributeType(lookupEntityCodeAttributeType);
        }
        JsonNode customPropsNode = node.get("customProperties");
        if(customPropsNode != null){
            attr.setCustomProperties(new ArrayList<>());
            Iterator<JsonNode> it =  customPropsNode.elements();
            while (it.hasNext()) {
                JsonNode propNode = it.next();
                CustomPropertyDefinition cpd = new CustomPropertyDefinition();
                cpd.setName(propNode.get("name").asText());
                cpd.setValue(propNode.get("value").asText());
                attr.addCustomProperty(cpd);
            }
        }
        return attr;
    }
}
