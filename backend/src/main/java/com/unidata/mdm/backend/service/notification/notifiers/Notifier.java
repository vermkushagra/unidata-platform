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
