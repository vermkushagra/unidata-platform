/**
 * Date: 16.03.2016
 */

package org.unidata.mdm.core.service.ext;

import java.util.Map;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class JobTemplateParameters {
    private String jobName;
    private Map<String, Object> valueMap;
    private JobParameterProcessor parameterProcessor;
    private Map<String, JobParameterValidator> validators;

    public JobTemplateParameters() {
        super();
    }

    public JobTemplateParameters(String jobName, Map<String, Object> valueMap, JobParameterProcessor parameterProcessor) {
        super();
        this.jobName = jobName;
        this.valueMap = valueMap;
        this.parameterProcessor = parameterProcessor;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Map<String, Object> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, Object> valueMap) {
        this.valueMap = valueMap;
    }

    /**
     * @return the parameterProcessor
     */
    public JobParameterProcessor getParameterProcessor() {
        return parameterProcessor;
    }

    /**
     * @param parameterProcessor the parameterProcessor to set
     */
    public void setParameterProcessor(JobParameterProcessor parameterProcessor) {
        this.parameterProcessor = parameterProcessor;
    }

    public Map<String, JobParameterValidator> getValidators() {
        return validators;
    }

    public void setValidators(Map<String, JobParameterValidator> validators) {
        this.validators = validators;
    }
}