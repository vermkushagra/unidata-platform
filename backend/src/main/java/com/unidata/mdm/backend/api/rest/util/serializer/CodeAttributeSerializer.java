package com.unidata.mdm.backend.api.rest.util.serializer;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unidata.mdm.backend.api.rest.dto.data.CodeAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.extended.ExtendedCodeAttributeRO;

public class CodeAttributeSerializer extends JsonSerializer<CodeAttributeRO> {

    @Override
    public void serialize(CodeAttributeRO value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField("name", value.getName());
        jgen.writeStringField("type", value.getType().value());

        if(value instanceof ExtendedCodeAttributeRO){
            jgen.writeBooleanField("winner", ((ExtendedCodeAttributeRO) value).isWinner());
        }

        jgen.writeFieldName("value");

        if (Objects.nonNull(value.getValue())) {
            jgen.writeObject(value.getValue());
        } else {
            jgen.writeNull();
        }

        jgen.writeArrayFieldStart("supplementary");

        if (Objects.nonNull(value.getSupplementary()) && !value.getSupplementary().isEmpty()) {
            for (Object val : value.getSupplementary()) {
                jgen.writeObject(val);
            }
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}
