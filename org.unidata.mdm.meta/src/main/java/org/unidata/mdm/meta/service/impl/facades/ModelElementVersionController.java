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

package org.unidata.mdm.meta.service.impl.facades;

import org.unidata.mdm.meta.VersionedObjectDef;

/**
 * Responsible for meta model elements version controlling.
 *
 * @param <V> marker param , which show that all top level meta model elements allow version control
 */
public interface ModelElementVersionController<V extends VersionedObjectDef> {

    /**
     * Increment version or set in initial state if before it wasn't present!
     *
     * @param modelElement -  first citizen model element.(top level model element)
     */
    void updateVersion(V modelElement);

    /**
     * Set version to initial state
     *
     * @param modelElement -  first citizen model element.(top level model element)
     */
    void setInitialVersion(V modelElement);

}
