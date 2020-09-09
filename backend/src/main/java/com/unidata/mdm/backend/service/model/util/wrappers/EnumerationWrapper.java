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

/**
 *
 */
package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.Map;

import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.meta.EnumerationDataType;

/**
 * @author Mikhail Mikhailov
 *
 */
public class EnumerationWrapper extends ModelWrapper {

    /**
     * Enumeration
     */
    private final EnumerationDataType enumeration;
    /**
     * Enumeration map.
     */
    private final Map<String, String> enumerationMap;
    /**
     * Constructor.
     */
    public EnumerationWrapper(EnumerationDataType enumeration) {
        super();
        this.enumeration = enumeration;
        this.enumerationMap = ModelUtils.createEnumerationMap(enumeration);
    }

    /**
     * @return the enumeration
     */
    public EnumerationDataType getEnumeration() {
        return enumeration;
    }

    /**
     * @return the enumerationMap
     */
    public Map<String, String> getEnumerationMap() {
        return enumerationMap;
    }

    @Override
    public String getUniqueIdentifier() {
        return enumeration.getName();
    }

    @Override
    public Long getVersionOfWrappedElement() {
        return enumeration.getVersion();
    }
}
