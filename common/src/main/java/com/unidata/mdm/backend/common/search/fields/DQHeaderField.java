package com.unidata.mdm.backend.common.search.fields;

import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;

/**
 *
 */
public enum DQHeaderField implements SearchField {
    /**
     * Parent etalon id.
     */
    ERROR_ID("errorId"),
    CREATE_DATE("createDate"),
    UPDATE_DATE("updateDate"),
    STATUS("status"),
    RULE_NAME("ruleName"),
    MESSAGE("message"),
    SEVERITY("severity"),
    CATEGORY("category"),
    EXECUTION_MODE("executionMode"),
    FIELD("field"),
    PATHS("paths");

    private String field;

    DQHeaderField(String field) {
        this.field = field;
    }

    public static String getParentField() {
        return RecordHeaderField.FIELD_DQ_ERRORS.getField();
    }

    public String getDirectField(){
        return field;
    }

    @Override
    public String getField() {
        return getParentField() + "." + field;
    }

    @Override
    public SearchType linkedSearchType() {
        return EntitySearchType.ETALON_DATA;
    }

}
