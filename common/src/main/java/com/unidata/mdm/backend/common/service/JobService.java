package com.unidata.mdm.backend.common.service;

import java.util.Collection;

import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobExecutionDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;

public interface JobService {

    Collection<JobDTO> findAll();

    /**
     * @param jobDto
     * @return
     */
    JobDTO saveJob(JobDTO jobDto);

    /**
     * @param jobId
     */
    void removeJob(long jobId);

    JobExecutionDTO runJob(long jobId, Collection<JobParameterDTO> jobParameters);

    JobExecutionDTO jobStatus(long jobId);

    JobDTO findJob(long jobId);

    Long stopJob(long jobId);
}
