package com.unidata.mdm.backend.service.notification;

import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

public interface NotificationHandler extends AfterContextRefresh {

    /**
     *
     * @param msgId
     * @throws InterruptedException
     */
    void handleNotification(long msgId) throws InterruptedException;

    /**
     *
     * @param msgId
     * @throws InterruptedException
     */
    void asyncHandleNotification(long msgId) throws InterruptedException;

}