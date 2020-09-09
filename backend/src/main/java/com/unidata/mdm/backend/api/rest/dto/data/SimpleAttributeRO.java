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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.util.serializer.SimpleAttributeDeserializer;
import com.unidata.mdm.backend.api.rest.util.serializer.SimpleAttributeSerializer;

/**
 * @author Michael Yashin. Created on 02.06.2015.
 */
@JsonDeserialize(using = SimpleAttributeDeserializer.class)
@JsonSerialize(using = SimpleAttributeSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleAttributeRO {
    /**
     * Name of the attribute.
     */
    protected String name;
    /**
     * Its value.
     */
    protected Object value;
    /**
     * Value data type.
     */
    protected SimpleDataType type;
    /**
     * Name of the attribute.
     */
    protected String displayValue;
    /**
     * Name of the attribute.
     */
    protected String targetEtalonId;
    /**
     * Value id
     */
    private String valueId;

    /**
     * Unit id
     */
    private String unitId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public SimpleDataType getType() {
        return type;
    }

    public void setType(SimpleDataType type) {
        this.type = type;
    }

    /**
     * @return the displayName
     */
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayValue(String displayName) {
        this.displayValue = displayName;
    }

    /**
     * @return the targetEtalonId
     */
    public String getTargetEtalonId() {
        return targetEtalonId;
    }

    /**
     * @param targetEtalonId the targetEtalonId to set
     */
    public void setTargetEtalonId(String targetEtalonId) {
        this.targetEtalonId = targetEtalonId;
    }

    /**
     *
     * @return
     */
    public String getValueId() {
        return valueId;
    }

    /**
     *
     * @param valueId
     */
    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    /**
     *
     * @return
     */
    public String getUnitId() {
        return unitId;
    }

    /**
     *
     * @param unitId
     */
    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
}
