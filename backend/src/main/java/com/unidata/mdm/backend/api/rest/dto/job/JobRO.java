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

/**
 * Date: 10.03.2016
 */

package com.unidata.mdm.backend.api.rest.dto.job;

import java.util.List;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class JobRO {
    private Long id;

    private String name;

    private String cronExpression;

    private String jobNameReference;

    private String description;

    private boolean enabled;

    private boolean error;

    private boolean skipCronWarnings;

    private List<JobParameterRO> parameters;

    private JobExecutionRO lastExecution;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobNameReference() {
        return jobNameReference;
    }

    public void setJobNameReference(String jobNameReference) {
        this.jobNameReference = jobNameReference;
    }

    public List<JobParameterRO> getParameters() {
        return parameters;
    }

    public void setParameters(List<JobParameterRO> parameters) {
        this.parameters = parameters;
    }

    public JobExecutionRO getLastExecution() {
        return lastExecution;
    }

    public void setLastExecution(JobExecutionRO lastExecution) {
        this.lastExecution = lastExecution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isSkipCronWarnings() {
        return skipCronWarnings;
    }

    public void setSkipCronWarnings(boolean skipCronWarnings) {
        this.skipCronWarnings = skipCronWarnings;
    }
}
