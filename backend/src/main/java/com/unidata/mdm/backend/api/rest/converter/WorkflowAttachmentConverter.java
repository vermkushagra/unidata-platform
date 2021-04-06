package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowAttachRO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAttachDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Denis Kostovarov
 */
public class WorkflowAttachmentConverter {
    private WorkflowAttachmentConverter() {
        super();
    }

    public static List<WorkflowAttachRO> to(List<WorkflowAttachDTO> processAttaches) {
        if (processAttaches != null && !processAttaches.isEmpty()) {
            return processAttaches.stream()
                    .map(WorkflowAttachmentConverter::to).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public static WorkflowAttachRO to(final WorkflowAttachDTO dto) {
        final WorkflowAttachRO ro = new WorkflowAttachRO();

        ro.setId(dto.getId());
        ro.setUsername(dto.getUsername());
        ro.setUserLogin(dto.getUserLogin());
        ro.setTaskId(dto.getTaskId());
        ro.setType(dto.getType());
        ro.setProcessInstanceId(dto.getProcessInstanceId());
        ro.setDateTime(dto.getDateTime());
        ro.setName(dto.getName());
        ro.setDescription(dto.getDescription());
        ro.setUrl(dto.getUrl());

        return ro;
    }
}
