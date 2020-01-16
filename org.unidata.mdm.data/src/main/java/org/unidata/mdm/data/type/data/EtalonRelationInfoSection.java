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

package org.unidata.mdm.data.type.data;

import java.util.Date;

import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.data.type.keys.RecordEtalonKey;

/**
 * @author Mikhail Mikhailov
 * Etalon relation info section.
 */
public class EtalonRelationInfoSection extends AbstractRelationInfoSection {
    /**
     * The from etalon key.
     */
    protected RecordEtalonKey fromEtalonKey;
    /**
     * The to etalon key.
     */
    protected RecordEtalonKey toEtalonKey;
    /**
     * The relation etalon key.
     */
    protected String relationEtalonKey;
    /**
     * Id (index) of the period on the timeline.
     */
    protected Long periodId;
    /**
     * @return the fromEtalonKey
     */
    public RecordEtalonKey getFromEtalonKey() {
        return fromEtalonKey;
    }
    /**
     * @param fromEtalonKey the fromEtalonKey to set
     */
    public void setFromEtalonKey(RecordEtalonKey fromEtalonKey) {
        this.fromEtalonKey = fromEtalonKey;
    }
    /**
     * @return the toEtalonKey
     */
    public RecordEtalonKey getToEtalonKey() {
        return toEtalonKey;
    }
    /**
     * @param toEtalonKey the toEtalonKey to set
     */
    public void setToEtalonKey(RecordEtalonKey toEtalonKey) {
        this.toEtalonKey = toEtalonKey;
    }
    /**
     * @return the relation etalonKey
     */
    public String getRelationEtalonKey() {
        return relationEtalonKey;
    }
    /**
     * @param etalonKey the relation etalonKey to set
     */
    public void setRelationEtalonKey(String etalonKey) {
        this.relationEtalonKey = etalonKey;
    }
    /**
     * Gets the period id (index).
     * @return the periodId
     */
    public Long getPeriodId() {
        return periodId;
    }
    /**
     * Sets the period id (index).
     * @param periodId the periodId to set
     */
    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }
    /**
     * Fluent type.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withRelationType(RelationType value) {
        setRelationType(value);
        return this;
    }
    /**
     * Fluent relation name.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withRelationName(String value) {
        setRelationName(value);
        return this;
    }
    /**
     * Fluent from etalon key.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withFromEtalonKey(RecordEtalonKey value) {
        setFromEtalonKey(value);
        return this;
    }
    /**
     * Fluent from entity name.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withFromEntityName(String value) {
        setFromEntityName(value);
        return this;
    }
    /**
     * Fluent to entity name.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withToEntityName(String value) {
        setToEntityName(value);
        return this;
    }
    /**
     * Fluent to etalon key.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withToEtalonKey(RecordEtalonKey value) {
        setToEtalonKey(value);
        return this;
    }
    /**
     * Fluent relation etalon key.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withRelationEtalonKey(String value) {
        setRelationEtalonKey(value);
        return this;
    }
    /**
     * Fluent created by.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withCreatedBy(String value) {
        setCreatedBy(value);
        return this;
    }
    /**
     * Fluent updated by.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withUpdatedBy(String value) {
        setUpdatedBy(value);
        return this;
    }
    /**
     * Fluent create date.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withCreateDate(Date value) {
        setCreateDate(value);
        return this;
    }
    /**
     * Fluent update date.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withUpdateDate(Date value) {
        setUpdateDate(value);
        return this;
    }
    /**
     * Fluent status.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withStatus(RecordStatus value) {
        setStatus(value);
        return this;
    }
    /**
     * Fluent approval.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withApproval(ApprovalState value) {
        setApproval(value);
        return this;
    }
    /**
     * Fluent operation type.
     * @param operationType the value to set
     * @return self
     */
    public EtalonRelationInfoSection withOperationType(OperationType operationType) {
        setOperationType(operationType);
        return this;
    }
    /**
     * Fluent valid from.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withValidFrom(Date value) {
        setValidFrom(value);
        return this;
    }
    /**
     * Fluent valid to.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withValidTo(Date value) {
        setValidTo(value);
        return this;
    }
    /**
     * Fluent period id.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withPeriodId(Long value) {
        setPeriodId(value);
        return this;
    }
}
