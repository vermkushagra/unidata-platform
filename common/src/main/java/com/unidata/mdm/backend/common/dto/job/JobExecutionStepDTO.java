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

import com.unidata.mdm.backend.common.job.JobExecutionBatchStatus;

public class JobExecutionStepDTO {
    private final String stepName;

    private final JobExecutionBatchStatus jobExecutionBatchStatus;

    private final JobExecutionExitStatusDTO jobExecutionExitStatus;

    private final ZonedDateTime startTime;

    private final ZonedDateTime endTime;

    private final ZonedDateTime lastUpdated;


    public JobExecutionStepDTO(
            final String stepName,
            final JobExecutionBatchStatus jobExecutionBatchStatus,
            final JobExecutionExitStatusDTO jobExecutionExitStatus,
            final ZonedDateTime startTime,
            final ZonedDateTime endTime,
            final ZonedDateTime lastUpdated
    ) {
        this.stepName = stepName;
        this.jobExecutionBatchStatus = jobExecutionBatchStatus;
        this.jobExecutionExitStatus = jobExecutionExitStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastUpdated = lastUpdated;
    }

    public String getStepName() {
        return stepName;
    }

    public JobExecutionBatchStatus getJobExecutionBatchStatus() {
        return jobExecutionBatchStatus;
    }

    public JobExecutionExitStatusDTO getJobExecutionExitStatus() {
        return jobExecutionExitStatus;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }
}
