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

/**
 * @author Mikhail Mikhailov on Oct 7, 2019
 * Type of a simple value.
 */
public enum FieldType {
    BOOLEAN,
    DATE,
    TIME,
    TIMESTAMP,
    INSTANT,
    INTEGER,
    NUMBER,
    STRING,
    COMPOSITE,  // Special field type, containing other fields.
    ANY;        // Special field type, marking an untyped SE. This is used for queries. FIXME: Rename to NONE, what is more precise.

    /**
     * Creates instance from JAXB friendly value
     * @param v the value
     * @return enum instamce
     */
    public static FieldType fromValue(String v) {

        for (FieldType c: FieldType.values()) {
            if (c.name().equalsIgnoreCase(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
