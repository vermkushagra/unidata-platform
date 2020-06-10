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

package com.unidata.mdm.backend.common.integration.wf;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.integration.auth.User;

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

    default List<String> groupForUser(final User user) {
        return Collections.emptyList();
    }

    default WorkflowProcessAfterStartState afterProcessStart(
            final String processInstanceId,
            final String processDefinitionId,
            final Map<String, Object> variables
    ) {
        return new WorkflowProcessAfterStartState();
    }
}