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
