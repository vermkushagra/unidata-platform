package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowInstanceHistoryRO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowHistoryItemDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Denis Kostovarov
 */
public class WorkflowHistoryConverter {
    private WorkflowHistoryConverter() {
        super();
    }

    public static WorkflowInstanceHistoryRO to(final WorkflowHistoryItemDTO dto) {
        final WorkflowInstanceHistoryRO ro = new WorkflowInstanceHistoryRO(
                WorkflowInstanceHistoryRO.WorkflowHistoryItemType.fromString(dto.getItemType()));
        ro.setId(dto.getId());
        ro.setName(dto.getName());
        ro.setDescription(dto.getDescription());
        ro.setFilename(dto.getFilename());
        ro.setStartTime(dto.getStartTime());
        ro.setEndTime(dto.getEndTime());
        ro.setClaimTime(dto.getClaimTime());
        ro.setAssignee(dto.getAssignee());
        return ro;
    }

    public static List<WorkflowInstanceHistoryRO> to(final List<WorkflowHistoryItemDTO> dto) {
        if (dto != null && !dto.isEmpty()) {
            return dto.stream().map(WorkflowHistoryConverter::to).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
