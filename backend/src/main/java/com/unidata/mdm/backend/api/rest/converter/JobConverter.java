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

package com.unidata.mdm.backend.api.rest.converter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.job.JobExecutionRO;
import com.unidata.mdm.backend.api.rest.dto.job.JobParameterRO;
import com.unidata.mdm.backend.api.rest.dto.job.JobRO;
import com.unidata.mdm.backend.api.rest.dto.job.JobStepExecutionRO;
import com.unidata.mdm.backend.api.rest.dto.job.JobTriggerRO;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.common.dto.job.JobTemplateDTO;
import com.unidata.mdm.backend.common.dto.job.JobTemplateParameterDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.common.job.JobEnumParamExtractor;
import com.unidata.mdm.backend.common.job.JobEnumType;
import com.unidata.mdm.backend.common.job.JobParameterType;
import com.unidata.mdm.backend.dto.job.JobTriggerDTO;
import com.unidata.mdm.backend.po.job.JobPO;
import com.unidata.mdm.backend.po.job.JobParameterPO;
import com.unidata.mdm.backend.po.job.JobTriggerPO;
import com.unidata.mdm.backend.service.job.registry.JobTemplateParameters;

/**
 * @author Denis Kostovarov
 */
public class JobConverter {
    private static final Logger log = LoggerFactory.getLogger(JobConverter.class);

    private JobConverter() {
        // No-op.
    }

    public static List<JobDTO> convertJobsPoToDto(final Collection<JobPO> jobPOs) {
        Assert.notNull(jobPOs, "Job POs list is null");

        final List<JobDTO> result = new ArrayList<>(jobPOs.size());

        for (final JobPO po : jobPOs) {
            JobDTO dto = convertJobPoToDto(po);

            result.add(dto);
        }

        return result;
    }

    public static List<JobPO> convertJobsDtoToPo(final Collection<JobDTO> jobDTOs) {
        Assert.notNull(jobDTOs, "Job DTOs list is null");

        final List<JobPO> result = new ArrayList<>(jobDTOs.size());

        for (final JobDTO dto : jobDTOs) {
            JobPO po = convertJobDtoToPo(dto);

            result.add(po);
        }

        return result;
    }

    public static List<JobParameterDTO> convertParamPoToDto(final List<JobParameterPO> parameterPOs) {
        if (parameterPOs == null) {
            return new ArrayList<>();
        }

        final List<JobParameterDTO> result = new ArrayList<>(parameterPOs.size());

        for (final JobParameterPO po : parameterPOs) {
            final JobParameterDTO dto;
            switch (po.getType()) {
                case STRING:
                case STRING_ARR: {
                    dto = new JobParameterDTO(po.getName(), (String[])po.getArrayValue());
                    break;
                }
                case DATE:
                case DATE_ARR: {
                    dto = new JobParameterDTO(po.getName(), (ZonedDateTime[])po.getArrayValue());
                    break;
                }
                case LONG:
                case LONG_ARR: {
                    dto = new JobParameterDTO(po.getName(), (Long[])po.getArrayValue());
                    break;
                }
                case DOUBLE:
                case DOUBLE_ARR: {
                    dto = new JobParameterDTO(po.getName(), (Double[])po.getArrayValue());
                    break;
                }
                case BOOLEAN:
                case BOOLEAN_ARR: {
                    dto = new JobParameterDTO(po.getName(), (Boolean[])po.getArrayValue());
                    break;
                }
                default:
                    dto = null;
            }
            dto.setId(po.getId());

            result.add(dto);
        }
        return result;
    }

    public static List<JobRO> convertJobsDtoToRo(final List<JobDTO> jobDTOs) {
        Assert.notNull(jobDTOs, "Job DTO list is null");

        final List<JobRO> result = new ArrayList<>(jobDTOs.size());

        for (final JobDTO dto : jobDTOs) {
            final JobRO ro = new JobRO();
            ro.setId(dto.getId());
            ro.setName(dto.getName());
            ro.setCronExpression(dto.getCronExpression());
            ro.setJobNameReference(dto.getJobNameReference());
            ro.setEnabled(dto.isEnabled());
            ro.setError(dto.isError());
            ro.setDescription(dto.getDescription());

            ro.setParameters(convertParamDtoToRo(dto.getParameters()));

            result.add(ro);
        }

        return result;
    }

