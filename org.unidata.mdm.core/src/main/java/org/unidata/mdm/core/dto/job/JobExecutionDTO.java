package org.unidata.mdm.core.dto.job;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.core.type.job.JobExecutionBatchStatus;

public class JobExecutionDTO {

    private final Long id;

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
            final Long id,
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
        this.id = id;
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

    /**
     * @return the id
     */
    public Long getId() {
        return id;
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
