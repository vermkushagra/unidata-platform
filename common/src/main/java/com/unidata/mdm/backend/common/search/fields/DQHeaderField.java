/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
