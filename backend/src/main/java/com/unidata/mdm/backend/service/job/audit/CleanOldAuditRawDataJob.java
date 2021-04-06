package com.unidata.mdm.backend.service.job.audit;

import com.unidata.mdm.backend.dao.AuditDao;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Quartz job for clean old audit data events in database.
 *
 * @author amagdenko
 */
public class CleanOldAuditRawDataJob  extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanOldAuditRawDataJob.class);

    @Autowired
    private AuditDao auditDao;

    private void execute(boolean disableJob, Long lifetimeInMinutes) {
        if (!disableJob) {
            LOGGER.info("Started process clean old audit raw data.");

            long count = auditDao.deleteOldAuditEvents(lifetimeInMinutes);

            LOGGER.info(String.format("Finished process clean old audit raw data. Totally remove :%s", count));
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
