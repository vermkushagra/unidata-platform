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

import org.unidata.mdm.search.type.IndexType;

/**
 * @author Mikhail Mikhailov on Oct 11, 2019
 */
public enum ModelIndexType implements IndexType {
    /**
     * Classifier data.
     */
    MODEL("model_element");
    /**
     * Index name.
     */
    public static final String INDEX_NAME = "model";
    /**
     * Name of type
     */
    private final String type;
    /**
     * Constructor.
     * @param type the name of the type
     */
    ModelIndexType(String type) {
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
        return false;
    }
}
