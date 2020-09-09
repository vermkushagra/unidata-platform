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
package com.unidata.mdm.backend.api.rest.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Mikhail Mikhailov
 * Etalon integral record REST type.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EtalonIntegralRecordRO extends AbstractIntegralRecordRO {

    /**
     * Etalon id of the RELATION.
     */
    private String etalonId;
    /**
     * Containment etalon record.
     */
    private EtalonRecordRO etalonRecord;

    /**
     * Constructor.
     */
    public EtalonIntegralRecordRO() {
        super();
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }

    /**
     * @return the etalonRecord
     */
    public EtalonRecordRO getEtalonRecord() {
        return etalonRecord;
    }

    /**
     * @param etalonRecord the etalonRecord to set
     */
    public void setEtalonRecord(EtalonRecordRO etalonRecord) {
        this.etalonRecord = etalonRecord;
    }
}
