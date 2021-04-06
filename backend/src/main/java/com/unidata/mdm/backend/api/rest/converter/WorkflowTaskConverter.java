/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowTaskRO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTaskDTO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

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
    public static WorkflowTaskRO to(WorkflowTaskDTO source) {

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
                && SecurityUtils.getSecurityTokenForCurrentUser().getRolesMap().containsKey(source.getTaskCandidate()));
        target.setUnassignableByCurrentUser(!source.isFinished()
                && SecurityUtils.getCurrentUserName().equals(source.getTaskAssignee())
                && Objects.nonNull(source.getTaskCandidate()));
        target.setActions(WorkflowActionConverter.to(source.getWorkflowActions() != null ? source.getWorkflowActions().getActions() : null));

        return target;
    }

    /**
     * To list.
     * @param source the source
     * @return list of REST objects
     */
    public static List<WorkflowTaskRO> to(List<WorkflowTaskDTO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<WorkflowTaskRO> target = new ArrayList<>();
        for (WorkflowTaskDTO dto : source) {
            target.add(to(dto));
        }

        return target;
    }
}
