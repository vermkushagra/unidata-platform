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
 * @author Mikhail Mikhailov
 * Classifier data fields.
 */
public enum ClassifierDataHeaderField implements SearchField {
    /**
     * Parent etalon id.
     */
    FIELD_ETALON_ID("$etalon_id"),
    /**
     * Parent record etalon id.
     */
    FIELD_ETALON_ID_RECORD("$etalon_id_record"),
    /**
     * Period id.
     */
    FIELD_PERIOD_ID("$period_id"),
    /**
     * Classifiers digest
     */
    FIELD_CLASSIFIERS("$classifiers"),
    /**
     * Classifer nodes
     */
    FIELD_NODES("$nodes"),
    /**
     * Classifier name.
     */
    FIELD_NAME("$name"),
    /**
     * Classifer node ids (array).
     */
    FIELD_NODE_ID("$node_id"),
    /**
     * Classifer root node if.
     */
    FIELD_ROOT_NODE_ID("$root_id"),
    /**
     * 'from' validity range mark.
     */
    FIELD_FROM("$from"),
    /**
     * 'to' validity range mark.
     */
    FIELD_TO("$to"),
    /**
     * 'created_at' creation date
     */
    FIELD_CREATED_AT("$created_at"),
    /**
     * 'updated_at' update date
     */
    FIELD_UPDATED_AT("$updated_at"),
    /**
     * 'created_by' creation date
     */
    FIELD_CREATED_BY("$created_by"),
    /**
     * 'updated_by' update date
     */
    FIELD_UPDATED_BY("$updated_by"),
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

    FIELD_CLS_NESTED_ATTRS("cls_attrs"),

    FIELD_CLS_ATTR_NAME("$attr_name"),

    FIELD_CLS_ATTR_VALUE_PREFIX("value_as_");


    /**
     * Field.
     */
    private final String field;
    /**
     * Constructor.
     * @param field the field name
     */
    ClassifierDataHeaderField(String field) {
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
        return EntitySearchType.CLASSIFIER;
    }
}
