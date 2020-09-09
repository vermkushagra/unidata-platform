/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
