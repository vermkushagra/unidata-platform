package com.unidata.mdm.backend.common.context;

/**
 * @author Mikhail Mikhailov
 * Identifiers for the context storage.
 */
public enum StorageId {
    /**
     * Keys. TODO merge with other keys.
     */
    DATA_MERGE_KEYS,
    /**
     * Duplicates keys.
     */
    DATA_MERGE_DUPLICATES_KEYS,
    /**
     * not found etalon keys.
     */
    DATA_MERGE_NOT_FOUND_ETALON_KEYS,
    /**
     * Etalon keys with dates when this records need be calculate for merge
     */
    DATA_MERGE_KEYS_FOR_DATES,
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
     * Intervals + etalon data before modifications.
     */
    DATA_INTERVALS_BEFORE,
    /**
     * Intervals + etalon data after modifications.
     */
    DATA_INTERVALS_AFTER,
    /**
     * Timeline before delete.
     */
    DATA_TIMELINE_BEFORE,
    /**
     * Timeline + etalon data after delete.
     */
    DATA_TIMELINE_AFTER,
    /**
     * Keys. TODO delete duplicate labels for keys.
     */
    DATA_DELETE_KEYS,
    /**
     * Entity type rights.
     */
    DATA_DELETE_RIGHTS,
    /**
     * Assigned WF. TODO delete duplicate labels for WF assignments.
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
     * Collected index updates.
     */
    DATA_UPSERT_ETALON_MATCHING_UPDATE,
    /**
     * Origin data upon upsert.
     */
    DATA_UPSERT_ORIGIN_RECORD,
    /**
     * Whether etalon or origin has been modified by DQ Enrichment.
     */
    DATA_UPSERT_IS_MODIFIED,
    /**
     * Modbox for this upsert.
     */
    DATA_UPSERT_MODIFICATION_BOX,
    /**
     * Begin of processing.
     */
    DATA_UPSERT_RECORD_TIMESTAMP,
    /**
     * Published flag.
     */
    DATA_UPSERT_IS_PUBLISHED,
    /**
     * Origins count for ss + ext id, being upserted.
     */
    DATA_DQ_ORIGINS_COUNT,
    /**
     * Cleanse function adapter of the function, being executed by current stack.
     */
    DATA_DQ_CURRENT_FUNCTION,
    /**
     * Copy of the current etalon data, which can be possibly modified by etalon enrichment rule.
     */
    DATA_DQ_ENRICH_ETALON_COPY,
    /**
     * Whole workflow timeline.
     */
    DATA_RECORD_TIMELINE,
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
     * Diff to draft map.
     */
    DATA_GET_DIFF_TO_DRAFT,
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
     * Key for access rights for all types of operations, supporting access rights.
     */
    COMMON_ACCESS_RIGHTS,
    /**
     * Key for WF assignments for all types of operations, supporting WF assignments.
     */
    COMMON_WF_ASSIGNMENTS,
    /**
     * Current spring ApplicationContext context, if any. Use with care.
     */
    COMMON_APPLICATION_CONTEXT,
    /**
     * TODO: use it for all types of contexts.
     * Record keys id.
     */
    RECORDS_RECORD_KEYS,
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
     * user exit errors
     */
    PROCESS_ERRORS,
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
     * Upsert result.
     */
    CLASSIFIERS_RESULT,
    /**
     * Ddefault classifiers.
     */
    DEFAULT_CLASSIFIERS,
    /**
     * Storage contains import row num in case when import happens.
     */
    IMPORT_ROW_NUM,
    /**
     * Denotes a context as accepted for batch processing.
     */
    DATA_BATCH_ACCEPT,
    /**
     * Import source (table name or similar).
     */
    IMPORT_RECORD_SOURCE,
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
     * Classifiers data
     */
    CLASSIFIERS_DATA,
    /**
     * Classifier data
     */
    CLASSIFIER_DATA,
    /**
     * vistory operation type.
     */
    DATA_UPSERT_VISTORY_OPERATION_TYPE,
    /**
     *
     */
    DEPENDED_CONTEXTS,

    /**
     * Delete entity by relation delete
     */
    DELETE_BY_RELATION
}
