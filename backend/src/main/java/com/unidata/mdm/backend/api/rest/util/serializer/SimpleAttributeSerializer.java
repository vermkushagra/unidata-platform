package com.unidata.mdm.backend.api.rest.util.serializer;

import java.io.IOException;
import java.util.Date;

import com.unidata.mdm.backend.api.rest.dto.data.extended.ExtendedSimpleAttributeRO;
import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;

public class SimpleAttributeSerializer extends JsonSerializer<SimpleAttributeRO> {

    private static final FastDateFormat DEFAULT_TIMESTAMP
            = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public void serialize(SimpleAttributeRO value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("name", value.getName());
        jgen.writeStringField("type", value.getType()==null?null:value.getType().value());
        jgen.writeStringField("displayValue", value.getDisplayValue());
        jgen.writeStringField("targetEtalonId", value.getTargetEtalonId());
        jgen.writeStringField("valueId", value.getValueId());
        jgen.writeStringField("unitId", value.getUnitId());

        if(value instanceof ExtendedSimpleAttributeRO){
            jgen.writeBooleanField("winner", ((ExtendedSimpleAttributeRO) value).isWinner());
        }

        switch (value.getType()) {
            case DATE:
            case TIMESTAMP:
            case TIME:
                if (value.getValue() == null) {
                    jgen.writeNullField("value");
                } else {
                    jgen.writeStringField("value", DEFAULT_TIMESTAMP.format((Date) value.getValue()));
                }
                break;
            default:
                jgen.writeObjectField("value", value.getValue());
        }

        jgen.writeEndObject();
    }
}
