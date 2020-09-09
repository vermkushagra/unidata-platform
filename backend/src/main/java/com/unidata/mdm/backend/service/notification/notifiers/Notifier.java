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

package com.unidata.mdm.backend.service.notification.notifiers;

import com.unidata.mdm.backend.service.notification.configs.NotificationConfig;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.service.notification.messages.NotificationMessage;

/**
 * General interface which responsible for notify external systems and users about system actions.
 *
 * @param <T> config type
 * @param <M> message type
 */
public interface Notifier<M extends NotificationMessage> {
    /**
     * @param notificationMessage message for send
     * @param notificationConfig  config which contain info about routing, delay and etc
     */
    void notify(@Nonnull M notificationMessage, @Nullable NotificationConfig notificationConfig);
}
