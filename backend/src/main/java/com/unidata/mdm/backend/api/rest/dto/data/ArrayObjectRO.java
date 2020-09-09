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

/**
 * @author Mikhail Mikhailov
 * Array object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrayObjectRO {
    /**
     * The value.
     */
    private Object value;
    /**
     * Display value, in case, this is a enum link, or a link to a code attribute.
     */
    @JsonIgnoreProperties(allowGetters = true, allowSetters = false)
    private String displayValue;
    /**
     * The etalon id value of the lookup record, in case of a link to a code attribute.
     */
    @JsonIgnoreProperties(allowGetters = true, allowSetters = false)
    private String targetEtalonId;
    /**
     * Constructor.
     */
    public ArrayObjectRO() {
        super();
    }
    /**
     * Constructor.
     */
    public ArrayObjectRO(Object value) {
        super();
        this.value = value;
    }
    /**
     * Constructor.
     */
    public ArrayObjectRO(Object value, String displayValue, String targetEtalonId) {
        super();
        this.value = value;
        this.displayValue = displayValue;
        this.targetEtalonId = targetEtalonId;
    }
    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }
    /**
     * @return the displayValue
     */
    public String getDisplayValue() {
        return displayValue;
    }
    /**
     * @param displayValue the displayValue to set
     */
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
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
}
