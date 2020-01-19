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

package org.unidata.mdm.core.service.impl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.unidata.mdm.core.dao.JobDao;
import org.unidata.mdm.core.dto.job.JobDTO;
import org.unidata.mdm.core.dto.job.JobParameterDTO;
import org.unidata.mdm.core.dto.job.JobTriggerDTO;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.exception.JobException;
import org.unidata.mdm.core.po.job.JobPO;
import org.unidata.mdm.core.po.job.JobTriggerPO;
import org.unidata.mdm.system.exception.ExceptionId;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Denis Kostovarov
 */
public final class JobValidator {
    private static final int JOB_NAME_LIMIT = 100;
    private static final int PARAM_NAME_LIMIT = 100;
    static final int PARAM_STRING_VALUE_LIMIT = 250;
    public static final String VIOLATION_JOB_NAME = "$$$name";
    private static final String VIOLATION_JOB_CRON_EXPRESSION = "$$$cronExpression";
    private static final String VIOLATION_PARAM = "param_";
    private static final String VIOLATION_VALUE = "value_";
    private static final String VIOLATION_LENGTH = "length:";
    private static final String VIOLATION_WARNING = "warning";
    private static final String VIOLATION_ERROR = "error";
    private static final String VIOLATION_UNIQUE = "unique:false";
    private static final Set<Integer> CRON_ALL_SECONDS_MINUTES;

    static {
        CRON_ALL_SECONDS_MINUTES = new HashSet<>();
        for (int i = 0; i < 60; ++i) {
            CRON_ALL_SECONDS_MINUTES.add(i);
        }
    }

    private JobValidator() {
        //no-op.
    }

    public static void validateJob(final JobDao jobDao, final JobDTO jobDto) {
        final JobPO existingJob = jobDao.findJob(jobDto.getName());
        if (existingJob != null && !existingJob.getId().equals(jobDto.getId())) {
            throw new JobException(
                    "Job with name [" + jobDto.getName() + "] already exists", CoreExceptionIds.EX_JOB_SAME_NAME,
                    Collections.singletonList(new ImmutablePair<>(VIOLATION_JOB_NAME, VIOLATION_VALUE + VIOLATION_UNIQUE)),
                    jobDto.getName());
        }

        final List<Pair> wrongParams = new ArrayList<>();
        if (jobDto.getName().length() > JOB_NAME_LIMIT) {
            wrongParams.add(new ImmutablePair<>(VIOLATION_JOB_NAME, VIOLATION_VALUE + VIOLATION_LENGTH + JOB_NAME_LIMIT));
        }

        // Check cron expression syntax.
        validateCronExpression(jobDto.getCronExpression(), jobDto.isSkipCronWarnings());

        final List<Pair> jobWrongParams = validateJobParameters(jobDto);
        if (!CollectionUtils.isEmpty(jobWrongParams)) {
            wrongParams.addAll(jobWrongParams);
        }

        if (!wrongParams.isEmpty()) {
            throw new JobException(
                "New job parameters validation error", CoreExceptionIds.EX_JOB_PARAMETER_VALIDATION_ERROR,
                wrongParams, jobDto.getName());
        }
    }

    static void validateCronExpression(final String cronExpression, final boolean ignoreWarnings) {
        if (StringUtils.hasText(cronExpression)) {
            try {
                if (ignoreWarnings) {
                    CronExpressionAdapter.validateExpression(cronExpression);
                } else {
                    final CronExpressionAdapter expr = new CronExpressionAdapter(cronExpression);
                    final Set<Integer> seconds = expr.getSecondSet();
                    final Set<Integer> minutes = expr.getMinuteSet();
                    final Set<Integer> daysOfMonth = expr.getDayOfMonthSet();
                    String errorMsg = null;
                    ExceptionId exceptionId = null;
                    final Set<Integer> temp = new HashSet<>(seconds);
                    temp.retainAll(CRON_ALL_SECONDS_MINUTES);
                    if (temp.size() == CRON_ALL_SECONDS_MINUTES.size()) {
                        errorMsg = "Do you really mean \"every second\" when you say [" + cronExpression + "]?";
                        exceptionId = CoreExceptionIds.EX_JOB_CRON_SUSPICIOUS_SECOND;
                    }
                    temp.clear();
                    temp.addAll(minutes);
                    temp.retainAll(CRON_ALL_SECONDS_MINUTES);
                    if (temp.size() == CRON_ALL_SECONDS_MINUTES.size()) {
                        errorMsg =  "Do you really mean \"every minute\" when you say [" + cronExpression + "]?";
                        exceptionId = CoreExceptionIds.EX_JOB_CRON_SUSPICIOUS_MINUTE;
                    }
                    // a bit arbitrary
                    if (daysOfMonth.size() > 5 && daysOfMonth.size() < 28) {
                        errorMsg =  "Short cycles in the day-of-month field will behave oddly near the end of a month";
                        exceptionId =  CoreExceptionIds.EX_JOB_CRON_SUSPICIOUS_SHORT_CYCLES_DOM;
                    }

                    if (errorMsg != null) {
                        throw new JobException(errorMsg, exceptionId,
                                Collections.singletonList(new ImmutablePair<>(VIOLATION_JOB_CRON_EXPRESSION,
                                        VIOLATION_VALUE + VIOLATION_WARNING)));
                    }
                }
            } catch (final ParseException e) {
                throw new JobException(
                        "Cron expression [" + cronExpression + "] parse error",
                        CoreExceptionIds.EX_JOB_CRON_EXPRESSION,
                        Collections.singletonList(new ImmutablePair<>(VIOLATION_JOB_CRON_EXPRESSION,
                                VIOLATION_VALUE + VIOLATION_ERROR)));
            }
        }
    }

