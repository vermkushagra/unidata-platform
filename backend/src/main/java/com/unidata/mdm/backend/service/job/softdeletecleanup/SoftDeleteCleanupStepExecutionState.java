package com.unidata.mdm.backend.service.job.softdeletecleanup;

import com.unidata.mdm.backend.service.job.common.StepExecutionState;

/**
 * @author Dmitrii Kopin
 */
public class SoftDeleteCleanupStepExecutionState implements StepExecutionState {

    private long processedRecords = 0L;
    private long failedRecords = 0L;
    private long deleteRecords = 0L;

    /**
     * Constructor.
     */
    public SoftDeleteCleanupStepExecutionState() {
        super();
    }

    /**
     * @param processedRecords the processedRecords to set
     */
    public void incrementProcessedRecords(long processedRecords) {
        this.processedRecords += processedRecords;
    }

    /**
     * @param failedRecords the failedRecords to set
     */
    public void incrementFailedRecords(long failedRecords) {
        this.failedRecords += failedRecords;
    }

    /**
     * @param deleteRecords the deleteRecords to set
     */
    public void incrementDeleteRecords(long deleteRecords) {
        this.deleteRecords += deleteRecords;
    }

    /**
     * get failed records count
     *
     * @return records count
     */
    public long getFailedRecords() {
        return failedRecords;
    }

    /**
     * get processed records
     *
     * @return records count
     */
    public long getProcessedRecords() {
        return processedRecords;
    }

    /**
     * get delete records
     *
     * @return records count
     */
    public long getDeleteRecords() {
        return deleteRecords;
    }
}
