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
