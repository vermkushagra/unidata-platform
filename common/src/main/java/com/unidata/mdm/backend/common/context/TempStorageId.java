package com.unidata.mdm.backend.common.context;

/**
 * @author Mikhail Mikhailov
 *
 */
public enum TempStorageId {
    /**
     * Keys. TODO merge with other keys.
     */
    DATA_MERGE_KEYS,
    /**
     * Duplicates keys.
     */
    DATA_MERGE_DUPLICATES_KEYS,
    /**
     * Entity type rights.
     */
    DATA_MERGE_RIGHTS,
    /**
     * Entity type rights.
     */
    DATA_MERGE_WF_ASSIGNMENTS,
    /**
     * Golden data upon merge.
     */
    DATA_MERGE_ETALON_RECORD,
    /**
     * Golden data duplicates upon merge.
     */
    DATA_MERGE_DUPLICATES,
    /**
     * Intervals + etalon data before delete.
     */
    DATA_DELETE_INTERVALS_BEFORE,
    /**
     * Timeline before delete.
     */
    DATA_DELETE_TIMELINE_BEFORE,
    /**
     * Timeline + etalon data after delete.
     */
    DATA_DELETE_TIMELINE_AFTER,
    /**
     * Keys. TODO delete duplicate labels for keys.
     */
    DATA_DELETE_KEYS,
    /**
     * Entity type rights.
     */
    DATA_DELETE_RIGHTS,
    /**
     * Entity type rights.
     */
    DATA_DELETE_WF_ASSIGNMENTS,
    /**
     * Golden data upon upsert.
     */
    DATA_UPSERT_ETALON_RECORD,
    /**
     * Origin records used as calculation base.
     */
    DATA_UPSERT_ETALON_BASE,
    /**
     * Origin records created during the 'after' etalon calculation phase.
     */
    DATA_UPSERT_ETALON_ENRICHMENT,
    /**
     * Collected index updates.
     */
    DATA_UPSERT_ETALON_INDEX_UPDATE,
    /**
     * Origin data upon upsert.
     */
    DATA_UPSERT_ORIGIN_RECORD,
    /**
     * Whether etalon or origin has been modified by DQ Enrichment.
     */
    DATA_UPSERT_IS_MODIFIED,
    /**
     * Begin of processing.
     */
    DATA_UPSERT_RECORD_TIMESTAMP,
    /**
     * Published flag.
     */
    DATA_UPSERT_IS_PUBLISHED,
    /**
     * Whole workflow timeline.
     */
    DATA_UPSERT_WORKFLOW_TIMELINE,
    /**
     * Particular interval.
     */
    DATA_UPSERT_WORKFLOW_INTERVAL,
    /**
     * Keys pair of the supplied origin or null, if the record can not be identified.
     * Keys. TODO merge with other keys.
     */
    DATA_UPSERT_KEYS,
    /**
     * Entity type rights.
     */
    DATA_UPSERT_RIGHTS,
    /**
     * Entity type rights.
     */
    DATA_UPSERT_WF_ASSIGNMENTS,
    /**
     * Exact action type, supplied to listener.
     */
    DATA_UPSERT_EXACT_ACTION,
    /**
     * Timeline calculated before upsert data. Use for strict calculate affected timeline
     */
    DATA_UPSERT_PREVIOUS_TIMELINE,
    /**
     * Golden record returned by get operations.
     */
    DATA_GET_ETALON_RECORD,
    /**
     * Golden record(s) returned by get records operations.
     */
    DATA_GET_ETALON_RECORDS,
    /**
     * Origin record
     */
    DATA_GET_ORIGINS_RECORDS,
    /**
     * Keys pair of the supplied origin or null, if the record can not be identified.
     * Keys. TODO merge with other keys.
     */
    DATA_GET_KEYS,
    /**
     * Entity type rights.
     */
    DATA_GET_RIGHTS,
    /**
     * Entity type rights.
     */
    DATA_GET_WF_ASSIGNMENTS,
    /**
     * Import records.
     */
    IMPORT_ORIGIN_RECORDS,
    /**
     * Import records.
     */
    IMPORT_ORIGIN_RELATIONS,
    /**
     * From key.
     */
    RELATIONS_FROM_KEY,
    /**
     * To key.
     */
    RELATIONS_TO_KEY,
    /**
     * Relation key.
     */
    RELATIONS_RELATION_KEY,
    /**
     * From side rights.
     */
    RELATIONS_FROM_RIGHTS,
    /**
     * Exact action type, supplied to listener.
     */
    RELATIONS_UPSERT_EXACT_ACTION,
    /**
     * Upsert from side WF assignments.
     */
    RELATIONS_FROM_WF_ASSIGNMENTS,
    /**
     * Reltion meta definition object.
     */
    RELATIONS_META_DEF,
    /**
     * Containment context.
     */
    RELATIONS_CONTAINMENT_CONTEXT,
    /**
     * Etalon relation data.
     */
    RELATIONS_ETALON_DATA,
    /**
     * Current spring ApplicationContext context, if any. Use with care.
     */
    COMMON_APPLICATION_CONTEXT,
    /**
     * Classifier keys.
     */
    CLASSIFIERS_CLASSIFIER_KEYS,
    /**
     * Exact action.
     */
    CLASSIFIERS_UPSERT_EXACT_ACTION,
    /**
     * Classifier node definition
     */
    CLASSIFIERS_UPSERT_RESOLVED_NODE_ID,
    /**
     * Classifier node definition
     */
    CLASSIFIERS_GET_RESOLVED_NODE_ID,
    /**
     * Upsert result.
     */
    CLASSIFIERS_RESULT,
    /**
     * Ddefault classifiers.
     */
    DEFAULT_CLASSIFIERS,
    /**
     * Record keys id.
     */
    RECORDS_RECORD_KEYS,
    /**
     * Storage contains import row num in case when import happens.
     */
    IMPORT_ROW_NUM,
    /**
     * Records vistory data as upsert objects for batch.
     */
    DATA_BATCH_RECORDS,
    /**
     * Relations vistory data as upsert objects for batch.
     */
    DATA_BATCH_RELATIONS,
    /**
     * Classifiers vistory data as upsert objects for batch.
     */
    DATA_BATCH_CLASSIFIERS,
    /**
     * user exit errors
     */
    PROCESS_ERRORS,
    /**
     * vistory operation type.
     */
    DATA_UPSERT_VISTORY_OPERATION_TYPE;
}
