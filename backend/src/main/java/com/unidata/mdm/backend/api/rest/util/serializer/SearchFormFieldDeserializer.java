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
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldRO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
     * Time stamp pattern (INPUT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";)
     */
    private static final FastDateFormat DEFAULT_TIMESTAMP_NO_OFFSET
            = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS");
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
     * Search type.
     */
    private static final String FIELD_SEARCH_TYPE= "searchType";

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
        JsonNode searchTypeNode = node.get(FIELD_SEARCH_TYPE);

        if (nameNode == null) {
            throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() + ": Name node is required.");
        }

        if (typeNode == null) {
            throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() + ": Type node is required.");
        }

        if (valueNode != null && rangeNode != null) {
            throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() + ": Both Value node AND Range node specified. Only one of them can be used.");
        }


        SimpleDataType type = typeNode != null ? SimpleDataType.fromValue(typeNode.asText()) : null;
        String field = nameNode.asText();

        boolean inverted = invertedNode != null && invertedNode.asBoolean(false);

        if ((valueNode == null || valueNode.isNull()) && (rangeNode == null || rangeNode.isNull())) {
            inverted = !inverted;
        }

        if(searchTypeNode != null && !searchTypeNode.isNull()) {
            SearchFormFieldRO.SearchTypeRO searchType =  SearchFormFieldRO.SearchTypeRO.valueOf(searchTypeNode.asText());
            if (valueNode != null) {
                return new SearchFormFieldRO(type, field, extractValue(type, valueNode, ctx), inverted, searchType);
            } else {
                return new SearchFormFieldRO(type, field, extractRange(type, rangeNode, ctx), inverted, searchType);
            }
        }

        boolean like = likeNode != null && likeNode.asBoolean(false);
        boolean startWith = startWithNode != null && startWithNode.asBoolean(false);
        boolean fuzzy = fuzzyNode != null && fuzzyNode.asBoolean(false);
        boolean morphological = morphologicalNode != null && morphologicalNode.asBoolean(false);
        if ((type != SimpleDataType.STRING && type != SimpleDataType.CLOB && type != SimpleDataType.BLOB) || rangeNode != null) {
            like = false;
            startWith = false;
            morphological = false;
        }

        if(startWith && like) {
            throw new JsonMappingException(SearchFormFieldRO.class.getSimpleName() + ": form can not be like and start with in the same time");
        }
        SearchFormFieldRO result;


        if (valueNode != null) {
            result = new SearchFormFieldRO(type, field, extractValue(type, valueNode, ctx), inverted, null);
        } else {
            result = new SearchFormFieldRO(type, field, extractRange(type, rangeNode, ctx), inverted, null);
        }

        if(fuzzy){
            result.setSearchTypeRO(SearchFormFieldRO.SearchTypeRO.FUZZY);
        }

        if(morphological){
            result.setSearchTypeRO(SearchFormFieldRO.SearchTypeRO.MORPHOLOGICAL);
        }

        if(startWith){
            result.setSearchTypeRO(SearchFormFieldRO.SearchTypeRO.START_WITH);
        }

        if(result.getSingle() == null && result.getRange() == null){
            result.setSearchTypeRO(SearchFormFieldRO.SearchTypeRO.EXIST);
        }

        if(like){
            result.setSearchTypeRO(SearchFormFieldRO.SearchTypeRO.LIKE);
        }

        return result;
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
                try {
                    return StringUtils.isNotBlank(valueNode.asText()) ? DEFAULT_TIMESTAMP_NO_OFFSET.parse(valueNode.asText()): null;
                } catch (ParseException e) {
                    throw new SystemRuntimeException("Incorrect date format found, unable to parse date string!",
                            ExceptionId.EX_DATA_CANNOT_PARSE_DATE, valueNode.asText());
                }
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

            return new ImmutablePair<>(left, right);
        }

        return null;
    }
}
