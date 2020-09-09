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


import java.util.Map;

/**
 *
 */
public class ExtendedRecordRO {

    /**
     * nested record
     */
    private NestedRecordRO record;

    /**
     * Attribute winner map
     */
    private Map<String, String> attributeWinnersMap;

    private String winnerEtalonId;

    /**
     * Constructor
     */
    public ExtendedRecordRO() {
    }

    /**
     * @param record record
     * @param attributeWinnersMap - attribute winner map.
     */
    public ExtendedRecordRO(NestedRecordRO record, Map<String, String> attributeWinnersMap) {
        this.record = record;
        this.attributeWinnersMap = attributeWinnersMap;
    }

    public NestedRecordRO getRecord() {
        return record;
    }

    public void setRecord(NestedRecordRO record) {
        this.record = record;
    }

    public Map<String, String> getAttributeWinnersMap() {
        return attributeWinnersMap;
    }

    public void setAttributeWinnersMap(Map<String, String> accessoryMap) {
        this.attributeWinnersMap = accessoryMap;
    }

    public String getWinnerEtalonId() {
        return winnerEtalonId;
    }

    public void setWinnerEtalonId(String winnerEtalonId) {
        this.winnerEtalonId = winnerEtalonId;
    }
}
