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

package com.unidata.mdm.backend.common.dto.job;

import java.util.List;

/**
 * @author Denis Kostovarov
 */
public class JobDTO extends JobMetaDTO<JobParameterDTO> {
    private Long id;
    private boolean enabled;
    private boolean error;
    private String cronExpression;
    private String description;
    private boolean skipCronWarnings;

    public JobDTO() {
    }

    public JobDTO(
            final Long id,
            final String name,
            final String jobNameReference,
            final List<JobParameterDTO> parameters,
            final boolean enabled,
            final boolean error,
            final String cronExpression,
            final String description,
            final boolean skipCronWarnings
    ) {
        super(name, jobNameReference, parameters);
        this.id = id;
        this.enabled = enabled;
        this.error = error;
        this.cronExpression = cronExpression;
        this.description = description;
        this.skipCronWarnings = skipCronWarnings;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSkipCronWarnings() {
        return skipCronWarnings;
    }

    public void setSkipCronWarnings(boolean skipCronWarnings) {
        this.skipCronWarnings = skipCronWarnings;
    }
}
