package org.unidata.mdm.core.dto.job;

import java.time.ZonedDateTime;

import org.unidata.mdm.core.type.job.JobExecutionBatchStatus;

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
