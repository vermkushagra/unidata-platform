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

package com.unidata.mdm.backend.service.notification.notifiers.jms;

import com.unidata.mdm.backend.dao.MessageDao;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handler called only if message delivered to broker.
 * Completion handler should exist per route:
 * <pre>
 * {@code
 *  <onCompletion onCompleteOnly="true">
 *      <process ref="notificationCompletionHandler"/>
 *  </onCompletion>
 * }
 * </pre>
 * @author Denis Kostovarov
 */
public class NotificationCompletionHandler implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationCompletionHandler.class);

    @Autowired
    private MessageDao messageDao;

    @Override
    public void process(Exchange exchange) throws Exception {
        final Message msg = exchange.getIn();
        final Long msgId = msg.getHeader("MESSAGE_ID", Long.class);
        if (msgId != null) {
            LOGGER.debug("Notification delivered: [{}]", msgId);

            messageDao.deliverMessage(msgId);
        } else {
            LOGGER.error("No MESSAGE_ID supplied for notification message " + msg);
        }
    }
}
