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

/**
 *
 */

package com.unidata.mdm.backend.service.notification.configs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RecordKeys.RecordKeysBuilder;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.notification.ProcessedAction;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class NotificationConfigDeserializer extends JsonDeserializer<NotificationConfig> {
    @Override
    public NotificationConfig deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        JsonNode node = p.getCodec().readTree(p);

        ProcessedAction processedAction
            = ProcessedAction.valueOf(node.get(NotificationConfig.FIELD_NAME_PROCESSED_ACTION).asText());

        RecordKeys recordKeys = null;
        JsonNode keysNode = node.get(NotificationConfig.FIELD_NAME_RECORD_KEYS);
        if (Objects.nonNull(keysNode)) {

            RecordKeysBuilder kb = RecordKeys.builder();
            Iterator<Entry<String, JsonNode>> ki = keysNode.fields();
            while (ki.hasNext()) {

                Entry<String, JsonNode> entry = ki.next();
                if (NotificationConfig.FIELD_NAME_ETALON_KEY.equals(entry.getKey())) {
                    kb.etalonKey(EtalonKey.builder()
                        .id(entry.getValue().get(NotificationConfig.FIELD_NAME_ID).asText())
                        .build());
                } else if (NotificationConfig.FIELD_NAME_ORIGIN_KEY.equals(entry.getKey())) {
                    kb.originKey(OriginKey.builder()
                        .id(entry.getValue().get(NotificationConfig.FIELD_NAME_ID).asText())
                        .entityName(entry.getValue().get(NotificationConfig.FIELD_NAME_ENTITY_NAME).asText())
                        .externalId(entry.getValue().get(NotificationConfig.FIELD_NAME_EXTERNAL_ID).asText())
                        .sourceSystem(entry.getValue().get(NotificationConfig.FIELD_NAME_SOURCE_SYSTEM).asText())
                        .build());
                } else if (NotificationConfig.FIELD_NAME_ENTITY_NAME.equals(entry.getKey())) {
                    String val = entry.getValue().asText();
                    if (StringUtils.hasText(val) && !"null".equals(val)) {
                        kb.entityName(val);
                    }
                } else if (NotificationConfig.FIELD_NAME_ETALON_STATUS.equals(entry.getKey())) {
                    String val = entry.getValue().asText();
                    if (StringUtils.hasText(val) && !"null".equals(val)) {
                        kb.etalonStatus(RecordStatus.fromValue(val));
                    }
                } else if (NotificationConfig.FIELD_NAME_ETALON_STATE.equals(entry.getKey())) {
                    String val = entry.getValue().asText();
                    if (StringUtils.hasText(val) && !"null".equals(val)) {
                        kb.etalonState(ApprovalState.fromValue(val));
                    }
                } else if (NotificationConfig.FIELD_NAME_ORIGIN_STATUS.equals(entry.getKey())) {
                    String val = entry.getValue().asText();
                    if (StringUtils.hasText(val) && !"null".equals(val)) {
                        kb.originStatus(RecordStatus.fromValue(val));
                    }
                }
            }

            recordKeys = kb.build();
        }

        NotificationConfig config = new NotificationConfig(processedAction, recordKeys);

        JsonNode userHeadersNode = node.get(NotificationConfig.FIELD_NAME_USER_HEADERS);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = p.getCodec().treeToValue(userHeadersNode, HashMap.class);

        config.addAllUserHeaders(map);

        return config;
    }
}
