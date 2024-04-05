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

package com.unidata.mdm.backend.service.notification.job;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.unidata.mdm.backend.dao.MessageDao;
import com.unidata.mdm.backend.po.MessagePO.MessageType;
import com.unidata.mdm.backend.service.notification.NotificationHandler;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
@DisallowConcurrentExecution
public class NotificationJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationJob.class);

    @Autowired
    private NotificationHandler notificationHandler;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private HazelcastInstance instance;

    private static final String NOTIFICATION_JOB_LOCK_NAME = "NOTIFICATION_JOB_LOCK";

    private void execute(boolean disableJob, int attemptCount) {

        if (disableJob) {
            return;
        }

        LOGGER.info("Started NotificationJob");

        ILock lock = instance.getLock(NOTIFICATION_JOB_LOCK_NAME);
        if (!lock.tryLock()) {
            LOGGER.info("Another instance is already active. Exiting.");
            return;
        } else {

            try {
                List<Long> ids = messageDao.loadNextUndeliveredBlock(0L, MessageType.NOTIFICATION, attemptCount, 500);
                while (CollectionUtils.isNotEmpty(ids)) {

                    for (int i = 0; i < ids.size(); i++) {
                        try {
                            notificationHandler.asyncHandleNotification(ids.get(i));
                        } catch (InterruptedException e) {
                            LOGGER.error("Failed to process notification message", e);
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }

                    ids = messageDao.loadNextUndeliveredBlock(ids.get(ids.size() - 1), MessageType.NOTIFICATION, attemptCount, 500);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // Process @Autowired injection for the given target object, based on the current web application context.
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        JobDataMap jobDataMap = context.getMergedJobDataMap();

        execute(
            Boolean.parseBoolean((String)jobDataMap.get("disableJob")),
            Integer.parseInt((String)jobDataMap.get("attemptCount")));
    }
}