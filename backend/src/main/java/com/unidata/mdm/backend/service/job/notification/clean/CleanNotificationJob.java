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

package com.unidata.mdm.backend.service.job.notification.clean;

import com.unidata.mdm.backend.dao.MessageDao;
import com.unidata.mdm.backend.po.MessagePO;
import com.unidata.mdm.backend.po.MessagePO.MessageType;
import com.unidata.mdm.backend.service.notification.NotificationHandler;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.List;

/**
 * Quartz job for clean old delivered messages
 *
 * @author Dmitry Kopin on 06.04.2017
 */
@DisallowConcurrentExecution
public class CleanNotificationJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanNotificationJob.class);

    @Autowired
    private MessageDao messageDao;

    private void execute(boolean disableJob, Long lifetimeInMinutes) {
        if (!disableJob) {
            LOGGER.info("Started process clean notification messages");

            long count = messageDao.cleanOldMessages(MessageType.NOTIFICATION, lifetimeInMinutes);

            LOGGER.info(String.format("Finished process clean notification messages. Totally remove :%s", count));
        }
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Process @Autowired injection for the given target object, based on the current web application context.
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        JobDataMap jobDataMap = context.getMergedJobDataMap();

        execute(Boolean.parseBoolean((String)jobDataMap.get("disableJob")),
            Long.parseLong((String)jobDataMap.get("lifetimeInMinutes")));
    }
}