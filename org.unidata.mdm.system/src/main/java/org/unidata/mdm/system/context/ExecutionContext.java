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

/**
 *
 */
package org.unidata.mdm.system.context;

/**
 * @author Mikhail Mikhailov
 * Execution context exposed to the user.
 */
@Deprecated
public interface ExecutionContext {
    /**
     * Puts a value to the context storage.
     * @param name the key
     * @param t the value
     */
    public<T extends Object> void putToUserContext(String name, T t);
    /**
     * Gets a value from the context storage, using supplied key.
     * @param name the key
     * @return object or null
     */
    public<T extends Object> T getFromUserContext(String name);
    /**
     * Gets a (read only) value from environment.
     * @param key the key
     * @return value or null
     */
    public String getFromEnvironment(String key);
}
