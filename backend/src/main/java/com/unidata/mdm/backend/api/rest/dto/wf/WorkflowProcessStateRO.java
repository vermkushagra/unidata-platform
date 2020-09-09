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

package com.unidata.mdm.backend.api.rest.dto.wf;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Dmitry Kopin on 15.05.2018.
 */
public class WorkflowProcessStateRO {
    /**
     * Optional number of all potential hits.
     */
    @JsonProperty(value = "total_count", index = 1)
    private long totalCount;
    /**
     * Collected tasks.
     */
    private List<WorkflowProcessRO> processes;

    /**
     * Collected tasks.
     */
    public List<WorkflowProcessRO> getProcesses() {
        return processes;
    }

    public void setProcesses(List<WorkflowProcessRO> processes) {
        this.processes = processes;
    }

    /**
     * Optional number of all potential hits.
     */
    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
