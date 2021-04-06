package com.unidata.mdm.backend.api.rest.util.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeAttrRO;

/**
 * Deserializer for {@link ClassifierAttr}
 */
public class ClassifierAttributeDeserializer extends JsonDeserializer<ClsfNodeAttrRO> {

    /**
     * @param jsonParser - json parser.
     * @param ctxt       - ctx
     * @return - demoralized classifier attr
     * @throws IOException
     */
    @Override
    public ClsfNodeAttrRO deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
    	ClsfNodeAttrRO attr = new ClsfNodeAttrRO();
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        attr.setDescription(node.get("description").asText(""));
        attr.setDisplayName(node.get("displayName").asText(""));
        attr.setHidden(node.get("hidden").asBoolean(false));
        attr.setReadOnly(node.get("readOnly").asBoolean(false));
        attr.setSearchable(node.get("searchable").asBoolean(false));
        attr.setNullable(node.get("nullable").asBoolean(false));
        attr.setName(node.get("name").asText());
        attr.setUnique(false);
        JsonNode valueNode = node.get("value");
        JsonNode typeNode = node.get("simpleDataType");
        SimpleDataType type = SimpleDataType.fromValue(typeNode.asText());
        attr.setSimpleDataType(type);
        if (type == null) {
            throw new RuntimeException();
        }

        if (valueNode == null || valueNode.isNull()) {
            attr.setValue(null);
            return attr;
        }
        String value = valueNode.asText();
        attr.setValue(value);
        return attr;
    }
}
