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

import com.unidata.mdm.backend.common.keys.OriginKey;

/**
 * @author Mikhail Mikhailov
 * Origin relation info section.
 */
public class OriginClassifierInfoSection extends AbstractClassifierInfoSection {
    /**
     * The revision.
     */
    protected int revision;
    /**
     * Data API major.
     */
    protected int major;
    /**
     * Data API minor.
     */
    protected int minor;
    /**
     * The record origin key.
     */
    protected OriginKey recordOriginKey;
    /**
     * The relation origin key.
     */
    protected String classifierOriginKey;
    /**
     * Relation source system.
     */
    protected String classifierSourceSystem;
    /**
     * Gets the revision number of the version.
     * @return revision number.
     */
    public int getRevision() {
        return revision;
    }
    /**
     * Sets revision field.
     * @param revision value to set
     */
    public void setRevision(int revision) {
        this.revision = revision;
    }
    /**
     * @return the major
     */
    public int getMajor() {
        return major;
    }
    /**
     * @param major the major to set
     */
    public void setMajor(int major) {
        this.major = major;
    }
    /**
     * @return the minor
     */
    public int getMinor() {
        return minor;
    }
    /**
     * @param minor the minor to set
     */
    public void setMinor(int minor) {
        this.minor = minor;
    }
    /**
     * @return the recordOriginKey
     */
    public OriginKey getRecordOriginKey() {
        return recordOriginKey;
    }
    /**
     * @param recordOriginKey the recordOriginKey to set
     */
    public void setRecordOriginKey(OriginKey recordOriginKey) {
        this.recordOriginKey = recordOriginKey;
    }
    /**
     * @return the classifierOriginKey
     */
    public String getClassifierOriginKey() {
        return classifierOriginKey;
    }
    /**
     * @param relationOriginKey the classifierOriginKey to set
     */
    public void setClassifierOriginKey(String relationOriginKey) {
        this.classifierOriginKey = relationOriginKey;
    }
    /**
     * @return the classifierSourceSystem
     */
    public String getClassifierSourceSystem() {
        return classifierSourceSystem;
    }
    /**
     * @param relationSourceSystem the classifierSourceSystem to set
     */
    public void setClassifierSourceSystem(String relationSourceSystem) {
        this.classifierSourceSystem = relationSourceSystem;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RecordType getRecordType() {
        return RecordType.CLASSIFIER_RECORD;
    }
    /**
     * Fluent classifier origin key.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withClassifierOriginKey(String value) {
        setClassifierOriginKey(value);
        return this;
    }
    /**
     * Fluent classifier source system.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withClassifierSourceSystem(String value) {
        setClassifierSourceSystem(value);
        return this;
    }
    /**
     * Fluent record origin key.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withRecordOriginKey(OriginKey value) {
        setRecordOriginKey(value);
        return this;
    }
    /**
     * Fluent from entity name.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withRecordEntityName(String value) {
        setRecordEntityName(value);
        return this;
    }
    /**
     * Fluent node id.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withNodeId(String value) {
        setNodeId(value);
        return this;
    }
    /**
     * Fluent revision.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withRevision(int value) {
        setRevision(value);
        return this;
    }
    /**
     * Fluent major.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withMajor(int value) {
        setMajor(value);
        return this;
    }
    /**
     * Fluent minor.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withMinor(int value) {
        setMinor(value);
        return this;
    }
    /**
     * Fluent classifier name.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withClassifierName(String value) {
        setClassifierName(value);
        return this;
    }
    /**
     * Fluent created by.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withCreatedBy(String value) {
        setCreatedBy(value);
        return this;
    }
    /**
     * Fluent updated by.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withUpdatedBy(String value) {
        setUpdatedBy(value);
        return this;
    }
    /**
     * Fluent create date.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withCreateDate(Date value) {
        setCreateDate(value);
        return this;
    }
    /**
     * Fluent update date.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withUpdateDate(Date value) {
        setUpdateDate(value);
        return this;
    }
    /**
     * Fluent status.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withStatus(RecordStatus value) {
        setStatus(value);
        return this;
    }
    /**
     * Fluent approval.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withApproval(ApprovalState value) {
        setApproval(value);
        return this;
    }
    /**
     * Fluent valid from.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withValidFrom(Date value) {
        setValidFrom(value);
        return this;
    }
    /**
     * Fluent valid to.
     * @param value the value to set
     * @return self
     */
    public OriginClassifierInfoSection withValidTo(Date value) {
        setValidTo(value);
        return this;
    }
}
