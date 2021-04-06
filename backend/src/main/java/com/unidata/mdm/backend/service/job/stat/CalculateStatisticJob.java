package com.unidata.mdm.backend.service.job.stat;

import com.unidata.mdm.backend.service.statistic.StatServiceExt;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Date;

/**
 * @author Dmitry Kopin on 13.06.2017.
 * Calculate statistic for period from previous fire time to current
 */
public class CalculateStatisticJob  extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateStatisticJob.class);

    /** Statistic service */
    @Autowired
    private StatServiceExt statService;

    private void execute(boolean disableJob, Date fromDate, Date toDate) {

        if (!disableJob) {
            LOGGER.info("Started process calculate statistic");

            statService.persistStatistic(fromDate, toDate);
        }
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Process @Autowired injection for the given target object, based on the current web application context.
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        JobDataMap jobDataMap = context.getMergedJobDataMap();
        execute(Boolean.parseBoolean((String)jobDataMap.get("disableJob")), context.getPreviousFireTime(), context.getFireTime());
    }
}