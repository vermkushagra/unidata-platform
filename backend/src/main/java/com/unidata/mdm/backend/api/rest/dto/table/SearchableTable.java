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

package com.unidata.mdm.backend.api.rest.dto.table;

public class SearchableTable extends Table{

    private String entityName;
    //todo add search type!

    private String rowSearchName;

    private String columnSearchName;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getRowSearchName() {
        return rowSearchName;
    }

    public void setRowSearchName(String rowSearchName) {
        this.rowSearchName = rowSearchName;
    }

    public String getColumnSearchName() {
        return columnSearchName;
    }

    public void setColumnSearchName(String columnSearchName) {
        this.columnSearchName = columnSearchName;
    }
}
