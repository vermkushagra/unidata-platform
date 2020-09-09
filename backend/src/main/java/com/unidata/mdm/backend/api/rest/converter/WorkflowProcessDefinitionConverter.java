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

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowProcessDefinitionRO;
import com.unidata.mdm.conf.WorkflowProcessDefinition;

/**
 * @author Mikhail Mikhailov
 * Process definition converter.
 */
public class WorkflowProcessDefinitionConverter {

    /**
     * Constructor.
     */
    private WorkflowProcessDefinitionConverter() {
        super();
    }

    /**
     * To external type.
     * @param source the source
     * @return REST object
     */
    public static WorkflowProcessDefinitionRO to(WorkflowProcessDefinition source) {

        if (source == null) {
            return null;
        }

        WorkflowProcessDefinitionRO target = new WorkflowProcessDefinitionRO();
        target.setDescription(source.getDescription());
        target.setId(source.getId());
        target.setName(source.getName());
        target.setPath(source.getPath());
        target.setType(source.getType() != null ? source.getType().name() : null);

        return target;
    }

    /**
     * To external type.
     * @param source the source
     * @return REST object
     */
    public static List<WorkflowProcessDefinitionRO> to(List<WorkflowProcessDefinition> source) {

        if (source != null && !source.isEmpty()) {

            List<WorkflowProcessDefinitionRO> result = new ArrayList<>();
            for (WorkflowProcessDefinition wpd : source) {
                result.add(WorkflowProcessDefinitionConverter.to(wpd));
            }

            return result;
        }

        return Collections.emptyList();
    }
}
