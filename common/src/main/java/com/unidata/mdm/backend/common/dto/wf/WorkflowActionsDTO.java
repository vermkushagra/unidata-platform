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

package com.unidata.mdm.backend.common.dto.wf;

import java.util.List;

import com.unidata.mdm.backend.common.integration.wf.WorkflowAction;

/**
 * @author Mikhail Mikhailov
 * Workflow actions container.
 */
public class WorkflowActionsDTO {

    /**
     * Available actions.
     */
    private List<WorkflowAction> actions;

    /**
     * Constructor.
     */
    public WorkflowActionsDTO() {
        super();
    }

    /**
     * Constructor.
     */
    public WorkflowActionsDTO(List<WorkflowAction> actions) {
        super();
        this.actions = actions;
    }

    /**
     * @return the actions
     */
    public List<WorkflowAction> getActions() {
        return actions;
    }

    /**
     * @param actions the actions to set
     */
    public void setActions(List<WorkflowAction> actions) {
        this.actions = actions;
    }
}
