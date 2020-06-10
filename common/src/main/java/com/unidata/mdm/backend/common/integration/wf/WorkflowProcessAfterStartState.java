package com.unidata.mdm.backend.common.integration.wf;

import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Process afert start state.
 */
public class WorkflowProcessAfterStartState {
    /**
     * Additional process variables.
     */
    private Map<String, Object> additionalProcessVariables;

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
