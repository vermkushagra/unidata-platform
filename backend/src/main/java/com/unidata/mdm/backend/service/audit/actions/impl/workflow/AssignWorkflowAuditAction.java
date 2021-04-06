package com.unidata.mdm.backend.service.audit.actions.impl.workflow;

import com.unidata.mdm.backend.common.context.CompleteTaskRequestContext;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class AssignWorkflowAuditAction extends WorkflowAuditAction {
    private static final String ACTION_NAME = "ASSIGN";

    @Override
    public void enrichEvent(Event event, Object... input) {
    }

    @Override
    public boolean isValidInput(Object... input) {
        return false;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}
