package com.unidata.mdm.backend.po.audit;

import java.util.Date;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class AuditPO {
    /**
     * Field Audit ID.
     */
    public static final String FIELD_AUDIT_ID = "id";
    /**
     * Field create date.
     */
    public static final String FIELD_CREATE_DATE = "create_date";
    /**
     * Field created by.
     */
    public static final String FIELD_CREATED_BY = "created_by";
    /**
     * Field operation id.
     */
    public static final String FIELD_OPERATION_ID = "operation_id";
    /**
     * Field details.
     */
    public static final String FIELD_DETAILS = "details";
    /**
     * Field action.
     */
    public static final String FIELD_ACTION = "action";

    /**
     *  id
     */
    private Long id;
    /**
     * Create time stamp.
     */
    private Date createDate;
    /**
     * Created by.
     */
    private String createdBy;
    /**
     * Operation id
     */
    private String operationId;
    /**
     * Audit details
     */
    private String details;
    /**
     * Audit action
     */
    private String action;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
