package com.unidata.mdm.backend.api.rest.dto.wf;

import java.util.Date;

import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Entity to process assignment REST object.
 */
public class WorkflowAssignmentRO {

    /**
     * ID.
     */
    private Long id;
    /**
     * Object (register) name.
     */
    private String entityName;
    /**
     * Process type.
     */
    private WorkflowProcessType processType;
    /**
     * Trigger type.
     */
    private String triggerType;
    /**
     * Name of the assigned process.
     */
    private String processDefinitionId;
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
    public WorkflowAssignmentRO() {
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
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param name the name to set
     */
    public void setEntityName(String name) {
        this.entityName = name;
    }

    /**
     * @return the type
     */
    public WorkflowProcessType getProcessType() {
        return processType;
    }

    /**
     * @param type the type to set
     */
    public void setProcessType(WorkflowProcessType type) {
        this.processType = type;
    }

    /**
     * @return the triggerType
     */
    public String getTriggerType() {
        return triggerType;
    }

    /**
     * @param triggerType the triggerType to set
     */
    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    /**
     * @return the processName
     */
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    /**
     * @param processName the processName to set
     */
    public void setProcessDefinitionId(String processName) {
        this.processDefinitionId = processName;
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
