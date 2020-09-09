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

import com.unidata.mdm.backend.common.types.ArrayAttribute.ArrayDataType;

/**
 * @author Mikhail Mikhailov
 * Cached array attribute.
 */
public class CachedClassifierNodeArrayAttribute extends CachedClassifierNodeLinkableAttribute {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -8603548932770177643L;
    /**
     * The data type.
     */
    private ArrayDataType dataType;
    /**
     * The default value.
     */
    private Serializable[] values;
    /**
     * Constructor.
     */
    public CachedClassifierNodeArrayAttribute() {
        super();
    }
    /**
     * @return the dataType
     */
    public ArrayDataType getDataType() {
        return dataType;
    }
    /**
     * @param dataType the dataType to set
     */
    public void setDataType(ArrayDataType dataType) {
        this.dataType = dataType;
    }
    /**
     * @return the values
     */
    public Serializable[] getValues() {
        return values;
    }
    /**
     * @param values the values to set
     */
    public void setValues(Serializable[] values) {
        this.values = values;
    }
    /**
     * Array or not.
     * @return true, if array, false otherwise
     */
    @Override
    public boolean isArray() {
        return true;
    }
}
