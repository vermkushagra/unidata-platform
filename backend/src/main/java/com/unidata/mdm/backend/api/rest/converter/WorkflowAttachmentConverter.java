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
