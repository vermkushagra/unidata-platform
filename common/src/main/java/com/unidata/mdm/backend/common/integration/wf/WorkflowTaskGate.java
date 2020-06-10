package com.unidata.mdm.backend.common.integration.wf;

import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Work flow task info.
 */
public interface WorkflowTaskGate {

    /**
     * Get actions for a given task id.
     * @param taskDefinitionId the task definition id
     * @param variables currently active and set variables (in both process and task scopes)
     * @return list of actions. Task will be completed immediately on empty or null return.
     */
    List<WorkflowAction> getActions(String taskDefinitionId, Map<String, Object> variables);
    /**
     * Allows denial or explicit completion (with a message) of a task.
     * @param taskDefinitionId the task definition id
     * @param variables variables
     * @param actionCode chosen action code or null if no actions defined for the task
     * @return state or null
     */
    WorkflowTaskCompleteState complete(String taskDefinitionId, Map<String, Object> variables, String actionCode);
}
