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

package org.unidata.mdm.search.type.mapping.impl;

import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.mapping.MappingField;

/**
 * @author Mikhail Mikhailov on Oct 7, 2019
 * Name.
 */
public abstract class AbstractMappingField<X extends AbstractMappingField<X>> implements MappingField {
    /**
     * The name.
     */
    private final String name;
    /**
     * IT.
     */
    private IndexType indexType;
    /**
     * Constructor.
     */
    public AbstractMappingField(String name) {
        super();
        this.name = name;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public IndexType getType() {
        return indexType;
    }
    /**
     * @param indexType the indexType to set
     */
    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }
    /**
     * Sets index type fluently.
     * @param type the type
     * @return self
     */
    public X withIndexType(IndexType type) {
        setIndexType(type);
        return self();
    }
    /**
     * Self cast.
     * @return
     */
    @SuppressWarnings("unchecked")
    protected X self() {
        return (X) this;
    }
}
