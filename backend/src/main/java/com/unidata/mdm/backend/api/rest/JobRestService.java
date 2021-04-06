package com.unidata.mdm.backend.api.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.converter.JobConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.job.JobExecutionRO;
import com.unidata.mdm.backend.api.rest.dto.job.JobMetaInfoRO;
import com.unidata.mdm.backend.api.rest.dto.job.JobRO;
import com.unidata.mdm.backend.api.rest.dto.job.JobStepExecutionRO;
import com.unidata.mdm.backend.api.rest.dto.job.JobTriggerRO;
import com.unidata.mdm.backend.api.rest.dto.job.PaginatedJobExecutionsResultRO;
import com.unidata.mdm.backend.api.rest.dto.job.PaginatedJobStepExecutionsResultRO;
import com.unidata.mdm.backend.api.rest.dto.job.PaginatedJobsResultRO;
import com.unidata.mdm.backend.common.dto.PaginatedResultDTO;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.StepExecutionPaginatedResultDTO;
import com.unidata.mdm.backend.dto.job.JobTriggerDTO;
import com.unidata.mdm.backend.service.job.JobServiceExt;
import com.unidata.mdm.backend.service.job.registry.JobTemplateParameters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Rest service for all job related activities.
 */
@Path("/jobs")
@Api(value = "job", description = "Задачи", produces = "application/json")
@Consumes({ "application/json" })
public class JobRestService extends AbstractRestService {


    /** The Constant ADMIN_SYSTEM_MANAGEMENT. */
    private static final String ADMIN_SYSTEM_MANAGEMENT = "ADMIN_SYSTEM_MANAGEMENT";

    /** The job service. */
    @Autowired
    private JobServiceExt jobServiceExt;

    /**
     * Find all job meta names.
     *
     * @return the response
     * @throws Exception the exception
     */
    @GET
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Получить список внешних идентификаторов задач", notes = "", response = JobMetaInfoRO.class, responseContainer = "List")
    @Path(value = "/jobmetanames")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response findAllJobMetaNames() throws Exception {
        List<JobMetaInfoRO> result = new ArrayList<>();

        Collection<String> jobNames = jobServiceExt.findAllUIJobReferences();

        if (!CollectionUtils.isEmpty(jobNames)) {
            for (String jobName : jobNames) {
                JobMetaInfoRO jobMeta = new JobMetaInfoRO();
                jobMeta.setJobNameReference(jobName);

                JobTemplateParameters parameters = jobServiceExt.findJobTemplateParameters(jobName);

                if (parameters != null) {
                    jobMeta.setParameters(JobConverter.convertJobTemplateParametersToRo(parameters));
                }

                result.add(jobMeta);
            }
        }

        return ok(result);
    }

    /**
     * Find all.
     *
     * @return the response
     * @throws Exception the exception
     */
    @GET
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Получить список задач", notes = "", response = JobRO.class, responseContainer = "List")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response findAll() throws Exception {
        List<JobDTO> allJobs = jobServiceExt.findAllJobsWithParams();

        final Map<Long, JobExecution> jobExecutionMap = jobServiceExt
                .findLastJobExecutions(allJobs.stream().map(JobDTO::getId).collect(Collectors.toList()));

        final List<JobRO> jobs = JobConverter.convertJobsDtoToRo(allJobs);

        jobs.stream().forEach(job -> {
            JobExecution jobExecution = jobExecutionMap.get(job.getId());

            if (jobExecution != null) {
                job.setLastExecution(JobConverter.convertJobExecutionPoToRo(jobExecution, job.getId()));
            }
        });

        PaginatedJobsResultRO resultRO = new PaginatedJobsResultRO();

        resultRO.setContent(jobs);
        resultRO.setTotalCount(jobs.size());

