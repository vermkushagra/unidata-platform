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

import javax.annotation.Nonnull;

import org.unidata.mdm.core.type.search.DataIndexType;
import org.unidata.mdm.search.type.IndexType;

/**
 * Index type for data entity indexes.
 */
public enum EntityIndexType implements DataIndexType {
    /**
     * Etalon type - the top type.
     */
    ETALON("etalon"){
        @Override
        public boolean isTopType() {
            return true;
        }
    },
    /**
     * Data type
     */
    RECORD("data"),
    /**
     * Relation type
     */
    RELATION("relation");
    /**
     * Name of type
     */
    private final String type;
    /**
     * Constructor.
     * @param type the name of the type
     */
    EntityIndexType(String type) {
        this.type = type;
    }
    /**
     * @return name of type
     */
    @Nonnull
    @Override
    public String getName() {
        return type;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRelated(IndexType searchType) {
        return searchType instanceof DataIndexType;
    }
    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public DataIndexType getTopType() {
        return EntityIndexType.ETALON;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTopType() {
        return false;
    }
}
