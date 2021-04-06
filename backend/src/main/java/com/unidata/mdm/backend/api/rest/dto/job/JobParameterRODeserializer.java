package com.unidata.mdm.backend.api.rest.dto.job;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldRO;
import com.unidata.mdm.backend.common.dto.job.JobParameterType;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * @author Denis Kostovarov
 */
class JobParameterRODeserializer extends JsonDeserializer<JobParameterRO> {

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_VALUE = "value";

    @Override
    public JobParameterRO deserialize(final JsonParser p, final DeserializationContext ctx) throws IOException {

        final ObjectCodec oc = p.getCodec();
        final JsonNode node = oc.readTree(p);
        final JsonNode idNode = node.get(FIELD_ID);
        final JsonNode nameNode = node.get(FIELD_NAME);
        final JsonNode typeNode = node.get(FIELD_TYPE);
        final JsonNode valueNode = node.get(FIELD_VALUE);

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

        switch (type) {
            case BOOLEAN:
                res = new JobParameterRO(name, valueNode.asBoolean());
                break;
            case DATE:
                res = new JobParameterRO(name, ZonedDateTime.parse(valueNode.asText()));
                break;
            case LONG:
                res = new JobParameterRO(name, valueNode.asLong());
                break;
            case DOUBLE:
                res = new JobParameterRO(name, valueNode.asDouble());
                break;
            case STRING:
                res = new JobParameterRO(name, valueNode.asText());
                break;
            default:
                throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() + ": Types other then simple types ["
                        + type.name()
                        + "] aren't supported.");
        }

        return res;
    }
}
