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
 * Date: 29.04.2016
 */

package com.unidata.mdm.backend.api.rest.dto.job;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class PaginatedJobStepExecutionsResultRO {
    private List<JobStepExecutionRO> content;

    @JsonProperty(value = "total_count")
    private int totalCount;

    @JsonProperty(value = "completed_count")
    private int completedCount;

    public List<JobStepExecutionRO> getContent() {
        return content;
    }

    public void setContent(List<JobStepExecutionRO> content) {
        this.content = content;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the completedCount
     */
    public int getCompletedCount() {
        return completedCount;
    }

    /**
     * @param completedCount the completedCount to set
     */
    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }
}
