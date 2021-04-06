package com.unidata.mdm.backend.common.dto.job;

public enum JobExecutionBatchStatus {
    COMPLETED,
    STARTING,
    STARTED,
    STOPPING,
    STOPPED,
    FAILED,
    ABANDONED,
    UNKNOWN
}
