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

package com.unidata.mdm.backend.service.notification.configs;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.notification.ProcessedAction;

public class NotificationConfig {

    /**
     * (De)serializer field "processedAction".
     */
    public static final String FIELD_NAME_PROCESSED_ACTION = "processedAction";
    /**
     * (De)serializer field "recordKeys".
     */
    public static final String FIELD_NAME_RECORD_KEYS =  "recordKeys";
    /**
     * (De)serializer field "etalonKey".
     */
    public static final String FIELD_NAME_ETALON_KEY =  "etalonKey";
    /**
     * (De)serializer field "id".
     */
    public static final String FIELD_NAME_ID = "id";
    /**
     * (De)serializer field "originKey".
     */
    public static final String FIELD_NAME_ORIGIN_KEY = "originKey";
    /**
     * (De)serializer field "sourceSystem".
     */
    public static final String FIELD_NAME_SOURCE_SYSTEM = "sourceSystem";
    /**
     * (De)serializer field "externalId".
     */
    public static final String FIELD_NAME_EXTERNAL_ID = "externalId";
    /**
     * (De)serializer field "entityName".
     */
    public static final String FIELD_NAME_ENTITY_NAME = "entityName";
    /**
     * (De)serializer field "etalonStatus".
     */
    public static final String FIELD_NAME_ETALON_STATUS = "etalonStatus";
    /**
     * (De)serializer field "etalonState".
     */
    public static final String FIELD_NAME_ETALON_STATE = "etalonState";
    /**
     * (De)serializer field "originStatus".
     */
    public static final String FIELD_NAME_ORIGIN_STATUS = "originStatus";
    /**
     * (De)serializer field "userHeaders".
     */
    public static final String FIELD_NAME_USER_HEADERS = "userHeaders";
    /**
     * The record keys.
     */
    @Nonnull
    final RecordKeys recordKeys;
    /**
     * Action which generate notification
     */
    @Nonnull
    private final ProcessedAction processedAction;
    /**
     * Custom user headers
     */
    private final Map<String, Object> userHeaders = new HashMap<>();

    public NotificationConfig(@Nonnull ProcessedAction processedAction, @Nonnull RecordKeys recordKeys) {
        this.processedAction = processedAction;
        this.recordKeys = recordKeys;
    }

    public void addUserHeader(String key, Object value) {
        this.userHeaders.put(key, value);
    }

    public void addAllUserHeaders(Map<String, Object> userHeaders) {
        this.userHeaders.putAll(userHeaders);
    }

    public Map<String, Object> getUserHeaders() {
        return userHeaders;
    }

    @Nonnull
    public ProcessedAction getProcessedAction() {
        return processedAction;
    }

    @Nonnull
    public RecordKeys getRecordKeys() {
        return recordKeys;
    }
}
