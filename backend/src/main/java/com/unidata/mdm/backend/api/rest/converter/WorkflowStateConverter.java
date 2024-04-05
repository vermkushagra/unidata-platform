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

import java.util.Collection;
import java.util.Collections;

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowStateRO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowStateDTO;

/**
 * @author Mikhail Mikhailov
 * WF state converter.
 */
public class WorkflowStateConverter {

    /**
     * Constructor.
     */
    private WorkflowStateConverter() {
        super();
    }

    /**
     * From internal to REST
     * @param source the source
     * @param strings
     * @return REST state
     */
    public static WorkflowStateRO to(WorkflowStateDTO source, Collection<String> candidateGroups) {

        if (source == null) {
            return null;
        }

        WorkflowStateRO target = new WorkflowStateRO();
        target.setTotalCount(source.getTotalCount());
        target.setTasks(WorkflowTaskConverter.to(source.getTasks(), candidateGroups));

        return target;
    }

    public static WorkflowStateRO to(WorkflowStateDTO source) {
        return to(source, Collections.emptyList());
    }
}