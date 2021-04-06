/**
 *
 */

package com.unidata.mdm.backend.service.notification.configs;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class NotificationConfigSerializer extends JsonSerializer<NotificationConfig> {
    @Override
    public void serialize(NotificationConfig value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {

        // START
        gen.writeStartObject();

        // processedAction
        gen.writeStringField(NotificationConfig.FIELD_NAME_PROCESSED_ACTION, value.getProcessedAction().name());

        // recordKeys START
        gen.writeObjectFieldStart(NotificationConfig.FIELD_NAME_RECORD_KEYS);

        // etalonKey START
        gen.writeObjectFieldStart(NotificationConfig.FIELD_NAME_ETALON_KEY);
        gen.writeStringField(NotificationConfig.FIELD_NAME_ID, value.getRecordKeys().getEtalonKey().getId());
        gen.writeEndObject();
        // etalonKey END

        // originKey START
        if (value.getRecordKeys().getOriginKey() != null) {
            gen.writeObjectFieldStart(NotificationConfig.FIELD_NAME_ORIGIN_KEY);
            gen.writeStringField(NotificationConfig.FIELD_NAME_ID, value.getRecordKeys().getOriginKey().getId());
            gen.writeStringField(NotificationConfig.FIELD_NAME_EXTERNAL_ID, value.getRecordKeys().getOriginKey().getExternalId());
            gen.writeStringField(NotificationConfig.FIELD_NAME_SOURCE_SYSTEM, value.getRecordKeys().getOriginKey().getSourceSystem());
            gen.writeStringField(NotificationConfig.FIELD_NAME_ENTITY_NAME, value.getRecordKeys().getOriginKey().getEntityName());
            gen.writeEndObject();
        }
        // originKey END

        // standalone fields START
        gen.writeStringField(NotificationConfig.FIELD_NAME_ENTITY_NAME, value.getRecordKeys().getEntityName());
        gen.writeObjectField(NotificationConfig.FIELD_NAME_ETALON_STATUS, value.getRecordKeys().getEtalonStatus());
        gen.writeObjectField(NotificationConfig.FIELD_NAME_ORIGIN_STATUS, value.getRecordKeys().getOriginStatus());
        gen.writeObjectField(NotificationConfig.FIELD_NAME_ETALON_STATE, value.getRecordKeys().getEtalonState());
        // standalone fields END

        // recordKeys END
        gen.writeEndObject();

        // userHeaders
        gen.writeObjectField(NotificationConfig.FIELD_NAME_USER_HEADERS, value.getUserHeaders());

        // END
        gen.writeEndObject();
    }
}
