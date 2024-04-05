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

package com.unidata.mdm.backend.exchange.def.db;

import com.unidata.mdm.backend.exchange.def.DqErrorsSection;

public class DbJsonDqErrorsSection extends DqErrorsSection {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6121640813210053268L;

    /**
     * table
     */
    private String table;
    /**
     * Source system column.
     */
    private String jsonColumn;


    public String getJsonColumn() {
        return jsonColumn;
    }

    public void setJsonColumn(String jsonColumn) {
        this.jsonColumn = jsonColumn;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}