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
