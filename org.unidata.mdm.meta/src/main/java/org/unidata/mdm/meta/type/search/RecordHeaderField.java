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
package org.unidata.mdm.meta.type.search;

import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.IndexField;
import org.unidata.mdm.search.type.IndexType;

/**
 * @author Mikhail Mikhailov
 * Header mark fields for indexed records.
 */
public enum RecordHeaderField implements IndexField {
    /**
     * Special not analyzed field value mark.
     */
    FIELD_NOT_ANALYZED("$nan", FieldType.STRING),
    /**
     * 'from' validity range mark.
     */
    FIELD_FROM("$from", FieldType.TIMESTAMP),
    /**
     * 'to' validity range mark
     */
    FIELD_TO("$to", FieldType.TIMESTAMP),
    /**
     * 'created_at' creation date
     */
    FIELD_CREATED_AT("$created_at", FieldType.TIMESTAMP),
    /**
     * 'updated_at' date of the last update
     */
    FIELD_UPDATED_AT("$updated_at", FieldType.TIMESTAMP),
    /**
     * Has errors mark.
     */
    FIELD_ERRORS("$errors", FieldType.BOOLEAN),
    /**
     *List of data quality validation errors.
     */
    FIELD_DQ_ERRORS("$dq_errors", FieldType.COMPOSITE),
    /**
     * Is pending mark.
     */
    FIELD_PENDING("$pending", FieldType.BOOLEAN),
    /**
     * Is published mark.
     */
    FIELD_PUBLISHED("$published", FieldType.BOOLEAN),
    /**
     * Is deleted mark.
     */
    FIELD_DELETED("$deleted", FieldType.BOOLEAN),
    /**
     * Is period inactive mark.
     */
    FIELD_INACTIVE("$inactive", FieldType.BOOLEAN),
    /**
     * Originator field.
     */
    FIELD_ORIGINATOR("$originator", FieldType.STRING),
    /**
     * Special etalon id value mark.
     */
    FIELD_ETALON_ID("$etalon_id", FieldType.STRING),
    /**
     * Period id.
     */
    FIELD_PERIOD_ID("$period_id", FieldType.STRING),
    /**
     * operation type of vistory
     */
    FIELD_OPERATION_TYPE("$operation_type", FieldType.STRING),
    /**
     * external keys
     */
    FIELD_EXTERNAL_KEYS("$external_keys", FieldType.STRING);

    RecordHeaderField(String field, FieldType type) {
        this.field = field;
        this.type = type;
    }
    /**
     * The name.
     */
    private final String field;
    /**
     * The type.
     */
    private final FieldType type;
    /**
     * {@inheritDoc}
     */
    @Override
    public IndexType getType() {
        return EntityIndexType.RECORD;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return type;
    }
    /**
     * @return the field
     */
    @Override
    public String getName() {
        return field;
    }
}
