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
 * @author Mikhail Mikhailov on Oct 7, 2019
 * _Lots_ of boolean flags - interface to internal bitset.
 */
public interface BooleanFlagsContext {
    /**
     * Sets a known flag to true.
     * @param flag the flag to set
     */
    void setFlag(int flag);
    /**
     * Sets a known flag true or false, according to given state.
     * @param flag the flag to set
     * @param state the state
     */
    default void setFlag(int flag, boolean state) {
        if (state) {
            setFlag(flag);
        } else {
            clearFlag(flag);
        }
    }
    /**
     * Sets a known flag to false.
     * @param flag the flag to clear
     */
    void clearFlag(int flag);
    /**
     * Returns the value of the given flag.
     * @param flag the flag id
     * @return boolean value
     */
    boolean getFlag(int flag);
}
