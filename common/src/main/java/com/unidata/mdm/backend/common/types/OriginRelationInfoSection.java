package com.unidata.mdm.backend.common.types;

import java.util.Date;

import com.unidata.mdm.backend.common.keys.OriginKey;

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
     * The from origin key.
     */
    protected OriginKey fromOriginKey;
    /**
     * The from origin key.
     */
    protected OriginKey toOriginKey;
    /**
     * The relation origin key.
     */
    protected String relationOriginKey;
    /**
     * Relation source system.
     */
    protected String relationSourceSystem;
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
     * @return the fromOriginKey
     */
    public OriginKey getFromOriginKey() {
        return fromOriginKey;
    }
    /**
     * @param fromOriginKey the fromOriginKey to set
     */
    public void setFromOriginKey(OriginKey fromOriginKey) {
        this.fromOriginKey = fromOriginKey;
    }
    /**
     * @return the toOriginKey
     */
    public OriginKey getToOriginKey() {
        return toOriginKey;
    }
    /**
     * @param toOriginKey the toOriginKey to set
     */
    public void setToOriginKey(OriginKey toOriginKey) {
        this.toOriginKey = toOriginKey;
    }
    /**
     * @return the relationOriginKey
     */
    public String getRelationOriginKey() {
        return relationOriginKey;
    }
    /**
     * @param relationOriginKey the relationOriginKey to set
     */
    public void setRelationOriginKey(String relationOriginKey) {
        this.relationOriginKey = relationOriginKey;
    }
    /**
     * @return the relationSourceSystem
     */
    public String getRelationSourceSystem() {
        return relationSourceSystem;
    }
    /**
     * @param relationSourceSystem the relationSourceSystem to set
     */
    public void setRelationSourceSystem(String relationSourceSystem) {
        this.relationSourceSystem = relationSourceSystem;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RecordType getRecordType() {
        return RecordType.RELATION_RECORD;
    }
    /**
     * Fluent relation origin key.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withRelationOriginKey(String value) {
        setRelationOriginKey(value);
        return this;
    }
    /**
     * Fluent relation source system.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withRelationSourceSystem(String value) {
        setRelationSourceSystem(value);
        return this;
    }
    /**
     * Fluent from origin key.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withFromOriginKey(OriginKey value) {
        setFromOriginKey(value);
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
     * Fluent to origin key.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withToOriginKey(OriginKey value) {
        setToOriginKey(value);
        return this;
    }
    /**
     * Fluent type.
     * @param value the value to set
     * @return self
     */
    public OriginRelationInfoSection withType(RelationType value) {
        setType(value);
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
