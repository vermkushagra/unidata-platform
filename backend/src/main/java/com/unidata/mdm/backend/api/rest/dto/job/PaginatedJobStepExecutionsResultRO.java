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
