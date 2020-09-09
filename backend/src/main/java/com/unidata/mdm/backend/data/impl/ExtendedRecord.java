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

package com.unidata.mdm.backend.data.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.types.DataRecord;

/**
 * It is a special class for describing info about attribute mappings.
 */
public class ExtendedRecord {

    /**
     * Entity name
     */
    private final String entityName;

    /**
     * Record
     */
    private final DataRecord record;

    /**
     * Map where key is a attribute name and value it is a etalon id or origin id.
     */
    private Map<String, String> attributeWinnersMap = new HashMap<>();

    public ExtendedRecord(@Nonnull DataRecord record, @Nonnull String entityName) {
        this.record = record;
        this.entityName = entityName;
    }

    /**
     * Etalon Record
     *
     * @return
     */
    public DataRecord getRecord() {
        return record;
    }

    /**
     * @return attributeWinnersMap
     */
    public Map<String, String> getAttributeWinnersMap() {
        return attributeWinnersMap;
    }

    /**
     * @param attributeWinnersMap - filled or partially filled  attributeWinnersMap
     */
    public void addAllWinnersAttributes(Map<String, String> attributeWinnersMap) {
        this.attributeWinnersMap.putAll(attributeWinnersMap);
    }

    /**
     * add to attribute winner map new attribute
     *
     * @param recordId - record id
     * @param attrName - attr name
     */
    public void addWinnerAttribute(String attrName, String recordId) {
        attributeWinnersMap.put(attrName, recordId);
    }

    /**
     * Entity name
     *
     * @return
     */
    public String getEntityName() {
        return entityName;
    }
}
