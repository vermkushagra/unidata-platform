/**
 * Date: 18.03.2016
 */

package com.unidata.mdm.backend.api.rest.dto.job;

import java.util.List;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class JobMetaInfoRO {
    private String jobNameReference;
    private List<JobParameterRO> parameters;

    public String getJobNameReference() {
        return jobNameReference;
    }

    public void setJobNameReference(String jobNameReference) {
        this.jobNameReference = jobNameReference;
    }

    public List<JobParameterRO> getParameters() {
        return parameters;
    }

    public void setParameters(List<JobParameterRO> parameters) {
        this.parameters = parameters;
    }
}
