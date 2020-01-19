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

import org.unidata.mdm.system.context.StorageCapableContext;
import org.unidata.mdm.system.context.StorageId;

/**
 * Adds containment feature to relation contexts.
 * @author Mikhail Mikhailov on Nov 5, 2019
 */
public interface ContainmentRelationContext<T extends RecordIdentityContext> extends StorageCapableContext {
    /**
     * The containment.
     */
    StorageId SID_CONTAINMENT_CONTEXT = new StorageId("CONTAINMENT_CONTEXT");
    /**
     * Get containment context.
     * @return containment
     */
    default T containmentContext() {
        return getFromStorage(SID_CONTAINMENT_CONTEXT);
    }
    /**
     * Put containment context.
     * @param containment the containment
     */
    default void containmentContext(T containment) {
        putToStorage(SID_CONTAINMENT_CONTEXT, containment);
    }
}
