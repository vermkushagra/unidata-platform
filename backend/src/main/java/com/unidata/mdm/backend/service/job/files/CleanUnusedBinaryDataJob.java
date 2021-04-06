package com.unidata.mdm.backend.service.job.files;

import com.unidata.mdm.backend.dao.LargeObjectsDao;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Quartz job for clean unused clob/blob data
 *
 * @author Dmitry Kopin on 10.04.2017
 */
@DisallowConcurrentExecution
public class CleanUnusedBinaryDataJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanUnusedBinaryDataJob.class);

    @Autowired
    private LargeObjectsDao largeObjectsDao;

    private void execute(boolean disableJob, Long lifetimeInMinutes) {
        if (!disableJob) {
            LOGGER.info("Started process clean unused binary data");

            long count = largeObjectsDao.cleanUnusedBinaryData(lifetimeInMinutes);

            LOGGER.info(String.format("Finished process clean unused binary. Totally remove :%s", count));
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
