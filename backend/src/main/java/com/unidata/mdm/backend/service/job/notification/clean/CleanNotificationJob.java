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
