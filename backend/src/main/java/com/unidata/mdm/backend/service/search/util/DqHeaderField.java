package com.unidata.mdm.backend.service.search.util;

import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;

/**
 *
 */
public enum DqHeaderField implements SearchField {
    ERROR_ID("errorId"),
    CREATE_DATE("createDate"),
    UPDATE_DATE("updateDate"),
    STATUS("status"),
    RULE_NAME("ruleName"),
    MESSAGE("message"),
    SEVERITY("severity"),
    CATEGORY("category"),
    FIELD("field");

    private String field;

    DqHeaderField(String field) {
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
