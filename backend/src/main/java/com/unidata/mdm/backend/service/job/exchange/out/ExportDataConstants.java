package com.unidata.mdm.backend.service.job.exchange.out;

/**
 * @author Mikhail Mikhailov
 * Export job constants.
 */
public interface ExportDataConstants {
    /**
     * Export job logger name.
     */
    public static final String EXPORT_JOB_LOGGER_NAME = "export-job-logger";
    /**
     * Exchange object prefix for the key of [prefix]_[jobId]_[partitionId].
     */
    public static final String EXCHANGE_OBJECTS_PREFIX = "eo";
    /**
     * Exchange objects map name.
     */
    public static final String EXCHANGE_OBJECTS_MAP_NAME = "export-data-job-exchange-objects";
    /**
     * Update result mark.
     */
    public static final String EXPORT_JOB_UPDATE_RESULT = "UPDATE";
    /**
     * Insert result mark.
     */
    public static final String EXPORT_JOB_INSERT_RESULT = "INSERT";
    /**
     * Skip result mark.
     */
    public static final String EXPORT_JOB_SKIP_RESULT = "SKIP";
    /**
     * Fail result mark.
     */
    public static final String EXPORT_JOB_FAIL_RESULT = "FAIL";
}
