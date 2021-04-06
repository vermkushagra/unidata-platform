package com.unidata.mdm.backend.api.rest.dto.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Michael Yashin. Created on 02.06.2015.
 */
public class OriginRecordRO extends NestedRecordRO {

    /**
     * List of classifier records.
     */
    private List<OriginClassifierRecordRO> classifiers = new ArrayList<>();
    /**
     * List of code attributes.
     */
    private List<CodeAttributeRO> codeAttributes = new ArrayList<>();
    /**
     * Entity key.
     */
    protected String originId;
    /**
     * Foreign system ID.
     */
    protected String externalId;
    /**
     * Source system name.
     */
    protected String sourceSystem;
    /**
     * Entity name.
     */
    protected String entityName;
    /**
     * Entity version.
     */
    protected int version;
    /**
     * Entity version.
     */
    protected int revision;
    /**
     * Optional validity range start date.
     */
    protected LocalDateTime validFrom;
    /**
     * Optional validity range end date.
     */
    protected LocalDateTime validTo;
    /**
     * Status {ACTIVE|INACTIVE|PENDING|MERGED}.
     */
    protected String status;
    /**
     * Create date of the origin (source definition) record.
     */
    @JsonFormat(timezone = "DEFAULT_TIMEZONE")
    protected Date createDate;
    /**
     * Created by.
     */
    protected String createdBy;
    /**
     * Update date of the origin version record.
     */
    @JsonFormat(timezone = "DEFAULT_TIMEZONE")
    protected Date updateDate;
    /**
     * Updated by.
     */
    protected String updatedBy;
    /**
     * Global sequence number.
     */
    protected String gsn;

    //TODO: add later
    //TODO: support integral records
    //protected List<RelationTo> relations;

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String entityKey) {
        this.originId = entityKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int entityVersion) {
        this.version = entityVersion;
    }

    /**
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * @param sourceSystem the sourceSystem to set
     */
    public void setSourceSystem(String systemName) {
        this.sourceSystem = systemName;
    }

    /**
     * @return the originId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @param originId the originId to set
     */
    public void setExternalId(String originId) {
        this.externalId = originId;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }


    /**
     * @param revision the revision to set
     */
    public void setRevision(int revision) {
        this.revision = revision;
    }


    /**
     * @return the validFrom
     */
    public LocalDateTime getValidFrom() {
        return validFrom;
    }


    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }


    /**
     * @return the validTo
     */
    public LocalDateTime getValidTo() {
        return validTo;
    }


    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }


    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }


    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }


    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    /**
     * @return the updateDate
     */
    public Date getUpdateDate() {
        return updateDate;
    }


    /**
     * @param updateDate the updateDate to set
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }


    /**
     * @return the updatedBy
     */
    public String getUpdatedBy() {
        return updatedBy;
    }


    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     *
     * @return classifiers
     */
    public List<OriginClassifierRecordRO> getClassifiers() {
        return classifiers;
    }

    /**
     *
     * @param classifiers classifiers
     */
    public void setClassifiers(List<OriginClassifierRecordRO> classifiers) {
        this.classifiers = classifiers;
    }

    /**
     * @return the codeAttributes
     */
    public List<CodeAttributeRO> getCodeAttributes() {
        return codeAttributes;
    }

    /**
     * @param codeAttributes the codeAttributes to set
     */
    public void setCodeAttributes(List<CodeAttributeRO> codeAttributes) {
        this.codeAttributes = codeAttributes;
    }

    /**
     * @return gsn.
     */
    public String getGsn() {
        return gsn;
    }
    /**
     * @param gsn the gsn to set
     */
    public void setGsn(String gsn) {
        this.gsn = gsn;
    }
}
