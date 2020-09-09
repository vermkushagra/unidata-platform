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

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowCommentRO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowCommentDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Denis Kostovarov
 */
public class WorkflowCommentConverter {
    private WorkflowCommentConverter() {
        super();
    }

    /**
     * Converts internal object to REST object.
     * @param source the source
     * @return result
     */
    public static WorkflowCommentRO to(final WorkflowCommentDTO source) {

        if (source == null) {
            return null;
        }

        final WorkflowCommentRO ro = new WorkflowCommentRO();
        ro.setId(source.getId());
        ro.setDateTime(source.getDateTime());
        ro.setMessage(source.getMessage());
        ro.setProcessInstanceId(source.getProcessInstanceId());
        ro.setTaskId(source.getTaskId());
        ro.setUsername(source.getUsername());
        ro.setUserLogin(source.getUserLogin());
        ro.setType(source.getType());

        return ro;
    }

    /**
     * Converts list of DTOs to list of REST objects.
     * @param source the source
     * @return result
     */
    public static List<WorkflowCommentRO> to(final List<WorkflowCommentDTO> source) {
        if (source != null && !source.isEmpty()) {
            return source.stream()
                    .map(WorkflowCommentConverter::to).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
