/**
 * Date: 10.03.2016
 */

package com.unidata.mdm.backend.api.rest.dto.job;

import java.util.Date;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class JobStepExecutionRO {
    private Long id;

    private Long jobExecutionId;

    private String stepName;

    private String status;

    private Date startTime;

    private Date endTime;

    private String exitCode;

   	private String exitDescription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
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

    public String getExitCode() {
        return exitCode;
    }

    public void setExitCode(String exitCode) {
        this.exitCode = exitCode;
    }

    public String getExitDescription() {
        return exitDescription;
    }

    public void setExitDescription(String exitDescription) {
        this.exitDescription = exitDescription;
    }
}
