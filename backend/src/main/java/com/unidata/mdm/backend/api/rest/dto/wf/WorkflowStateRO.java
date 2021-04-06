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
