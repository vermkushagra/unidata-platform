package com.unidata.mdm.backend.common.integration.wf;

import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Process start state.
 */
public class WorkflowProcessStartState {
    /**
     * Tells the caller, whether it is allowed to start the process.
     */
    private boolean allow;
    /**
     * Describe denial or acceptance reason.
     */
    private String message;
    /**
     * Additional process variables.
     */
    private Map<String, Object> additionalProcessVariables;
    /**
     * Constructor.
     */
    public WorkflowProcessStartState() {
        super();
    }
    /**
     * Constructor.
     */
    public WorkflowProcessStartState(boolean allow, String message) {
        super();
        this.allow = allow;
        this.message = message;
    }
    /**
     * @return the allow
     */
    public boolean isAllowed() {
        return allow;
    }
    /**
     * @param allow the allow to set
     */
    public void setAllow(boolean allow) {
        this.allow = allow;
    }
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    /**
     * @return the additionalProcessVariables
     */
    public Map<String, Object> getAdditionalProcessVariables() {
        return additionalProcessVariables;
    }
    /**
     * @param additionalProcessVariables the additionalProcessVariables to set
     */
    public void setAdditionalProcessVariables(Map<String, Object> additionalProcessVariables) {
        this.additionalProcessVariables = additionalProcessVariables;
    }
}
