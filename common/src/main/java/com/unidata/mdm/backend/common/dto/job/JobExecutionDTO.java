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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.common.job.JobExecutionBatchStatus;

public class JobExecutionDTO {

    private final JobDTO jobDTO;

    private final Collection<JobParameterDTO> jobParameters;

    private final ZonedDateTime startTime;

    private final ZonedDateTime createTime;

    private final ZonedDateTime endTime;

    private final ZonedDateTime lastUpdated;

    private final JobExecutionBatchStatus jobExecutionBatchStatus;

    private final JobExecutionExitStatusDTO jobExecutionExitStatus;

    private final List<JobExecutionStepDTO> jobExecutionSteps = new ArrayList<>();

    public JobExecutionDTO(
            final JobDTO jobDTO,
            final Collection<JobParameterDTO> jobParameters,
            final ZonedDateTime startTime,
            final ZonedDateTime createTime,
            final ZonedDateTime endTime,
            final ZonedDateTime lastUpdated,
            final JobExecutionBatchStatus jobExecutionBatchStatus,
            final JobExecutionExitStatusDTO jobExecutionExitStatus,
            final Collection<JobExecutionStepDTO> jobExecutionSteps
    ) {
        this.jobDTO = jobDTO;
        this.jobParameters = jobParameters;
        this.startTime = startTime;
        this.createTime = createTime;
        this.endTime = endTime;
        this.lastUpdated = lastUpdated;
        this.jobExecutionBatchStatus = jobExecutionBatchStatus;
        this.jobExecutionExitStatus = jobExecutionExitStatus;
        if (!CollectionUtils.isEmpty(jobExecutionSteps)) {
            this.jobExecutionSteps.addAll(jobExecutionSteps);
        }
    }

    public JobDTO getJobDTO() {
        return jobDTO;
    }

    public Collection<JobParameterDTO> getJobParameters() {
        return Collections.unmodifiableCollection(jobParameters);
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public JobExecutionBatchStatus getJobExecutionBatchStatus() {
        return jobExecutionBatchStatus;
    }

    public JobExecutionExitStatusDTO getJobExecutionExitStatus() {
        return jobExecutionExitStatus;
    }

    public Collection<JobExecutionStepDTO> getJobExecutionSteps() {
        return Collections.unmodifiableCollection(jobExecutionSteps);
    }
}
