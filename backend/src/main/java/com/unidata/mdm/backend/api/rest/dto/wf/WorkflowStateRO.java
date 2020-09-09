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
package com.unidata.mdm.backend.api.rest.dto.wf;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mikhail Mikhailov
 * Workflow state for a particular user.
 */
public class WorkflowStateRO {
    /**
     * Optional number of all potential hits.
     */
    @JsonProperty(value = "total_count", index = 1)
    private long totalCount;
    /**
     * Collected tasks.
     */
    private List<WorkflowTaskRO> tasks;

    /**
     * Constructor.
     */
    public WorkflowStateRO() {
        super();
    }

    /**
     * Gets the total number of potential hits.
     * @return the totalCount
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the total number of potential hits.
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the tasks
     */
    public List<WorkflowTaskRO> getTasks() {
        return tasks;
    }

    /**
     * @param tasks the tasks to set
     */
    public void setTasks(List<WorkflowTaskRO> tasks) {
        this.tasks = tasks;
    }
}
