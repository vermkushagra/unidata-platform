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

package com.unidata.mdm.backend.api.rest.dto.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.api.rest.dto.ArrayDataType;
import com.unidata.mdm.backend.api.rest.util.serializer.ArrayAttributeDeserializer;
import com.unidata.mdm.backend.api.rest.util.serializer.ArrayAttributeSerializer;

/**
 * @author Michael Yashin. Created on 02.06.2015.
 */
@JsonDeserialize(using = ArrayAttributeDeserializer.class)
@JsonSerialize(using = ArrayAttributeSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrayAttributeRO {
    /**
     * Name of the attribute.
     */
    protected String name;
    /**
     * Its value.
     */
    protected List<ArrayObjectRO> value;
    /**
     * Value data type.
     */
    protected ArrayDataType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ArrayObjectRO> getValue() {
        return value;
    }

    public void setValue(List<ArrayObjectRO> value) {
        this.value = value;
    }

    public ArrayDataType getType() {
        return type;
    }

    public void setType(ArrayDataType type) {
        this.type = type;
    }
}
