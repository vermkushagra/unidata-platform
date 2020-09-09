/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.service.job;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.unidata.mdm.backend.service.job.batch.core.CustomJobParameter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.unidata.mdm.backend.api.rest.converter.JobConverter;
import com.unidata.mdm.backend.api.rest.dto.Param;
import com.unidata.mdm.backend.api.rest.dto.job.JobRO;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext;
import com.unidata.mdm.backend.common.dto.PaginatedResultDTO;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobExecutionDTO;
import com.unidata.mdm.backend.common.dto.job.JobExecutionExitStatusDTO;
import com.unidata.mdm.backend.common.dto.job.JobExecutionPaginatedResultDTO;
import com.unidata.mdm.backend.common.dto.job.JobExecutionStepDTO;
import com.unidata.mdm.backend.common.dto.job.JobPaginatedResultDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.common.dto.job.JobTemplateParameterDTO;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.common.job.JobExecutionBatchStatus;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
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
import com.unidata.mdm.backend.common.service.UserService;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.JsonUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.constants.UserMessageConstants;

import reactor.core.publisher.Flux;

/**
 * Job service to manipulate all jobs deployed in system.
 *
 * @author Alexander Magdenko
 */
@Service
public class JobServiceImpl implements JobServiceExt, ApplicationContextAware, ConfigurationUpdatesConsumer {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    private static final String QUARTZ_GROUP = "quartz-batch";
    private static final String ERROR_WHILE_EXPORTING_JOBS_LOG_MESSAGE = "Error while exporting jobs.";
    private static final String ERROR_WHILE_IMPORTING_JOBS_LOG_MESSAGE = "Error while importing jobs.";
    private static final TypeReference<List<JobRO>> JOB_RO_LIST_TYPE_REFERENCE = new TypeReference<List<JobRO>>(){};

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
        Objects.requireNonNull(jobName, "Job name must be not null");

        final Job job = (Job) applicationContext.getBean(jobName);

        JobExecution execution;
        try {
            final JobParameters springJobParameters = convertJobParameters(new ArrayList<>(jobParameters),
                    parameterProcessors.get(job.getName()));

            execution = jobLauncher.run(job, springJobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobParametersInvalidException
                | JobInstanceAlreadyCompleteException e) {
            throw new JobException("Failed to run job:" + jobName, e, ExceptionId.EX_JOB_BATCH_EXECUTION_FAILED,
                    jobName);
        }

        LOGGER.debug("Run job [jobName={}, jobStatus={}]", jobName, execution.getStatus());

        return execution;
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

    // TODO: fix that convertion from Spring to DTO
    private JobParameterDTO springJobParameterToJobParameterDTO(String name, JobParameter jobParameter) {
        switch (jobParameter.getType()) {
            case DATE:
                return new JobParameterDTO(
                        name,
                        ZonedDateTime.ofInstant(((Date) jobParameter.getValue()).toInstant(), ZoneId.systemDefault())
                );
            case DOUBLE: {
                if (jobParameter instanceof CustomJobParameter) {
                    return new JobParameterDTO(name, (Double[]) jobParameter.getValue());
                }
                return new JobParameterDTO(name, (Double) jobParameter.getValue());
            }
            case LONG: {
                if (jobParameter instanceof CustomJobParameter) {
                    return new JobParameterDTO(name, (Long[]) jobParameter.getValue());
                }
                return new JobParameterDTO(name, (Long) jobParameter.getValue());
            }
            case STRING: {
                // TODO: resolve problem with convertion Spring STRING type to Unidata BOOLEAN type.
                if (jobParameter instanceof CustomJobParameter) {
                    return new JobParameterDTO(name, (String[]) jobParameter.getValue());
                }
                return new JobParameterDTO(name, (String) jobParameter.getValue());
            }
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

        JobTemplateParameters parameters = jobRegistry.getJobTemplateParameters(jobName);
        JobParameterProcessor processor = parameterProcessors.get(jobName);
        if (Objects.nonNull(processor)) {
            parameters = processor.filter(parameters);
        }

        return parameters;
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

        allExecutions.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));

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
        JobTemplateParameters jobTemplateParameters = findJobTemplateParameters(jobNameReference);

        if (jobTemplateParameters == null) {
            throw new JobException(
                    "Job with name reference [" + jobNameReference + "] not found!",
                    ExceptionId.EX_JOB_NOT_FOUND,
                    jobNameReference
            );
        }

        if (MapUtils.isNotEmpty(jobTemplateParameters.getValidators())) {
            List<String> validateErrors = jobParameters.stream()
                    .map(param -> validateParameter(jobTemplateParameters.getValidators().get(param.getName()),
                            jobParameters, param.getName()))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(validateErrors)) {
                throw new JobException(
                        "Wrong parameters for job [" + jobNameReference + "]",
                        ExceptionId.EX_JOB_PARAMETERS_VALIDATION_ERRORS,
                        validateErrors
                );
            }
        }

