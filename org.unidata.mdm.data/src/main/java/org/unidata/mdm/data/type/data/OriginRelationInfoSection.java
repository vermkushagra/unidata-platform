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
import org.unidata.mdm.core.type.data.DataShift;
import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.data.type.keys.RelationOriginKey;

/**
 * @author Mikhail Mikhailov
 * Origin relation info section.
 */
public class OriginRelationInfoSection extends AbstractRelationInfoSection {
    /**
     * The version shift.
     */
    protected DataShift shift;
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
     * The relation origin key.
     */
    protected RelationOriginKey relationOriginKey;
    /**
     * Gets the version shift.
     * @return shift
     */
    public DataShift getShift() {
        return shift;
    }
    /**
     * Sets version shift.
     * @param shift value to set
     */
    public void setShift(DataShift shift) {
        this.shift = shift;
    }
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
     * @return the relationOriginKey
     */
    public RelationOriginKey getRelationOriginKey() {
        return relationOriginKey;
    }
    /**
     * @param relationOriginKey the relationOriginKey to set
     */
    public void setRelationOriginKey(RelationOriginKey relationOriginKey) {
        this.relationOriginKey = relationOriginKey;
    }
    /**
     * Fluent relation origin key.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withRelationOriginKey(RelationOriginKey value) {
        setRelationOriginKey(value);
        return this;
    }
    /**
     * Fluent from entity name.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withFromEntityName(String value) {
        setFromEntityName(value);
        return this;
    }
    /**
     * Fluent to entity name.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withToEntityName(String value) {
        setToEntityName(value);
        return this;
    }
    /**
     * Fluent type.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withRelationType(RelationType value) {
        setRelationType(value);
        return this;
    }
    /**
     * Fluent revision.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withRevision(int value) {
        setRevision(value);
        return this;
    }
    /**
     * Fluent major.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withMajor(int value) {
        setMajor(value);
        return this;
    }
    /**
     * Fluent minor.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withMinor(int value) {
        setMinor(value);
        return this;
    }
    /**
     * Fluent relation name.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withRelationName(String value) {
        setRelationName(value);
        return this;
    }
    /**
     * Fluent created by.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withCreatedBy(String value) {
        setCreatedBy(value);
        return this;
    }
    /**
     * Fluent updated by.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withUpdatedBy(String value) {
        setUpdatedBy(value);
        return this;
    }
    /**
     * Fluent create date.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withCreateDate(Date value) {
        setCreateDate(value);
        return this;
    }
    /**
     * Fluent update date.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withUpdateDate(Date value) {
        setUpdateDate(value);
        return this;
    }
    /**
     * Fluent status.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withStatus(RecordStatus value) {
        setStatus(value);
        return this;
    }
    /**
     * Fluent approval.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withApproval(ApprovalState value) {
        setApproval(value);
        return this;
    }
    /**
     * Fluent operation type.
     * @param operationType the value to set
     * @return self
     */
    public OriginRelationInfoSection withOperationType(OperationType operationType) {
        setOperationType(operationType);
        return this;
    }
    /**
     * Fluent valid from.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withValidFrom(Date value) {
        setValidFrom(value);
        return this;
    }
    /**
     * Fluent valid to.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withValidTo(Date value) {
        setValidTo(value);
        return this;
    }
    /**
     * Fluent version shift.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withShift(DataShift value) {
        setShift(value);
        return this;
    }
}
