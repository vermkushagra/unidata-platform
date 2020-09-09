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
package com.unidata.mdm.backend.po;


/**
 * @author Mikhail Mikhailov
 * Origins transitions log
 */
public class OriginTransitionPO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "origins_transitions";
    /**
     * Etalon transition point id.
     */
    public static final String FIELD_ETALON_TRANSITION_ID = "etalon_transition_id";
    /**
     * Origin id.
     */
    public static final String FIELD_ORIGIN_ID = "origin_id";
    /**
     * Etalon merge point id.
     */
    private String etalonTransitionId;
    /**
     * Origin id.
     */
    private String originId;
    /**
     * @return the etalonsMergePointsId
     */
    public String getEtalonTransitionId() {
        return etalonTransitionId;
    }

    /**
     * @param etalonsMergePointsId the etalonsMergePointsId to set
     */
    public void setEtalonTransitionId(String etalonsMergePointsId) {
        this.etalonTransitionId = etalonsMergePointsId;
    }

    /**
     * @return the originId
     */
    public String getOriginId() {
        return originId;
    }

    /**
     * @param originId the originId to set
     */
    public void setOriginId(String originId) {
        this.originId = originId;
    }
}
