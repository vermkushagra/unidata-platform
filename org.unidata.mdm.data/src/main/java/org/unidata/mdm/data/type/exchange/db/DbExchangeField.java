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

import java.sql.Array;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.unidata.mdm.data.type.exchange.ExchangeField;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author Mikhail Mikhailov
 *
 */
public class DbExchangeField extends ExchangeField {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 7581999574099928870L;

    /**
     * Column name.
     */
    private String column;

    /**
     * SQL alias name.
     */
    private String alias;

    /**
     * Column type.
     */
    private String type;
    /**
     * Type class.
     */
    @JsonIgnore
    private Class<?> typeClazz;
    /**
     * Constructor.
     */
    public DbExchangeField() {
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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
        try {
            typeClazz = Class.forName(type);
            if (typeClazz != Boolean.class
             && typeClazz != String.class
             && typeClazz != Long.class
             && typeClazz != Integer.class
             && typeClazz != Float.class
             && typeClazz != Double.class
             && typeClazz != Date.class
             && typeClazz != java.util.Date.class
             && typeClazz != Time.class
             && typeClazz != Timestamp.class
             && typeClazz != Array.class) {
                System.err.println("Type '" + typeClazz.getName() + "' is not supported as conversion type!");
                throw new IllegalArgumentException("Type '" + typeClazz.getName() + "' is not supported as conversion type!");
            }
        } catch (Throwable th) {
            System.err.println("Cannot initialize class for type '" + type +
                    "'. Class is either not allowed or not found. Value conversion will be not functioning!");
            typeClazz = null;
        }
    }

    /**
     * @return the typeClazz
     */
    @JsonIgnore
    public Class<?> getTypeClazz() {
        return typeClazz;
    }


    /**
     * @return the alias
     */
    public String getAlias() {
        return alias == null ? getName() : alias;
    }


    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
