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

package com.unidata.mdm.backend.common.types;

import java.util.Date;

import com.unidata.mdm.backend.common.keys.EtalonKey;

/**
 * @author Mikhail Mikhailov
 * Etalon record info section.
 */
public class EtalonRecordInfoSection extends InfoSection {
    /**
     * The entity name.
     */
    protected String entityName;
    /**
     * The etalon key.
     */
    protected EtalonKey etalonKey;
    /**
     * Id (index) of the period on the timeline.
     */
    protected Long periodId;
    /**
     * Sets entity name field.
     * @param entityName value to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    /**
     * Gets the entity name.
     * @return name
     */
    public String getEntityName() {
        return entityName;
    }
    /**
     * Sets entity key field.
     * @param etalonKey the etalonKey to set
     */
    public void setEtalonKey(EtalonKey etalonKey) {
        this.etalonKey = etalonKey;
    }
    /**
     * Gets entity key field.
     * @return the etalonKey
     */
    public EtalonKey getEtalonKey() {
        return etalonKey;
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
     * {@inheritDoc}
     */
    @Override
    public RecordType getRecordType() {
        return RecordType.DATA_RECORD;
    }
    /**
     * Fluent etalon key.
     * @param value the value to set
     * @return self
     */
    public EtalonRecordInfoSection withEtalonKey(EtalonKey value) {
        setEtalonKey(value);
        return this;
    }
    /**
     * Fluent entity name.
     * @param value the value to set
     * @return self
     */
    public EtalonRecordInfoSection withEntityName(String value) {
        setEntityName(value);
        return this;
    }
    /**
     * Fluent created by.
     * @param value the value to set
     * @return self
     */
    public EtalonRecordInfoSection withCreatedBy(String value) {
        setCreatedBy(value);
        return this;
    }
    /**
     * Fluent updated by.
     * @param value the value to set
     * @return self
     */
    public EtalonRecordInfoSection withUpdatedBy(String value) {
        setUpdatedBy(value);
        return this;
    }
    /**
     * Fluent create date.
     * @param value the value to set
     * @return self
     */
    public EtalonRecordInfoSection withCreateDate(Date value) {
        setCreateDate(value);
        return this;
    }
    /**
     * Fluent update date.
     * @param value the value to set
     * @return self
     */
    public EtalonRecordInfoSection withUpdateDate(Date value) {
        setUpdateDate(value);
        return this;
    }
    /**
     * Fluent status.
     * @param value the value to set
     * @return self
     */
    public EtalonRecordInfoSection withStatus(RecordStatus value) {
        setStatus(value);
        return this;
    }
    /**
     * Fluent approval.
     * @param value the value to set
     * @return self
     */
    public EtalonRecordInfoSection withApproval(ApprovalState value) {
        setApproval(value);
        return this;
    }
    /**
     * Fluent valid from.
     * @param value the value to set
     * @return self
     */
    public EtalonRecordInfoSection withValidFrom(Date value) {
        setValidFrom(value);
        return this;
    }
    /**
     * Fluent valid to.
     * @param value the value to set
     * @return self
     */
    public EtalonRecordInfoSection withValidTo(Date value) {
        setValidTo(value);
        return this;
    }
    /**
     * Fluent period id.
     * @param value the value to set
     * @return self
     */
    public EtalonRecordInfoSection withPeriodId(Long value) {
        setPeriodId(value);
        return this;
    }
}
