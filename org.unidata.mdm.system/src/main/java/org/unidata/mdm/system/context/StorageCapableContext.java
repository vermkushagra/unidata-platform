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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Mikhail Mikhailov
 * A context type, which is capable to store something in its internal storage.
 */
public interface StorageCapableContext {
    /**
     * Puts an object to the storage.
     * @param <T> the type of the object to store
     * @param <R> the type of an extending context
     * @param id the storage id
     * @param t the object to store
     * @return self
     */
    <T, R extends StorageCapableContext> R putToStorage(@Nonnull StorageId id, T t);
    /**
     * Gets a stored object from the context storage.
     * @param <T> the object's type
     * @param id the id
     * @return object or null
     */
    @Nullable
    <T> T getFromStorage(@Nonnull StorageId id);
}
