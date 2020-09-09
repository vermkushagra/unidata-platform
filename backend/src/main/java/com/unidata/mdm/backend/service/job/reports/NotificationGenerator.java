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

package com.unidata.mdm.backend.service.job.reports;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;
import com.unidata.mdm.backend.common.service.UserService;
import com.unidata.mdm.backend.service.security.utils.SecurityConstants;
import com.unidata.mdm.backend.util.MessageUtils;

/**
 * Notification Generator for result of batch job
 */
public abstract class NotificationGenerator implements JobExecutionListener {

    /**
     * user service
     */
    @Autowired
    private UserService userService;
    /**
     * User event ids.
     */
    private List<String> userEventIds;
    /**
     * User name
     */
    private String userName;
    /**
     * Additional users, to send notifications to.
     */
    private String usersSelector;
    /**
     * Job description
     */
    private String jobDescription;

    @Override
    public void beforeJob(JobExecution jobExecution) {}

    @Override
    public void afterJob(JobExecution jobExecution) {
        userEventIds = getUserEventIds(jobExecution);
    }

    private List<String> getUserEventIds(JobExecution jobExecution) {

        Set<String> userNames = new HashSet<>();
        String userNameValue = jobExecution.getJobParameters().getString("userName");
        String usersSelectorValue = jobExecution.getJobParameters().getString("usersSelector");
        if (StringUtils.isNotBlank(userNameValue)) {

            if(SecurityConstants.SYSTEM_USER_NAME.equals(userNameValue)){
                userNameValue = SecurityConstants.ADMIN;
            }

            userNames.add(userNameValue);
        }

        if (StringUtils.isNotBlank(usersSelectorValue)) {

            String[] split = StringUtils.split(usersSelectorValue, "|");
            if (Objects.nonNull(split)) {

                for (String s : split) {
                    userNames.add(StringUtils.trim(s));
                }
            }
        }

        if (userNames.isEmpty()) {
            return Collections.emptyList();
        }

        final String generalMessage = getGeneralMessage(jobExecution);
        final String additionMessage = getAdditionMessage(jobExecution);
        final String result = StringUtils.isNotBlank(additionMessage) ?
                generalMessage + StringUtils.LF + StringUtils.LF + additionMessage :
                generalMessage;

        return userNames.stream()
            .map(name -> {

                final String reportType = jobExecution.getJobParameters()
                        .getString(JobReportConstants.JOB_REPORT_TYPE, "Text report");

                UpsertUserEventRequestContext eCtx = UpsertUserEventRequestContext.builder()
                        .type(reportType)
                        .content(result)
                        .login(name)
                        .build();

                UserEventDTO userEventDTO = userService.upsert(eCtx);
                return userEventDTO.getId();
            })
            .collect(Collectors.toList());
    }

    protected String getGeneralMessage(JobExecution jobExecution) {
        String message = convertStatusToMessage(jobExecution.getStatus());
        return MessageUtils.getMessage(jobDescription) + message;
    }

    @Nonnull
    private String convertStatusToMessage(BatchStatus status) {
        switch (status) {
        case STARTED:
            return MessageUtils.getMessage(JobReportConstants.JOB_STATUS_STARTED);
        case STARTING:
            return MessageUtils.getMessage(JobReportConstants.JOB_STATUS_STARTING);
        case COMPLETED:
            return MessageUtils.getMessage(JobReportConstants.JOB_STATUS_COMPLETED);
        case STOPPING:
            return MessageUtils.getMessage(JobReportConstants.JOB_STATUS_STOPPING);
        case STOPPED:
            return MessageUtils.getMessage(JobReportConstants.JOB_STATUS_STOPPED);
        case FAILED:
            return MessageUtils.getMessage(JobReportConstants.JOB_STATUS_FAILED);
        default:
            return MessageUtils.getMessage(JobReportConstants.JOB_STATUS_UNKNOWN);
        }
    }

    @Nonnull
    protected abstract String getAdditionMessage(JobExecution jobExecution);

    protected final List<String> getUserEventIds() {
        return userEventIds;
    }

    /**
     * @param usersSelector the usersSelector to set
     */
    public void setUsersSelector(String usersSelector) {
        this.usersSelector = usersSelector;
    }

    @Required
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Required
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
