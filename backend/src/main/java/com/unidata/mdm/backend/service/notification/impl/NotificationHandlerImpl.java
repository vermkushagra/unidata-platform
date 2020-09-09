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

package com.unidata.mdm.backend.service.notification.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.unidata.mdm.backend.dao.MessageDao;
import com.unidata.mdm.backend.po.MessagePO;
import com.unidata.mdm.backend.service.notification.NotificationHandler;
import com.unidata.mdm.backend.service.notification.NotificationUtils;
import com.unidata.mdm.backend.service.notification.configs.NotificationConfig;
import com.unidata.mdm.backend.service.notification.messages.NotificationCacheMessage;
import com.unidata.mdm.backend.service.notification.messages.UnidataMessage;
import com.unidata.mdm.backend.service.notification.notifiers.Notifier;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
@Component
public class NotificationHandlerImpl implements NotificationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationHandlerImpl.class);

    //private static final int TRY_LOCK_AWAIT_TIMEOUT = 10;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    private List<Notifier> notifiers = new ArrayList<>();

    @Override
    public void afterContextRefresh() {
        Map<String, Notifier> notifiers = applicationContext.getBeansOfType(Notifier.class);

        this.notifiers.addAll(notifiers.values());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.notification.INotificationHandler#handleNotification(long)
     */
    @Override
    public void handleNotification(long msgId) throws InterruptedException {
        // Note, that inner call for asyncHandleNotification() don't use thread pool for async call.
        asyncHandleNotification(msgId);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.notification.INotificationHandler#asyncHandleNotification(long)
     */
    @Override
    @Async("notificationHandlerExecutor")
    public void asyncHandleNotification(long msgId) throws InterruptedException {

        /*
        ILock lock = hazelcastInstance.getLock("notification_" + msgId);
        if (lock.tryLock(TRY_LOCK_AWAIT_TIMEOUT, TimeUnit.SECONDS)) {
          try {
        */
              processMessage(msgId);
        /*
          } finally {
            lock.unlock();
          }
        } else {
          LOGGER.info("Failed to get lock for notification: {}", msgId);
        }
        */
    }

    /**
     *
     * @param msgId
     */
    private void processMessage(long msgId) {
        // Load undelivered message by ID.
        MessagePO msg = messageDao.findUndeliveredMessage(msgId);

        if (msg != null && !msg.isDelivered()) {
            try {
                LOGGER.debug("Deliver message: {}", msgId);

                NotificationCacheMessage cacheMsg = NotificationUtils
                    .unmarshalNotificationCacheMessage(msg.getMessage());

                UnidataMessage notificationMsg = new UnidataMessage(NotificationUtils
                    .unmarshalUnidataMessageDef(cacheMsg.getMessageBody()));

                final NotificationConfig config = cacheMsg.getConfig();

                config.addUserHeader("MESSAGE_ID", msgId);

                // Send directly to receiver (Camel routes/endpoints)
                notifiers.forEach(notifier -> {
                    notifier.notify(notificationMsg, config);
                });
            } catch (RuntimeException | IOException | JAXBException e) {
                LOGGER.error("Failed to handle notification message [msgId=" + msg.getId() + ']', e);
                messageDao.incrementFailedSendMessage(msgId);
            }
        } else {
            LOGGER.error("Failed to find message in database to deliver: {}", msgId);
        }
    }
}
