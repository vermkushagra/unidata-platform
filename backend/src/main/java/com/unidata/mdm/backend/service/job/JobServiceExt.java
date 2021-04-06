package com.unidata.mdm.backend.service.job;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

import com.unidata.mdm.backend.common.dto.PaginatedResultDTO;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.common.service.JobService;
import com.unidata.mdm.backend.dto.job.JobTriggerDTO;
import com.unidata.mdm.backend.po.job.JobPO;
import com.unidata.mdm.backend.service.job.registry.JobTemplateParameters;

public interface JobServiceExt extends JobService {

    String SUCCESS_FINISH_JOB_ID_PARAMETER = "$success_finish_job_id$";

    String FAIL_FINISH_JOB_ID_PARAMETER = "$fail_finish_job_id$";

    /**
     * @param jobName
     * @return
     * @throws com.unidata.mdm.backend.common.exception.JobException
     */
    JobExecution run(String jobName) throws JobException;

    /**
     * @param jobName
     * @param jobParameters
     * @return
     * @throws JobException
     */
    JobExecution run(String jobName, Collection<JobParameterDTO> jobParameters) throws JobException;

//    JobExecution run(Job job, JobParameters parameters) throws JobException;

    /**
     * @param jobId
     * @return
     * @throws JobException
     */
    JobExecution start(long jobId) throws JobException;

    /**
     * Start system job
     *
     * @param jobDto job description
     * @return job execution
     */
    JobExecution startSystemJob(JobDTO jobDto);

    /**
     * Restart job by jobExecution ID.
     *
     * @param jobExecutionId
     */
    Long restart(long jobExecutionId);

    /**
     * @param jobId
     * @return
     * @throws JobException
     */
    Long stop(long jobId) throws JobException;

    /**
     * @param jobId
     * @throws JobException
     */
    void enableJob(long jobId) throws JobException;

    /**
     * @param jobId
     * @throws JobException
     */
    void disableJob(long jobId) throws JobException;

    /**
     * @param jobs
     * @throws JobException
     */
    void markErrorJobs(Collection<JobDTO> jobs, boolean error) throws JobException;

    List<JobDTO> findAllJobsWithParams();

    List<JobPO> findAllJobs();

    /**
     *
     * @param fromInd
     * @param itemCount
     * @return
     */
    PaginatedResultDTO<JobDTO> searchJobs(long fromInd, int itemCount, Boolean enabled);

    /**
     * @return
     */
    Collection<String> findAllJobReferences();

    /**
     *
     * @return
     */
    Collection<String> findAllUIJobReferences();

    /**
     * @param jobName
     * @return
     */
    JobTemplateParameters findJobTemplateParameters(String jobName);

    /**
     * @return
     */
    @Deprecated
    List<JobExecution> findAllJobExecutions(long jobId);

    /**
     *
     * @param jobId
     * @param fromInd
     * @param itemCount
     * @return
     */
    PaginatedResultDTO<JobExecution> findJobExecutions(long jobId, long fromInd, int itemCount);

    PaginatedResultDTO<StepExecution> searchStepExecutions(long jobExecutionId, long fromInd, int itemCount);

    /**
     * @param jobIds
     * @return
     */
    Map<Long, JobExecution> findLastJobExecutions(List<Long> jobIds);

    /**
     *
     * @param jobIds
     * @return
     */
    Map<Long, Long> findLastJobExecutionIds(List<Long> jobIds);

    double getJobProgress(long jobId, long execId);

    List<JobTriggerDTO> findJobTriggers(Long jobId);

    JobTriggerDTO saveJobTrigger(JobTriggerDTO jobTriggerDto);

    void removeTrigger(Long jobId, Long triggerId);

    boolean isJobRestartable(long jobId);

    /**
     * @param complexParameter - any object which you want to pass to job
     * @return key in storage which help find complex parameter in partitioner
     */
    String putComplexParameter(Object complexParameter);

    /**
     * Schedule unidata job.
     *
     * @param jobId          Unidata job id.
     * @param cronExpression Cron expression.
     */
    void schedule(final long jobId, final String cronExpression);
}
