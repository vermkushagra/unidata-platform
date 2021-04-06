/**
 *
 */
package com.unidata.mdm.backend.common.context;

/**
 * @author Mikhail Mikhailov
 * Context related utilities.
 */
public class ContextUtils {
    // Upsert context
    /**
     * Skip sending notification. Default is true.
     */
    public static final int CTX_FLAG_SEND_NOTIFICATION = 0;
    /**
     * Skip matching. Default is false.
     */
    public static final int CTX_FLAG_SKIP_MATCHING = 1;
    /**
     * Skip DQ. Default is false.
     */
    public static final int CTX_FLAG_SKIP_DQ = 2;
    /**
     * Merge with previous. Default is false.
     */
    public static final int CTX_FLAG_MERGE_WITH_PREVIOUS_VERSION = 3;
    /**
     * Skip drop while indexing a record. Default is false.
     */
    public static final int CTX_FLAG_SKIP_INDEX_DROP = 4;
    /**
     * Bypass extension points, i. e. user exits. Default is false.
     */
    public static final int CTX_FLAG_BYPASS_EXTENSION_POINTS = 5;
    /**
     * Recalculate whole timeline instead of affected periods only. Default is false.
     */
    public static final int CTX_FLAG_RECALCULATE_WHOLE_TIMELINE = 6;
    /**
     * Tells whether this upsert is an enrichment. Default is false.
     */
    public static final int CTX_FLAG_IS_ENRICHMENT = 7;
    /**
     * Return etalon upon calculation or not. Default is false.
     */
    public static final int CTX_FLAG_RETURN_ETALON = 8;
    /**
     * Return index context instead of immediate indexing. Default is false.
     */
    public static final int CTX_FLAG_RETURN_INDEX_CONTEXT = 9;
    /**
     * Tells if this context is a restore context. Default is false.
     */
    public static final int CTX_FLAG_IS_RESTORE = 10;
    /**
     * Tells whether to include draft versions. Default is false.
     */
    public static final int CTX_FLAG_INCLUDE_DRAFT_VERSIONS = 11;
    /**
     * Tells whether emission of audit events should be suppressed. Default is false.
     */
    public static final int CTX_FLAG_SUPPRESS_AUDIT = 12;
    // Get context
    /**
     * Tells whether origins elements should be fetched.
     */
    public static final int CTX_FLAG_FETCH_ORIGINS = 20;
    /**
     * Tells whether origins history elements should be fetched.
     */
    public static final int CTX_FLAG_FETCH_ORIGINS_HISTORY = 21;
    /**
     * Tells whether soft deleted elements should be fetched.
     */
    public static final int CTX_FLAG_FETCH_SOFT_DELETED = 22;
    /**
     * Tells whether merged elements should be takenn into account.
     */
    public static final int CTX_FLAG_INCLUDE_MERGED = 23;
    /**
     * Tells whether tasks elements should be fetched.
     */
    public static final int CTX_FLAG_FETCH_TASKS = 24;
    /**
     * Tells whether relations elements should be fetched.
     */
    public static final int CTX_FLAG_FETCH_RELATIONS = 25;
    /**
     * Tells whether classifiers elements should be fetched.
     */
    public static final int CTX_FLAG_FETCH_CLASSIFIERS = 26;
    /**
     * Tells whether inactive elements should be taken into account.
     */
    public static final int CTX_FLAG_INCLUDE_INACTIVE = 27;
    /**
     * Tells whether draft elements should be taken into account.
     */
    public static final int CTX_FLAG_INCLUDE_DRAFTS = 28;
    /**
     * Tells whether periods should be inactivated. Default is false.
     */
    public static final int CTX_FLAG_INACTIVATE_PERIOD = 29;
    /**
     * Tells whether origin should be inactivated. Default is false.
     */
    public static final int CTX_FLAG_INACTIVATE_ORIGIN = 30;
    /**
     * Tells whether etalon should be inactivated. Default is false.
     */
    public static final int CTX_FLAG_INACTIVATE_ETALON = 31;
    /**
     * Tells whether this context is a WF action. Default is false.
     */
    public static final int CTX_FLAG_WORKFLOW_ACTION = 32;
    /**
     * Tells whether records should be inactivated in cascade fashon. Default is false.
     */
    public static final int CTX_FLAG_INACTIVATE_CASCADE = 33;
    /**
     * Tells whether data should be physically removed from storage. Default is false.
     */
    public static final int CTX_FLAG_INACTIVATE_WIPE = 34;
    /**
     * This context is participating in a batch upsert. Collect upsert artifacts instead of upserting immediately. Default is false.
     */
    public static final int CTX_FLAG_BATCH_UPSERT = 35;
    /**
     * Tells whether information about winner attributes should be included
     */
    public static final int CTX_FLAG_INCLUDE_WINNERS= 36;
    /**
     * Tells whether this upsert opeartion is part of initial load process.
     */
    public static final int CTX_FLAG_INITIAL_LOAD = 37;
    /**
     * Use strictDrafts value without check by author record name
     */
    public static final int CTX_FLAG_STRICT_DRAFT = 38;
    /**
     * Tells whether executors needs to run
     */
    public static final int CTX_FLAG_RUN_EXECUTORS = 39;
    /**
     * Tells whether diff to draft state must be calculated and returned.
     */
    public static final int CTX_FLAG_DIFF_TO_DRAFT = 40;
    /**
     * Tells whether diff to previous etalon state must be calculated and returned.
     */
    public static final int CTX_FLAG_DIFF_TO_PREVIOUS = 41;
    /**
     * Tells whether clusters should be fetched.
     */
    public static final int CTX_FLAG_FETCH_CLUSTERS = 42;
    /**
     * Skip consistency checks, performed by DQ. Default is false.
     */
    public static final int CTX_FLAG_SKIP_CONSISTENCY_CHECKS = 43;
    /**
     * Tells whether large objects should be fetched .
     */
    public static final int CTX_FLAG_FETCH_LARGE_OBJECTS = 44;

    /**
     * Constructor.
     */
    private ContextUtils() {
        super();
    }

    /**
     * Copies system values from one context to another.
     * @param from the from
     * @param to the to
     * @param fields set of fields
     */
    public static void storageCopy(CommonRequestContext from, CommonRequestContext to, StorageId... fields) {
        for (StorageId id : fields) {
            to.putToStorage(id, from.getFromStorage(id));
        }
    }

    /**
     * Copies user values from one context to another.
     * @param from the from
     * @param to the to
     */
    public static void userCopy(CommonRequestContext from, CommonRequestContext to) {
        to.copyFromUserContext(from);
    }
}
