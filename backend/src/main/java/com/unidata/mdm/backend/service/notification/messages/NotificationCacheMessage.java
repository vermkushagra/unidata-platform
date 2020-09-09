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

package com.unidata.mdm.backend.service.notification.messages;

import com.unidata.mdm.backend.service.notification.configs.NotificationConfig;
import java.io.Serializable;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class NotificationCacheMessage implements Serializable {
    private final static long serialVersionUID = 12345L;
    private NotificationConfig config;
    private String messageBody;

    public NotificationCacheMessage() {
        // No-op.
    }

    public NotificationCacheMessage(NotificationConfig config, String messageBody) {
        this.config = config;
        this.messageBody = messageBody;
    }

    public NotificationConfig getConfig() {
        return config;
    }

    public void setConfig(NotificationConfig config) {
        this.config = config;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
