/**
 * Date: 10.03.2016
 */

package com.unidata.mdm.backend.api.rest.dto.job;

import java.util.Date;
import java.util.List;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class JobExecutionRO {
    private Long id;

    private Long jobId;

    private Date startTime;

    private Date endTime;

    private String status;

    private Boolean restartable;

    private List<JobStepExecutionRO> stepExecutions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<JobStepExecutionRO> getStepExecutions() {
        return stepExecutions;
    }

    public void setStepExecutions(List<JobStepExecutionRO> stepExecutions) {
        this.stepExecutions = stepExecutions;
    }

    public Boolean isRestartable() {
        return restartable;
    }

    public void setRestartable(Boolean restartable) {
        this.restartable = restartable;
    }
}
