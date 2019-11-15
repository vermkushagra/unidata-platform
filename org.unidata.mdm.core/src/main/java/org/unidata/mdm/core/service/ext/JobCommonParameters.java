package org.unidata.mdm.core.service.ext;


/**
 * @author Mikhail Mikhailov
 * Parameters, shared by many jobs.
 */
public abstract class JobCommonParameters {
    /**
     * Parameter 'auditLevel'.
     */
    public static final String PARAM_AUDIT_LEVEL = "auditLevel";
    /**
     * Parameter 'executionMode'.
     */
    public static final String PARAM_EXECUTION_MODE = "executionMode";
    /**
     * Parameter 'partitionGroup'.
     */
    public static final String PARAM_PARTITION_GROUP = "partitionGroup";
    /**
     * Parameter 'entityName'.
     */
    public static final String PARAM_ENTITY_NAME = "entityName";
    /**
     * Parameter 'relationName'.
     */
    public static final String PARAM_RELATION_NAME = "relationName";
    /**
     * Parameter 'classifierName'.
     */
    public static final String PARAM_CLASSIFIER_NAME = "classifierName";
    /**
     * Parameter 'definition'.
     */
    public static final String PARAM_DEFINITION = "definition";
    /**
     * Parameter 'blockSize'.
     */
    public static final String PARAM_BLOCK_SIZE = "blockSize";
    /**
     * Parameter 'offset'.
     */
    public static final String PARAM_OFFSET = "offset";
    /**
     * Parameter 'startGsn'.
     */
    public static final String PARAM_START_LSN = "startLsn";
    /**
     * Parameter 'endGsn'.
     */
    public static final String PARAM_END_LSN = "endLsn";

    public static final String PARAM_SHARD_NUMBER = "dataShard";
    /**
     * Parameter 'filters'.
     */
    public static final String PARAM_FILTERS = "filters";
    /**
     * Parameter 'databaseUrl'.
     */
    public static final String PARAM_DATABASE_URL = "databaseUrl";
    /**
     * Parameter 'operationId'.
     */
    public static final String PARAM_OPERATION_ID = "operationId";
    /**
     * Parameter 'runId'.
     */
    public static final String PARAM_RUN_ID = "runId";

    public static final String PARAM_PARENT_JOB_EXECUTION_ID = "parentJobExecutionId";
    /**
     * Parameter 'paritionId'.
     */
    public static final String PARAM_PARTITION_ID = "paritionId";
    /**
     * Parameter 'exchangeObjectId'.
     */
    public static final String PARAM_EXCHANGE_OBJECT_ID = "exchangeObjectId";
    /**
     * Parameter 'timestamp'.
     */
    public static final String PARAM_START_TIMESTAMP = "timestamp";
    /**
     * Parameter 'userName'.
     */
    public static final String PARAM_USER_NAME = "userName";
    /**
     * Parameter 'userToken'.
     */
    public static final String PARAM_USER_TOKEN = "userToken";
    /**
     * Parameter 'reportCharSet'.
     */
    public static final String PARAM_REPORT_CHAR_SET = "reportCharSet";
    /**
     * Parameter 'reportSeparator'.
     */
    public static final String PARAM_REPORT_SEPARATOR = "reportSeparator";
    /**
     * Parameter 'reportName'.
     */
    public static final String PARAM_REPORT_NAME = "reportName";
    /**
     * Parameter 'description' (job decsription).
     */
    public static final String PARAM_JOB_DESCRIPTION = "description";

    public static final String PARAM_PREVIOUS_SUCCESS_START_DATE = "previousSuccessStartDate";
    /**
     * Parameter 'jobUser'.
     */
    public static final String PARAM_JOB_USER = "jobUser";

    public static final String PARAM_JOB_ALIAS = "jobName";
    /**
     * Constructor.
     */
    protected JobCommonParameters() {
       super();
    }

}