    public static List<JobParameterRO> convertTemplateParamDtoToRo(final List<JobTemplateParameterDTO> parameterDTOs) {
        if (parameterDTOs == null) {
            return new ArrayList<>();
        }

        final List<JobParameterRO> result = new ArrayList<>(parameterDTOs.size());

        for (final JobTemplateParameterDTO dto : parameterDTOs) {
            final JobParameterRO ro = new JobParameterRO(dto.getName(), dto.getType());

            result.add(ro);
        }
        return result;
    }

    public static List<JobParameterRO> convertParamDtoToRo(final List<JobParameterDTO> parameterDTOs) {
        if (parameterDTOs == null) {
            return new ArrayList<>();
        }

        final List<JobParameterRO> result = new ArrayList<>(parameterDTOs.size());

        for (final JobParameterDTO dto : parameterDTOs) {
            final JobParameterRO ro;

            switch (dto.getType()) {
                case STRING:
                    ro = new JobParameterRO(dto.getName(), dto.getStringArrayValue());
                    break;
                case DATE:
                    ro = new JobParameterRO(dto.getName(), dto.getDateArrayValue());
                    break;
                case LONG:
                    ro = new JobParameterRO(dto.getName(), dto.getLongArrayValue());
                    break;
                case DOUBLE:
                    ro = new JobParameterRO(dto.getName(), dto.getDoubleArrayValue());
                    break;
                case BOOLEAN:
                    ro = new JobParameterRO(dto.getName(), dto.getBooleanArrayValue());
                    break;
                default:
                    ro = null;
            }

            ro.setId(dto.getId());

            result.add(ro);
        }
        return result;
    }

    public static JobDTO convertJobRoToDto(final JobRO ro) {
        Assert.notNull(ro, "Job RO object is null");

        final JobDTO dto = new JobDTO();
        dto.setId(ro.getId());
        dto.setName(ro.getName());
        dto.setEnabled(ro.isEnabled());
        dto.setError(ro.isError());
        dto.setDescription(ro.getDescription());
        dto.setCronExpression(ro.getCronExpression());
        dto.setJobNameReference(ro.getJobNameReference());
        dto.setSkipCronWarnings(ro.isSkipCronWarnings());

        dto.setParameters(convertParamRoToDto(ro.getParameters()));

        return dto;
    }

    public static List<JobParameterDTO> convertParamRoToDto(final List<JobParameterRO> parameterROs) {
        if (parameterROs == null) {
            return new ArrayList<>();
        }

        final List<JobParameterDTO> result = new ArrayList<>(parameterROs.size());

        for (final JobParameterRO ro : parameterROs) {
            final JobParameterDTO dto;
            switch (ro.getType()) {
                case STRING:
                    dto = new JobParameterDTO(ro.getName(), ro.getStringArrayValue());
                    break;
                case DATE:
                    dto = new JobParameterDTO(ro.getName(), ro.getDateArrayValue());
                    break;
                case LONG:
                    dto = new JobParameterDTO(ro.getName(), ro.getLongArrayValue());
                    break;
                case DOUBLE:
                    dto = new JobParameterDTO(ro.getName(), ro.getDoubleArrayValue());
                    break;
                case BOOLEAN:
                    dto = new JobParameterDTO(ro.getName(), ro.getBooleanArrayValue());
                    break;
                default:
                    dto = null;
            }
            dto.setId(ro.getId());

            result.add(dto);
        }
        return result;
    }

    public static JobPO convertJobDtoToPo(JobDTO dto) {
        Assert.notNull(dto, "Job DTO object is null");

        final JobPO po = new JobPO();
        po.setId(dto.getId());
        po.setName(dto.getName());
        po.setEnabled(dto.isEnabled());
        po.setError(dto.isError());
        po.setDescription(dto.getDescription());
        po.setCronExpression(dto.getCronExpression());
        po.setJobNameReference(dto.getJobNameReference());

        po.setParameters(convertParamDtoToPo(dto.getParameters()));

        return po;
    }

