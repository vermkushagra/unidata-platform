package com.unidata.mdm.backend.service.job;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.unidata.mdm.backend.api.rest.converter.JobConverter;
import com.unidata.mdm.backend.api.rest.dto.Param;
import com.unidata.mdm.backend.common.dto.PaginatedResultDTO;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobExecutionBatchStatus;
import com.unidata.mdm.backend.common.dto.job.JobExecutionDTO;
import com.unidata.mdm.backend.common.dto.job.JobExecutionExitStatusDTO;
import com.unidata.mdm.backend.common.dto.job.JobExecutionPaginatedResultDTO;
import com.unidata.mdm.backend.common.dto.job.JobExecutionStepDTO;
import com.unidata.mdm.backend.common.dto.job.JobPaginatedResultDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.common.dto.job.JobTemplateParameterDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.dao.JobDao;
import com.unidata.mdm.backend.dto.job.JobExecutionFilter;
import com.unidata.mdm.backend.dto.job.JobFilter;
import com.unidata.mdm.backend.dto.job.JobTriggerDTO;
import com.unidata.mdm.backend.dto.job.StepExecutionFilter;
import com.unidata.mdm.backend.po.job.JobPO;
import com.unidata.mdm.backend.po.job.JobParameterPO;
import com.unidata.mdm.backend.po.job.JobTriggerPO;
import com.unidata.mdm.backend.service.job.batch.core.CustomJobExplorer;
import com.unidata.mdm.backend.service.job.registry.JobTemplateParameters;
import com.unidata.mdm.backend.service.job.registry.JobWithParamsRegistry;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;

/**
 * Job service to manipulate all jobs deployed in system.
 *
 * @author Alexander Magdenko
 */
@Service
public class JobServiceImpl implements JobServiceExt, ApplicationContextAware {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    private static final String QUARTZ_GROUP = "quartz-batch";

    @Autowired
    private JobLauncher jobLauncher;

    private ApplicationContext applicationContext;

    @Autowired
    private JobDao jobDao;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobWithParamsRegistry jobRegistry;

    @Autowired
    private ComplexJobParameterHolder complexJobParameterHolder;

    @Autowired
    private SchedulerFactoryBean quartzSchedulerFactory;

    private DefaultJobParameterProcessor defaultJobParameterProcessor = new DefaultJobParameterProcessor();

    /**
     * Parameter processors..
     */
    private Map<String, JobParameterProcessor> parameterProcessors;

    /**
     * @param parameterProcessors the operations to set
     */
    @Required
    @Resource(name = "jobParameterProcessorsMap")
    public void setOperations(Map<String, JobParameterProcessor> parameterProcessors) {
        this.parameterProcessors = parameterProcessors;
    }

    public static boolean isCronJob(JobPO job) {
        return StringUtils.hasText(job.getCronExpression());
    }

    /**
     * @param jobName
     * @return
     * @throws JobException
     */
    @Override
    public JobExecution run(String jobName) throws JobException {
        return run(jobName, Collections.emptyList());
    }

