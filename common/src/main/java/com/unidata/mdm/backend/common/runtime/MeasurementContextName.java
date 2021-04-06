/**
 *
 */
package com.unidata.mdm.backend.common.runtime;

/**
 * @author Mikhail Mikhailov
 *         Measurement context names.
 */
//TODO made it as interface, and separate different end points to grouped classed
public enum MeasurementContextName {
    /**
     * SOAP Get.
     */
    MEASURE_SOAP_GET,
    /**
     * SOAP Get.
     */
    MEASURE_SOAP_GET_ALL_PERIODS,
    /**
     * SOAP Get info.
     */
    MEASURE_SOAP_GET_INFO,
    /**
     * SOAP Get.
     */
    MEASURE_SOAP_RELATIONS_GET,
    /**
     * SOAP upsert.
     */
    MEASURE_SOAP_UPSERT,
    /**
     * SOAP bulk upsert!
     */
    MEASURE_SOAP_BULK_UPSERT,
    /**
     * SOAP Relations upsert.
     */
    MEASURE_SOAP_RELATIONS_UPSERT,
    /**
     * SOAP soft delete.
     */
    MEASURE_SOAP_DELETE,
    /**
     * SOAP Relations delete.
     */
    MEASURE_SOAP_RELATIONS_DELETE,
    /**
     * SOAP Auth.
     */
    MEASURE_SOAP_AUTH,
    /**
     * SOAP Search.
     */
    MEASURE_SOAP_SEARCH,
    /**
     * REST Get.
     */
    MEASURE_UI_GET,
    /**
     * REST Create.
     */
    MEASURE_UI_CREATE,
    /**
     * REST Update.
     */
    MEASURE_UI_UPDATE,
    /**
     * REST Delete etalon
     */
    MEASURE_UI_DELETE_BY_ETALON,
    /**
     * REST Delete origin
     */
    MEASURE_UI_DELETE_BY_ORIGIN,
    /**
     * REST Delete period
     */
    MEASURE_UI_DELETE_BY_PERIOD,
    /**
     * REST Merge.
     */
    MEASURE_UI_MERGE,
    /**
     * REST Merge.
     */
    MEASURE_UI_MERGE_PREVIEW,
    /**
     * REST restore.
     */
    MEASURE_UI_RESTORE,
    /**
     * REST relations Get.
     */
    MEASURE_UI_RELATIONS_GET,
    /**
     * REST relations contains upsert.
     */
    MEASURE_UI_RELATIONS_INTEGRAL_UPSERT,
    /**
     * REST RelTo upsert endpoint.
     */
    MEASURE_UI_RELATIONS_TO_UPSERT,
    /**
     * REST relations etalon delete.
     */
    MEASURE_UI_RELATIONS_ETALON_DELETE,
    /**
     * REST relations origin delete.
     */
    MEASURE_UI_RELATIONS_ORIGIN_DELETE,
    /**
     * REST relations version delete.
     */
    MEASURE_UI_RELATIONS_VERSION_DELETE,
    /**
     * REST Auth.
     */
    MEASURE_UI_AUTH,
    /**
     * REST Search and XLS export simple.
     */
    MEASURE_UI_SEARCH_EXPORT_SIMPLE,
    /**
     * REST Search and XLS export form.
     */
    MEASURE_UI_SEARCH_EXPORT_FORM,
    /**
     * REST Search simple.
     */
    MEASURE_UI_SEARCH_SIMPLE,
    /**
     * REST Search form.
     */
    MEASURE_UI_SEARCH_FORM,
    /**
     * REST Search combo.
     */
    MEASURE_UI_SEARCH_COMBO,
    /**
     * REST Search complex.
     */
    MEASURE_UI_SEARCH_COMPLEX,
    /**
     * REST Search SAYT.
     */
    MEASURE_UI_SEARCH_SAYT,
    /**
     * Record set upsert from utility.
     */
    MEASURE_UTIL_RECORDS_UPSERT,
    /**
     * Relation set upsert from utility.
     */
    MEASURE_UTIL_RELS_UPSERT,
    /**
     * REST complete.
     */
    MEASURE_UI_COMPLETE,
    /**
     * REST tasks.
     */
    MEASURE_UI_TASKS,
    /**
     * REST bulk operations list.
     */
    MEASURE_UI_BULK_LIST,
    /**
     * REST bulk operation configure.
     */
    MEASURE_UI_BULK_CONFIGURE,
    /**
     * REST bulk operation run.
     */
    MEASURE_UI_BULK_RUN,
    /**
     * REST user notifications.
     */
    MEASURE_UI_GET_USER_NOTIFICATIONS,
    /**
     * REST user notifications count.
     */
    MEASURE_UI_COUNT_USER_NOTIFICATIONS,
    /**
     * REST user notifications delete.
     */
    MEASURE_UI_DELETE_USER_NOTIFICATION,
    /**
     * REST selected user notifications delete.
     */
    MEASURE_UI_DELETE_SELECTED_USER_NOTIFICATIONS,
    /**
     * REST all user notifications delete.
     */
    MEASURE_UI_DELETE_ALL_USER_NOTIFICATIONS,
    /**
     * BL etalon re-application.
     */
    MEASURE_BL_ETALONS_REAPPLY,
    /**
     * BL etalon calculation.
     */
    MEASURE_BL_ETALON_CALCULATION,
    /**
     * Matching fucntion
     */
    MEASURE_MATCHING,
    /**
     * Reindex step
     */
    MEASURE_STEP_REINDEX,
    /**
     * Reindex meta steps
     */
    MEASURE_STEP_META_REINDEX,
    /**
     * stat step
     */
    MEASURE_STEP_STAT,
    /**
     * duplicate step
     */
    MEASURE_STEP_DUPLICATE,
    /**
     * delete step
     */
    MEASURE_STEP_DELETE,
    /**
     * republish step
     */
    MEASURE_STEP_REPUBLISH,
    /**
     * modify step
     */
    MEASURE_STEP_MODIFY,
    /**
     * remove step
     */
    MEASURE_STEP_REMOVE,
    /**
     * remove step
     */
    MEASURE_STEP_REMOVE_RELATIONS,
    /**
     * import step
     */
    MEASURE_STEP_IMPORT_RECORDS,
    /**
     * import relations
     */
    MEASURE_STEP_IMPORT_RELATIONS,
    /**
     * export records
     */
    MEASURE_STEP_EXPORT_RECORDS,
    /**
     * matching records
     */
    MEASURE_STEP_MATCHING,
    /**
     * SOAP apply dq.
     */
    MEASURE_DQ_SOAP_APPLY,
    /**
     * REST apply dq.
     */
    MEASURE_DQ_REST_APPLY,
    /**
     * Update indexes.
     */
    MEASURE_STEP_UPDATE_MAPPING;
}
