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
 * Classifier data fields.
 */
public enum ModelHeaderField implements IndexField {
    /**
     * Parent etalon id.
     */
    FIELD_SEARCH_OBJECTS("$search_objects", FieldType.STRING),
    /**
     * Parent record etalon id.
     */
    FIELD_DISPLAY_NAME("displayName", FieldType.STRING),
    /**
     * Period id.
     */
    FIELD_NAME("name", FieldType.STRING),
    /**
     * Classifiers digest
     */
    FIELD_VALUE("value", FieldType.STRING);
    /**
     * Field.
     */
    private final String field;
    /**
     * The type.
     */
    private final FieldType type;
    /**
     * Constructor.
     * @param field the field name
     */
    ModelHeaderField(String field, FieldType type) {
        this.field = field;
        this.type = type;
    }
    /**
     * @return the field
     */
    @Override
    public String getName() {
        return field;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return type;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public IndexType getType() {
        return ModelIndexType.MODEL;
    }
}
