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

package org.unidata.mdm.core.dto.job;

import java.time.ZonedDateTime;

import org.unidata.mdm.core.type.job.JobExecutionBatchStatus;

public class JobExecutionStepDTO {
    // The step execution ID
    private final Long id;
    // The job execution ID
    private final Long jobExecutionId;

    private final String stepName;

    private final JobExecutionBatchStatus jobExecutionBatchStatus;

    private final JobExecutionExitStatusDTO jobExecutionExitStatus;

    private final ZonedDateTime startTime;

    private final ZonedDateTime endTime;

    private final ZonedDateTime lastUpdated;

    private int readCount = 0;

    private int writeCount = 0;

    private int commitCount = 0;

    private int rollbackCount = 0;

    private int readSkipCount = 0;

    private int processSkipCount = 0;

    private int writeSkipCount = 0;

    public JobExecutionStepDTO(
            final Long id,
            final Long jobExecutionId,
            final String stepName,
            final JobExecutionBatchStatus jobExecutionBatchStatus,
            final JobExecutionExitStatusDTO jobExecutionExitStatus,
            final ZonedDateTime startTime,
            final ZonedDateTime endTime,
            final ZonedDateTime lastUpdated
    ) {
        this.id = id;
        this.jobExecutionId = jobExecutionId;
        this.stepName = stepName;
        this.jobExecutionBatchStatus = jobExecutionBatchStatus;
        this.jobExecutionExitStatus = jobExecutionExitStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastUpdated = lastUpdated;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the jobExecutionId
     */
    public Long getJobExecutionId() {
        return jobExecutionId;
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

    /**
     * @return the readCount
     */
    public int getReadCount() {
        return readCount;
    }

    /**
     * @param readCount the readCount to set
     */
    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    /**
     * @return the writeCount
     */
    public int getWriteCount() {
        return writeCount;
    }

    /**
     * @param writeCount the writeCount to set
     */
    public void setWriteCount(int writeCount) {
        this.writeCount = writeCount;
    }

    /**
     * @return the commitCount
     */
    public int getCommitCount() {
        return commitCount;
    }

    /**
     * @param commitCount the commitCount to set
     */
    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }

    /**
     * @return the rollbackCount
     */
    public int getRollbackCount() {
        return rollbackCount;
    }

    /**
     * @param rollbackCount the rollbackCount to set
     */
    public void setRollbackCount(int rollbackCount) {
        this.rollbackCount = rollbackCount;
    }

    /**
     * @return the readSkipCount
     */
    public int getReadSkipCount() {
        return readSkipCount;
    }

    /**
     * @param readSkipCount the readSkipCount to set
     */
    public void setReadSkipCount(int readSkipCount) {
        this.readSkipCount = readSkipCount;
    }

    /**
     * @return the processSkipCount
     */
    public int getProcessSkipCount() {
        return processSkipCount;
    }

    /**
     * @param processSkipCount the processSkipCount to set
     */
    public void setProcessSkipCount(int processSkipCount) {
        this.processSkipCount = processSkipCount;
    }

    /**
     * @return the writeSkipCount
     */
    public int getWriteSkipCount() {
        return writeSkipCount;
    }

    /**
     * @param writeSkipCount the writeSkipCount to set
     */
    public void setWriteSkipCount(int writeSkipCount) {
        this.writeSkipCount = writeSkipCount;
    }
}
