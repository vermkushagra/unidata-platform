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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.unidata.mdm.backend.service.job.JobParameterProcessor;
import com.unidata.mdm.backend.service.job.TriggerExecutionListener;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class JobWithParamsRegistryImpl extends MapJobRegistry implements JobWithParamsRegistry {
    private static final Logger log = LoggerFactory.getLogger(JobWithParamsRegistryImpl.class);

    private final ConcurrentMap<String, JobTemplateParameters> map = new ConcurrentHashMap<>();
    // Concurrent map
    private Map<String, JobParameterProcessor> jobParameterProcessorsMap;

    @Autowired
    private TriggerExecutionListener listener;

    @Override
    public void register(JobTemplateParameters parameters) throws DuplicateJobException {
        log.debug("Register job parameters [jobName={}, parameters={}]", parameters.getJobName(), parameters);

        Assert.notNull(parameters);
        final String name = parameters.getJobName();

        Assert.notNull(name, "Job configuration must have a name.");
        if (map.containsKey(name)) {
            throw new DuplicateJobException("A job parameters configuration with this name [" + name
                + "] was already registered");
        }

        JobParameterProcessor parameterProcessor = parameters.getParameterProcessor();
        if (Objects.nonNull(parameterProcessor)) {
            jobParameterProcessorsMap.put(name, parameterProcessor);
        }

        map.put(name, parameters);
    }

    @Override
    public void registerTriggerListener(final Job j) {
        if (j instanceof AbstractJob) {
            final AbstractJob job = (AbstractJob) j;
            job.setJobExecutionListeners(new JobExecutionListener[]{listener});
        } else {
            log.warn("Trigger support disabled for job with name '" + j + "'");
        }
    }

    @Override
    public JobTemplateParameters getJobTemplateParameters(String name){
        return map.get(name);
    }

    @Override
    public Set<String> getJobParameterNames() {
        return Collections.unmodifiableSet(map.keySet());
    }

    /**
     * @return the jobParameterProcessorsMap
     */
    public Map<String, JobParameterProcessor> getJobParameterProcessorsMap() {
        return jobParameterProcessorsMap;
    }

    /**
     * @param jobParameterProcessorsMap the jobParameterProcessorsMap to set
     */
    public void setJobParameterProcessorsMap(Map<String, JobParameterProcessor> jobParameterProcessorsMap) {
        this.jobParameterProcessorsMap = jobParameterProcessorsMap;
    }
}
