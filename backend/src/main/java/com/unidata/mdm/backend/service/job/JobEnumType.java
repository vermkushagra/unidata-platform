package com.unidata.mdm.backend.service.job;

import com.unidata.mdm.backend.common.dto.job.JobParameterType;

import java.util.List;

/**
 * @author Denis Kostovarov
 */
public class JobEnumType {
    private JobParameterType parameterType;

    private List<?> parameters;

    public JobParameterType getParameterType() {
        return parameterType;
    }

    public void setParameterType(JobParameterType parameterType) {
        this.parameterType = parameterType;
    }

    public List<?> getParameters() {
        return parameters;
    }

    public void setParameters(List<?> parameters) {
        this.parameters = parameters;
    }
}
