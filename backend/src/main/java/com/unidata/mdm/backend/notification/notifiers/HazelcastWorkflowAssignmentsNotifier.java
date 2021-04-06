package com.unidata.mdm.backend.notification.notifiers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.unidata.mdm.backend.notification.events.WorkflowAssignmentEvent;
import com.unidata.mdm.backend.notification.listeners.WorkflowAssignmentListener;

/**
 * @author Mikhail Mikhailov
 * Workflow assignments notifier.
 */
@Component
public class HazelcastWorkflowAssignmentsNotifier implements WorkflowAssignmentsNotifier {
    /**
     * Assignments topic name.
     */
    public static final String WF_ASSIGNMENTS_TOPIC_NAME = "wfAssignmentsTopic";
    /**
     * Notifications topic.
     */
    private ITopic<WorkflowAssignmentEvent> assignmentsTopic;
    /**
     * Hazelcast distributed cache
     */
    @Autowired
    private HazelcastInstance instance;
    /**
     * Assignments listener.
     */
    @Autowired
    private WorkflowAssignmentListener assignmentsListener;
    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyAssignmetsChanged() {
        assignmentsTopic.publish(new WorkflowAssignmentEvent());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterContextRefresh() {
        assignmentsTopic = instance.getTopic(WF_ASSIGNMENTS_TOPIC_NAME);
        assignmentsTopic.addMessageListener(assignmentsListener);
    }
}
