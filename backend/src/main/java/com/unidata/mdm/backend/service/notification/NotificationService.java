package com.unidata.mdm.backend.service.notification;

import java.util.Collection;

import javax.annotation.Nonnull;

/**
 * Responsible for all kind of notifications in system.
 */
public interface NotificationService {

    /**
     * Notify external users about actions in system.
     *
     * @param notifications collection which contains notification
     */
    void notify(@Nonnull Collection<Notification<?>> notifications);

    /**
     * Notify external users about actions in system.
     *
     * @param notification - notification for send
     */
    void notify(@Nonnull Notification<?> notification);

}
