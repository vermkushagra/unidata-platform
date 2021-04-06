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
