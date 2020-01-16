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

package org.unidata.mdm.data.type.exchange.db;

import java.util.ArrayList;
import java.util.List;

import org.unidata.mdm.data.type.exchange.ExchangeField;

/**
 * @author Alexey Tsarapkin
 * Exchange field for import multiple complex attributes
 */
public class DbExchangeComplexField extends DbExchangeField {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Parent key column for select query.
     */
    private String parentKeyColumn;

    /**
     * Select complex attributes query.
     */
    private String query;

    /**
     * Complex attribute fields.
     */
    private List<ExchangeField> fields;

    /**
     * Get the parentKeyColumn.
     *
     * @return the parentKeyColumn
     */
    public String getParentKeyColumn() {
        return parentKeyColumn;
    }

    /**
     * Set the parentKeyColumn.
     *
     */
    public void setParentKeyColumn(String parentKeyColumn) {
        this.parentKeyColumn = parentKeyColumn;
    }

    /**
     * Get the query.
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the query.
     *
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Gets the fields.
     *
     * @return the fields
     */
    public List<ExchangeField> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    /**
     * Sets the fields.
     *
     * @param fields the fields to set
     */
    public void setFields(List<ExchangeField> fields) {
        this.fields = fields;
    }
}
