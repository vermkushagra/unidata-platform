package com.unidata.mdm.backend.common.types;

import java.util.Date;

import com.unidata.mdm.backend.common.keys.EtalonKey;

/**
 * @author Mikhail Mikhailov
 * Etalon relation info section.
 */
public class EtalonRelationInfoSection extends AbstractRelationInfoSection {
    /**
     * The from etalon key.
     */
    protected EtalonKey fromEtalonKey;
    /**
     * The to etalon key.
     */
    protected EtalonKey toEtalonKey;
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
    public EtalonKey getFromEtalonKey() {
        return fromEtalonKey;
    }
    /**
     * @param fromEtalonKey the fromEtalonKey to set
     */
    public void setFromEtalonKey(EtalonKey fromEtalonKey) {
        this.fromEtalonKey = fromEtalonKey;
    }
    /**
     * @return the toEtalonKey
     */
    public EtalonKey getToEtalonKey() {
        return toEtalonKey;
    }
    /**
     * @param toEtalonKey the toEtalonKey to set
     */
    public void setToEtalonKey(EtalonKey toEtalonKey) {
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
     * {@inheritDoc}
     */
    @Override
    public RecordType getRecordType() {
        return RecordType.RELATION_RECORD;
    }
    /**
     * Fluent type.
     * @param value the value to set
     * @return self
     */
    public EtalonRelationInfoSection withType(RelationType value) {
        setType(value);
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
    public EtalonRelationInfoSection withFromEtalonKey(EtalonKey value) {
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
    public EtalonRelationInfoSection withToEtalonKey(EtalonKey value) {
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
