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

/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.unidata.mdm.backend.util.MessageUtils;

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowTaskRO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTaskDTO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author Mikhail Mikhailov
 *
 */
public class WorkflowTaskConverter {

    /**
     * Constructor.
     */
    private WorkflowTaskConverter() {
        super();
    }

    /**
     * To.
     * @param source the source
     * @return REST task.
     */
    public static WorkflowTaskRO to(WorkflowTaskDTO source, Collection<String> candidateGroups) {

        if (source == null) {
            return null;
        }

        WorkflowTaskRO target = new WorkflowTaskRO();
        target.setCreateDate(source.getCreateDate());
        target.setOriginator(source.getOriginator());
        target.setOriginatorName(source.getOriginatorName());
        target.setOriginatorEmail(source.getOriginatorEmail());
        target.setProcessId(source.getProcessId());
        target.setProcessTitle(source.getProcessTitle());
        target.setProcessType(source.getProcessType() != null ? source.getProcessType().name() : null);
        target.setProcessTypeName(source.getProcessType() != null ? MessageUtils.getMessage("app.wf.WorkflowProcessType." + source.getProcessType().name()) : null);
        target.setTriggerType(source.getTriggerType() != null ? source.getTriggerType().asString() : null);
        target.setProcessDefinitionId(source.getProcessDefinitionId());
        target.setTaskDescription(source.getTaskDescription());
        target.setTaskId(source.getTaskId());
        target.setTaskKey(source.getTaskKey());
        target.setTaskTitle(source.getTaskTitle());
        target.setTaskAssignee(source.getTaskAssignee());
        target.setTaskAssigneeName(source.getTaskAssigneeName());
        target.setTaskCandidate(source.getTaskCandidate());
        target.setApprovalMessage(source.getApprovalMessage());
        target.setFinished(source.isFinished());
        target.setProcessFinished(source.isProcessFinished());
        target.setTaskCompletedBy(source.getTaskCompletedBy());
        target.setFinishedDate(source.getFinishedDate());
        target.setVariables(source.getVariables());
        target.setAssignableToCurrentUser(!source.isFinished()
                && source.getTaskAssignee() == null
                && isAssignableToCurrentUser(source, candidateGroups));
        target.setUnassignableByCurrentUser(!source.isFinished()
                && SecurityUtils.getCurrentUserName().equals(source.getTaskAssignee())
                && Objects.nonNull(source.getTaskCandidate()));
        target.setActions(WorkflowActionConverter.to(source.getWorkflowActions() != null ? source.getWorkflowActions().getActions() : null));

        return target;
    }

    private static boolean isAssignableToCurrentUser(WorkflowTaskDTO source, Collection<String> candidateGroups) {
        if (CollectionUtils.isNotEmpty(candidateGroups)) {
            return candidateGroups.contains(source.getTaskCandidate());
        }
        return SecurityUtils.getSecurityTokenForCurrentUser().getRolesMap().containsKey(source.getTaskCandidate());
    }

    /**
     * To list.
     * @param source the source
     * @return list of REST objects
     */
    public static List<WorkflowTaskRO> to(List<WorkflowTaskDTO> source, Collection<String> candidateGroups) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<WorkflowTaskRO> target = new ArrayList<>();
        for (WorkflowTaskDTO dto : source) {
            target.add(to(dto, candidateGroups));
        }

        return target;
    }

    public static List<WorkflowTaskRO> to(List<WorkflowTaskDTO> source) {
        return to(source, Collections.emptyList());
    }
}
