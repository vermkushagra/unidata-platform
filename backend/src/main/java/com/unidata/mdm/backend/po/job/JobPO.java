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

package com.unidata.mdm.backend.po.job;

import com.unidata.mdm.backend.po.AbstractPO;

import java.util.ArrayList;
import java.util.List;

/**
 * Job PO from/to database.
 * @author Denis Kostovarov
 */
public class JobPO extends AbstractPO {
    public static final String TABLE_NAME = "job";
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ENABLED = "enabled";
    public static final String FIELD_ERROR = "error";
    public static final String FIELD_CRON_EXPRESSION = "cron_expr";
    public static final String FIELD_JOB_NAME_REFERENCE = "job_name_ref";
    public static final String FIELD_DESCRIPTION = "descr";

    private Long id;
    private String name;
    private boolean enabled;
    private boolean error;
    private String cronExpression;
    private String jobNameReference;
    private String description;
    private List<JobParameterPO> parameters;

    public JobPO() {
    }

    public JobPO(
            final Long id,
            final String name,
            final boolean enabled,
            final String cronExpression,
            final String jobNameReference,
            final String description
    ) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.cronExpression = cronExpression;
        this.jobNameReference = jobNameReference;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JobParameterPO> getParameters() {
        return parameters;
    }

    public void setParameters(List<JobParameterPO> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(final JobParameterPO param) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }

        parameters.add(param);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
