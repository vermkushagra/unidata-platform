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

import java.sql.Timestamp;

import org.unidata.mdm.data.type.exchange.UpdateMark;
import org.unidata.mdm.data.type.exchange.UpdateMarkType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Mikhail Mikhailov
 * Db update mark.
 */
public class DbUpdateMark extends UpdateMark {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6121640813210053268L;
    /**
     * Column name.
     */
    private String column;
    /**
     * Source system column.
     */
    private String sourceSystemColumn;
    /**
     * Select alias.
     */
    private String alias;
    /**
     * Constructor.
     */
    public DbUpdateMark() {
        super();
    }
    /**
     * @return the column
     */
    public String getColumn() {
        return column;
    }
    /**
     * @param column the column to set
     */
    public void setColumn(String column) {
        this.column = column;
    }
    /**
     * @return the sourceSystemColumn
     */
    public String getSourceSystemColumn() {
        return sourceSystemColumn;
    }
    /**
     * @param sourceSystemColumn the sourceSystemColumn to set
     */
    public void setSourceSystemColumn(String sourceSystemColumn) {
        this.sourceSystemColumn = sourceSystemColumn;
    }
    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }
    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }
    /**
     * @return the typeClazz
     */
    @JsonIgnore
    public Class<?> getTypeClazz() {
        return getUpdateMarkType() == UpdateMarkType.TIMESTAMP
                ? Timestamp.class
                : getUpdateMarkType() == UpdateMarkType.BOOLEAN
                    ? Boolean.class
                    : null;
    }
}
