package com.unidata.mdm.backend.common.search.fields;

import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;

/**
 * Header mark fields for indexed matching information.
 * We have to have support for all possible analysis types in cluster/block data fields,
 * because
 */
public enum MatchingHeaderField implements SearchField {
    /**
     * Parent etalon id.
     */
    FIELD_ETALON_ID("$etalon_id"),
    /**
     * 'from' validity range mark.
     */
    FIELD_FROM("$from"),
    /**
     * 'to' validity range mark.
     */
    FIELD_TO("$to"),
    /**
     * Match block rule id.
     */
    FIELD_RULE_ID("$rule_id"),
    /**
     * Match block/cluster data (array) for exact rules.
     */
    FIELD_EXACT_CLUSTER_DATA("$cluster_data_exact"),
    /**
     * 'created_at' creation date
     */
    FIELD_CREATED_AT("$created_at");

    private final String field;

    MatchingHeaderField(String field) {
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
