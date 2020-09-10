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
 * Etalon classifier info section.
 */
public class EtalonClassifierInfoSection extends AbstractClassifierInfoSection {
    /**
     * The record etalon key.
     */
    protected EtalonKey recordEtalonKey;
    /**
     * The classifier etalon key.
     */
    protected String classifierEtalonKey;
    /**
     * @return the recordEtalonKey
     */
    public EtalonKey getRecordEtalonKey() {
        return recordEtalonKey;
    }
    /**
     * @param recordEtalonKey the recordEtalonKey to set
     */
    public void setRecordEtalonKey(EtalonKey recordEtalonKey) {
        this.recordEtalonKey = recordEtalonKey;
    }
    /**
     * @return the classifierEtalonKey
     */
    public String getClassifierEtalonKey() {
        return classifierEtalonKey;
    }
    /**
     * @param classifierEtalonKey the classifierEtalonKey to set
     */
    public void setClassifierEtalonKey(String classifierEtalonKey) {
        this.classifierEtalonKey = classifierEtalonKey;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RecordType getRecordType() {
        return RecordType.CLASSIFIER_RECORD;
    }
    /**
     * Fluent classifier etalon key.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withClassifierEtalonKey(String value) {
        setClassifierEtalonKey(value);
        return this;
    }
    /**
     * Fluent record etalon key.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withRecordEtalonKey(EtalonKey value) {
        setRecordEtalonKey(value);
        return this;
    }
    /**
     * Fluent from entity name.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withRecordEntityName(String value) {
        setRecordEntityName(value);
        return this;
    }
    /**
     * Fluent node id.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withNodeId(String value) {
        setNodeId(value);
        return this;
    }
    /**
     * Fluent classifier name.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withClassifierName(String value) {
        setClassifierName(value);
        return this;
    }
    /**
     * Fluent created by.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withCreatedBy(String value) {
        setCreatedBy(value);
        return this;
    }
    /**
     * Fluent updated by.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withUpdatedBy(String value) {
        setUpdatedBy(value);
        return this;
    }
    /**
     * Fluent create date.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withCreateDate(Date value) {
        setCreateDate(value);
        return this;
    }
    /**
     * Fluent update date.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withUpdateDate(Date value) {
        setUpdateDate(value);
        return this;
    }
    /**
     * Fluent status.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withStatus(RecordStatus value) {
        setStatus(value);
        return this;
    }
    /**
     * Fluent approval.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withApproval(ApprovalState value) {
        setApproval(value);
        return this;
    }
    /**
     * Fluent valid from.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withValidFrom(Date value) {
        setValidFrom(value);
        return this;
    }
    /**
     * Fluent valid to.
     * @param value the value to set
     * @return self
     */
    public EtalonClassifierInfoSection withValidTo(Date value) {
        setValidTo(value);
        return this;
    }
}
