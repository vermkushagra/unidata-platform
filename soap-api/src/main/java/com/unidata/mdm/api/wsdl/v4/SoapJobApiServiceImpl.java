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

package com.unidata.mdm.api.wsdl.v4;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.xml.datatype.XMLGregorianCalendar;

import com.unidata.mdm.api.v4.*;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobExecutionDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.service.JobService;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import static java.util.stream.Collectors.toList;

public class SoapJobApiServiceImpl implements SoapJobApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoapJobApiServiceImpl.class);

    private static final Locale RU_LOCALE = Locale.forLanguageTag("ru");

    /**
     * Job service.
     */
    private JobService jobService;

    /**
     * Message source.
     */
    private MessageSource messageSource;

    @Autowired
    public void setJobService(JobService jobService) {
        this.jobService = jobService;
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void handleFindAllJobs(final UnidataRequestBody request, final UnidataResponseBody response) {
        handleRequest(request, response, () ->
                response.setResponseFindAllJobs(
                        new ResponseFindAllJobs()
                                .withJobs(jobService.findAll().stream().map(this::convertJobDTOToJob).collect(toList()))
                )
        );
    }

    @Override
    public void handleSaveJob(final UnidataRequestBody request, final UnidataResponseBody response) {
        handleRequest(request, response, () -> {
            final Job job = request.getRequestSaveJob().getJob();
            validateParameters(job.getParameters());
            final JobDTO jobDTO = jobService.saveJob(convertJobToJobDTO(job));
            response.setResponseSaveJob(new ResponseSaveJob().withJob(convertJobDTOToJob(jobDTO)));
        });
    }

    @Override
    public void handleRemoveJob(final UnidataRequestBody request, final UnidataResponseBody response) {
        handleRequest(request, response, () -> {
            final Long jobId = request.getRequestRemoveJob().getId();
            jobService.removeJob(jobId);
            response.setResponseRemoveJob(new ResponseRemoveJob());
        });
    }

    @Override
    public void handleFindJob(final UnidataRequestBody request, final UnidataResponseBody response) {
        handleRequest(request, response, () -> {
            final Long jobId = request.getRequestFindJob().getId();
            final JobDTO job = jobService.findJob(jobId);
            if (job == null) {
                throw new JobException("Job with id [" + jobId + "] not found", ExceptionId.EX_JOB_NOT_FOUND, jobId);
            }
            response.setResponseFindJob(
                    new ResponseFindJob().withJob(convertJobDTOToJob(job))
            );
        });
    }

    @Override
    public void handleRunJob(UnidataRequestBody request, UnidataResponseBody response) {
        handleRequest(request, response, () -> {
            validateParameters(request.getRequestRunJob().getParameters());
            final JobExecutionDTO jobExecutionDTO = jobService.runJob(
                    request.getRequestRunJob().getId(),
                    convertJobParametersToJobDTOParameters(request.getRequestRunJob().getParameters())
            );
            response.setResponseRunJob(
                    new ResponseRunJob().withJobExecution(convertJobExecutionDTOToJobExecution(jobExecutionDTO))
            );
        });
    }

    @Override
    public void handleJobStatus(final UnidataRequestBody request, final UnidataResponseBody response) {
        handleRequest(request, response, () -> {
            final JobExecutionDTO jobExecutionDTO = jobService.jobStatus(request.getRequestJobStatus().getId());
            response.setResponseJobStatus(
                    new ResponseJobStatus().withJobExecution(convertJobExecutionDTOToJobExecution(jobExecutionDTO))
            );
        });
    }

    private void handleRequest(
            final UnidataRequestBody request,
            final UnidataResponseBody response,
            final Runnable handler
    ) {
        try {
            handler.run();
        } catch (Exception e) {
            LOGGER.error("Error while handling job request.", e);
            errorResponse(response, e, request.getCommon().getOperationId());
        }
    }

    private void errorResponse(final UnidataResponseBody response, final Exception e, final String operationId) {
        final ExecutionErrorDef executionError = new ExecutionErrorDef();
        if (e instanceof SystemRuntimeException) {
            SystemRuntimeException jobException = (SystemRuntimeException) e;
            executionError
                    .withInternalMessage(jobException.getMessage())
                    .withErrorCode(jobException.getId().getCode())
                    .withUserMessage(
                            messageSource.getMessage(
                                    jobException.getId().getCode(),
                                    jobException.getArgs(),
                                    "ххх" + jobException.getId().getCode() + "ххх",
                                    RU_LOCALE
                            )
                    );
        }
        else {
            executionError
                    .withErrorCode(e.getClass().getName())
                    .withInternalMessage(e.getMessage());
        }
        response.setCommon(
                new CommonResponseDef()
                        .withExitCode(ExitCodeType.ERROR)
                        .withOperationId(operationId)
                        .withMessage(new ExecutionMessageDef().withError(executionError))
        );

    }

    private JobDTO convertJobToJobDTO(final Job job) {
        return new JobDTO(
                job.getId(),
                job.getName(),
                job.getJobNameReference(),
                convertJobParametersToJobDTOParameters(job.getParameters()),
                job.isEnabled(),
                job.isError(),
                job.getCronExpression(),
                job.getDescription(),
                job.isSkipCronWarnings()
        );
    }

    private List<JobParameterDTO> convertJobParametersToJobDTOParameters(final List<JobParameter> parameters) {
        if (CollectionUtils.isEmpty(parameters)) {
            return Collections.emptyList();
        }
        return parameters.stream()
                .map(this::convertJobParameterToJobParameterDTO)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private JobParameterDTO convertJobParameterToJobParameterDTO(JobParameter jobParameter) {
        switch (jobParameter.getType()) {
            case BOOLEAN:
                return new JobParameterDTO(
                        jobParameter.getId(),
                        jobParameter.getName(),
                        jobParameter.getValue().isBooleanValue()
                );
            case DATE:
                return new JobParameterDTO(
                        jobParameter.getId(),
                        jobParameter.getName(),
                        jobParameter.getValue().getDateValue().toGregorianCalendar().toZonedDateTime()
                );
            case DOUBLE:
                return new JobParameterDTO(
                        jobParameter.getId(),
                        jobParameter.getName(),
                        jobParameter.getValue().getDoubleValue());
            case LONG:
                return new JobParameterDTO(
                        jobParameter.getId(),
                        jobParameter.getName(),
                        jobParameter.getValue().getLongValue()
                );
            case STRING:
                return new JobParameterDTO(
                        jobParameter.getId(),
                        jobParameter.getName(),
                        jobParameter.getValue().getStringValue()
                );
        }
        return null;
    }

    private Job convertJobDTOToJob(final JobDTO jobDTO) {
        return new Job()
                .withCronExpression(jobDTO.getCronExpression())
                .withDescription(jobDTO.getDescription())
                .withEnabled(jobDTO.isEnabled())
                .withError(jobDTO.isError())
                .withId(jobDTO.getId())
                .withJobNameReference(jobDTO.getJobNameReference())
                .withName(jobDTO.getName())
                .withParameters(
                        convertJobDTOParametersToJobParameters(jobDTO.getParameters())
                )
                .withSkipCronWarnings(jobDTO.isSkipCronWarnings());
    }

    private List<JobParameter> convertJobDTOParametersToJobParameters(final Collection<JobParameterDTO> parameters) {
        return parameters.stream()
                .map(jobParameterDTO -> {
                    JobParameterType jobParameterType = JobParameterType.fromValue(jobParameterDTO.getType().name());
                    return new JobParameter()
                            .withId(jobParameterDTO.getId())
                            .withName(jobParameterDTO.getName())
                            .withType(jobParameterType)
                            .withValue(convertJobParameterDTOToJobParameterValue(jobParameterDTO));
                })
                .filter(jobParameter -> jobParameter.getValue() != null)
                .collect(toList());
    }

    private JobParameterValue convertJobParameterDTOToJobParameterValue(final JobParameterDTO jobParameterDTO) {
        switch (jobParameterDTO.getType()) {
            case BOOLEAN:
                return new JobParameterValue().withBooleanValue(jobParameterDTO.getBooleanValue());
            case DATE:
                final XMLGregorianCalendar xmlGregorianCalendar =
                        convertZonedDateTimeToXmlGregorianCalendar(jobParameterDTO.getDateValue());
                return new JobParameterValue().withDateValue(xmlGregorianCalendar);
            case DOUBLE:
                return new JobParameterValue().withDoubleValue(jobParameterDTO.getDoubleValue());
            case LONG:
                return new JobParameterValue().withLongValue(jobParameterDTO.getLongValue());
            case STRING:
                return new JobParameterValue().withStringValue(jobParameterDTO.getStringValue());

        }
        return null;
    }

    private List<JobExecutionStep> convertJobDTOExecutionStepsToJobExecutionSteps(JobExecutionDTO jobExecutionDTO) {
        return jobExecutionDTO.getJobExecutionSteps().stream()
                .map(step -> new JobExecutionStep()
                        .withName(step.getStepName())
                        .withStatus(JobExecutionStatus.fromValue(step.getJobExecutionBatchStatus().name()))
                        .withExitStatus(
                                new JobExecutionExitStatus()
                                        .withCode(step.getJobExecutionExitStatus().getExitCode())
                                        .withDescription(step.getJobExecutionExitStatus().getExitDescription())
                        )
                        .withStartTime(convertZonedDateTimeToXmlGregorianCalendar(step.getStartTime()))
                        .withEndTime(convertZonedDateTimeToXmlGregorianCalendar(step.getEndTime()))
                        .withLastUpdated(convertZonedDateTimeToXmlGregorianCalendar(step.getLastUpdated()))

                )
                .collect(Collectors.toList());
    }

    private XMLGregorianCalendar convertZonedDateTimeToXmlGregorianCalendar(final ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        return JaxbUtils.getDatatypeFactory().newXMLGregorianCalendar(
                GregorianCalendar.from(zonedDateTime)
        );
    }

    private JobExecution convertJobExecutionDTOToJobExecution(final JobExecutionDTO jobExecutionDTO) {
        return new JobExecution()
                .withJob(convertJobDTOToJob(jobExecutionDTO.getJobDTO()))
                .withParameters(
                        convertJobDTOParametersToJobParameters(jobExecutionDTO.getJobParameters())
                )
                .withCreateTime(convertZonedDateTimeToXmlGregorianCalendar(jobExecutionDTO.getCreateTime()))
                .withStartTime(convertZonedDateTimeToXmlGregorianCalendar(jobExecutionDTO.getStartTime()))
                .withEndTime(convertZonedDateTimeToXmlGregorianCalendar(jobExecutionDTO.getEndTime()))
                .withLastUpdated(convertZonedDateTimeToXmlGregorianCalendar(jobExecutionDTO.getLastUpdated()))
                .withStatus(JobExecutionStatus.fromValue(jobExecutionDTO.getJobExecutionBatchStatus().name()))
                .withExitStatus(
                        new JobExecutionExitStatus()
                                .withCode(jobExecutionDTO.getJobExecutionExitStatus().getExitCode())
                                .withDescription(jobExecutionDTO.getJobExecutionExitStatus().getExitDescription())
                )
                .withExecutionSteps(
                        convertJobDTOExecutionStepsToJobExecutionSteps(jobExecutionDTO)
                );
    }

    private void validateParameters(List<JobParameter> jobParameters) {
        if (CollectionUtils.isEmpty(jobParameters)) {
            return;
        }
        final List<String> validationErrors = jobParameters.stream()
                .flatMap(jobParameter -> {
                    final List<String> errors = new ArrayList<>();
                    if (StringUtils.isEmpty(jobParameter.getName())) {
                        errors.add(
                                messageSource.getMessage(
                                        "app.soap.job.parameter.withoutName",
                                        new Object[]{ jobParameter.toString() },
                                        RU_LOCALE
                                )
                        );
                    }
                    if (jobParameter.getType() == null) {
                        errors.add(
                                messageSource.getMessage(
                                        "app.soap.job.parameter.withoutType",
                                        new Object[]{ jobParameter.toString() },
                                        RU_LOCALE
                                )
                        );
                    }
                    if (jobParameter.getValue() == null || isNullValue(jobParameter)) {
                        errors.add(
                                messageSource.getMessage(
                                        "app.soap.job.parameter.withNullValue",
                                        new Object[]{ jobParameter.toString() },
                                        RU_LOCALE
                                )
                        );
                    }
                    return errors.stream();
                })
                .collect(toList());

        if (validationErrors.isEmpty()) {
            return;
        }
        final StringJoiner stringJoiner = new StringJoiner("\n");
        validationErrors.forEach(stringJoiner::add);
        throw new JobException(
                "Parameters not valid",
                ExceptionId.EX_JOB_PARAMETERS_SOAP_VALIDATION_ERRORS,
                stringJoiner.toString()
        );
    }

    private boolean isNullValue(JobParameter jobParameter) {
        if (jobParameter.getType() == null) {
            return false;
        }
        switch (jobParameter.getType()) {
            case BOOLEAN:
                return jobParameter.getValue().isBooleanValue() == null;
            case DATE:
                return jobParameter.getValue().getDateValue() == null;
            case DOUBLE:
                return jobParameter.getValue().getDoubleValue() == null;
            case LONG:
                return jobParameter.getValue().getLongValue() == null;
            case STRING:
                return jobParameter.getValue().getStringValue() == null;
        }
        return false;
    }
}
