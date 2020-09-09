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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DQApplicableDefinition {
    
    /** The etalon. */
    ETALON("ETALON"),
    
    /** The origin. */
    ORIGIN("ORIGIN");
    
    /** The value. */
    private final String value;

    /**
     * Instantiates a new DQ action definition.
     *
     * @param v the v
     */
    DQApplicableDefinition(String v) {
        value = v;
    }

    /**
     * Value.
     *
     * @return the string
     */
    @JsonValue
    public String value() {
        return value;
    }

    /**
     * From value.
     *
     * @param v the v
     * @return the DQ action definition
     */
    @JsonCreator
    public static DQApplicableDefinition fromValue(String v) {
    	if(StringUtils.isEmpty(v)){
    		return null;
    	}
        for (DQApplicableDefinition c : DQApplicableDefinition.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Could not parse DQApplicableDefinition id from string [" + v + "]");
    }
}