    private static List<JobParameterPO> convertParamDtoToPo(final List<JobParameterDTO> parameterDTOs) {
        if (parameterDTOs == null) {
            return new ArrayList<>();
        }

        final List<JobParameterPO> result = new ArrayList<>(parameterDTOs.size());

        for (final JobParameterDTO dto : parameterDTOs) {
            final JobParameterPO po;
            switch (dto.getType()) {
                case STRING: {
                    po = dto.getValueSize() > 1 ?
                            new JobParameterPO(dto.getName(), dto.getStringArrayValue()) :
                            new JobParameterPO(dto.getName(), dto.getStringValue());
                    break;
                }
                case DATE: {
                    po = dto.getValueSize() > 1 ?
                            new JobParameterPO(dto.getName(), dto.getDateArrayValue()) :
                            new JobParameterPO(dto.getName(), dto.getDateValue());
                    break;
                }
                case LONG: {
                    po = dto.getValueSize() > 1 ?
                            new JobParameterPO(dto.getName(), dto.getLongArrayValue()) :
                            new JobParameterPO(dto.getName(), dto.getLongValue());
                    break;
                }
                case DOUBLE: {
                    po = dto.getValueSize() > 1 ?
                            new JobParameterPO(dto.getName(), dto.getDoubleArrayValue()) :
                            new JobParameterPO(dto.getName(), dto.getDoubleValue());
                    break;
                }
                case BOOLEAN: {
                    po = dto.getValueSize() > 1 ?
                            new JobParameterPO(dto.getName(), dto.getBooleanArrayValue()) :
                            new JobParameterPO(dto.getName(), dto.getBooleanValue());
                    break;
                }
                default:
                    po = null;
            }
            po.setId(dto.getId());

            result.add(po);
        }
        return result;
    }

    public static JobDTO convertJobPoToDto(final JobPO jobPo) {
        Assert.notNull(jobPo, "Job PO object is null");

        final JobDTO dto = new JobDTO();
        dto.setId(jobPo.getId());
        dto.setName(jobPo.getName());
        dto.setEnabled(jobPo.isEnabled());
        dto.setError(jobPo.isError());
        dto.setDescription(jobPo.getDescription());
        dto.setCronExpression(jobPo.getCronExpression());
        dto.setJobNameReference(jobPo.getJobNameReference());

        dto.setParameters(convertParamPoToDto(jobPo.getParameters()));

        return dto;
    }

    public static JobRO convertJobDtoToRo(final JobDTO dto) {
        Assert.notNull(dto, "Job DTO object is null");

        final JobRO ro = new JobRO();
        ro.setId(dto.getId());
        ro.setName(dto.getName());
        ro.setEnabled(dto.isEnabled());
        ro.setError(dto.isError());
        ro.setDescription(dto.getDescription());
        ro.setCronExpression(dto.getCronExpression());
        ro.setJobNameReference(dto.getJobNameReference());
        ro.setSkipCronWarnings(dto.isSkipCronWarnings());

        ro.setParameters(convertParamDtoToRo(dto.getParameters()));

        return ro;
    }

    public static List<JobExecutionRO> convertJobExecutionPoToRo(Collection<JobExecution> executions, Long jobId) {
        List<JobExecutionRO> result = new ArrayList<>();

        if (!CollectionUtils.isEmpty(executions)) {
            result.addAll(executions.stream()
                    .map(execution -> convertJobExecutionPoToRo(execution, jobId))
                    .collect(Collectors.toList()));
        }

        return result;
    }

    public static JobExecutionRO convertJobExecutionPoToRo(final JobExecution jobExecution, Long jobId) {
        Assert.notNull(jobExecution, "jobExecution object is null");

        final JobExecutionRO ro = new JobExecutionRO();
        ro.setId(jobExecution.getId());
        // Set external unidata jobId instead of springBatch jobId.
        ro.setJobId(jobId);
        ro.setStatus(jobExecution.getStatus() == null ? null : jobExecution.getStatus().name());
        ro.setStartTime(jobExecution.getStartTime());
        ro.setEndTime(jobExecution.getEndTime());

        ro.setStepExecutions(convertStepExecutionPoToRo(jobExecution.getStepExecutions()));

        return ro;
    }

