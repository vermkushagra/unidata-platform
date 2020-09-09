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

package com.unidata.mdm.backend.api.rest.dto;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Supported simple data types.
 */
public enum CodeDataType {
    /**
     * String, as defined in XSD.
     */
    STRING("String"),
    /**
     * Integer, as defined in XSD.
     */
    INTEGER("Integer");
    /**
     * The value, really used for marshaling / unmarshaling.
     */
    private final String value;
    /**
     * Constructor.
     * @param v the value
     */
    private CodeDataType(String v) {
        value = v;
    }
    /**
     * @return the value
     */
    @JsonValue
    public String value() {
        return value;
    }
    /**
     * From value creator.
     * @param v the value
     * @return enum instance
     */
    @JsonCreator
    public static CodeDataType fromValue(String v) {
        for (CodeDataType c: CodeDataType.values()) {
            if (StringUtils.equals(v, c.value())) {
                return c;
            }
        }
        return null;
    }
}
