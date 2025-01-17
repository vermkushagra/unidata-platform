package com.unidata.mdm.backend.api.rest.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowAssignmentRO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.integration.wf.EditWorkflowProcessTriggerType;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Work flow assignment converter.
 */
public class WorkflowAssignmentConverter {

    /**
     * Constructor.
     */
    private WorkflowAssignmentConverter() {
        super();
    }

    /**
     * Converts REST object to internal object.
     * @param source the source
     * @return result
     */
    public static WorkflowAssignmentDTO from(final WorkflowAssignmentRO source) {

        if (source == null) {
            return null;
        }

        final WorkflowAssignmentDTO dto = new WorkflowAssignmentDTO();
        dto.setCreateDate(source.getCreateDate());
        dto.setCreatedBy(source.getCreatedBy());
        dto.setId(source.getId());
        dto.setName(source.getEntityName());
        dto.setProcessName(source.getProcessDefinitionId());
        dto.setType(source.getProcessType());

        if (dto.getType() == WorkflowProcessType.RECORD_EDIT) {
            dto.setTriggerType(EditWorkflowProcessTriggerType.fromString(source.getTriggerType()));
        }

        dto.setUpdateDate(source.getUpdateDate());
        dto.setUpdatedBy(source.getUpdatedBy());

        return dto;
    }

    /**
     * Converts internal object to REST object.
     * @param source the source
     * @return result
     */
    public static WorkflowAssignmentRO to(final WorkflowAssignmentDTO source) {

        if (source == null) {
            return null;
        }

        final WorkflowAssignmentRO ro = new WorkflowAssignmentRO();
        ro.setCreateDate(source.getCreateDate());
        ro.setCreatedBy(source.getCreatedBy());
        ro.setId(source.getId());
        ro.setEntityName(source.getName());
        ro.setProcessDefinitionId(source.getProcessName());
        ro.setProcessType(source.getType());
        ro.setTriggerType(source.getTriggerType() != null ? source.getTriggerType().asString() : null);
        ro.setUpdateDate(source.getUpdateDate());
        ro.setUpdatedBy(source.getUpdatedBy());

        return ro;
    }

    /**
     * Converts list of ROs to list of internal objects.
     * @param source the source
     * @return result
     */
    public static List<WorkflowAssignmentDTO> from(List<WorkflowAssignmentRO> source) {

        if (source != null && !source.isEmpty()) {
            return source.stream().map(WorkflowAssignmentConverter::from).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     * Converts list of DTOs to list of REST objects.
     * @param source the source
     * @return result
     */
    public static List<WorkflowAssignmentRO> to(List<WorkflowAssignmentDTO> source) {

        if (source != null && !source.isEmpty()) {
            return source.stream().map(WorkflowAssignmentConverter::to).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
