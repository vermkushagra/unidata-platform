/**
 * Date: 16.03.2016
 */

package com.unidata.mdm.backend.service.job.registry;

import java.util.Map;

import com.unidata.mdm.backend.service.job.JobParameterProcessor;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class JobTemplateParameters {
    private String jobName;
    private Map<String, Object> valueMap;
    private JobParameterProcessor parameterProcessor;

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
}