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

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowCompletionStateRO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowCompletionStateDTO;

/**
 * @author Mikhail Mikhailov
 * WF completion state converter.
 */
public class WorkflowCompletionStateConverter {

    /**
     * Constructor.
     */
    private WorkflowCompletionStateConverter() {
        super();
    }

    /**
     * From internal to REST.
     * @param source the source
     * @return REST object
     */
    public static WorkflowCompletionStateRO to(WorkflowCompletionStateDTO source) {

        WorkflowCompletionStateRO result = new WorkflowCompletionStateRO();
        result.setComplete(source.isComplete());
        result.setMessage(source.getMessage());

        return result;
    }
}
