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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * The Enum DQActionDefinition.
 */
public enum DQActionDefinition {
    
    /** The create new. */
    CREATE_NEW("CREATE_NEW"),
    
    /** The update current. */
    UPDATE_CURRENT("UPDATE_CURRENT");
    
    /** The value. */
    private final String value;

    /**
     * Instantiates a new DQ action definition.
     *
     * @param v the v
     */
    DQActionDefinition(String v) {
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
    public static DQActionDefinition fromValue(String v) {
        for (DQActionDefinition c : DQActionDefinition.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Could not parse DQActionDefinition id from string [" + v + "]");
    }
}
