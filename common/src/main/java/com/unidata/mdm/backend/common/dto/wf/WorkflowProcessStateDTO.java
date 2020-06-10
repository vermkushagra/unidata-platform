package com.unidata.mdm.backend.common.dto.wf;

import java.util.List;

/**
 * @author Dmitry Kopin on 15.05.2018.
 */
public class WorkflowProcessStateDTO {
    /**
     * Total processes count.
     */
    private long totalCount = 0;
    /**
     * Processes.
     */
    private List<WorkflowProcessDTO> processes;

    /**
     * Total count.
     */
    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * Processes.
     */
    public List<WorkflowProcessDTO> getProcesses() {
        return processes;
    }

    public void setProcesses(List<WorkflowProcessDTO> processes) {
        this.processes = processes;
    }
}