    static List<Pair> validateJobParameters(final JobDTO jobDto) {
        final List<JobParameterDTO> jobParameters = jobDto.getParameters();
        final List<Pair> wrongParams = new ArrayList<>();
        final Set<String> uniqueParams = new HashSet<>();
        for (final JobParameterDTO p : jobParameters) {
            if (uniqueParams.contains(p.getName())) {
                wrongParams.add(new ImmutablePair<>(p.getName(), VIOLATION_PARAM + VIOLATION_UNIQUE));
                continue;
            }
            uniqueParams.add(p.getName());
            if (p.getName().length() > PARAM_NAME_LIMIT) {
                wrongParams.add(new ImmutablePair<>(p.getName(), VIOLATION_PARAM + VIOLATION_LENGTH + PARAM_NAME_LIMIT));
                continue;
            }
            /*
            final String val = p.getStringValue();
            if (p.getStringValue() != null && val.length() > PARAM_STRING_VALUE_LIMIT) {
                wrongParams.add(new Param(p.getName(), VIOLATION_VALUE + VIOLATION_LENGTH + PARAM_STRING_VALUE_LIMIT));
            }
            */
        }

        return wrongParams;
    }

    public static void validateJobTrigger(final JobDao jobDao, final JobTriggerDTO jobTriggerDto) {
        Assert.notNull(jobTriggerDto, "Cannot validate null object");

        final JobTriggerPO existingJobTrigger = jobDao.findJobTrigger(jobTriggerDto.getName());
        if (existingJobTrigger != null && !existingJobTrigger.getId().equals(jobTriggerDto.getId())) {
            throw new JobException(
                    "Job trigger with name [" + jobTriggerDto.getName() + "] already exists",
                    CoreExceptionIds.EX_JOB_TRIGGER_SAME_NAME,
                    Collections.singletonList(new ImmutablePair<>(VIOLATION_JOB_NAME, VIOLATION_VALUE + VIOLATION_UNIQUE)),
                    jobTriggerDto.getName());
        }

        final List<Pair> wrongParams = new ArrayList<>();
        final JobPO finishJob;
        if (jobTriggerDto.getFinishJobId() != null) {
            finishJob = jobDao.findJob(jobTriggerDto.getFinishJobId());
        } else {
            finishJob = null;
        }

        if (finishJob == null) {
            throw new JobException(
                    "New job trigger validation error", CoreExceptionIds.EX_JOB_NOT_FOUND, jobTriggerDto.getFinishJobId());
        }

        final JobPO startJob;
        if (jobTriggerDto.getStartJobId() != null) {
            startJob = jobDao.findJob(jobTriggerDto.getStartJobId());
        } else {
            startJob = null;
        }

        if (startJob == null) {
            throw new JobException(
                    "New job trigger validation error, start job not found",
                    CoreExceptionIds.EX_JOB_TRIGGER_START_JOB_NOT_FOUND, jobTriggerDto.getStartJobId());
        }

        if (startJob.getId().longValue() == finishJob.getId().longValue()) {
            throw new JobException(
                    "New job trigger validation error, recursive call is invalid",
                    CoreExceptionIds.EX_JOB_TRIGGER_RECURSIVE_CALL);
        }

        if (!StringUtils.hasText(jobTriggerDto.getName())) {
            wrongParams.add(new ImmutablePair<>(VIOLATION_JOB_NAME, VIOLATION_VALUE + VIOLATION_LENGTH + 0));
        }

        if (jobTriggerDto.getName().length() > JOB_NAME_LIMIT) {
            wrongParams.add(new ImmutablePair<>(VIOLATION_JOB_NAME, VIOLATION_VALUE + VIOLATION_LENGTH + JOB_NAME_LIMIT));
        }

        if (!wrongParams.isEmpty()) {
            throw new JobException(
                    "New job trigger parameters validation error", CoreExceptionIds.EX_JOB_TRIGGER_PARAMETER_VALIDATION_ERROR,
                    jobTriggerDto.getName(),wrongParams);
        }
    }
}