    public static List<JobStepExecutionRO> convertStepExecutionPoToRo(Collection<StepExecution> stepExecutions) {
        List<JobStepExecutionRO> result = new ArrayList<>();

        if (!CollectionUtils.isEmpty(stepExecutions)) {
            result.addAll(stepExecutions.stream()
                .map(JobConverter::convertStepExecutionPoToRo)
                .collect(Collectors.toList()));
        }

        return result;
    }

    public static JobStepExecutionRO convertStepExecutionPoToRo(final StepExecution stepExecution) {
        Assert.notNull(stepExecution, "stepExecution object is null");

        JobStepExecutionRO ro = new JobStepExecutionRO();

        ro.setId(stepExecution.getId());
        ro.setStepName(stepExecution.getStepName());
        ro.setStartTime(stepExecution.getStartTime());
        ro.setEndTime(stepExecution.getEndTime());
        ro.setStatus(stepExecution.getStatus() == null ? null : stepExecution.getStatus().name());
        ro.setJobExecutionId(stepExecution.getJobExecutionId());

        ExitStatus exitStatus = stepExecution.getExitStatus();

        if (exitStatus != null) {
            ro.setExitCode(exitStatus.getExitCode());
            ro.setExitDescription(exitStatus.getExitDescription());
        }

        return ro;
    }

    public static JobParameterType convertParameterType(JobParameterPO.JobParameterType type) {
        Assert.notNull(type, "'type' cannot be null");

        switch (type) {
            case STRING:
                return JobParameterType.STRING;
            case LONG:
                return JobParameterType.LONG;
            case DOUBLE:
                return JobParameterType.DOUBLE;
            case DATE:
                return JobParameterType.DATE;
            case BOOLEAN:
                return JobParameterType.BOOLEAN;
        }

        throw new JobException("Unable to convert job parameter type: [{}]", ExceptionId.EX_JOB_PARAMETER_INVALID_TYPE, type);
    }

    public static JobRO convertJobTemplateDtoToRo(final JobTemplateDTO jobTemplateDTO) {
        Assert.notNull(jobTemplateDTO, "'jobTemplateDTO' parameter cannot be null");

        final JobRO result = new JobRO();

        result.setName(jobTemplateDTO.getName());
        result.setJobNameReference(jobTemplateDTO.getJobNameReference());
        result.setParameters(convertTemplateParamDtoToRo(jobTemplateDTO.getParameters()));

        return result;
    }

