package com.unidata.mdm.backend.common.integration.wf;

import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Customization gate for work flow processes.
 */
public interface WorkflowProcessSupport extends WorkflowTaskGate {

    /**
     * Called upon process start. Can prevent a process from being started.
     * @param processDefinitionId process definition id
     * @param variables variables
     * @return workflow completion state
     */
    default WorkflowProcessStartState processStart(String processDefinitionId, Map<String, Object> variables) {
        return new WorkflowProcessStartState(true, "");
    }

    /**
     * Called upon process completion. Can not prevent a process from completion.
     * @param processDefinitionId process definition id
     * @param variables variables
     * @return workflow completion state
     */
    WorkflowProcessEndState processEnd(String processDefinitionId, Map<String, Object> variables);
}