    /**
     * @param jobName
     * @param jobParameters
     * @return
     * @throws JobException
     */
    @Override
    public JobExecution run(String jobName, Collection<JobParameterDTO> jobParameters) throws JobException {
        Assert.notNull(jobName);

        final Job job = (Job) applicationContext.getBean(jobName);

        JobExecution execution = null;
        try {
            final JobParameters springJobParameters = new JobParameters(
                jobParameters.stream()
                        .map(jobParameterDTO ->
                                new AbstractMap.SimpleImmutableEntry<>(
                                        jobParameterDTO.getName(),
                                        jobParameterDTOToJobParameter(jobParameterDTO)
                                )
                        )
                        .collect(
                                toMap(
                                        AbstractMap.SimpleImmutableEntry::getKey,
                                        AbstractMap.SimpleImmutableEntry::getValue
                                )
                        )
            );
            execution = jobLauncher.run(job, springJobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobParametersInvalidException
                | JobInstanceAlreadyCompleteException e) {
            throw new JobException("Failed to run job:" + jobName, e, ExceptionId.EX_JOB_BATCH_EXECUTION_FAILED,
                    jobName);
        }

        LOGGER.debug("Run job [jobName={}, jobStatus={}]", jobName, execution.getStatus());

        return execution;
    }

    private JobParameter jobParameterDTOToJobParameter(JobParameterDTO jobParameterDTO) {
        switch (jobParameterDTO.getType()) {
            case DATE:
                return new JobParameter(Date.from(jobParameterDTO.getDateValue().toInstant()));
            case DOUBLE:
                return new JobParameter(jobParameterDTO.getDoubleValue());
            case LONG:
                return new JobParameter(jobParameterDTO.getLongValue());
            case STRING:
                return new JobParameter(jobParameterDTO.getStringValue());
        }
        return new JobParameter(jobParameterDTO.getBooleanValue().toString());
    }

    @Override
    public JobExecutionDTO runJob(final long jobId, final Collection<JobParameterDTO> jobParameters) {
        final JobPO job = jobDao.findJob(jobId);
        if (job == null) {
            throw new JobException("Job with id [" + jobId + "] not found", ExceptionId.EX_JOB_NOT_FOUND, jobId);
        }

        validateJobParameters(job.getJobNameReference(), jobParameters, true);

        final JobExecution jobExecution = start(job, jobParameters);
        return jobExecutionDTOFromJobExecution(
                JobConverter.convertJobPoToDto(job),
                jobExecution
        );
    }

    private JobExecutionDTO jobExecutionDTOFromJobExecution(
            final JobDTO jobDTO,
            final JobExecution jobExecution
    ) {
        return new JobExecutionDTO(
                jobDTO,
                jobExecution.getJobParameters().getParameters().entrySet()
                        .stream()
                        .map(entry -> springJobParameterToJobParameterDTO(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList()),
                zonedDateTimeFromDate(jobExecution.getStartTime()),
                zonedDateTimeFromDate(jobExecution.getCreateTime()),
                zonedDateTimeFromDate(jobExecution.getEndTime()),
                zonedDateTimeFromDate(jobExecution.getLastUpdated()),
                JobExecutionBatchStatus.valueOf(jobExecution.getStatus().name()),
                new JobExecutionExitStatusDTO(
                        jobExecution.getExitStatus().getExitCode(),
                        jobExecution.getExitStatus().getExitDescription()
                ),
                jobExecution.getStepExecutions().stream()
                        .map(step -> new JobExecutionStepDTO(
                                step.getStepName(),
                                JobExecutionBatchStatus.valueOf(step.getStatus().name()),
                                new JobExecutionExitStatusDTO(
                                        step.getExitStatus().getExitCode(),
                                        step.getExitStatus().getExitDescription()
                                ),
                                zonedDateTimeFromDate(step.getStartTime()),
                                zonedDateTimeFromDate(step.getEndTime()),
                                zonedDateTimeFromDate(step.getLastUpdated())
                        ))
                        .collect(Collectors.toList())
        );
    }

    private ZonedDateTime zonedDateTimeFromDate(final Date date) {
        return Optional.ofNullable(date)
                .map(Date::toInstant)
                .map(instant -> ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))
                .orElse(null);
    }

    private JobParameterDTO springJobParameterToJobParameterDTO(String name, JobParameter jobParameter) {
        switch (jobParameter.getType()) {
            case DATE:
                return new JobParameterDTO(
                        name,
                        ZonedDateTime.ofInstant(((Date) jobParameter.getValue()).toInstant(), ZoneId.systemDefault())
                );
            case DOUBLE:
                return new JobParameterDTO(name, (Double) jobParameter.getValue());
            case LONG:
                return new JobParameterDTO(name, (Long) jobParameter.getValue());
            case STRING:
                return new JobParameterDTO(name, (String) jobParameter.getValue());
        }
        return new JobParameterDTO(name, jobParameter.getValue().toString());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * @param jobId
     * @return
     * @throws JobException
     */
    @Override
    public JobExecution start(long jobId) throws JobException {
        JobPO job = jobDao.findJob(jobId);

        if (job == null) {
            throw new JobException("Job with id [" + jobId + "] not found", ExceptionId.EX_JOB_NOT_FOUND, jobId);
        }

        return start(job, Collections.singletonList(new JobParameterDTO("jobName", job.getName())));
    }

    private JobExecution start(final JobPO job, final Collection<JobParameterDTO> jobParameters) throws JobException {
        if (!job.isEnabled() || job.isError()) {
            throw new JobException("Job with id [" + job.getId() + "] not enabled", ExceptionId.EX_JOB_DISABLED, job.getId());
        }

        if (findLastRunningJobExecutionId(job.getId()) != null) {
            throw new JobException("Job with id [" + job.getId() + "] is already running",
                    ExceptionId.EX_JOB_ALREADY_RUNNING, job.getName());
        }

        final List<JobParameterPO> jobParams = jobDao.getJobParameters(job.getId());

        final List<JobParameterDTO> dtoParams = merge(
                JobConverter.convertParamPoToDto(jobParams),
                jobParameters
        );

        addTriggerJobParams(job.getId(), dtoParams);
        addLastSuccessJobExecutionDate(job.getId(), dtoParams);

        JobParameters parameters = convertJobParameters(dtoParams, parameterProcessors.get(job.getJobNameReference()));
        JobExecution jobExecution;

        try {
            Job batchJob = jobRegistry.getJob(job.getJobNameReference());

            parameters = addGeneralParameters(parameters);

            jobExecution = jobLauncher.run(batchJob, parameters);

            jobDao.saveBatchJobInstance(job.getId(), jobExecution.getJobId(), SecurityUtils.getCurrentUserName(), new Date());

            LOGGER.debug("Started job [jobId={}, jobInstanceId={}, jobExecutionId={}]", job.getId(),
                    jobExecution.getJobId(), jobExecution.getId());
        } catch (final JobExecutionException e) {
            throw new JobException("Failed to execute job [" + job.getName() + "] with jobReference [" +
                    job.getJobNameReference() + ']', e, ExceptionId.EX_JOB_BATCH_EXECUTION_FAILED, job.getName());
        }

        return jobExecution;
    }

    private List<JobParameterDTO> merge(
            final Collection<JobParameterDTO> fromDB,
            final Collection<JobParameterDTO> fromRequest
    ) {
        final Set<JobParameterDTO> result = new HashSet<>(fromDB);
        result.removeAll(fromRequest);
        result.addAll(fromRequest);
        return new ArrayList<>(result);
    }

    /**
     * Start system job
     *
     * @param jobDto job description
     * @return job execution
     */
    @Override
    public JobExecution startSystemJob(final JobDTO jobDto) {
        try {
            JobParameters parameters = convertJobParameters(jobDto.getParameters(), null);
            parameters = addGeneralParameters(parameters);
            Job batchJob = jobRegistry.getJob(jobDto.getJobNameReference());
            return jobLauncher.run(batchJob, parameters);
        } catch (final JobExecutionException e) {
            throw new JobException("Failed to execute job [" + jobDto.getName() + "] with jobReference [" +
                    jobDto.getJobNameReference() + ']', e, ExceptionId.EX_JOB_BATCH_EXECUTION_FAILED, jobDto.getName());
        }
    }

    private void addTriggerJobParams(final long jobId, final List<JobParameterDTO> dtoParams) {
        final List<JobTriggerDTO> jobTriggerIds = findJobTriggers(jobId);

        int successTriggerCount = 0;
        int failTriggerCount = 0;
        for (final JobTriggerDTO t : jobTriggerIds) {
            if (t.getSuccessRule()) {
                dtoParams.add(new JobParameterDTO(SUCCESS_FINISH_JOB_ID_PARAMETER + successTriggerCount++, t.getStartJobId()));
            } else {
                dtoParams.add(new JobParameterDTO(FAIL_FINISH_JOB_ID_PARAMETER + failTriggerCount++, t.getStartJobId()));
            }
        }
    }

    /**
     * Restart job by jobExecution ID.
     *
     * @param jobExecutionId
     */
    @Override
    public Long restart(long jobExecutionId) {
        LOGGER.debug("Restart job with executionId: {}", jobExecutionId);

        Long newJobExecutionId = null;
        try {
            newJobExecutionId = jobOperator.restart(jobExecutionId);

            LOGGER.debug("Restarted job [oldJobExecutionId={}, newJobExecutionId={}]",
                    jobExecutionId, newJobExecutionId);
        }
        catch (UnexpectedJobExecutionException | JobExecutionException e) {
            if (e.getCause() instanceof JobExecutionAlreadyRunningException) {
                throw new JobException("Failed to restart job with jobExecutionId: " + jobExecutionId, e,
                        ExceptionId.EX_JOB_BATCH_RESTART_FAILED_ALREADY_RUNNING, jobExecutionId);
            }

            throw new JobException("Failed to restart job with jobExecutionId: " + jobExecutionId, e,
                    ExceptionId.EX_JOB_BATCH_RESTART_FAILED, jobExecutionId);
        }

        return newJobExecutionId;
    }

    /**
     * @param jobId
     * @return
     * @throws JobException
     */
    @Override
    public Long stop(long jobId) throws JobException {
        JobPO job = jobDao.findJob(jobId);

        if (job == null) {
            throw new JobException("Job with id [" + jobId + "] not found", ExceptionId.EX_JOB_NOT_FOUND, jobId);
        }

        return stop(job);
    }

    /**
     * @param jobId
     * @throws JobException
     */
    @Override
    @Transactional
    public void enableJob(long jobId) throws JobException {
        JobPO job = jobDao.findJob(jobId);

        if (job == null) {
            throw new JobException("Job with id [" + jobId + "] not found", ExceptionId.EX_JOB_NOT_FOUND, jobId);
        }

        jobDao.markJobEnabled(jobId, true);

        if (isCronJob(job)) {
            schedule(job.getId(), job.getCronExpression());
        }
    }

    /**
     * @param jobId
     * @throws JobException
     */
    @Override
    @Transactional
    public void disableJob(long jobId) throws JobException {
        JobPO job = jobDao.findJob(jobId);

        if (job == null) {
            throw new JobException("Job with id [" + jobId + "] not found", ExceptionId.EX_JOB_NOT_FOUND, jobId);
        }

        jobDao.markJobEnabled(jobId, false);

        unschedule(jobId);

        // Make stop for already started job.
        stop(job);
    }

    /**
     * @param jobs
     * @throws JobException
     */
    @Override
    @Transactional
    public void markErrorJobs(Collection<JobDTO> jobs, boolean error) throws JobException {
        Set<Long> jobIds = jobs.stream().map(JobDTO::getId).collect(Collectors.toSet());

        if (error) {
            jobDao.markJobError(jobIds, true);

            for (JobDTO job : jobs) {
                unschedule(job.getId());

                // Make stop for already started job.
                stop(JobConverter.convertJobDtoToPo(job));
            }
        } else {
            jobDao.markJobError(jobIds, false);

            List<JobPO> jobPOs = JobConverter.convertJobsDtoToPo(jobs);

            for (JobPO job : jobPOs) {
                if (isCronJob(job)) {
                    schedule(job.getId(), job.getCronExpression());
                }
            }
        }
    }

    @Override
    @Transactional
    public List<JobDTO> findAllJobsWithParams() {
        final List<JobPO> dbJobs = jobDao.getJobsWithParameters();

        return JobConverter.convertJobsPoToDto(dbJobs);
    }

    @Override
    @Transactional
    public List<JobPO> findAllJobs() {
        return jobDao.getJobsWithParameters();
    }

    /**
     *
     * @param fromInd
     * @param itemCount
     * @return
     */
    @Override
    @Transactional
    public PaginatedResultDTO<JobDTO> searchJobs(long fromInd, int itemCount, Boolean enabled) {
        JobPaginatedResultDTO<JobDTO> result = new JobPaginatedResultDTO<>();

        JobFilter filter = new JobFilter();
        filter.setFromInd(fromInd);
        filter.setItemCount(itemCount);
        filter.setEnabled(enabled);

        List<JobPO> jobPOs = jobDao.searchJobs(filter);

        if (!CollectionUtils.isEmpty(jobPOs)) {
            Set<Long> jobIds = jobPOs.stream().map(JobPO::getId).collect(Collectors.toSet());

            Map<Long, List<JobParameterPO>> jobParametersMap = jobDao.getJobsParameters(new ArrayList<>(jobIds));

            jobPOs.forEach(jobPO -> jobPO.setParameters(jobParametersMap.get(jobPO.getId())));

            result.setPage(JobConverter.convertJobsPoToDto(jobPOs));
        }

        result.setTotalCount(jobDao.getJobsCount(filter));

        return result;
    }

    /**
     * @return
     */
    @Override
    public Collection<String> findAllJobReferences() {
        return jobRegistry.getJobNames();
    }

    /**
     *
     * @return
     */
    @Override
    public Collection<String> findAllUIJobReferences(){
        return jobRegistry.getJobParameterNames();
    }

    /**
     * @param jobName
     * @return
     */
    @Override
    public JobTemplateParameters findJobTemplateParameters(String jobName) {
        return jobRegistry.getJobTemplateParameters(jobName);
    }

    /**
     * @return
     */
    @Override
    @Deprecated
    @Transactional
    public List<JobExecution> findAllJobExecutions(long jobId) {
        List<Long> batchJobIds = jobDao.findAllBatchJobIds(Collections.singletonList(jobId)).get(jobId);

        List<JobExecution> allExecutions = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(batchJobIds)) {
            Map<Long, List<JobExecution>> jobExecutionsMap = getJobExplorer().getJobExecutions(batchJobIds);

            if (MapUtils.isNotEmpty(jobExecutionsMap)) {
                jobExecutionsMap.values().stream()
                        .filter(v -> !CollectionUtils.isEmpty(v))
                        .forEach(allExecutions::addAll);
            }
        }

        Collections.sort(allExecutions, (o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));

        return allExecutions;
    }

    /**
     *
     * @param jobId
     * @param fromInd
     * @param itemCount
     * @return
     */
    @Override
    @Transactional
    public PaginatedResultDTO<JobExecution> findJobExecutions(long jobId, long fromInd, int itemCount) {
        List<Long> batchJobIds = jobDao.findAllBatchJobIds(Collections.singletonList(jobId)).get(jobId);

        if (!CollectionUtils.isEmpty(batchJobIds)) {
            JobExecutionFilter filter = new JobExecutionFilter();
            filter.setJobInstanceIds(batchJobIds);
            filter.setFromInd(fromInd);
            filter.setItemCount(itemCount);

            return getJobExplorer().searchJobExecutions(filter);
        }

        JobExecutionPaginatedResultDTO<JobExecution> result = new JobExecutionPaginatedResultDTO<>();
        result.setTotalCount(0);

        return result;
    }

    @Override
    @Transactional
    public PaginatedResultDTO<StepExecution> searchStepExecutions(long jobExecutionId, long fromInd, int itemCount) {
        StepExecutionFilter filter = new StepExecutionFilter();
        filter.setJobExecutionId(jobExecutionId);
        filter.setFromInd(fromInd);
        filter.setItemCount(itemCount);

        return getJobExplorer().searchStepExecutions(filter);
    }

    /**
     * @param jobDto
     * @return
     */
    @Override
    @Transactional
    public JobDTO saveJob(final JobDTO jobDto) {
        assert jobDto != null;

        validateJobParameters(jobDto.getJobNameReference(), jobDto.getParameters(), false);

        final JobDTO saved = jobDto.getId() == null?  insertJob(jobDto) : updateJob(jobDto);

        return findJob(saved.getId());
    }

    private void validateJobParameters(
            final String jobNameReference,
            final Collection<JobParameterDTO> jobParameters,
            final boolean onlyUnknown
    ) {
        final Set<String> templateParameters =
                new HashSet<>(
                        Optional.ofNullable(jobRegistry.getJobTemplateParameters(jobNameReference))
                                .map(JobTemplateParameters::getValueMap)
                                .orElseGet(HashMap::new)
                                .keySet()
                );

        final Set<String> parameters = new HashSet<>(
                Optional.ofNullable(jobParameters).orElseGet(ArrayList::new).stream()
                        .map(JobTemplateParameterDTO::getName)
                        .collect(Collectors.toList())
        );

        final List<String> unknownParameters =
                parameters.stream()
                        .filter(((Predicate<String>) templateParameters::contains).negate())
                        .collect(Collectors.toList());
        if (!unknownParameters.isEmpty()) {
            throw new JobException(
                    "Unknown parameters for job [" + jobNameReference + "]: " + unknownParameters,
                    ExceptionId.EX_JOB_UNKNOWN_PARAMETERS,
                    jobNameReference,
                    unknownParameters
            );
        }

        if (!onlyUnknown) {
            final List<String> notSet = templateParameters.stream()
                    .filter(((Predicate<String>) parameters::contains).negate())
                    .collect(Collectors.toList());
            if (!notSet.isEmpty()) {
                throw new JobException(
                        "Parameters for job [" + jobNameReference + "] not set: " + notSet,
                        ExceptionId.EX_JOB_PARAMETERS_NOT_SET,
                        jobNameReference,
                        notSet
                );
            }
        }
    }

    /**
     * @param jobId
     */
    @Override
    @Transactional
    public void removeJob(long jobId) {
        JobPO job = jobDao.findJob(jobId);

        if (job == null) {
            throw new JobException("Job with id [" + jobId + "] not found", ExceptionId.EX_JOB_NOT_FOUND, jobId);
        }

        stop(job);

        unschedule(jobId);

        jobDao.removeJob(jobId);

        LOGGER.debug("Job was removed: {}", jobId);
    }

    /**
     * @param jobDto
     * @return
     */
    private JobDTO insertJob(final JobDTO jobDto) {
        JobValidator.validateJob(jobDao, jobDto);

        final JobPO jobPo = JobConverter.convertJobDtoToPo(jobDto);

        jobPo.setEnabled(jobDto.isEnabled());
        jobPo.setError(false);
        jobPo.setCreateDate(new Date());
        jobPo.setCreatedBy(SecurityUtils.getCurrentUserName());

        addCreatedCommonParams(jobPo.getParameters());

        String sameJobByParamsName = jobDao.checkJobByParams(jobPo);

        if (sameJobByParamsName != null) {
            throw new JobException("Job [" + sameJobByParamsName + "] with the same parameter set already exists",
                    ExceptionId.EX_JOB_SAME_PARAMETERS,
                    sameJobByParamsName,
                    Collections.singletonList(new Param(JobValidator.VIOLATION_JOB_NAME, sameJobByParamsName)));
        }

        jobDao.insertJob(jobPo);

        if (jobPo.isEnabled() && !jobPo.isError() && isCronJob(jobPo)) {
            schedule(jobPo.getId(), jobDto.getCronExpression());
        }

        return JobConverter.convertJobPoToDto(jobPo);
    }

    /**
     * @param jobDto
     * @return
     */
    private JobDTO updateJob(final JobDTO jobDto) {
        JobValidator.validateJob(jobDao, jobDto);

        final JobPO jobPo = JobConverter.convertJobDtoToPo(jobDto);

        jobPo.setEnabled(jobDto.isEnabled());
        jobPo.setUpdateDate(new Date());
        jobPo.setUpdatedBy(SecurityUtils.getCurrentUserName());

        addUpdatedCommonParams(jobPo.getParameters());

        String sameJobByParamsName = jobDao.checkJobByParams(jobPo);

        if (sameJobByParamsName != null) {
            throw new JobException("Job [" + sameJobByParamsName + "] with the same parameter set already exists",
                    ExceptionId.EX_JOB_SAME_PARAMETERS,
                    sameJobByParamsName,
                    Collections.singletonList(new Param(JobValidator.VIOLATION_JOB_NAME, sameJobByParamsName)));
        }

        unschedule(jobPo.getId());

        jobDao.updateJob(jobPo);

        if (jobDto.isEnabled() && !jobDto.isError() && isCronJob(jobPo)) {
            schedule(jobPo.getId(), jobPo.getCronExpression());
        }

        return JobConverter.convertJobPoToDto(jobPo);
    }

    /**
     * @param jobIds
     * @return
     */
    @Override
    public Map<Long, JobExecution> findLastJobExecutions(List<Long> jobIds) {
        return findLastJobExecutions(jobIds, false);
    }


    private Map<Long, JobExecution> findLastJobExecutions(List<Long> jobIds, boolean loadSteps) {
        Map<Long, JobExecution> result = new HashMap<>();

        Map<Long, Long> jobMap = jobDao.findLastBatchJobIds(jobIds);

        Set<Long> batchJobIds = jobMap.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(batchJobIds)) {
            return result;
        }

        Map<Long, JobExecution> jobLastExecutionMap = getJobExplorer().getLastJobExecutions(batchJobIds, loadSteps);

        for (Entry<Long, Long> entry : jobMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            result.put(entry.getKey(), jobLastExecutionMap.get(entry.getValue()));
        }

        return result;
    }