        return ok(resultRO);
    }

    /**
     * Find jobs page.
     *
     * @param fromInd the from ind
     * @param itemCount the item count
     * @param status the status
     * @return the response
     * @throws Exception the exception
     */
    @GET
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Получить ограниченный список задач", notes = "", response = JobRO.class, responseContainer = "List")
    @Path(value = "/{fromInd}/{itemCount}/{status}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response findJobsPage(@PathParam("fromInd")
    final long fromInd, @PathParam("itemCount")
    final int itemCount, @PathParam("status")
    final String status) throws Exception {
        Boolean enabled = null;
        if ("active".equalsIgnoreCase(status)) {
            enabled = true;
        } else if ("inactive".equalsIgnoreCase(status)) {
            enabled = false;
        }

        PaginatedResultDTO<JobDTO> paginatedResult = jobServiceExt.searchJobs(fromInd, itemCount, enabled);

        PaginatedJobsResultRO resultRO = new PaginatedJobsResultRO();
        resultRO.setTotalCount(paginatedResult.getTotalCount());

        if (!CollectionUtils.isEmpty(paginatedResult.getPage())) {
            List<Long> jobIds = paginatedResult.getPage().stream().map(JobDTO::getId).collect(Collectors.toList());

            final Map<Long, JobExecution> jobExecutionMap = jobServiceExt.findLastJobExecutions(jobIds);

            final List<JobRO> jobs = JobConverter.convertJobsDtoToRo(paginatedResult.getPage());

            jobs.stream().forEach(job -> {
                JobExecution jobExecution = jobExecutionMap.get(job.getId());

                if (jobExecution != null) {
                    job.setLastExecution(JobConverter.convertJobExecutionPoToRo(jobExecution, job.getId()));
                }
            });

            resultRO.setContent(jobs);
        }

        return ok(resultRO);
    }

    /**
     * Find jobs page.
     *
     * @param fromInd the from ind
     * @param itemCount the item count
     * @return the response
     * @throws Exception the exception
     */
    @GET
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Получить ограниченный список задач", notes = "", response = JobRO.class, responseContainer = "List")
    @Path(value = "/{fromInd}/{itemCount}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    @Deprecated
    public Response findJobsPage(@PathParam("fromInd")
    final long fromInd, @PathParam("itemCount")
    final int itemCount) throws Exception {
        Boolean enabled = null;

        PaginatedResultDTO<JobDTO> paginatedResult = jobServiceExt.searchJobs(fromInd, itemCount, enabled);

        PaginatedJobsResultRO resultRO = new PaginatedJobsResultRO();
        resultRO.setTotalCount(paginatedResult.getTotalCount());

        if (!CollectionUtils.isEmpty(paginatedResult.getPage())) {
            List<Long> jobIds = paginatedResult.getPage().stream().map(JobDTO::getId).collect(Collectors.toList());

            final Map<Long, JobExecution> jobExecutionMap = jobServiceExt.findLastJobExecutions(jobIds);

            final List<JobRO> jobs = JobConverter.convertJobsDtoToRo(paginatedResult.getPage());

            jobs.stream().forEach(job -> {
                JobExecution jobExecution = jobExecutionMap.get(job.getId());

                if (jobExecution != null) {
                    job.setLastExecution(JobConverter.convertJobExecutionPoToRo(jobExecution, job.getId()));
                }
            });

            resultRO.setContent(jobs);
        }

        return ok(resultRO);
    }

    /**
     * Creates the job.
     *
     * @param job the job
     * @return the response
     * @throws Exception the exception
     */
    @PUT
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isCreate()")
    @ApiOperation(value = "Сохранить задачу", notes = "", response = JobRO.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response createJob(JobRO job) throws Exception {
        final JobRO resultJob = JobConverter.convertJobDtoToRo(jobServiceExt.saveJob(JobConverter.convertJobRoToDto(job)));

        return ok(resultJob);
    }

    /**
     * Save job.
     *
     * @param jobId the job id
     * @param job the job
     * @return the response
     * @throws Exception the exception
     */
    @PUT
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isUpdate()")
    @ApiOperation(value = "Редактировать задачу", notes = "", response = JobRO.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    @Path(value = "/{jobId}")
    public Response saveJob(@PathParam("jobId")
    final Long jobId, JobRO job) throws Exception {
        Assert.notNull(jobId);

        if (job.getId() != null) {
            Assert.isTrue(jobId.equals(job.getId()));
        } else {
            job.setId(jobId);
        }

        final JobRO newJob = JobConverter.convertJobDtoToRo(jobServiceExt.saveJob(JobConverter.convertJobRoToDto(job)));

        return ok(newJob);
    }

    /**
     * Find job executions.
     *
     * @param jobId the job id
     * @return the response
     * @throws Exception the exception
     */
    @GET
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Получить список запусков задачи", notes = "", response = JobExecutionRO.class, responseContainer = "List")
    @Path(value = "/executions/{jobId}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    @Deprecated
    public Response findJobExecutions(@PathParam("jobId")
    final Long jobId) throws Exception {
        List<JobExecution> executions = jobServiceExt.findAllJobExecutions(jobId);

        List<JobExecutionRO> executionsRO = JobConverter.convertJobExecutionPoToRo(executions, jobId);

        // Set restartable only if last execution not COMPLETED if it allowed in
        // spring batch for restarting.
        if (!CollectionUtils.isEmpty(executions)) {
            // Set not restartable for all elements in collection.
            executionsRO.stream().forEach(e -> e.setRestartable(false));

            JobExecution jobExecution = executions.get(0);

            if (jobExecution.getExitStatus() != null && (jobExecution.getStatus() == BatchStatus.STOPPED
                    || jobExecution.getStatus() == BatchStatus.FAILED)) {

                boolean restartable = jobServiceExt.isJobRestartable(jobId);

                executionsRO.get(0).setRestartable(restartable);
            }
        }

        return ok(executionsRO);
    }

    /**
     * Find job executions page.
     *
     * @param jobId the job id
     * @param fromInd the from ind
     * @param itemCount the item count
     * @return the response
     * @throws Exception the exception
     */
    @GET
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Получить ограниченный список запусков задачи", notes = "", response = PaginatedJobExecutionsResultRO.class, responseContainer = "List")
    @Path(value = "/executions/{jobId}/{fromInd}/{itemCount}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response findJobExecutionsPage(@PathParam("jobId")
    final Long jobId, @PathParam("fromInd")
    final long fromInd, @PathParam("itemCount")
    final int itemCount) throws Exception {

        PaginatedResultDTO<JobExecution> paginatedResult = jobServiceExt.findJobExecutions(jobId, fromInd, itemCount);
        PaginatedJobExecutionsResultRO resultRO = new PaginatedJobExecutionsResultRO();
        resultRO.setTotalCount(paginatedResult.getTotalCount());

        List<JobExecutionRO> executionsRO = JobConverter.convertJobExecutionPoToRo(paginatedResult.getPage(), jobId);
        resultRO.setContent(executionsRO);

        // Set restartable only if last execution not COMPLETED if it allowed in
        // spring batch for restarting.
        if (!CollectionUtils.isEmpty(paginatedResult.getPage())) {
            // Set not restartable for all elements in collection.
            executionsRO.stream().forEach(e -> e.setRestartable(false));

            Long lastJobExecutionId = jobServiceExt.findLastJobExecutionIds(Collections.singletonList(jobId)).get(jobId);

            // Set restartable flag only for last execution if allowed by
            // conditions.
            if (lastJobExecutionId != null) {
                paginatedResult.getPage().stream().filter(je -> lastJobExecutionId.equals(je.getId())).findFirst()
                        .ifPresent(jobExecution -> {
                            if (jobExecution.getStatus() == BatchStatus.STOPPED
                                    || jobExecution.getStatus() == BatchStatus.FAILED) {
                                boolean restartable = jobServiceExt.isJobRestartable(jobId);

                                executionsRO.stream()
                                        .filter(jobExecutionRO -> jobExecution.getId().equals(jobExecutionRO.getId()))
                                        .findFirst()
                                        .ifPresent(jobExecutionRO -> jobExecutionRO.setRestartable(restartable));
                            }
                        });

            }
        }

        return ok(resultRO);
    }

    /**
     * Find step executions page.
     *
     * @param jobExecutionId the job execution id
     * @param fromInd the from ind
     * @param itemCount the item count
     * @return the response
     * @throws Exception the exception
     */
    @GET
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Получить ограниченный список шагов для указанного запуска задачи", notes = "", response = PaginatedJobStepExecutionsResultRO.class, responseContainer = "List")
    @Path(value = "/stepexecutions/{jobExecutionId}/{fromInd}/{itemCount}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response findStepExecutionsPage(@PathParam("jobExecutionId")
    final Long jobExecutionId, @PathParam("fromInd")
    final long fromInd, @PathParam("itemCount")
    final int itemCount) throws Exception {

        StepExecutionPaginatedResultDTO<StepExecution> paginatedResult
            = (StepExecutionPaginatedResultDTO<StepExecution>) jobServiceExt.searchStepExecutions(jobExecutionId, fromInd,
                itemCount);

        PaginatedJobStepExecutionsResultRO resultRO = new PaginatedJobStepExecutionsResultRO();
        resultRO.setTotalCount(paginatedResult.getTotalCount());
        resultRO.setCompletedCount(paginatedResult.getFinishedCount());

        List<JobStepExecutionRO> executionsRO = JobConverter.convertStepExecutionPoToRo(paginatedResult.getPage());
        resultRO.setContent(executionsRO);

        return ok(resultRO);
    }

    /**
     * Gets the job execution percentage.
     *
     * @param jobId the job id
     * @param execId the exec id
     * @return the job execution percentage
     * @throws Exception the exception
     */
    @GET
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Получить относительную готовность выполнения задачи", notes = "Количество шагов относительно количества выполненных шагов", response = JobExecutionRO.class, responseContainer = "List")
    @Path(value = "/executions/{jobId}/{execId}/progress")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getJobExecutionPercentage(@PathParam("jobId")
    final Long jobId, @PathParam("execId")
    final Long execId) throws Exception {
        final double progress = jobServiceExt.getJobProgress(jobId, execId);

        return ok(progress);
    }

    /**
     * Start job.
     *
     * @param jobId the job id
     * @return the response
     * @throws Exception the exception
     */
    @POST
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Запуск задачи", notes = "", response = Long.class)
    @Path(value = "/start/{jobId}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response startJob(@PathParam("jobId")
    final Long jobId) throws Exception {
        JobExecution execution = jobServiceExt.start(jobId);

        return ok(execution.getId());
    }

    /**
     * Stop job.
     *
     * @param jobId the job id
     * @return the response
     * @throws Exception the exception
     */
    @POST
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Остановка задачи", notes = "", response = Boolean.class)
    @Path(value = "/stop/{jobId}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response stopJob(@PathParam("jobId")
    final Long jobId) throws Exception {
        jobServiceExt.stop(jobId);

        return ok(true);
    }

    /**
     * Restart job.
     *
     * @param jobExecutionId the job execution id
     * @return the response
     * @throws Exception the exception
     */
    @POST
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Перезапуск задачи", notes = "", response = Long.class)
    @Path(value = "/restart/{jobExecutionId}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response restartJob(@PathParam("jobExecutionId")
    final Long jobExecutionId) throws Exception {
        Long newJobExecutionId = jobServiceExt.restart(jobExecutionId);

        return ok(newJobExecutionId);
    }

    /**
     * Mark job.
     *
     * @param jobId the job id
     * @param enabled the enabled
     * @return the response
     * @throws Exception the exception
     */
    @POST
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Активация/деактивация задачи", notes = "", response = Boolean.class)
    @Path(value = "/mark/{jobId}/{enabled}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response markJob(@PathParam("jobId")
    final Long jobId, @PathParam("enabled")
    final boolean enabled) throws Exception {
        if (enabled) {
            jobServiceExt.enableJob(jobId);
        } else {
            jobServiceExt.disableJob(jobId);
        }

        return ok(true);
    }

    /**
     * Removes the job.
     *
     * @param jobId the job id
     * @return the response
     * @throws Exception the exception
     */
    @DELETE
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isDelete()")
    @ApiOperation(value = "Удалить задачу", notes = "", response = Boolean.class)
    @Path(value = "/{jobId}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response removeJob(@PathParam("jobId")
    final Long jobId) throws Exception {
        jobServiceExt.removeJob(jobId);

        return ok(true);
    }

    /**
     * Find job triggers.
     *
     * @param jobId the job id
     * @return the response
     * @throws Exception the exception
     */
    @GET
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isRead()")
    @ApiOperation(value = "Получить список триггеров", response = JobTriggerRO.class, responseContainer = "List")
    @Path(value = "/{jobId}/triggers")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response findJobTriggers(@PathParam("jobId")
    final Long jobId) throws Exception {
        Assert.notNull(jobId);

        final List<JobTriggerDTO> jobTriggers = jobServiceExt.findJobTriggers(jobId);

        final List<JobTriggerRO> jobTriggerROs = JobConverter.convertJobTriggersDtoToRo(jobTriggers);

        return ok(jobTriggerROs);
    }

    /**
     * Creates the job trigger.
     *
     * @param jobId the job id
     * @param jobTrigger the job trigger
     * @return the response
     * @throws Exception the exception
     */
    @PUT
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isCreate()")
    @ApiOperation(value = "Сохранить новый триггер", response = JobTriggerRO.class)
    @Path(value = "/{jobId}/triggers/")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response createJobTrigger(@PathParam("jobId")
    final Long jobId, final JobTriggerRO jobTrigger) throws Exception {
        Assert.notNull(jobId);

        final JobTriggerDTO triggerDto = JobConverter.convertJobTriggerRoToDto(jobTrigger);
        triggerDto.setFinishJobId(jobId);

        final JobTriggerRO resultTrigger = JobConverter.convertJobTriggerDtoToRo(jobServiceExt.saveJobTrigger(triggerDto));

        return ok(resultTrigger);
    }

    /**
     * Save job trigger.
     *
     * @param jobId the job id
     * @param triggerId the trigger id
     * @param jobTrigger the job trigger
     * @return the response
     * @throws Exception the exception
     */
    @PUT
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isUpdate()")
    @ApiOperation(value = "Редактировать триггер", response = JobTriggerRO.class)
    @Path(value = "/{jobId}/triggers/{triggerId}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response saveJobTrigger(@PathParam("jobId")
    final Long jobId, @PathParam("triggerId")
    final Long triggerId, final JobTriggerRO jobTrigger) throws Exception {
        Assert.notNull(jobId);
        Assert.notNull(triggerId);

        final JobTriggerDTO triggerDto = JobConverter.convertJobTriggerRoToDto(jobTrigger);
        triggerDto.setId(triggerId);
        triggerDto.setFinishJobId(jobId);

        final JobTriggerRO newJobTrigger = JobConverter.convertJobTriggerDtoToRo(jobServiceExt.saveJobTrigger(triggerDto));

        return ok(newJobTrigger);
    }

    /**
     * Removes the trigger.
     *
     * @param jobId the job id
     * @param triggerId the trigger id
     * @return the response
     * @throws Exception the exception
     */
    @DELETE
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()"
            + " or"
            + " T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + ADMIN_SYSTEM_MANAGEMENT + "').isDelete()")
    @ApiOperation(value = "Удалить триггер", notes = "", response = Boolean.class)
    @Path(value = "/{jobId}/triggers/{triggerId}")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response removeTrigger(@PathParam("jobId")
    final Long jobId, @PathParam("triggerId")
    final Long triggerId) throws Exception {
        jobServiceExt.removeTrigger(jobId, triggerId);

        return ok(true);
    }

}