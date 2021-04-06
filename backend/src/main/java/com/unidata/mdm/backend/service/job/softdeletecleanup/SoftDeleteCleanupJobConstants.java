package com.unidata.mdm.backend.service.job.softdeletecleanup;

/**
 * @author Dmitry Kopin on 11.01.2018.
 */
public class SoftDeleteCleanupJobConstants {
    public static final String MODIFY_ITEM_JOB_RECORDS_COUNTER = "#RECORDS_PROCESSED";

    public static final String MODIFY_ITEM_JOB_RECORDS_DELETE_COUNTER = "#RECORDS_DELETED";

    public static final String MODIFY_ITEM_JOB_RECORDS_FAILED_COUNTER = "#RECORDS_FAILED";

    /**
     * Total records message.
     */
    public static final String MSG_REPORT_RECORDS_TOTAL = "app.job.soft.delete.cleanup.records.total";
    /**
     * Total relations message.
     */
    public static final String MSG_REPORT_RECORDS_DELETED = "app.job.soft.delete.cleanup.records.deleted";
    /**
     * Total classifiers message.
     */
    public static final String MSG_REPORT_RECORDS_FAILED = "app.job.soft.delete.cleanup.records.failed";

}