    public static List<JobParameterRO> convertJobTemplateParametersToRo(final JobTemplateParameters parameters) {
        if (parameters == null || CollectionUtils.isEmpty(parameters.getValueMap())) {
            return Collections.emptyList();
        }

        final List<JobParameterRO> result = new ArrayList<>();
        for (final Entry<String, Object> entry : parameters.getValueMap().entrySet()) {
            final JobParameterRO ro;
            final Object val = entry.getValue();

            if (val instanceof String) {
                ro = new JobParameterRO(entry.getKey(), (String) val);
            } else if (val instanceof Long) {
                ro = new JobParameterRO(entry.getKey(), (Long) val);
            } else if (val instanceof Double) {
                ro = new JobParameterRO(entry.getKey(), (Double) val);
            } else if (val instanceof ZonedDateTime) {
                ro = new JobParameterRO(entry.getKey(), (ZonedDateTime) val);
            } else if (val instanceof Boolean) {
                ro = new JobParameterRO(entry.getKey(), (Boolean) val);
            } else if (val instanceof JobEnumParamExtractor) {
                final JobEnumType jobEnum = ((JobEnumParamExtractor) val).extractParameters();
                if (jobEnum != null && !CollectionUtils.isEmpty(jobEnum.getParameters())) {
                    switch (jobEnum.getParameterType()) {
                        case STRING:
                            ro = new JobParameterRO(entry.getKey(), jobEnum.getParameters().toArray(new String[]{}));
                            break;
                        case DATE:
                            ro = new JobParameterRO(entry.getKey(), jobEnum.getParameters().toArray(new ZonedDateTime[]{}));
                            break;
                        case LONG:
                            ro = new JobParameterRO(entry.getKey(), jobEnum.getParameters().toArray(new Long[]{}));
                            break;
                        case DOUBLE:
                            ro = new JobParameterRO(entry.getKey(), jobEnum.getParameters().toArray(new Double[]{}));
                            break;
                        case BOOLEAN:
                            if (jobEnum.getParameters().size() == 1) {
                                ro = new JobParameterRO(entry.getKey(), (Boolean) jobEnum.getParameters().get(0));
                            } else {
                                final Boolean[] boolArray = new Boolean[]{Boolean.FALSE, Boolean.TRUE};
                                ro = new JobParameterRO(entry.getKey(), boolArray);
                            }
                            break;
                        default:
                            ro = null;
                    }

                    if (Objects.nonNull(ro)) {
                        ro.setMultiSelect(jobEnum.isMultiSelect());
                    }

                } else {
                    ro = null;
                }
            } else {
                log.error("Cannot convert parameter [" + entry.getKey() + "] of type [" + (val != null ? val.getClass() : null) + "]");
                ro = null;
            }

            if (ro != null) {
                result.add(ro);
            }
        }

        return result;
    }

    public static List<JobTriggerDTO> convertJobTriggersPoToDTo(final List<JobTriggerPO> pos) {
        return pos.stream().map(JobConverter::convertJobTriggerPoToDto).collect(Collectors.toList());
    }

    public static List<JobTriggerRO> convertJobTriggersDtoToRo(final List<JobTriggerDTO> dtos) {
        return dtos.stream().map(JobConverter::convertJobTriggerDtoToRo).collect(Collectors.toList());
    }

    public static JobTriggerDTO convertJobTriggerRoToDto(JobTriggerRO ro) {
        Assert.notNull(ro, "Cannot convert null element");

        final JobTriggerDTO dto = new JobTriggerDTO();
        dto.setId(ro.getId());
        dto.setStartJobId(ro.getStartJobId());
        dto.setSuccessRule(ro.getSuccessRule());
        dto.setName(ro.getName());
        dto.setDescription(ro.getDescription());

        return dto;
    }

    public static JobTriggerPO convertJobTriggerDtoToPo(final JobTriggerDTO dto) {
        Assert.notNull(dto, "Cannot convert null element");

        final JobTriggerPO po = new JobTriggerPO();
        po.setId(dto.getId());
        po.setFinishJobId(dto.getFinishJobId());
        po.setStartJobId(dto.getStartJobId());
        po.setSuccessRule(dto.getSuccessRule());
        po.setName(dto.getName());
        po.setDescription(dto.getDescription());

        return po;
    }

    public static JobTriggerDTO convertJobTriggerPoToDto(final JobTriggerPO po) {
        Assert.notNull(po, "Cannot convert null element");

        final JobTriggerDTO dto = new JobTriggerDTO();
        dto.setId(po.getId());
        dto.setFinishJobId(po.getFinishJobId());
        dto.setStartJobId(po.getStartJobId());
        dto.setSuccessRule(po.getSuccessRule());
        dto.setName(po.getName());
        dto.setDescription(po.getDescription());

        return dto;
    }

    public static JobTriggerRO convertJobTriggerDtoToRo(final JobTriggerDTO dto) {
        Assert.notNull(dto, "Cannot convert null element");

        final JobTriggerRO ro = new JobTriggerRO();
        ro.setId(dto.getId());
        ro.setStartJobId(dto.getStartJobId());
        ro.setSuccessRule(dto.getSuccessRule());
        ro.setName(dto.getName());
        ro.setDescription(dto.getDescription());

        return ro;
    }

    public static List<JobDTO> convertJobsRoToDto(List<JobRO> jobROs) {
        return jobROs.stream().map(JobConverter::convertJobRoToDto).collect(Collectors.toList());
    }
}
