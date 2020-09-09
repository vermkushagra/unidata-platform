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

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;


/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class BVTMapWrapper extends SearchableElementsWrapper {

    /**
     * BVT attributes map.
     */
    protected Map<String, Map<String, Integer>> bvtMap;

    /**
     * Constructor.
     * @param id
     * @param attrs
     */
    public BVTMapWrapper(String id, Map<String, AttributeInfoHolder> attrs, Map<String, Map<String, Integer>> bvtMap) {
        super(id, attrs);
        this.bvtMap = bvtMap;
    }

    /**
     * @return the bvtMap
     */
    public Map<String, Map<String, Integer>> getBvtMap() {
        return bvtMap;
    }


    /**
     * @param bvtMap the bvtMap to set
     */
    public void setBvtMap(Map<String, Map<String, Integer>> bvtMap) {
        this.bvtMap = bvtMap;
    }
    /**
     * Tells whether this wrapper is a lookup entity.
     * @return true, if so, false otherwise
     */
    public abstract boolean isLookup();
    /**
     * Tells whether this wrapper is a regular entity.
     * @return true, if so, false otherwise
     */
    public abstract boolean isEntity();
}
