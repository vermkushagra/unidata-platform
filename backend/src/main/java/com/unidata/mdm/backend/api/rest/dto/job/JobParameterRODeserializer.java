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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldRO;
import com.unidata.mdm.backend.common.job.JobParameterType;

/**
 * @author Denis Kostovarov
 */
class JobParameterRODeserializer extends JsonDeserializer<JobParameterRO> {

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_MULTISELECT = "multi_select";

    @Override
    public JobParameterRO deserialize(final JsonParser p, final DeserializationContext ctx) throws IOException {

        final ObjectCodec oc = p.getCodec();
        final JsonNode node = oc.readTree(p);
        final JsonNode idNode = node.get(FIELD_ID);
        final JsonNode nameNode = node.get(FIELD_NAME);
        final JsonNode typeNode = node.get(FIELD_TYPE);
        final JsonNode valueNode = node.get(FIELD_VALUE);
        final JsonNode multiselectNode = node.get(FIELD_MULTISELECT);

        if (nameNode == null) {
            throw new JsonMappingException(JobParameterRO.class.getSimpleName() + ": Name node is required.");
        }
        if (typeNode == null) {
            throw new JsonMappingException(JobParameterRO.class.getSimpleName() + ": Type node is required.");
        }
        if (valueNode == null && idNode == null) {
            throw new JsonMappingException(JobParameterRO.class.getSimpleName() + ": Either Value node OR Range node is required.");
        }

        final JobParameterType type = JobParameterType.fromValue(typeNode.asText());

        final JobParameterRO result = extractValue(type, nameNode.asText(), valueNode, ctx);

        if (result != null && idNode != null) {
            result.setId(idNode.asLong());
        }

        if (multiselectNode != null) {
            result.setMultiSelect(multiselectNode.asBoolean());
        }

        return result;
    }

    /**
     * Extracts value node value.
     *
     * @param type      the value type
     * @param valueNode the value node
     * @param ctx       Deserialization context.
     * @return value of specified type or <code>null</code>.
     */
    private JobParameterRO extractValue(JobParameterType type, final String name, JsonNode valueNode, DeserializationContext ctx)
            throws JsonMappingException {

        if (valueNode.isNull()) {
            return null;
        }

        final JobParameterRO res;

        List<Object> values = new ArrayList<>();

        if (valueNode.isArray()) {
            Iterator<JsonNode> it = ((ArrayNode) valueNode).elements();
            while (it.hasNext()) {
                JsonNode itemNode = it.next();

                values.add(getValue(type, itemNode));
            }
        } else {
            values.add(getValue(type, valueNode));
        }

        switch (type) {
            case BOOLEAN:
                res = new JobParameterRO(name, values.toArray(new Boolean[values.size()]));
                break;
            case DATE:
                res = new JobParameterRO(name, values.toArray(new ZonedDateTime[values.size()]));
                break;
            case LONG:
                res = new JobParameterRO(name, values.toArray(new Long[values.size()]));
                break;
            case DOUBLE:
                res = new JobParameterRO(name, values.toArray(new Double[values.size()]));
                break;
            case STRING:
                res = new JobParameterRO(name, values.toArray(new String[values.size()]));
                break;
            default:
                throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() + ": Types other then simple types ["
                        + type.name()
                        + "] aren't supported.");
        }

        return res;
    }

    /**
     * Get value according to it type
     * @param type
     * @param node
     * @return
     * @throws JsonMappingException
     */
    private Object getValue(JobParameterType type, JsonNode node) throws JsonMappingException {
        switch (type) {
            case BOOLEAN:
                return node.asBoolean();
            case DATE:
                return ZonedDateTime.parse(node.asText());
            case LONG:
                return node.asLong();
            case DOUBLE:
                return node.asDouble();
            case STRING:
                return node.asText();
            default:
                throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() +
                        ": Types other then simple types [" + type.name() + "] aren't supported.");
        }
    }
}
