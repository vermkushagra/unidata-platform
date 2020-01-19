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
import org.unidata.mdm.core.type.data.InfoSection;
import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.data.type.keys.RecordOriginKey;

/**
 * @author Mikhail Mikhailov
 * Origin record info section.
 */
public class OriginRecordInfoSection extends InfoSection {
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
     * The key.
     */
    protected RecordOriginKey originKey;
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
     * @return the originKey
     */
    public RecordOriginKey getOriginKey() {
        return originKey;
    }
    /**
     * @param originKey the originKey to set
     */
    public void setOriginKey(RecordOriginKey originKey) {
        this.originKey = originKey;
    }
    /**
     * Fluent origin key.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withOriginKey(RecordOriginKey value) {
        setOriginKey(value);
        return this;
    }
    /**
     * Fluent revision.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withRevision(int value) {
        setRevision(value);
        return this;
    }
    /**
     * Fluent major.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withMajor(int value) {
        setMajor(value);
        return this;
    }
    /**
     * Fluent minor.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withMinor(int value) {
        setMinor(value);
        return this;
    }
    /**
     * Fluent version shift.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withShift(DataShift value) {
        setShift(value);
        return this;
    }
    /**
     * Fluent created by.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withCreatedBy(String value) {
        setCreatedBy(value);
        return this;
    }
    /**
     * Fluent updated by.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withUpdatedBy(String value) {
        setUpdatedBy(value);
        return this;
    }
    /**
     * Fluent create date.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withCreateDate(Date value) {
        setCreateDate(value);
        return this;
    }
    /**
     * Fluent update date.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withUpdateDate(Date value) {
        setUpdateDate(value);
        return this;
    }
    /**
     * Fluent status.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withStatus(RecordStatus value) {
        setStatus(value);
        return this;
    }
    /**
     * Fluent approval.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withApproval(ApprovalState value) {
        setApproval(value);
        return this;
    }
    /**
     * Fluent operation type.
     * @param operationType the value to set
     * @return self
     */
    public OriginRecordInfoSection withOperationType(OperationType operationType) {
        setOperationType(operationType);
        return this;
    }
    /**
     * Fluent valid from.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withValidFrom(Date value) {
        setValidFrom(value);
        return this;
    }
    /**
     * Fluent valid to.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withValidTo(Date value) {
        setValidTo(value);
        return this;
    }
}
