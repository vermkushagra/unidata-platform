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

/**
 * Date: 16.03.2016
 */

package com.unidata.mdm.backend.service.job.registry;

import java.util.Map;

import com.unidata.mdm.backend.service.job.JobParameterProcessor;
import com.unidata.mdm.backend.service.job.JobParameterValidator;

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