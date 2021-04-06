package com.unidata.mdm.backend.service.job.modify;

/**
 * @author Dmitry Kopin on 11.01.2018.
 */
public class ModifyItemJobConstants {
    public static final String MODIFY_ITEM_JOB_RECORDS_COUNTER = "#RECORDS_MODIFIED";

    public static final String MODIFY_ITEM_JOB_RECORDS_SKEPT_COUNTER = "#RECORDS_SKEPT";

    public static final String MODIFY_ITEM_JOB_RECORDS_FAILED_COUNTER = "#RECORDS_FAILED";

    public static final String MODIFY_ITEM_JOB_CLASSIFIERS_COUNTER = "#CLASSIFIERS_MODIFIED";

    public static final String MODIFY_ITEM_JOB_CLASSIFIERS_SKEPT_COUNTER = "#CLASSIFIERS_SKEPT";

    public static final String MODIFY_ITEM_JOB_CLASSIFIERS_FAILED_COUNTER = "#CLASSIFIERS_FAILED";

    public static final String MODIFY_ITEM_JOB_CLASSIFIERS_DELETED_COUNTER = "#CLASSIFIERS_DELETED";

    public static final String MODIFY_ITEM_JOB_RELATIONS_COUNTER = "#RELATIONS_MODIFIED";

    public static final String MODIFY_ITEM_JOB_RELATIONS_SKEPT_COUNTER = "#RELATIONS_SKEPT";

    public static final String MODIFY_ITEM_JOB_RELATIONS_DELETED_COUNTER = "#RELATIONS_DELETED";

    public static final String MODIFY_ITEM_JOB_RELATIONS_FAILED_COUNTER = "#RELATIONS_FAILED";

    /**
     * Total records message.
     */
    public static final String MSG_REPORT_RECORDS_TOTAL = "app.job.batch.modify.records.total";
    /**
     * Total relations message.
     */
    public static final String MSG_REPORT_RELATIONS_TOTAL = "app.job.batch.modify.relations.total";
    /**
     * Total classifiers message.
     */
    public static final String MSG_REPORT_CLASSIFIERS_TOTAL = "app.job.batch.modify.classifiers.total";
    /**
     * Updated count.
     */
    public static final String MSG_REPORT_UPDATED = "app.job.batch.modify.updated";
    /**
     * Failed count.
     */
    public static final String MSG_REPORT_FAILED = "app.job.batch.modify.failed";
    /**
     * Deleted count.
     */
    public static final String MSG_REPORT_DELETED = "app.job.batch.modify.deleted";


    public static final String JOB_NAME = "modifyJob";
    /**
     * Skept count.
     */
    public static final String MSG_REPORT_SKEPT = "app.job.batch.modify.skept";
}
