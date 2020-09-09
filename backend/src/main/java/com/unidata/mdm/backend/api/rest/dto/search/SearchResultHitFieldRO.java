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

/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.search;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Hit fields container.
 */
public class SearchResultHitFieldRO {

    /**
     * The field name.
     */
    private final String field;
    /**
     * Field value. First value from the search hits array.
     */
    private final Object value;
    /**
     * Multiple values.
     */
    private final List<Object> values;
    /**
     * Constructor.
     */
    public SearchResultHitFieldRO(final String field, final Object value, final List<Object> values) {
        super();
        this.field = field;
        this.value = value;
        this.values = values;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return the values
     */
    public List<Object> getValues() {
        return values;
    }

}
