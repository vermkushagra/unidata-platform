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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeAttrRO;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeSimpleAttrRO;
import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Serializer for {@link ClsfNodeSimpleAttrRO}
 */
public class ClassifierAttributeSerializer<T extends ClsfNodeAttrRO> extends AbstractJsonSerializer<T> {

    /**
     * @param value
     * @param jgen
     * @param serializers
     * @throws IOException
     */
    @Override
    public void serialize(T value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
        serializers.defaultSerializeField("lookupEntityType", value.getLookupEntityType(), jgen);
        serializers.defaultSerializeField(
                "lookupEntityCodeAttributeType",
                value.getLookupEntityCodeAttributeType() == null ? null : value.getLookupEntityCodeAttributeType().value(),
                jgen
        );
        serializers.defaultSerializeField("name", value.getName(), jgen);
        serializers.defaultSerializeField("description", value.getDescription(), jgen);
        serializers.defaultSerializeField("displayName", value.getDisplayName(), jgen);
        serializers.defaultSerializeField("hidden", value.isHidden(), jgen);
        serializers.defaultSerializeField("readOnly", value.isReadOnly(), jgen);
        serializers.defaultSerializeField("searchable", value.isSearchable(), jgen);
        serializers.defaultSerializeField("unique", value.isUnique(), jgen);
        serializers.defaultSerializeField("nullable", value.isNullable(), jgen);
        serializers.defaultSerializeField("order", value.getOrder(), jgen);

        jgen.writeFieldName("customProperties");
        jgen.writeStartArray();
        if (CollectionUtils.isNotEmpty(value.getCustomProperties())) {
            for (CustomPropertyDefinition cpd : value.getCustomProperties()) {
                jgen.writeStartObject();
                jgen.writeStringField("name", cpd.getName());
                jgen.writeStringField("value", cpd.getValue());
                jgen.writeEndObject();
            }
        }
        jgen.writeEndArray();

    }
}
