package com.unidata.mdm.backend.common.types;

import java.util.Date;

import com.unidata.mdm.backend.common.keys.OriginKey;

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
    protected OriginKey originKey;
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
    public OriginKey getOriginKey() {
        return originKey;
    }
    /**
     * @param originKey the originKey to set
     */
    public void setOriginKey(OriginKey originKey) {
        this.originKey = originKey;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RecordType getRecordType() {
        return RecordType.DATA_RECORD;
    }
    /**
     * Fluent origin key.
     * @param value the value to set
     * @return self
     */
    public OriginRecordInfoSection withOriginKey(OriginKey value) {
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
