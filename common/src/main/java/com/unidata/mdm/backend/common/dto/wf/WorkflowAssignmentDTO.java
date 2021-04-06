package com.unidata.mdm.backend.common.dto.wf;

import java.util.Date;

import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessTriggerType;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Workflow assignment DTO.
 */
public class WorkflowAssignmentDTO {
    /**
     * ID.
     */
    private Long id;
    /**
     * Object (register) name.
     */
    private String name;
    /**
     * Process type.
     */
    private WorkflowProcessType type;
    /**
     * Name of the assigned process.
     */
    private String processName;
    /**
     * Trigger type.
     */
    private WorkflowProcessTriggerType triggerType;
    /**
     * Create time stamp.
     */
    private Date createDate;
    /**
     * Update time stamp.
     */
    private Date updateDate;
    /**
     * Created by.
     */
    private String createdBy;
    /**
     * Updated by.
     */
    private String updatedBy;
    /**
     * Constructor.
     */
    public WorkflowAssignmentDTO() {
        super();
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public WorkflowProcessType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(WorkflowProcessType type) {
        this.type = type;
    }

    /**
     * @return the processName
     */
    public String getProcessName() {
        return processName;
    }

    /**
     * @param processName the processName to set
     */
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    /**
     * @return the triggerType
     */
    public WorkflowProcessTriggerType getTriggerType() {
        return triggerType;
    }

    /**
     * @param triggerType the triggerType to set
     */
    public void setTriggerType(WorkflowProcessTriggerType triggerType) {
        this.triggerType = triggerType;
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

}
