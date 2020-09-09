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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowActionRO;
import com.unidata.mdm.backend.common.integration.wf.WorkflowAction;

/**
 * @author Mikhail Mikhailov
 * Actions converter.
 */
public class WorkflowActionConverter {

    /**
     * Constructor.
     */
    private WorkflowActionConverter() {
        super();
    }

    /**
     * From internal to REST.
     * @param action the action
     * @return REST
     */
    public static List<WorkflowActionRO> to(List<WorkflowAction> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<WorkflowActionRO> result = new ArrayList<>();
        for (WorkflowAction action : source) {
            result.add(to(action));
        }

        return result;
    }

    /**
     * From internal to REST.
     * @param action the action
     * @return REST
     */
    public static WorkflowActionRO to(WorkflowAction source) {

        WorkflowActionRO result = new WorkflowActionRO();
        result.setCode(source.getCode());
        result.setDescription(source.getDescription());
        result.setName(source.getName());

        return result;
    }
}
