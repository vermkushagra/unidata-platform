package com.unidata.mdm.backend.api.rest.dto.search;

import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;

public class SearchSortFieldRO {

    /**
     * sort field name
     */
    private String field;
    /**
     * order - ASC/DESC
     */
    private String order;
    /**
     * data type for sorting
     */
    private SimpleDataType type;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public SimpleDataType getType() {
        return type;
    }

    public void setType(SimpleDataType type) {
        this.type = type;
    }
}
