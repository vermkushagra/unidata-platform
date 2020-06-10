package com.unidata.mdm.backend.common.types;

import java.util.Date;

/**
 * @author Mikhail Mikhailov
 * Basic interval contributor interface.
 */
public class TimeIntervalContributorInfo implements Calculable {
    /**
     * Origin ID.
     */
    private String originId;
    /**
     * Revision.
     */
    private int revision;
    /**
     * Source system.
     */
    private String sourceSystem;
    /**
     * Owner string.
     */
    private String createdBy;
    /**
     * Last update date.
     */
    private Date createDate;
    /**
     * Status.
     */
    private RecordStatus status;
    /**
     * Approval.
     */
    private ApprovalState approval;
    /**
     * VistoryOperationType.
     */
    private VistoryOperationType operationType;
    /**
     * Constructor.
     */
    public TimeIntervalContributorInfo() {
        super();
    }
    /**
     * @return the originId
     */
    public String getOriginId() {
        return originId;
    }
    /**
     * @param originId the originId to set
     */
    public void setOriginId(String originId) {
        this.originId = originId;
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
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }
    /**
     * @param sourceSystem the sourceSystem to set
     */
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
    /**
     * @return the owner
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
     * @return the lastUpdate
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
     * @return the status
     */
    public RecordStatus getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(RecordStatus status) {
        this.status = status;
    }
    /**
     * @return the approval
     */
    public ApprovalState getApproval() {
        return approval;
    }
    /**
     * @param approval the approval to set
     */
    public void setApproval(ApprovalState approval) {
        this.approval = approval;
    }
    /**
     * @return the vistoryOperationType
     */
    public VistoryOperationType getOperationType() {
        return operationType;
    }
    /**
     * @param operationType the operationType to set
     */
    public void setOperationType(VistoryOperationType operationType) {
        this.operationType = operationType;
    }
    /**
     * Fluent origin id builder.
     * @param originId the origin id
     * @return self
     */
    public TimeIntervalContributorInfo withOriginId(String originId) {
        setOriginId(originId);
        return this;
    }
    /**
     * Fluent revision builder.
     * @param revision the revision
     * @return self
     */
    public TimeIntervalContributorInfo withRevision(int revision) {
        setRevision(revision);
        return this;
    }
    /**
     * Fluent source system builder.
     * @param sourceSystem the source system
     * @return self
     */
    public TimeIntervalContributorInfo withSourceSystem(String sourceSystem) {
        setSourceSystem(sourceSystem);
        return this;
    }
    /**
     * Fluent created by builder.
     * @param createdBy the created by
     * @return self
     */
    public TimeIntervalContributorInfo withCreatedBy(String createdBy) {
        setCreatedBy(createdBy);
        return this;
    }
    /**
     * Fluent create date builder.
     * @param createDate the create date
     * @return self
     */
    public TimeIntervalContributorInfo withCreateDate(Date createDate) {
        setCreateDate(createDate);
        return this;
    }
    /**
     * Fluent status builder.
     * @param status the status
     * @return self
     */
    public TimeIntervalContributorInfo withStatus(RecordStatus status) {
        setStatus(status);
        return this;
    }
    /**
     * Fluent approval state builder.
     * @param approvalState the approval state
     * @return self
     */
    public TimeIntervalContributorInfo withApprovalState(ApprovalState approvalState) {
        setApproval(approvalState);
        return this;
    }
    /**
     * Fluent vistory operation type builder.
     * @param vistoryOperationType the vistory operation type
     * @return self
     */
    public TimeIntervalContributorInfo withOperationType(VistoryOperationType vistoryOperationType) {
        setOperationType(vistoryOperationType);
        return this;
    }

}
