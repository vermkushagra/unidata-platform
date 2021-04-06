package com.unidata.mdm.backend.service.job.reindex;

/**
 * @author Mikhail Mikhailov
 * Execution mode.
 */
public enum ReindexDataJobExecutionMode {
    /**
     * Reindex job run.
     */
    DEFAULT,
    /**
     * Called from import records.
     */
    IMPORT_RECORDS_INITIAL_MULTIVERSIONS,
    /**
     * Called from import relations.
     */
    IMPORT_RELATIONS_INITIAL_MULTIVERSIONS,
    /**
     * Records update (triggered by job).
     */
    IMPORT_RECORDS_UPDATE,
    /**
     * Relations update (triggered by job).
     */
    IMPORT_RELATIONS_UPDATE
}
