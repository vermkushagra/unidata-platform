package com.unidata.mdm.backend.service.job.reindex;

import com.unidata.mdm.backend.service.job.JobCommonParameters;

/**
 * @author Mikhail Mikhailov
 * Parameters.
 */
public class ReindexDataJobConstants extends JobCommonParameters {
    /**
     * Parameter 'reindexTypes'.
     */
    public static final String PARAM_REINDEX_TYPES = "reindexTypes";
    /**
     * Param updateMappings.
     */
    public static final String PARAM_UPDATE_MAPPINGS = "updateMappings";
    /**
     * Parameter 'cleanTypes'.
     */
    public static final String PARAM_CLEAN_INDEXES = "cleanIndexes";
    /**
     * Parameter 'reindexRecords'.
     */
    public static final String PARAM_REINDEX_RECORDS = "reindexRecords";
    /**
     * Parameter 'reindexRelations'.
     */
    public static final String PARAM_REINDEX_RELATIONS = "reindexRelations";
    /**
     * Parameter 'reindexClassifiers'.
     */
    public static final String PARAM_REINDEX_CLASSIFIERS = "reindexClassifiers";
    /**
     * Parameter 'reindexMatching'.
     */
    public static final String PARAM_REINDEX_MATCHING = "reindexMatching";
    /**
     * Parameter 'skipDq'.
     */
    public static final String PARAM_SKIP_DQ = "skipDq";
    /**
     * Suppress consistency check, performed by system DQ rules.
     */
    public static final String PARAM_SUPPRESS_CONSISTENCY_CHECK = "suppressConsistencyCheck";
    /**
     * Suppress default report. Needed for classifier updates primarily.
     */
    public static final String PARAM_SUPPRESS_DEFAULT_REPORT = "suppressDefaultReport";
    /**
     * Parameter 'jmsEnabled'.
     */
    public static final String PARAM_SKIP_NOTIFICATIONS = "skipNotifications";
    /**
     * Records result mark.
     */
    public static final String REINDEX_JOB_REINDEXED_RECORDS_COUNTER = "#REINDEXED_RECORDS";
    /**
     * Classifier data result mark.
     */
    public static final String REINDEX_JOB_REINDEXED_CLASSIFIERS_COUNTER = "#REINDEXED_CLASSIFIERS";
    /**
     * Classified records result mark.
     */
    public static final String REINDEX_JOB_CLASSIFIED_RECORDS_COUNTER = "#CLASSIFIED_RECORDS";
    /**
     * Relations result mark.
     */
    public static final String REINDEX_JOB_REINDEXED_RELATIONS_COUNTER = "#REINDEXED_RELATIONS";
    /**
     * Job name
     */
    public static final String JOB_NAME = "reindexDataJob";

    public static final String USER_REPORT_PARAM = "userReport";

    public static final String USER_REPORT_MESSAGE_PARAM = "message";

    public static final String USER_REPORT_FAIL_MESSAGE_PARAM = "failMessage";
    /**
     * Param entityName.
     */
    public static final String PARAM_ENTITY_NAME = "entityName";

    /**
     * Constructor.
     */
    private ReindexDataJobConstants() {
        super();
    }
}