    /**
     *
     * @param jobIds
     * @return
     */
    @Override
    public Map<Long, Long> findLastJobExecutionIds(List<Long> jobIds) {
        Map<Long, Long> result = new HashMap<>();

        Map<Long, Long> jobMap = jobDao.findLastBatchJobIds(jobIds);

        Set<Long> batchJobIds = jobMap.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(batchJobIds)) {
            return Collections.emptyMap();
        }

        Map<Long, Long> jobLastExecutionIdMap = getJobExplorer().getLastJobExecutionIds(batchJobIds);

        for (Entry<Long, Long> entry : jobMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            result.put(entry.getKey(), jobLastExecutionIdMap.get(entry.getValue()));
        }

        return result;
    }

    @Override
    public double getJobProgress(final long jobId, final long execId) {
        final Long jobInstanceId = jobDao.findLastBatchJobIds(Collections.singletonList(jobId)).get(jobId);

        if (jobInstanceId != null) {
            final JobExecution jobExecution = getJobExplorer().
                    getLastJobExecutions(Collections.singletonList(jobInstanceId), true).get(jobInstanceId);

            if (jobExecution != null) {
                final Collection<StepExecution> steps = jobExecution.getStepExecutions();

                long amount = steps.size();
                long completedAmount = steps.stream()
                        .filter(step -> step.getStatus() == BatchStatus.COMPLETED)
                        .count();

                return 1.0 * completedAmount / amount;
            }
        }

        return 0;
    }

    @Override
    @Transactional
    public List<JobTriggerDTO> findJobTriggers(final Long jobId) {
        final List<JobTriggerPO> jobTriggers = jobDao.findAllJobTriggers(jobId);

        return JobConverter.convertJobTriggersPoToDTo(jobTriggers);
    }

    @Override
    @Transactional
    public JobTriggerDTO saveJobTrigger(final JobTriggerDTO jobTriggerDto) {
        assert jobTriggerDto != null;

        if (jobTriggerDto.getId() == null) {
            return insertJobTrigger(jobTriggerDto);
        } else {
            return updateJobTrigger(jobTriggerDto);
        }
    }

    @Override
    @Transactional
    public void removeTrigger(Long jobId, Long triggerId) {
        Assert.notNull(jobId, "Job id cannot be null");
        Assert.notNull(triggerId, "Trigger id cannot be null");

        final JobTriggerPO jobTrigger = jobDao.findJobTrigger(jobId, triggerId);

        if (jobTrigger == null) {
            throw new JobException("Job trigger with job id [" + jobId + "] and trigger id ["
                    + triggerId + "] not found", ExceptionId.EX_JOB_TRIGGER_NOT_FOUND, triggerId);
        }

        jobDao.removeJobTrigger(jobId, triggerId);

        LOGGER.debug("Job trigger was removed: jobId [{}[, triggerId [{}]", jobId, triggerId);
    }

    @Override
    public boolean isJobRestartable(long jobId) {
        JobPO job = jobDao.findJob(jobId);

        try {
            Job batchJob = jobRegistry.getJob(job.getJobNameReference());

            return batchJob.isRestartable();
        }
        catch (NoSuchJobException e) {
            LOGGER.warn("Failed to get batch job by jobName [jobId=" + jobId +
                    ", jobName=" + job.getJobNameReference() + ']' , e);
        }

        return false;
    }

    @Override
    public JobExecutionDTO jobStatus(long jobId) {
        final Map<Long, JobExecution> lastJobExecutions = findLastJobExecutions(Collections.singletonList(jobId), true);
        if (!lastJobExecutions.containsKey(jobId)) {
            throw new JobException("Job execution not found", ExceptionId.EX_JOB_EXECUTION_NOT_FOUND, jobId);
        }
        return jobExecutionDTOFromJobExecution(
                JobConverter.convertJobPoToDto(jobDao.findJob(jobId)),
                lastJobExecutions.get(jobId)
        );
    }

    /*
    private List<Long> findTriggerSuccessfulJobIds(final long jobId) {
        return jobDao.getTriggerSuccessfulJobIds(jobId);
    }

    private List<Long> findTriggerFailedJobIds(final long jobId) {
        return jobDao.getTriggerFailedJobIds(jobId);
    }
    */

    private void addCreatedCommonParams(final List<JobParameterPO> params) {
        final Date createDate = new Date();
        final String createdBy = SecurityUtils.getCurrentUserName();

        for (final JobParameterPO param : params) {
            param.setCreateDate(createDate);
            param.setCreatedBy(createdBy);
        }
    }

    private void addUpdatedCommonParams(final List<JobParameterPO> params) {
        final Date updateDate = new Date();
        final String updatedBy = SecurityUtils.getCurrentUserName();

        for (final JobParameterPO param : params) {
            if (param.getId() != null) {
                param.setUpdateDate(updateDate);
                param.setUpdatedBy(updatedBy);
            } else {
                param.setCreateDate(updateDate);
                param.setCreatedBy(updatedBy);
            }
        }
    }

    private JobParameters convertJobParameters(List<JobParameterDTO> jobParameters, JobParameterProcessor parameterProcessor) {
        JobParametersBuilder builder = new JobParametersBuilder();

        if (!CollectionUtils.isEmpty(jobParameters)) {
            for (final JobParameterDTO jobParameter : jobParameters) {
                JobParameterProcessor processor = parameterProcessor != null
                        ? parameterProcessor
                        : defaultJobParameterProcessor;

                processor.process(jobParameter, builder);
            }
        }

        return builder.toJobParameters();
    }

    /**
     * Add general parameters as - userName + operationId + timestamp
     *
     * @param parameters - initial parameters
     * @return enriched parameters.
     */
    private JobParameters addGeneralParameters(JobParameters parameters) {

        JobParametersBuilder builder = new JobParametersBuilder(parameters);

        // Add timestamp in job params to avoid issue with completed job.
        builder.addString(JobCommonParameters.PARAM_START_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        // Add internal id - runId unconditionally, since operationId may be supplied from UI
        builder.addString(JobCommonParameters.PARAM_RUN_ID, IdUtils.v4String());

        if (isBlank(parameters.getString(JobCommonParameters.PARAM_USER_NAME))) {
            builder.addString(JobCommonParameters.PARAM_USER_NAME, SecurityUtils.getCurrentUserName());
            builder.addString(JobCommonParameters.PARAM_USER_TOKEN, SecurityUtils.getCurrentUserToken());
        }

        if (isBlank(parameters.getString(JobCommonParameters.PARAM_OPERATION_ID))) {
            builder.addString(JobCommonParameters.PARAM_OPERATION_ID, IdUtils.v4String());
        }

        return builder.toJobParameters();
    }

    /**
     * @param job
     * @return
     */
    private Long stop(JobPO job) {
        long jobId = job.getId();

        Long jobInstanceId = jobDao.findLastBatchJobIds(Collections.singletonList(jobId)).get(jobId);

        if (jobInstanceId == null) {
            LOGGER.debug("No jobInstances found for jobId: " + jobId);
            return null;
        }

        try {
            final Long executionId = findLastRunningJobExecutionId(jobId);

            if (executionId != null) {
                jobOperator.stop(executionId);
            }

            return executionId;
        } catch (final JobExecutionException e) {
            throw new JobException("Failed to stop job: " + jobId, e,
                    ExceptionId.EX_JOB_BATCH_STOP_FAILED, job.getJobNameReference());
        }
    }

    /**
     * Schedule unidata job.
     *
     * @param jobId          Unidata job id.
     * @param cronExpression Cron expression.
     */
    @Override
    public void schedule(final long jobId, final String cronExpression) {
        try {
            JobDetail jobDetail = JobBuilder
                    .newJob(JobLauncherDetail.class)
                    .withIdentity(createJobKey(jobId))
                    .usingJobData("jobId", String.valueOf(jobId))
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(createTriggerKey(jobId))
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            quartzSchedulerFactory.getScheduler().scheduleJob(jobDetail, trigger);

            LOGGER.debug("Schedule job in quartz [jobId={}, triggerKey={}]", jobId, trigger.getKey());
        } catch (SchedulerException e) {
            LOGGER.error("Failed to schedule quartz job: " + jobId, e);
        }
    }

    /**
     * @param jobId
     */
    private void unschedule(long jobId) {
        try {
            TriggerKey key = createTriggerKey(jobId);


            if ((quartzSchedulerFactory.getScheduler() != null) && (quartzSchedulerFactory.getScheduler().checkExists(key))) {
                quartzSchedulerFactory.getScheduler().unscheduleJob(key);

                LOGGER.debug("Unschedule job in quartz [jobId={}, triggerKey={}]", jobId, key);
            }
        } catch (SchedulerException e) {
            LOGGER.error("Failed to unschedule job: " + jobId, e);
        }
    }

    private JobKey createJobKey(long jobId) {
        return JobKey.jobKey("job_" + jobId, QUARTZ_GROUP);
    }

    private TriggerKey createTriggerKey(long jobId) {
        return TriggerKey.triggerKey("trigger_" + jobId, QUARTZ_GROUP);
    }

    /**
     * @param jobId
     * @return
     */
    private Long findLastRunningJobExecutionId(final long jobId) {
        final Long jobInstanceId = jobDao.findLastBatchJobIds(Collections.singletonList(jobId)).get(jobId);

        if (jobInstanceId != null) {
            JobExecution jobExecution = getJobExplorer()
                    .getLastJobExecutions(Collections.singletonList(jobInstanceId), false)
                    .get(jobInstanceId);

            if (jobExecution != null &&
                    (jobExecution.getStatus() == BatchStatus.STARTED || jobExecution.getStatus() == BatchStatus.STARTING)) {
                return jobExecution.getId();
            }
        }

        return null;
    }

    private void addLastSuccessJobExecutionDate(final long jobId, final List<JobParameterDTO> jobParameters ) {

        List<Long> jobInstanceIdIds = jobDao.findAllBatchJobIds(Collections.singletonList(jobId)).get(jobId);

        Date previousStartDate = null;
        if (CollectionUtils.isNotEmpty(jobInstanceIdIds)) {
            previousStartDate = getJobExplorer()
                    .getLastSuccessJobExecutionsDate(jobInstanceIdIds);
        }

        if(previousStartDate != null){
            jobParameters.add(new JobParameterDTO(JobCommonParameters.PARAM_PREVIOUS_SUCCESS_START_DATE,
                                ZonedDateTime.ofInstant(previousStartDate.toInstant(), ZoneId.of("UTC"))));
        }
    }

    private JobTriggerDTO insertJobTrigger(final JobTriggerDTO jobTriggerDto) {
        JobValidator.validateJobTrigger(jobDao, jobTriggerDto);

        final JobTriggerPO jobPo = JobConverter.convertJobTriggerDtoToPo(jobTriggerDto);

        jobPo.setCreateDate(new Date());
        jobPo.setCreatedBy(SecurityUtils.getCurrentUserName());

        jobDao.insertJobTrigger(jobPo);

        return JobConverter.convertJobTriggerPoToDto(jobPo);
    }

    private JobTriggerDTO updateJobTrigger(final JobTriggerDTO jobTriggerDto) {
        JobValidator.validateJobTrigger(jobDao, jobTriggerDto);

        final JobTriggerPO jobTriggerPO = JobConverter.convertJobTriggerDtoToPo(jobTriggerDto);

        jobTriggerPO.setUpdateDate(new Date());
        jobTriggerPO.setUpdatedBy(SecurityUtils.getCurrentUserName());

        jobDao.updateJobTrigger(jobTriggerPO);

        return JobConverter.convertJobTriggerPoToDto(jobTriggerPO);
    }

    /**
     * Make casting to CustomJobExplorer because CustomJobExplorerFactoryBean can't return CustomJobExplorer to avoid
     * issues with Autowiring in another classes.
     *
     * @return
     */
    private CustomJobExplorer getJobExplorer() {
        return (CustomJobExplorer) jobExplorer;
    }

    /**
     * @param complexParameter - any object which you want to pass to job
     * @return key in storage which help find complex parameter in partitioner
     */
    @Override
    public String putComplexParameter(Object complexParameter) {
        String storageKey = IdUtils.v4String();
        complexJobParameterHolder.putComplexParameter(storageKey, complexParameter);
        return storageKey;
    }


    @Override
    @Transactional
    public Collection<JobDTO> findAll() {
        return findAllJobs().stream().map(JobConverter::convertJobPoToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobDTO findJob(long jobId) {
        final JobPO jobWithParameters = jobDao.findJobWithParameters(jobId);
        return jobWithParameters != null ? JobConverter.convertJobPoToDto(jobWithParameters) : null;
    }

    @Override
    public Long stopJob(long jobId) {
        return stop(jobId);
    }
}
