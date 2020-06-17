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
package com.unidata.mdm.backend.common.dto;

import java.util.Date;

import com.unidata.mdm.meta.RelType;

/**
 * @author Mikhail Mikhailov
 * Relation state DTO.
 */
public class RelationStateDTO {

    /**
     * Relation name.
     */
    private String relationName;
    /**
     * Relation type.
     */
    private RelType relationType;
    /**
     * Minimum lower bound.
     */
    private Date rangeFrom;
    /**
     * Maximum upper bound.
     */
    private Date rangeTo;
    /**
     * Constructor.
     */
    public RelationStateDTO() {
        super();
    }
    /**
     * Constructor.
     */
    public RelationStateDTO(String relationName, RelType relationType, Date rangeFrom, Date rangeTo) {
        super();
        this.relationName = relationName;
        this.relationType = relationType;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
    }
    /**
     * @return the relationName
     */
    public String getRelationName() {
        return relationName;
    }
    /**
     * @param relationName the relationName to set
     */
    public void setRelationName(String relName) {
        this.relationName = relName;
    }
    /**
     * @return the relationType
     */
    public RelType getRelationType() {
        return relationType;
    }
    /**
     * @param relationType the relationType to set
     */
    public void setRelationType(RelType relType) {
        this.relationType = relType;
    }
    /**
     * @return the rangeFrom
     */
    public Date getRangeFrom() {
        return rangeFrom;
    }
    /**
     * @param rangeFrom the rangeFrom to set
     */
    public void setRangeFrom(Date rangeFromMin) {
        this.rangeFrom = rangeFromMin;
    }
    /**
     * @return the rangeTo
     */
    public Date getRangeTo() {
        return rangeTo;
    }
    /**
     * @param rangeTo the rangeTo to set
     */
    public void setRangeTo(Date rangeToMax) {
        this.rangeTo = rangeToMax;
    }
}
