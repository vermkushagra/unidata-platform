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
public enum SimpleDataType {

    /**
     * Date, as defined in XSD.
     */
    DATE("Date"),
    /**
     * Time, as defined in XSD.
     */
    TIME("Time"),
    /**
     * Date-time, as defined in XSD.
     */
    TIMESTAMP("Timestamp"),
    /**
     * String, as defined in XSD.
     */
    STRING("String"),
    /**
     * Integer, as defined in XSD.
     */
    INTEGER("Integer"),
    /**
     * FP number, as defined in XSD.
     */
    NUMBER("Number"),
    /**
     * Boolean, as defined in XSD.
     */
    BOOLEAN("Boolean"),
    /**
     * BLOB as defined in XSD.
     */
    BLOB("Blob"),
    /**
     * CLOB as defined in XSD.
     */
    CLOB("Clob"),
    /**
     * CLOB as defined in XSD.
     */
    ANY("Any");
    /**
     * The value, really used for marshaling / unmarshaling.
     */
    private final String value;

    private SimpleDataType(String v) {
        value = v;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static SimpleDataType fromValue(String v) {
        if(com.unidata.mdm.meta.SimpleDataType.MEASURED.name().equals(v)){
            return NUMBER;
        }

        for (SimpleDataType c: SimpleDataType.values()) {
            if (StringUtils.equalsIgnoreCase(v, c.value())) {
                return c;
            }
        }
        return null;
    }

}
