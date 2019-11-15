package org.unidata.mdm.core.service;

import org.unidata.mdm.core.dto.PaginatedResultDTO;
import org.unidata.mdm.core.dto.job.JobDTO;
import org.unidata.mdm.core.dto.job.JobExecutionDTO;
import org.unidata.mdm.core.dto.job.JobParameterDTO;
import org.unidata.mdm.core.dto.job.JobTriggerDTO;
import org.unidata.mdm.core.dto.job.StepExecutionDTO;
import org.unidata.mdm.core.exception.JobException;
import org.unidata.mdm.core.service.ext.JobTemplateParameters;
import org.unidata.mdm.core.type.job.JobFilter;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface JobService {

    String SUCCESS_FINISH_JOB_ID_PARAMETER = "$success_finish_job_id$";
    String FAIL_FINISH_JOB_ID_PARAMETER = "$fail_finish_job_id$";

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

    /**
     * @param jobName
     * @return
     * @throws JobException
     */
    JobExecutionDTO run(String jobName) throws JobException;

    /**
     * @param jobName
     * @param jobParameters
     * @return
     * @throws JobException
     */
    JobExecutionDTO run(String jobName, Collection<JobParameterDTO> jobParameters) throws JobException;

    /**
     * @param jobId
     * @return
     * @throws JobException
     */
    JobExecutionDTO start(long jobId) throws JobException;

    /**
     * @param jobId jobId
     * @param  parentJobExecutionId parent job execution id
     * @return
     * @throws JobException
     */
    JobExecutionDTO start(long jobId, Long parentJobExecutionId) throws JobException;

    /**
     * Start system job
     *
     * @param jobDto job description
     * @return job execution
     */
    JobExecutionDTO startSystemJob(JobDTO jobDto);

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

    /**
	 * Search jobs by filter.
	 * @param filter job filter.
	 * @return search results.
	 */
    PaginatedResultDTO<JobDTO> searchJobs(JobFilter filter);

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
    List<JobExecutionDTO> findAllJobExecutions(long jobId);

    /**
     *
     * @param jobId
     * @param fromInd
     * @param itemCount
     * @return
     */
    PaginatedResultDTO<JobExecutionDTO> findJobExecutions(long jobId, long fromInd, int itemCount);

    PaginatedResultDTO<StepExecutionDTO> searchStepExecutions(long jobExecutionId, long fromInd, int itemCount);

    /**
     * @param jobIds
     * @return
     */
    Map<Long, JobExecutionDTO> findLastJobExecutions(List<Long> jobIds);

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
    void schedule(long jobId, String cronExpression);

    /**
     * Schedule unidata job.
     *
     * @param jobId          Unidata job id.
     * @param cronExpression Cron expression.
     * @param parentJobExecutionId parent job execution id
     */
    void schedule(long jobId, String cronExpression, Long parentJobExecutionId);

    void exportJobs(String userLogin, List<Long> jobsIds);

    void importJobs(String userLogin, Path path);

    List<String> getAllTags();
}
