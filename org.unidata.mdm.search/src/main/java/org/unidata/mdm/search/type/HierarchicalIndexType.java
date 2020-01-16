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

package org.unidata.mdm.search.type;

import javax.annotation.Nonnull;

/**
 * Special extension of search type, which detect that search type is included in a hierarchical structure (directed graph).
 * Notice: now it used only for directed graph with one level of leafs, and should be extended if it will be needed.
 */
public interface HierarchicalIndexType extends IndexType {
    /**
     * @return top element of a directed graph.
     */
    @Nonnull
    HierarchicalIndexType getTopType();
    /**
     * @return true if search type is a top type of a hierarchical structure.
     */
    boolean isTopType();
    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isHierarchical() {
        return true;
    }
}
