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

package org.unidata.mdm.data.context;

import javax.annotation.Nullable;

import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.system.context.StorageCapableContext;
import org.unidata.mdm.system.context.StorageId;

/**
 * A context, holding access rights to the subject, it operates on (an entity, lookup, rel, clsf etc.).
 * @author Mikhail Mikhailov on Nov 6, 2019
 */
public interface AccessRightContext extends StorageCapableContext {
    /**
     * Access rights instance.
     */
    StorageId SID_ACCESS_RIGHT = new StorageId("ACCESS_RIGHT");
    /**
     * Gets access right.
     * @return right or null
     */
    @Nullable
    default<R extends Right> R accessRight() {
        return getFromStorage(SID_ACCESS_RIGHT);
    }
    /**
     * Sets access right for this context.
     * @param right the right
     */
    default void accessRight(Right right) {
        putToStorage(SID_ACCESS_RIGHT, right);
    }
}
