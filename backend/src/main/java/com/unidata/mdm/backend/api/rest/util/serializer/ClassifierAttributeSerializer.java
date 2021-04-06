package com.unidata.mdm.backend.api.rest.util.serializer;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeAttrRO;

/**
 * Serializer for {@link ClassifierAttr}
 */
public class ClassifierAttributeSerializer extends JsonSerializer<ClsfNodeAttrRO> {

    /**
     *
     */
    private static final FastDateFormat DEFAULT_TIMESTAMP_NO_MS = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * @param value
     * @param jgen
     * @param serializers
     * @throws IOException
     */
    @Override
    public void serialize(ClsfNodeAttrRO value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
        jgen.writeStartObject();
        serializers.defaultSerializeField("simpleDataType", value.getSimpleDataType().value(), jgen);
        serializers.defaultSerializeField("name", value.getName(), jgen);
        serializers.defaultSerializeField("description", value.getDescription(), jgen);
        serializers.defaultSerializeField("displayName", value.getDisplayName(), jgen);
        serializers.defaultSerializeField("hidden", value.isHidden(), jgen);
        serializers.defaultSerializeField("readOnly", value.isReadOnly(), jgen);
        serializers.defaultSerializeField("searchable", value.isSearchable(), jgen);
        serializers.defaultSerializeField("unique", value.isUnique(), jgen);
        serializers.defaultSerializeField("nullable", value.isNullable(), jgen);
        switch (value.getSimpleDataType()) {
            case DATE:
            case TIMESTAMP:
            case TIME:
                if (value.getValue() == null) {
                    jgen.writeNullField("value");
                } else {
                    jgen.writeStringField("value", DEFAULT_TIMESTAMP_NO_MS.format((Date) value.getValue()));
                }
                break;
            default:
                jgen.writeObjectField("value", value.getValue());
        }

        jgen.writeEndObject();
    }
}
