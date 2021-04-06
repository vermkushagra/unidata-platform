package com.unidata.mdm.backend.api.rest.dto.wf;

import java.util.Date;

/**
 * @author Mikhail Mikhailov
 * Task complete request.
 */
public class WorkflowTaskCompleteRequestRO {
    /**
     * Selected action.
     */
    private String action;
    /**
     * Process key (such as etalon id.)
     */
    private String processKey;
    /**
     * Process definition key.
     */
    private String processDefinitionKey;
    /**
     * Task id.
     */
    private String taskId;
    /**
     * Approval timestamp to use.
     */
    private Date asOf;
    /**
    /**
     * Constructor.
     */
    public WorkflowTaskCompleteRequestRO() {
        super();
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the processKey
     */
    public String getProcessKey() {
        return processKey;
    }

    /**
     * @param processKey the processKey to set
     */
    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }


    /**
     * @return the processDefinitionKey
     */
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }


    /**
     * @param processDefinitionKey the processDefinitionKey to set
     */
    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    /**
     * @return the asOf
     */
    public Date getAsOf() {
        return asOf;
    }


    /**
     * @param asOf the asOf to set
     */
    public void setAsOf(Date asOf) {
        this.asOf = asOf;
    }

}
