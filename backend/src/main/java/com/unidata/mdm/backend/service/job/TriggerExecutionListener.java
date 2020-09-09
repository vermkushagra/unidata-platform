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

package com.unidata.mdm.backend.service.job;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.service.ServiceUtils;

/**
 * Listener for starting next jobs after finish current.
 */
@Component
public class TriggerExecutionListener implements JobExecutionListener {
    private static final int DEFAULT_TRIGGER_START_TIMEOUT = 10;

    private static final Logger log = LoggerFactory.getLogger(TriggerExecutionListener.class);

    private Integer triggerTimeout;

    @Autowired
    private JobServiceExt jobServiceExt;

    @Override
    public void beforeJob(final JobExecution jobExecution) {
        // no-op.
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        final JobParameters jobParameters = jobExecution.getJobParameters();
        final String nameParamString;
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            nameParamString = JobServiceExt.SUCCESS_FINISH_JOB_ID_PARAMETER;
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            nameParamString = JobServiceExt.FAIL_FINISH_JOB_ID_PARAMETER;
        } else {
            nameParamString = null;
            log.warn("Wrong status [" + jobExecution.getStatus() + "] for scheduling trigger jobs");
        }

        if (nameParamString != null) {
            int index = 0;
            Long jobId;
            final ZonedDateTime zdt = ZonedDateTime.now();
            final Map<String, JobParameter> params = jobParameters.getParameters();

            while (params.containsKey(nameParamString + index)) {
                jobId = jobParameters.getLong(nameParamString + index);
                log.info("Scheduling next job by status [" + jobExecution.getStatus() + "], jobId [" + jobId + "]");
                zdt.plus(getTriggerTimeout(), ChronoUnit.SECONDS);
                jobServiceExt.schedule(jobId, generateCronExpression(
                        String.valueOf(zdt.getSecond()),
                        String.valueOf(zdt.getMinute()),
                        String.valueOf(zdt.getHour()),
                        String.valueOf(zdt.getDayOfMonth()),
                        String.valueOf(zdt.getMonth().getValue()),
                        "?",
                        String.valueOf(zdt.getYear())));
                index++;
            }
            if (index == 0) {
                log.debug("No trigger jobs found for job with job instance id [" + jobExecution.getJobId() + "]");
            }
        }
    }

    /**
     * Generate a CRON expression is a string comprising 6 or 7 fields separated by white space.
     *
     * @param seconds    mandatory = yes. allowed values = {@code  0-59    * / , -}
     * @param minutes    mandatory = yes. allowed values = {@code  0-59    * / , -}
     * @param hours      mandatory = yes. allowed values = {@code 0-23   * / , -}
     * @param dayOfMonth mandatory = yes. allowed values = {@code 1-31  * / , - ? L W}
     * @param month      mandatory = yes. allowed values = {@code 1-12 or JAN-DEC    * / , -}
     * @param dayOfWeek  mandatory = yes. allowed values = {@code 0-6 or SUN-SAT * / , - ? L #}
     * @param year       mandatory = no. allowed values = {@code 1970-2099    * / , -}
     * @return a CRON Formatted String.
     */
    private String generateCronExpression(final String seconds, final String minutes, final String hours,
                                          final String dayOfMonth,
                                          final String month, final String dayOfWeek, final String year) {
        return String.format("%1$s %2$s %3$s %4$s %5$s %6$s %7$s", seconds, minutes, hours, dayOfMonth, month, dayOfWeek, year);
    }

    private int getTriggerTimeout() {
        if (triggerTimeout == null) {

            final String timeout = ServiceUtils.getConfigurationService()
                    .getSystemStringPropertyWithDefault(
                            ConfigurationConstants.JOB_TRIGGER_START_TIMEOUT_PROPERTY,
                            String.valueOf(DEFAULT_TRIGGER_START_TIMEOUT));

            try {
                triggerTimeout = Integer.valueOf(timeout);
            } catch (final NumberFormatException e) {
                triggerTimeout = DEFAULT_TRIGGER_START_TIMEOUT;
            }
        }

        return triggerTimeout;
    }
}
