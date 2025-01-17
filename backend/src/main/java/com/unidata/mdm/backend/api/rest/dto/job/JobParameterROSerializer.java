package com.unidata.mdm.backend.api.rest.dto.job;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * @author Denis Kostovarov
 */
class JobParameterROSerializer extends JsonSerializer<JobParameterRO> {

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_VALUE = "value";

    @Override
    public void serialize(final JobParameterRO value, final JsonGenerator generator, final SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        if (value.getId() != null) {
            generator.writeNumberField(FIELD_ID, value.getId());
        }
        generator.writeStringField(FIELD_NAME, value.getName());
        if (value.getValueSze() > 1) {
            generator.writeFieldName(FIELD_VALUE);
            generator.writeStartArray();
            switch (value.getType()) {
                case STRING:
                    for (final String s : (String[]) value.getArrayValue()) {
                        generator.writeString(s);
                    }
                    break;
                case DATE:
                    for (final ZonedDateTime dt : (ZonedDateTime[]) value.getArrayValue()) {
                        generator.writeString(dt.truncatedTo(ChronoUnit.SECONDS)
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    }
                    break;
                case LONG:
                    for (final Long l : (Long[]) value.getArrayValue()) {
                        generator.writeNumber(l);
                    }
                    break;
                case DOUBLE:
                    for (final Double d : (Double[]) value.getArrayValue()) {
                        generator.writeNumber(d);
                    }
                    break;
                case BOOLEAN:
                    for (final Boolean b : (Boolean[]) value.getArrayValue()) {
                        generator.writeBoolean(b);
                    }
                    break;
            }
            generator.writeEndArray();
        } else {
            switch (value.getType()) {
                case STRING:
                    generator.writeStringField(FIELD_VALUE, value.getStringValue());
                    break;
                case DATE:
                    generator.writeStringField(FIELD_VALUE, value.getDateValue().truncatedTo(ChronoUnit.SECONDS)
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    break;
                case LONG:
                    generator.writeNumberField(FIELD_VALUE, value.getLongValue());
                    break;
                case DOUBLE:
                    generator.writeNumberField(FIELD_VALUE, value.getDoubleValue());
                    break;
                case BOOLEAN:
                    generator.writeBooleanField(FIELD_VALUE, value.getBooleanValue());
                    break;
            }
        }
        generator.writeObjectField(FIELD_TYPE, value.getType());
        generator.writeEndObject();
    }
}
