package com.unidata.mdm.backend.service.audit.actions.impl.workflow;

import com.unidata.mdm.backend.common.context.CompleteTaskRequestContext;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class DeclineWorkflowAuditAction extends WorkflowAuditAction {
    private static final String ACTION_NAME = "DECLINE";

    @Override
    public void enrichEvent(Event event, Object... input) {
        CompleteTaskRequestContext context = (CompleteTaskRequestContext) input[0];
        event.putTaskId(context.getTaskId());
        //todo think about process key(etalon id)
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof CompleteTaskRequestContext;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}
