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

package com.unidata.mdm.backend.service.classifier.cache;

import java.io.Serializable;

import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;

/**
 * @author Mikhail Mikhailov
 * Simple attribute.
 */
public class CachedClassifierNodeSimpleAttribute extends CachedClassifierNodeLinkableAttribute {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5862516537224442353L;
    /**
     * The data type.
     */
    private DataType dataType;
    /**
     * The default value.
     */
    private Serializable value;
    /**
     * Constructor.
     */
    public CachedClassifierNodeSimpleAttribute() {
        super();
    }
    /**
     * @return the dataType
     */
    public DataType getDataType() {
        return dataType;
    }
    /**
     * @param dataType the dataType to set
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
    /**
     * @return the value
     */
    public Serializable getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(Serializable value) {
        this.value = value;
    }
    /**
     * Simple or not.
     * @return true, if simple, false otherwise
     */
    @Override
    public boolean isSimple() {
        return true;
    }
}
