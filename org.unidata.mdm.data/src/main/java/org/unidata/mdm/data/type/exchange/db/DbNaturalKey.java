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
package org.unidata.mdm.data.type.exchange.db;

import org.unidata.mdm.data.type.exchange.NaturalKey;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author Mikhail Mikhailov
 * DB natural key.
 */
public class DbNaturalKey extends NaturalKey {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5572681031691583097L;
    /**
     * Column name.
     */
    private String column;
    /**
     * Column type.
     */
    private String type;
    /**
     * Select alias.
     */
    private String alias;
    /**
     * Hack.
     */
    private String sqlAdditionLeft;
    /**
     * Hack.
     */
    private String sqlAdditionRight;
    /**
     * Type class.
     */
    @JsonIgnore
    private Class<?> typeClazz;

    /**
     * Constructor.
     */
    public DbNaturalKey() {
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
             && typeClazz != Double.class) {
                System.err.println("Type '" + typeClazz.getName() + "' is not supported as natural key type!");
                throw new IllegalArgumentException("Type '" + typeClazz.getName() + "' is not supported as natural key type!");
            }
        } catch (Throwable th) {
            System.err.println("Cannot initialize value type for type '" + "'. Natural key will be not functionaing!");
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
        return alias;
    }



    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }



    /**
     * @return the sqlAdditionLeft
     */
    public String getSqlAdditionLeft() {
        return sqlAdditionLeft;
    }



    /**
     * @param sqlAdditionLeft the sqlAdditionLeft to set
     */
    public void setSqlAdditionLeft(String sqlAdditionLeft) {
        this.sqlAdditionLeft = sqlAdditionLeft;
    }



    /**
     * @return the sqlAdditionRight
     */
    public String getSqlAdditionRight() {
        return sqlAdditionRight;
    }



    /**
     * @param sqlAdditionRight the sqlAdditionRight to set
     */
    public void setSqlAdditionRight(String sqlAdditionRight) {
        this.sqlAdditionRight = sqlAdditionRight;
    }

}
