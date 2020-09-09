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
        ro.setCompletedBy(dto.getCompletedBy());
        return ro;
    }

    public static List<WorkflowInstanceHistoryRO> to(final List<WorkflowHistoryItemDTO> dto) {
        if (dto != null && !dto.isEmpty()) {
            return dto.stream().map(WorkflowHistoryConverter::to).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
