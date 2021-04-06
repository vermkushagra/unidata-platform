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
