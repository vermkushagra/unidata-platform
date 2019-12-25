/**
 * Date: 21.03.2016
 */

package org.unidata.mdm.core.service.impl;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.unidata.mdm.core.service.JobService;
import org.unidata.mdm.core.service.job.JobCommonParameters;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class JobLauncherDetail extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(JobLauncherDetail.class);

    @Autowired
    private JobService jobService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Process @Autowired injection for the given target object, based on the current web application context.
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        Object parentJobExecutionId = context.getMergedJobDataMap().get(JobCommonParameters.PARAM_PARENT_JOB_EXECUTION_ID);
        execute(Long.parseLong((String)context.getMergedJobDataMap().get("jobId")),
                parentJobExecutionId == null ? null : Long.parseLong((String)parentJobExecutionId));
    }

    public void execute(long jobId) {
        log.info("Execute job [jobId=" + jobId + ", jobService=" + jobService + ']');

        jobService.start(jobId);
    }

    private void execute(long jobId, Long parentJobExecutionId) {
        log.info("Execute job [jobId=" + jobId + ", jobService=" + jobService + ']');

        jobService.start(jobId, parentJobExecutionId);
    }
}
