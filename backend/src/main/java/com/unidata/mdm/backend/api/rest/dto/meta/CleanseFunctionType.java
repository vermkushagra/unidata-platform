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

package com.unidata.mdm.backend.api.rest.dto.meta;

/**
 * The Enum CleanseFunctionType.
 *
 * @author Michael Yashin. Created on 20.05.2015.
 */
public enum CleanseFunctionType {

    /** The simple function. */
    BASIC_FUNCTION("BASIC_FUNCTION"),
    /** Custom cleanse function(loaded from JAR file) */
    CUSTOM_FUNCTION("CUSTOM_FUNCTION"),
    /** The composite function. */
    COMPOSITE_FUNCTION("COMPOSITE_FUNCTION");

    /** The value. */
    private final String value;

    /**
     * Instantiates a new cleanse function type.
     *
     * @param v
     *            the v
     */
    CleanseFunctionType(String v) {
        value = v;
    }

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return value;
    }

    /**
     * From value.
     *
     * @param v
     *            the v
     * @return the cleanse function type
     */
    public static CleanseFunctionType fromValue(String v) {
        for (CleanseFunctionType c : CleanseFunctionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
