package com.unidata.mdm.backend.common.dto.wf;

import java.util.List;

import com.unidata.mdm.backend.common.integration.wf.WorkflowAction;

/**
 * @author Mikhail Mikhailov
 * Workflow actions container.
 */
public class WorkflowActionsDTO {

    /**
     * Available actions.
     */
    private List<WorkflowAction> actions;

    /**
     * Constructor.
     */
    public WorkflowActionsDTO() {
        super();
    }

    /**
     * Constructor.
     */
    public WorkflowActionsDTO(List<WorkflowAction> actions) {
        super();
        this.actions = actions;
    }

    /**
     * @return the actions
     */
    public List<WorkflowAction> getActions() {
        return actions;
    }

    /**
     * @param actions the actions to set
     */
    public void setActions(List<WorkflowAction> actions) {
        this.actions = actions;
    }
}
