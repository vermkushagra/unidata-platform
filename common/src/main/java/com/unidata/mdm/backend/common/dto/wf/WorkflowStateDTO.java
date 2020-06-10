package com.unidata.mdm.backend.common.dto.wf;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Workflow state for a particular user.
 */
public class WorkflowStateDTO {

    /**
     * Total count.
     */
    private long totalCount = 0;
    /**
     * Tasks.
     */
    private List<WorkflowTaskDTO> tasks;

    /**
     * Constructor.
     */
    public WorkflowStateDTO() {
        super();
    }

    /**
     * @return the totalCount
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the tasks
     */
    public List<WorkflowTaskDTO> getTasks() {
        return tasks;
    }

    /**
     * @param tasks the tasks to set
     */
    public void setTasks(List<WorkflowTaskDTO> tasks) {
        this.tasks = tasks;
    }
}
