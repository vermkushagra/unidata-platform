/**
 *
 */
package com.unidata.mdm.backend.common.search.fields;

import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;

/**
 * @author Mikhail Mikhailov
 * Header mark fields for indexed records.
 */
public enum RecordHeaderField implements SearchField {
    /**
     * Special not analyzed field value mark.
     */
    FIELD_NOT_ANALYZED("$nan"),
    /**
     * 'from' validity range mark.
     */
    FIELD_FROM("$from"),
    /**
     * 'to' validity range mark
     */
    FIELD_TO("$to"),
    /**
     * 'created_at' creation date
     */
    FIELD_CREATED_AT("$created_at"),
    /**
     * 'updated_at' date of the last update
     */
    FIELD_UPDATED_AT("$updated_at"),
    /**
     * Has errors mark.
     */
    FIELD_ERRORS("$errors"),
    /**
     *List of data quality validation errors.
     */
    FIELD_DQ_ERRORS("$dq_errors"),
    /**
     * Is pending mark.
     */
    FIELD_PENDING("$pending"),
    /**
     * Is published mark.
     */
    FIELD_PUBLISHED("$published"),
    /**
     * Is deleted mark.
     */
    FIELD_DELETED("$deleted"),
    /**
     * Is period inactive mark.
     */
    FIELD_INACTIVE("$inactive"),
    /**
     * Originator field.
     */
    FIELD_ORIGINATOR("$originator"),
    /**
     * Special etalon id value mark.
     */
    FIELD_ETALON_ID("$etalon_id"),
    /**
     * Period id.
     */
    FIELD_PERIOD_ID("$period_id"),
    /**
     * operation type of vistory
     */
    FIELD_OPERATION_TYPE("$operation_type");

    private RecordHeaderField(String field) {
        this.field = field;
    }

    private final String field;

    @Override
    public SearchType linkedSearchType() {
        return EntitySearchType.ETALON_DATA;
    }

    /**
     * @return the field
     */
    @Override
    public String getField() {
        return field;
    }
}
