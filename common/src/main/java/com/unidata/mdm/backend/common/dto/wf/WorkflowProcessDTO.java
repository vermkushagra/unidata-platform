package com.unidata.mdm.backend.common.dto.wf;

import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessTriggerType;
import com.unidata.mdm.conf.WorkflowProcessType;

import java.util.Map;

/**
 * @author Mikhail Mikhailov
 *
 */
public class WorkflowProcessDTO {

    /**
     * Instance id.
     */
    private String processInstanceId;
    /**
     * Process title.
     */
    private String processTitle;
    /**
     * Process definition id.
     */
    private String processDefinitionId;
    /**
     * Process type.
     */
    private WorkflowProcessType processType;
    /**
     * Trigger type.
     */
    private WorkflowProcessTriggerType triggerType;
    /**
     * Process suspended.
     */
    private boolean suspended;
    /**
     * Process ended.
     */
    private boolean ended;
    /**
     * Originator user login.
     */
    private String originator;
    /**
     * Originator user full name.
     */
    private String originatorName;
    /**
     * Originator email.
     */
    private String originatorEmail;
    /**
     * Variables.
     */
    private Map<String, Object> variables = null;
    /**
     * Constructor.
     */
    public WorkflowProcessDTO() {
        super();
    }

    /**
     * @return the processInstanceId
     */
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    /**
     * @param processInstanceId the processInstanceId to set
     */
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    /**
     * @return the processTitle
     */
    public String getProcessTitle() {
        return processTitle;
    }

    /**
     * @param processTitle the processTitle to set
     */
    public void setProcessTitle(String processTitle) {
        this.processTitle = processTitle;
    }

    /**
     * @return the processType
     */
    public WorkflowProcessType getProcessType() {
        return processType;
    }

    /**
     * @param processType the processType to set
     */
    public void setProcessType(WorkflowProcessType processType) {
        this.processType = processType;
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
     * @return the processDefinitionId
     */
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }


    /**
     * @param processDefinitionId the processDefinitionId to set
     */
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    /**
     * @return the suspended
     */
    public boolean isSuspended() {
        return suspended;
    }


    /**
     * @param suspended the suspended to set
     */
    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }


    /**
     * @return the ended
     */
    public boolean isEnded() {
        return ended;
    }


    /**
     * @param ended the ended to set
     */
    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    /**
     * Variables.
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public String getOriginatorName() {
        return originatorName;
    }

    public void setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
    }

    public String getOriginatorEmail() {
        return originatorEmail;
    }

    public void setOriginatorEmail(String originatorEmail) {
        this.originatorEmail = originatorEmail;
    }
}
