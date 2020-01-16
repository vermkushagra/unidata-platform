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

package org.unidata.mdm.search.type.id;

import org.unidata.mdm.search.type.IndexType;

/**
 * @author Mikhail Mikhailov
 * Managed index id common stuff.
 */
public interface ManagedIndexId {
    /**
     * Gets search type for this id object.
     * @return the search type
     */
    IndexType getSearchType();
    /**
     * Generates index id for this object.
     * @return full index id
     */
    String getIndexId();
    /**
     * Generates routing string for this object.
     * @return the routing string
     */
    String getRouting();
    /**
     * TODO: Rename method
     * Gets the entity name, the object belongs to.
     * @return the name
     */
    String getEntityName();
}
