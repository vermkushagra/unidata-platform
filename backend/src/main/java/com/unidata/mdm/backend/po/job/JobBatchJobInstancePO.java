package com.unidata.mdm.backend.po.job;

import com.unidata.mdm.backend.po.AbstractPO;

/**
 * @author Denis Kostovarov
 */
public class JobBatchJobInstancePO extends AbstractPO {
    public static final String TABLE_NAME = "job_batch_job_instance";

    public static final String FIELD_JOB_ID = "job_id";

    public static final String FIELD_JOB_INSTANCE_ID = "job_instance_id";

    private Long jobId;

    private Long jobInstanceId;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    public void setJobInstanceId(Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }
}
