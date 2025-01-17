package com.unidata.mdm.backend.common.types;

import java.util.Date;

/**
 * @author Mikhail Mikhailov
 * Info section (Common part).
 */
public abstract class InfoSection {
    /**
     * Created by.
     */
    protected String createdBy;
    /**
     * Updated by.
     */
    protected String updatedBy;
    /**
     * Create date.
     */
    protected Date createDate;
    /**
     * Update date.
     */
    protected Date updateDate;
    /**
     * Record status.
     */
    protected RecordStatus status;
    /**
     * Record approval.
     */
    protected ApprovalState approval;
    /**
     * Valid from.
     */
    protected Date validFrom;
    /**
     * Valid to.
     */
    protected Date validTo;
    /**
     * Gets createdBy field.
     * @return field
     */
    public String getCreatedBy() {
        return createdBy;
    }
    /**
     * Sets created by field.
     * @param createdBy the value to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    /**
     * Gets updatedBy field.
     * @return field
     */
    public String getUpdatedBy() {
        return updatedBy;
    }
    /**
     * Sets updated by field.
     * @param updatedBy the value to set
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    /**
     * Gets createDate field.
     * @return field
     */
    public Date getCreateDate() {
        return createDate;
    }
    /**
     * Sets create date field.
     * @param createDate value to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    /**
     * Gets updateDate field.
     * @return field
     */
    public Date getUpdateDate() {
        return updateDate;
    }
    /**
     * Sets update date field.
     * @param updateDate value to set
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    /**
     * Gets status field.
     * @return field
     */
    public RecordStatus getStatus() {
        return status;
    }
    /**
     * Sets status field.
     * @param status value to set
     */
    public void setStatus(RecordStatus status) {
        this.status = status;
    }
    /**
     * Gets approval field.
     * @return field
     */
    public ApprovalState getApproval() {
        return approval;
    }
    /**
     * Sets approval field.
     * @param approval vlaue to set
     */
    public void setApproval(ApprovalState approval) {
        this.approval = approval;
    }
    /**
     * Gets validFrom field.
     * @return field
     */
    public Date getValidFrom() {
        return validFrom;
    }
    /**
     * Sets valid from field.
     * @param validFrom the value to set
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }
    /**
     * Gets validTo field.
     * @return field
     */
    public Date getValidTo() {
        return validTo;
    }
    /**
     * Sets valid to field.
     * @param validTo the value to set
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }
    /**
     * Gets the type of the rcord.
     * @return
     */
    public abstract RecordType getRecordType();
}