        final Set<String> templateParamNames =
                new HashSet<>(
                        Optional.ofNullable(findJobTemplateParameters(jobNameReference))
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
                        .filter(((Predicate<String>) templateParamNames::contains).negate())
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
            final List<String> notSet = templateParamNames.stream()
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

    private List<String> validateParameter(JobParameterValidator validator, Collection<JobParameterDTO> jobParameters, String paramName) {
        return validator == null ? Collections.emptyList() : validator.validate(paramName, jobParameters);
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

    private final ThreadPoolExecutor exportJobsExecutor = new ThreadPoolExecutor(
            (Integer) UnidataConfigurationProperty.JOBS_IMPORT_THREADS_POOL_SIZE.getDefaultValue().get(),
            (Integer) UnidataConfigurationProperty.JOBS_IMPORT_THREADS_POOL_SIZE.getDefaultValue().get(),
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new CustomizableThreadFactory("exportJobs-worker-")
    );

    private final ThreadPoolExecutor importJobsExecutor = new ThreadPoolExecutor(
            (Integer) UnidataConfigurationProperty.JOBS_IMPORT_THREADS_POOL_SIZE.getDefaultValue().get(),
            (Integer) UnidataConfigurationProperty.JOBS_IMPORT_THREADS_POOL_SIZE.getDefaultValue().get(),
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new CustomizableThreadFactory("importJobs-worker-")
    );

    @Autowired
    private UserService userService;

    @Autowired
    private DataRecordsService dataRecordsService;

    @Override
    public void exportJobs(final String userLogin, final List<Long> jobsIds) {
        CompletableFuture.supplyAsync(() -> jobDao.findJobWithParameters(jobsIds), exportJobsExecutor)
                .thenApply(JobConverter::convertJobsPoToDto)
                .thenApply(JobConverter::convertJobsDtoToRo)
                .thenApply(this::toJSON)
                .whenComplete((json, ex) -> {
                    final UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder jobExportUserEvent =
                            new UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder()
                                    .login(userLogin)
                                    .type("JOB_EXPORT");
                    if (ex != null) {
                        handleExportImportError(
                                ERROR_WHILE_EXPORTING_JOBS_LOG_MESSAGE,
                                UserMessageConstants.JOBS_EXPORT_FAIL,
                                ex,
                                jobExportUserEvent
                        );
                        return;
                    }
                    sendExportDataToUser(json, jobExportUserEvent);
                });
    }

    private void sendExportDataToUser(String json, UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder jobExportUserEvent) {
        try (final InputStream is = new ByteArrayInputStream(json.getBytes())) {
            final UpsertUserEventRequestContext upsertUserEventRequestContext =
                    jobExportUserEvent
                            .content(MessageUtils.getMessage(UserMessageConstants.JOBS_EXPORT_SUCCESS))
                            .build();
            final UserEventDTO userEventDTO = userService.upsert(upsertUserEventRequestContext);
            final SaveLargeObjectRequestContext saveLargeObjectRequestContext =
                    new SaveLargeObjectRequestContext.SaveLargeObjectRequestContextBuilder()
                            .eventKey(userEventDTO.getId())
                            .mimeType("application/json")
                            .binary(false)
                            .inputStream(is)
                            .filename(fileName())
                            .build();
            dataRecordsService.saveLargeObject(saveLargeObjectRequestContext);
        } catch (IOException e) {
            handleExportImportError(
                    ERROR_WHILE_EXPORTING_JOBS_LOG_MESSAGE,
                    UserMessageConstants.JOBS_EXPORT_FAIL,
                    e,
                    jobExportUserEvent
            );
        }
    }

    private void handleExportImportError(
            final String logMessage,
            final String userMessage,
            final Throwable ex,
            final UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder jobExportUserEvent
    ) {
        LOGGER.error(logMessage, ex);
        final UpsertUserEventRequestContext upsertUserEventRequestContext =
                jobExportUserEvent
                        .content(MessageUtils.getMessage(userMessage))
                        .build();
        userService.upsert(upsertUserEventRequestContext);
    }

    private String fileName() {
        try {
            return URLEncoder.encode(
                    "jobs_"
                            + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_HH-mm-ss")
                            + ".json",
                    "UTF-8"
            );
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error generating jobs file name", e);
        }
        return "jobs.json";
    }

    private String toJSON(final Object object) {
        return JsonUtils.write(object);
    }

    @Override
    public void importJobs(final String userLogin, final Path path) {
        CompletableFuture.supplyAsync(() -> fromJSONToJobsList(path), importJobsExecutor)
                .thenApply(JobConverter::convertJobsRoToDto)
                .thenApply(JobConverter::convertJobsDtoToPo)
                .whenComplete((jobs, ex) -> {
                    final UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder jobImportUserEvent =
                            new UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder()
                                    .login(userLogin)
                                    .type("JOB_IMPORT");
                    if (ex != null) {
                        handleExportImportError(
                                ERROR_WHILE_IMPORTING_JOBS_LOG_MESSAGE,
                                UserMessageConstants.JOBS_IMPORT_FAIL,
                                ex,
                                jobImportUserEvent
                        );
                    }
                    try {
                        final Date currentDate = new Date();
                        jobs.forEach(job -> {
                            job.setCreateDate(currentDate);
                            job.setCreatedBy(userLogin);
                            job.getParameters().forEach(jobParameter -> {
                                jobParameter.setCreateDate(currentDate);
                                jobParameter.setCreatedBy(userLogin);
                            });
                        });
                        jobDao.saveJobs(jobs);
                    }
                    catch (Exception e) {
                        handleExportImportError(
                                ERROR_WHILE_IMPORTING_JOBS_LOG_MESSAGE,
                                UserMessageConstants.JOBS_IMPORT_FAIL,
                                e,
                                jobImportUserEvent
                        );
                        return;
                    }
                    final UpsertUserEventRequestContext upsertUserEventRequestContext =
                            jobImportUserEvent
                                    .content(MessageUtils.getMessage(UserMessageConstants.JOBS_IMPORT_SUCCESS))
                                    .build();
                    userService.upsert(upsertUserEventRequestContext);
                });
    }

    private List<JobRO> fromJSONToJobsList(final Path path) {
        return JsonUtils.read(path.toFile(), JOB_RO_LIST_TYPE_REFERENCE);
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final String jobsExportThreadsPoolSizeKey = UnidataConfigurationProperty.JOBS_EXPORT_THREADS_POOL_SIZE.getKey();
        final String jobsImportThreadsPoolSizeKey = UnidataConfigurationProperty.JOBS_IMPORT_THREADS_POOL_SIZE.getKey();
        updates
                .filter(values ->
                        values.containsKey(jobsExportThreadsPoolSizeKey) && values.get(jobsExportThreadsPoolSizeKey).isPresent()
                )
                .map(values -> (Integer) values.get(jobsExportThreadsPoolSizeKey).get())
                .subscribe(value -> {
                    exportJobsExecutor.setCorePoolSize(value);
                    exportJobsExecutor.setMaximumPoolSize(value);
                });

        updates
                .filter(values ->
                        values.containsKey(jobsImportThreadsPoolSizeKey) && values.get(jobsImportThreadsPoolSizeKey).isPresent()
                )
                .map(values -> (Integer) values.get(jobsImportThreadsPoolSizeKey).get())
                .subscribe(value -> {
                    importJobsExecutor.setCorePoolSize(value);
                    importJobsExecutor.setMaximumPoolSize(value);
                });
    }

    @PreDestroy
    public void preDestroy() {
        exportJobsExecutor.shutdown();
        importJobsExecutor.shutdown();
    }
}
