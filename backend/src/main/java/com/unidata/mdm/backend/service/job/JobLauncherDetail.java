/**
 * Date: 21.03.2016
 */

package com.unidata.mdm.backend.service.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class JobLauncherDetail extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(JobLauncherDetail.class);

    @Autowired
    private JobServiceExt jobServiceExt;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Process @Autowired injection for the given target object, based on the current web application context.
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        execute(Long.parseLong((String)context.getMergedJobDataMap().get("jobId")));
    }

    public void execute(long jobId) {
        log.info("Execute job [jobId=" + jobId + ", jobService=" + jobServiceExt + ']');

        jobServiceExt.start(jobId);
    }
}
