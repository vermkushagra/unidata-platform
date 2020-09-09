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

/**
 * @author Dmitry Kopin
 * Workflow statistic for a particular user.
 */
public class WorkflowShortStateRO {

    /**
     * Total tasks assigned to user
     */
    @JsonProperty(value = "total_user_count", index = 1)
    private long totalUserCount;
    /**
     * Count new tasks available for user
     */
    @JsonProperty(value = "new_count_from_date", index = 2)
    private long newCount;
    /**
     * Count tasks available for user
     */
    @JsonProperty(value = "available_count", index = 3)
    private long availableCount;


    /**
     * Constructor.
     */
    public WorkflowShortStateRO() {
        super();
    }

    /**
     * Total tasks assigned to user
     */
    public long getTotalUserCount() {
        return totalUserCount;
    }

    public void setTotalUserCount(long totalUserCount) {
        this.totalUserCount = totalUserCount;
    }

    /**
     * Count new tasks available for user
     */
    public long getNewCount() {
        return newCount;
    }

    public void setNewCount(long newCount) {
        this.newCount = newCount;
    }

    /**
     * Count tasks available for user
     */
    public long getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(long availableCount) {
        this.availableCount = availableCount;
    }
}
