/**
 *
 */
package com.unidata.mdm.api.wsdl.v4;

/**
 * @author Mikhail Mikhailov
 * Operation IDs for SOAP operations in the global request.
 */
public enum SoapOperation {

    /**
     * Auth request.
     */
    REQUEST_AUTHENTICATE,
    /**
     * Request Cleanse.
     */
    REQUEST_CLEANSE,
    /**
     * Request Get (data objects).
     */
    REQUEST_GET,
    /**
     * Request Get All periods objects (data objects).
     */
    REQUEST_GET_ALL_PERIODS,
    /**
     * Request Get info.
     */
    REQUEST_INFO_GET,
    /**
     * Request Get (relation).
     */
    REQUEST_GET_REL,
    /**
     * Request get data quality errors.
     */
    REQUEST_GET_DATA_QUALITY_ERRORS,
    /**
     * Request get lookup values.
     */
    REQUEST_GET_LOOKUP_VALUES,
    /**
     * Request Merge.
     */
    REQUEST_MERGE,
    /**
     * Request Join.
     */
    REQUEST_JOIN,
    /**
     * Request Merge preview.
     */
    REQUEST_MERGE_PREVIEW,
    /**
     * Request Meta get cleanse function description.
     */
    REQUEST_META_GET_CLEANSE_FUNCTION_DESC,
    /**
     * Request meta get cleanse function list.
     */
    REQUEST_META_GET_CLEANSE_FUNCTION_LIST,
    /**
     * Request search.
     */
    REQUEST_SEARCH,
    /**
     * Request soft delete.
     */
    REQUEST_SOFT_DELETE,
    /**
     * Request soft delete relation.
     */
    REQUEST_SOFT_DELETE_REL,
    /**
     * Request upsert.
     */
    REQUEST_UPSERT,
    /**
     * Request bulk upsert
     */
    REQUEST_BULK_UPSERT,
    /**
     * Request upsert relation.
     */
    REQUEST_UPSERT_REL
}
