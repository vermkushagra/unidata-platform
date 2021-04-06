package com.unidata.mdm.backend.service.job.reports;

import javax.annotation.Nonnull;

import org.springframework.batch.core.JobExecution;

/**
 * Create report about results of matching job
 */
public class SimpleNotificationJobListener extends NotificationGenerator {
    @Nonnull
    @Override
    protected String getAdditionMessage(JobExecution jobExecution) {
        return "";
    }
}
