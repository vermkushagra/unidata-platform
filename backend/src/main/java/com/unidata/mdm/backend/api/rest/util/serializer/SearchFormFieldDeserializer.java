/**
 *
 */
package com.unidata.mdm.backend.api.rest.util.serializer;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldRO;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;


/**
 * @author Mikhail Mikhailov
 *
 */
public class SearchFormFieldDeserializer extends JsonDeserializer<SearchFormFieldRO> {

    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchFormFieldDeserializer.class);

    /**
     * Name object field.
     */
    private static final String FIELD_NAME = "name";
    /**
     * Type object field.
     */
    private static final String FIELD_TYPE = "type";
    /**
     * Value object field.
     */
    private static final String FIELD_VALUE = "value";
    /**
     * Range object field.
     */
    private static final String FIELD_RANGE = "range";

    /**
     * Field which contains inverted or not search form
     */
    private static final String FIELD_INVERTED = "inverted";

    /**
     * Field which contains is it like form or not
     */
    private static final String FIELD_LIKE = "like";

    /**
     * Field which contains is it start with form ot not.
     */
    private static final String FIELD_START_WITH = "startWith";

    /**
     * Field which contains is it fuzzy search or not.
     */
    private static final String FIELD_FUZZY= "fuzzy";

    /**
     * Search fiуld taking morphology into account.
     */
    private static final String FIELD_MORPHOLOGICAL= "morphological";

    /**
     * Constructor.
     */
    public SearchFormFieldDeserializer() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchFormFieldRO deserialize(JsonParser p, DeserializationContext ctx) throws IOException {

        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);
        JsonNode nameNode = node.get(FIELD_NAME);
        JsonNode typeNode = node.get(FIELD_TYPE);
        JsonNode valueNode = node.get(FIELD_VALUE);
        JsonNode rangeNode = node.get(FIELD_RANGE);
        JsonNode invertedNode = node.get(FIELD_INVERTED);
        JsonNode likeNode = node.get(FIELD_LIKE);
        JsonNode startWithNode = node.get(FIELD_START_WITH);
        JsonNode fuzzyNode = node.get(FIELD_FUZZY);
        JsonNode morphologicalNode = node.get(FIELD_MORPHOLOGICAL);

        if (nameNode == null) {
            throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() + ": Name node is required.");
        }
        if (typeNode == null) {
            throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() + ": Type node is required.");
        }
        if (valueNode != null && rangeNode != null) {
            throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() + ": Both Value node AND Range node specified. Only one of them can be used.");
        }

        SimpleDataType type = SimpleDataType.fromValue(typeNode.asText());
        String field = nameNode.asText();
        boolean inverted = invertedNode == null ? false : invertedNode.asBoolean(false);
        boolean like = likeNode == null ? false : likeNode.asBoolean(false);
        boolean startWith = startWithNode == null ? false : startWithNode.asBoolean(false);
        boolean fuzzy = fuzzyNode == null ? false : fuzzyNode.asBoolean(false);
        boolean morphological = morphologicalNode == null ? false : morphologicalNode.asBoolean(false);
        if ((type != SimpleDataType.STRING && type != SimpleDataType.CLOB && type != SimpleDataType.BLOB) || rangeNode != null) {
            like = false;
            startWith = false;
            morphological = false;
        }

        if(startWith && like) {
            throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() + ": form can not be like and start with in the same time");
        }

        if (fuzzy) {
            return new SearchFormFieldRO(type, field, extractValue(type, valueNode, ctx), inverted, like, startWith, fuzzy, morphological);
        } else if (valueNode != null) {
            return new SearchFormFieldRO(type, field, extractValue(type, valueNode, ctx), inverted, like, startWith, morphological);
        } else {
            return new SearchFormFieldRO(type, field, extractRange(type, rangeNode, ctx), inverted, like, startWith);
        }
    }

    /**
     * Extracts value node value.
     * @param type the value type
     * @param valueNode the value node
     * @param ctx
     * @return
     */
    private Object extractValue(SimpleDataType type, JsonNode valueNode, DeserializationContext ctx)
        throws JsonMappingException {

        if (valueNode.isNull()) {
            return null;
        }

        switch (type) {
            case BOOLEAN:
                return valueNode.asBoolean();
            case DATE:
            case TIMESTAMP:
                    return ValidityPeriodUtils.parse(valueNode.asText());

            case TIME:
                try {
                    return DateFormatUtils.ISO_TIME_FORMAT.parse(valueNode.asText());
                } catch (Exception e) {
                    LOGGER.warn("Failed to parse time value. Caught exception.", e);
                    return null;
                }
            case INTEGER:
                return valueNode.asLong();
            case NUMBER:
                return valueNode.asDouble();
            case STRING:
            case BLOB:
            case CLOB:
                return valueNode.asText();
            default:
                throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() + ": Types other then simple types ["
                        + type.name()
                        + "] aren't supported.");
        }
    }

    /**
     * Extracts node value.
     * @param type the value type
     * @param rangeNode range node
     * @param ctx the deserialization context
     * @return pair
     * @throws JsonMappingException
     */
    private Pair<Object, Object> extractRange(SimpleDataType type, JsonNode rangeNode, DeserializationContext ctx)
            throws JsonMappingException {

        if (rangeNode.isArray()) {
            Object right = null;
            Object left = null;
            int count = 0;
            for(Iterator<JsonNode> ei = rangeNode.elements(); ei.hasNext(); ) {
                if (++count > 2) {
                    throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName()
                            + ": More then two range values supplied.");
                }

                JsonNode valueNode = ei.next();
                if (count == 1) {
                    left = valueNode == null ? null : extractValue(type, valueNode, ctx);
                } else {
                    right = valueNode == null ? null : extractValue(type, valueNode, ctx);
                }
            }

            return new ImmutablePair<Object, Object>(left, right);
        }

        return null;
    }
}
