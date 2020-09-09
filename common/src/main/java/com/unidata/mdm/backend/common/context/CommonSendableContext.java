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

package com.unidata.mdm.backend.common.context;

/**
 * Common sendable context for modifying operations.
 * @author Mikhail Mikhailov
 *
 */
public abstract class CommonSendableContext
    extends CommonRequestContext
    implements NotificationSendableContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 7972675023238757045L;
    /**
     * Sendable destination support
     */
    private String notificationDestination;
    /**
     * Sendable destination support
     */
    private String notificationId;
    /**
     * Constructor.
     */
    protected CommonSendableContext() {
        super();
        flags.set(ContextUtils.CTX_FLAG_SEND_NOTIFICATION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendNotification() {
        return flags.get(ContextUtils.CTX_FLAG_SEND_NOTIFICATION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getNotificationDestination() {
        return notificationDestination;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * Mark context as not sendable
     */
    public void skipNotification() {
        flags.clear(ContextUtils.CTX_FLAG_SEND_NOTIFICATION);
    }

    /**
     * Marks context as sendble and sets destination and notification id.
     * @param notificationDestination
     * @param notificationId
     */
    public void sendNotification(String notificationDestination, String notificationId) {
        flags.set(ContextUtils.CTX_FLAG_SEND_NOTIFICATION);
        this.notificationDestination = notificationDestination;
        this.notificationId = notificationId;
    }

    /**
     * Method copies behavior of argument context.
     * @param notificationSendableContext
     */
    public void repeatNotificationBehavior(NotificationSendableContext notificationSendableContext){
        flags.set(ContextUtils.CTX_FLAG_SEND_NOTIFICATION, notificationSendableContext.sendNotification());
        this.notificationDestination = notificationSendableContext.getNotificationDestination();
        this.notificationId = notificationSendableContext.getNotificationId();
    }
}
