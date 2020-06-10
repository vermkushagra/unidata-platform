package com.unidata.mdm.backend.common.dto.job;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Kostovarov
 */
abstract class JobMetaDTO<T extends JobTemplateParameterDTO> {
    private String name;
    private String jobNameReference;
    private List<T> parameters;

    JobMetaDTO(){
        //no-op.
    }

    JobMetaDTO(String name, String jobNameReference, List<T> parameters) {
        this.name = name;
        this.jobNameReference = jobNameReference;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobNameReference() {
        return jobNameReference;
    }

    public void setJobNameReference(String jobNameReference) {
        this.jobNameReference = jobNameReference;
    }

    public List<T> getParameters() {
        return parameters;
    }

    public void setParameters(List<T> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(T param) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(param);
    }
}
