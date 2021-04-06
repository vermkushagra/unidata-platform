package com.unidata.mdm.backend.service.search.util;

import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.types.SearchType;
import com.unidata.mdm.backend.common.search.types.ServiceSearchType;

/**
 * @author Mikhail Mikhailov
 *         Header mark fields for indexed meta model data.
 */
public enum ModelHeaderField implements SearchField {
    /**
     * Type of search element in model
     */
    SEARCH_OBJECT("$search_object"),
    /**
     * Entity or lookup entity name
     */
    ENTITY_NAME("name"),
    /**
     * Entity or lookup entity display name
     */
    DISPLAY_ENTITY_NAME("displayName"),
    /**
     * value of search element in model
     */
    VALUE("value");

    private String field;

    ModelHeaderField(String field) {
        this.field = field;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public SearchType linkedSearchType() {
        return ServiceSearchType.MODEL;
    }
}
