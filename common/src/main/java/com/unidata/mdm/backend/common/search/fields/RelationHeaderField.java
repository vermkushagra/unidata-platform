package com.unidata.mdm.backend.common.search.fields;

import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;

/**
 * Header mark fields for indexed relation.
 */
public enum RelationHeaderField implements SearchField {
    /**
     * Left end of relation
     */
    FIELD_ETALON_ID("$etalon_id"),
    /**
     * Left end of relation
     */
    FIELD_FROM_ETALON_ID("$etalon_id_from"),
    /**
     * Right end of relation
     */
    FIELD_TO_ETALON_ID("$etalon_id_to"),
    /**
     * Period id.
     */
    FIELD_PERIOD_ID("$period_id"),
    /**
     * 'from' validity range mark.
     */
    FIELD_FROM("$from"),
    /**
     * 'to' validity range mark
     */
    FIELD_TO("$to"),
    /**
     * Type of relation {@link com.unidata.mdm.meta.RelType}
     */
    REL_TYPE("$type"),
    /**
     * User defined relation name
     */
    REL_NAME("$rel_name"),
    /**
     * 'created_at' creation date
     */
    FIELD_CREATED_AT("$created_at"),
    /**
     * 'updated_at' date of the last update
     */
    FIELD_UPDATED_AT("$updated_at"),
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
    FIELD_DELETED("$deleted");

    private final String field;

    RelationHeaderField(String field) {
        this.field = field;
    }

    /**
     * @return the field
     */
    @Override
    public String getField() {
        return field;
    }

    @Override
    public SearchType linkedSearchType() {
        return EntitySearchType.ETALON_RELATION;
    }
}
