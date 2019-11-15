package org.unidata.mdm.core.convert.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepExecution;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.unidata.mdm.core.dto.job.JobDTO;
import org.unidata.mdm.core.dto.job.StepExecutionDTO;
import org.unidata.mdm.core.service.ext.CustomJobParameter;
import org.unidata.mdm.core.service.ext.JobEnumParamExtractor;
import org.unidata.mdm.core.service.ext.JobTemplateParameters;
import org.unidata.mdm.core.type.job.JobEnumType;
import org.unidata.mdm.core.type.job.JobExecutionBatchStatus;
import org.unidata.mdm.core.type.job.JobParameterType;
import org.unidata.mdm.core.dto.job.JobExecutionDTO;
import org.unidata.mdm.core.dto.job.JobExecutionExitStatusDTO;
import org.unidata.mdm.core.dto.job.JobExecutionStepDTO;
import org.unidata.mdm.core.dto.job.JobParameterDTO;
import org.unidata.mdm.core.dto.job.JobTemplateDTO;
import org.unidata.mdm.core.dto.job.JobTemplateParameterDTO;
import org.unidata.mdm.core.dto.job.JobTriggerDTO;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.exception.JobException;
import org.unidata.mdm.core.po.job.JobPO;
import org.unidata.mdm.core.po.job.JobParameterPO;
import org.unidata.mdm.core.po.job.JobTriggerPO;
import org.unidata.mdm.core.rest.ro.JobExecutionRO;
import org.unidata.mdm.core.rest.ro.JobParameterRO;
import org.unidata.mdm.core.rest.ro.JobRO;
import org.unidata.mdm.core.rest.ro.JobStepExecutionRO;
import org.unidata.mdm.core.rest.ro.JobTriggerRO;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
                case STRING_ARR:
                    dto = new JobParameterDTO(po.getName(), (String[])po.getArrayValue());
                    break;
                case DATE:
                case DATE_ARR:
                    dto = new JobParameterDTO(po.getName(), (ZonedDateTime[])po.getArrayValue());
                    break;
                case LONG:
                case LONG_ARR:
                    dto = new JobParameterDTO(po.getName(), (Long[])po.getArrayValue());
                    break;
                case DOUBLE:
                case DOUBLE_ARR:
                    dto = new JobParameterDTO(po.getName(), (Double[])po.getArrayValue());
                    break;
                case BOOLEAN:
                case BOOLEAN_ARR:
                    dto = new JobParameterDTO(po.getName(), (Boolean[])po.getArrayValue());
                    break;
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
            ro.setTags(dto.getTags());
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
        dto.setTags(ro.getTags());
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
        po.setTags(dto.getTags());
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
    public static JobExecutionDTO jobExecutionDTOFromJobExecution(
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

    public static ZonedDateTime zonedDateTimeFromDate(final Date date) {
        return Optional.ofNullable(date)
                .map(Date::toInstant)
                .map(instant -> ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))
                .orElse(null);
    }

    // TODO: fix that convertion from Spring to DTO
    public static JobParameterDTO springJobParameterToJobParameterDTO(String name, JobParameter jobParameter) {
        if (jobParameter==null || jobParameter.getValue() == null) {
            return new JobParameterDTO(name);
        }
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
        dto.setTags(jobPo.getTags());
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
        ro.setTags(dto.getTags());
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
        if (jobExecution.getExitStatus().getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
            ro.setExitDescription(jobExecution.getExitStatus().getExitDescription());
        }
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

        throw new JobException("Unable to convert job parameter type: [{}]", CoreExceptionIds.EX_JOB_PARAMETER_INVALID_TYPE, type);
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
                            ro = new JobParameterRO(entry.getKey(), jobEnum.getParameters().toArray(new String[0]));
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

    public static StepExecutionDTO convertStepExecutionToDTO(final StepExecution stepExecution) {
        final StepExecutionDTO stepExecutionDTO = new StepExecutionDTO();
        stepExecutionDTO.setCommitCount(stepExecution.getCommitCount());
        stepExecutionDTO.setEndTime(stepExecution.getEndTime());
        stepExecutionDTO.setExitStatus(
                new StepExecutionDTO.ExitStatus(
                        stepExecution.getExitStatus().getExitCode(),
                        stepExecution.getExitStatus().getExitDescription()
                )
        );
        stepExecutionDTO.setFailureExceptions(stepExecution.getFailureExceptions());
        stepExecutionDTO.setFilterCount(stepExecution.getFilterCount());
        stepExecutionDTO.setJobExecution(
                jobExecutionDTOFromJobExecution(null, stepExecution.getJobExecution())
        );
        stepExecutionDTO.setLastUpdated(stepExecution.getLastUpdated());
        stepExecutionDTO.setProcessSkipCount(stepExecution.getProcessSkipCount());
        stepExecutionDTO.setReadCount(stepExecution.getReadCount());
        stepExecutionDTO.setReadSkipCount(stepExecution.getReadSkipCount());
        stepExecutionDTO.setRollbackCount(stepExecution.getRollbackCount());
        stepExecutionDTO.setStartTime(stepExecution.getStartTime());
        stepExecutionDTO.setStatus(StepExecutionDTO.BatchStatus.valueOf(stepExecution.getStatus().name()));
        stepExecutionDTO.setStepName(stepExecution.getStepName());
        stepExecutionDTO.setTerminateOnly(stepExecution.isTerminateOnly());
        stepExecutionDTO.setWriteCount(stepExecution.getWriteCount());
        stepExecutionDTO.setWriteSkipCount(stepExecution.getWriteSkipCount());
        return stepExecutionDTO;
    }
}
