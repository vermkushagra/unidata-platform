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

package com.unidata.mdm.backend.service.notification;

import com.unidata.mdm.backend.service.notification.configs.NotificationConfig;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.service.notification.messages.NotificationMessage;

public class Notification<M extends NotificationMessage> {

    /**
     * All necessary info required for creation right notification send
     */
    @Nullable
    private final NotificationConfig notificationConfig;

    /**
     * Message which contain info for sending and message type.
     */
    @Nonnull
    private final M notificationMessage;

    public Notification(@Nullable NotificationConfig notificationConfig, @Nonnull M notificationMessage) {
        this.notificationConfig = notificationConfig;
        this.notificationMessage = notificationMessage;
    }

    /**
     * @return notification info
     */
    @Nullable
    public NotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    /**
     * @return Text required for notification
     */
    @Nonnull
    public M getNotificationMessage() {
        return notificationMessage;
    }
}
