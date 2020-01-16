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

package org.unidata.mdm.system.context;

/**
 * @author Mikhail Mikhailov
 * Object id for transient values in a context.
 */
public final class StorageId {
    /**
     * The name of the ID.
     */
    private final String name;
    /**
     * Constructor.
     * @param name the name of the ID
     */
    public StorageId(String name) {
        this.name = name;
    }
    /**
     * Gets the tag of this ID.
     * @return TAG/name of this id
     */
    String getName() {
        return name;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }
}
