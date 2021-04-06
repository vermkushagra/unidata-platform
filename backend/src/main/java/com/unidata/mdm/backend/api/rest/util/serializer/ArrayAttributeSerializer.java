package com.unidata.mdm.backend.api.rest.util.serializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;

import com.unidata.mdm.backend.api.rest.dto.data.extended.ExtendedArrayAttributeRO;
import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unidata.mdm.backend.api.rest.dto.data.ArrayAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.ArrayObjectRO;
import com.unidata.mdm.backend.common.ConvertUtils;

public class ArrayAttributeSerializer extends JsonSerializer<ArrayAttributeRO> {

    private static final FastDateFormat DEFAULT_TIMESTAMP
            = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public void serialize(ArrayAttributeRO value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField("name", value.getName());
        jgen.writeStringField("type", value.getType().value());
        if(value instanceof ExtendedArrayAttributeRO){
            jgen.writeBooleanField("winner", ((ExtendedArrayAttributeRO) value).isWinner());
        }
        jgen.writeArrayFieldStart("value");



        if (Objects.nonNull(value.getValue())) {
            for (ArrayObjectRO val : value.getValue()) {
                jgen.writeStartObject();

                switch (value.getType()) {
                    case DATE:
                        Date dt = ConvertUtils.localDate2Date((LocalDate) val.getValue());
                        jgen.writeStringField("value", DEFAULT_TIMESTAMP.format(dt));
                        break;
                    case TIMESTAMP:
                        Date ts = ConvertUtils.localDateTime2Date((LocalDateTime) val.getValue());
                        jgen.writeStringField("value", DEFAULT_TIMESTAMP.format(ts));
                        break;
                    case TIME:
                        Date tm = ConvertUtils.localTime2Date((LocalTime) val.getValue());
                        jgen.writeStringField("value", DEFAULT_TIMESTAMP.format(tm));
                        break;
                    default:
                        jgen.writeObjectField("value", val.getValue());
                }

                jgen.writeStringField("displayValue", val.getDisplayValue());
                jgen.writeStringField("targetEtalonId", val.getTargetEtalonId());
                jgen.writeEndObject();
            }
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}
