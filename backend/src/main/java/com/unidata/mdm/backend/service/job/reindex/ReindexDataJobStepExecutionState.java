package com.unidata.mdm.backend.service.job.reindex;

import com.unidata.mdm.backend.service.job.common.StepExecutionState;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ReindexDataJobStepExecutionState implements StepExecutionState {
    /**
     * Records.
     */
    private long reindexedRecords = 0L;
    /**
     * Classified records.
     */
    private long classifiedRecords = 0L;
    /**
     * Classifier data records.
     */
    private long reindexedClassifiers = 0L;
    /**
     * Inserted.
     */
    private long reindexedRelations = 0L;
    /**
     * Constructor.
     */
    public ReindexDataJobStepExecutionState() {
        super();
    }
    /**
     * @param reindexedRecords the reindexedRecords to set
     */
    public void incrementReindexedRecords(long reindexedRecords) {
        this.reindexedRecords += reindexedRecords;
    }
    /**
     * @param reindexedClassifiers the reindexedClassifiers to set
     */
    public void incrementReindexedClassifiers(long reindexedClassifiers) {
        this.reindexedClassifiers += reindexedClassifiers;
    }
    /**
     * @param classifiedRecords the classifiedRecords to set
     */
    public void incrementClassifiedRecords(long classifiedRecords) {
        this.classifiedRecords += classifiedRecords;
    }
    /**
     * @param reindexedRelations the reindexedRelations to set
     */
    public void incrementReindexedRelations(long reindexedRelations) {
        this.reindexedRelations += reindexedRelations;
    }
    /**
     * @return the failed
     */
    public long getReindexedRecords() {
        return reindexedRecords;
    }
    /**
     * @return the skept
     */
    public long getReindexedClassifiers() {
        return reindexedClassifiers;
    }
    /**
     * @return the updated
     */
    public long getClassifiedRecords() {
        return classifiedRecords;
    }
    /**
     * @return the inserted
     */
    public long getReindexedRelations() {
        return reindexedRelations;
    }
}
