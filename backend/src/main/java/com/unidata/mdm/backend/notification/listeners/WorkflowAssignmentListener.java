package com.unidata.mdm.backend.notification.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.Message;
import com.unidata.mdm.backend.notification.events.WorkflowAssignmentEvent;
import com.unidata.mdm.backend.service.wf.WorkflowService;

/**
 * @author Mikhail Mikhailov
 * Process assignment events.
 */
@Component
public class WorkflowAssignmentListener extends AbstractOwnRejectMessageListener<WorkflowAssignmentEvent> {
    /**
     * The workflow service.
     */
    @Autowired
    private WorkflowService workflowService;
    /**
     * {@inheritDoc}
     */
    @Override
    public void onForeignMessage(Message<WorkflowAssignmentEvent> message) {
        workflowService.readAssignments();
    }
}
