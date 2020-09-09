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

package com.unidata.mdm.backend.service.wf;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowHistoryItemDTO;
import com.unidata.mdm.backend.common.service.WorkflowService;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

public interface WorkflowServiceExt extends WorkflowService, AfterContextRefresh {

    /**
     * Activates a process definition, denoted by path.
     * @param resourcePath the path
     */
    void deployProcess(String resourcePath);

    /**
     * Set updates.
     * @param assignments the assignments
     */
    void updateAssignments(List<WorkflowAssignmentDTO> assignments);

    /**
     * Reads assignments from DB to cache.
     */
    void readAssignments();

    List<WorkflowHistoryItemDTO> getInstanceHistory(String processInstanceId, boolean sortDateAsc, Set<String> types);

    /**
     * Gets PNG diagram othe process, with current step (s) highlightet.
     * @param processInstanceId process instance id
     * @param finished whether the process ahs finished or not
     * @return byte stream
     */
    InputStream generateDiagram(String processInstanceId, boolean finished);

    /**
     * tells whether current user has edit tasks
     * @param etalonId the record etalon id
     * @return true, if has, false otherwise
     */
    boolean hasEditTasks(String etalonId);

    void updateWorkflowProcessTitle(String processInstanceId, String recordTitle);

    Collection<String> currentUserCandidateGroups();
}