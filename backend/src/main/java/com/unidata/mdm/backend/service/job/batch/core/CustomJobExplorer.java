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
 * Date: 31.03.2016
 */

package com.unidata.mdm.backend.service.job.batch.core;

import com.unidata.mdm.backend.common.dto.PaginatedResultDTO;
import com.unidata.mdm.backend.dto.job.JobExecutionFilter;
import com.unidata.mdm.backend.dto.job.StepExecutionFilter;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.util.CollectionUtils;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class CustomJobExplorer extends SimpleJobExplorer {
    private CustomJdbcJobExecutionDao customJdbcJobExecutionDao;
    private CustomJdbcStepExecutionDao customJdbcStepExecutionDao;
    private CustomJdbcExecutionContextDao customJdbcExecutionContextDao;

    public CustomJobExplorer(JobInstanceDao jobInstanceDao, CustomJdbcJobExecutionDao jobExecutionDao,
        CustomJdbcStepExecutionDao stepExecutionDao, CustomJdbcExecutionContextDao ecDao) {
        super(jobInstanceDao, jobExecutionDao, stepExecutionDao, ecDao);
        customJdbcJobExecutionDao = jobExecutionDao;
        customJdbcStepExecutionDao = stepExecutionDao;
        customJdbcExecutionContextDao = ecDao;
    }

    /**
     *
     * @param jobInstanceIds
     * @return
     */
    public Map<Long, List<JobExecution>> getJobExecutions(Collection<Long> jobInstanceIds) {
        Map<Long, List<JobExecution>> result = customJdbcJobExecutionDao.findJobExecutions(jobInstanceIds);

        final Map<Long, JobExecution> jobExecutionMap = new HashMap<>();
        result.values().stream()
            .filter(v -> !CollectionUtils.isEmpty(v))
            .flatMap(Collection::stream).forEach(jobExecution -> {
            jobExecutionMap.put(jobExecution.getId(), jobExecution);
        });

        if (CollectionUtils.isEmpty(jobExecutionMap)) {
            return result;
        }

        // Fill executions with steps.
        customJdbcStepExecutionDao.fillStepExecutions(jobExecutionMap.values());

        // Load all jobExecutionContexts.
        Map<Long, ExecutionContext> jobExecutionContextMap =
            customJdbcExecutionContextDao.loadJobExecutionContexts(jobExecutionMap.keySet());

        final Map<Long, StepExecution> stepExecutionMap = new HashMap<>();

        jobExecutionMap.values().stream().forEach(jobExecution -> {
            jobExecution.setExecutionContext(jobExecutionContextMap.get(jobExecution.getId()));

            if (!CollectionUtils.isEmpty(jobExecution.getStepExecutions())) {
                jobExecution.getStepExecutions().stream()
                    .forEach(stepExecution -> {
                        stepExecutionMap.put(stepExecution.getId(), stepExecution);
                    });
            }
        });

        if (CollectionUtils.isEmpty(stepExecutionMap)) {
            return result;
        }

        // Load all stepExecutionContexts.
        Map<Long, ExecutionContext> stepExecutionContextMap =
            customJdbcExecutionContextDao.loadStepExecutionContexts(stepExecutionMap.keySet());

        stepExecutionMap.values().stream().forEach(stepExecution -> {
            stepExecution.setExecutionContext(stepExecutionContextMap.get(stepExecution.getId()));
        });

        return result;
    }

    /**
     *
     * @param filter
     * @return
     */
    public PaginatedResultDTO<JobExecution> searchJobExecutions(JobExecutionFilter filter) {
        PaginatedResultDTO<JobExecution> result = customJdbcJobExecutionDao.searchJobExecutions(filter);

        final Map<Long, JobExecution> jobExecutionMap = new LinkedHashMap<>();
        result.getPage().stream()
            .forEach(jobExecution -> {
            jobExecutionMap.put(jobExecution.getId(), jobExecution);
        });

        if (CollectionUtils.isEmpty(jobExecutionMap)) {
            return result;
        }

        return result;
    }

    /**
     *
     * @param filter
     * @return
     */
    public PaginatedResultDTO<StepExecution> searchStepExecutions(StepExecutionFilter filter) {
        return customJdbcStepExecutionDao.searchStepExecutions(filter);
    }

    /**
     *
     *
     * @param jobInstanceIds
     * @param loadSteps
     * @return
     */
    public Map<Long, JobExecution> getLastJobExecutions(Collection<Long> jobInstanceIds, boolean loadSteps) {
        Map<Long, JobExecution> result = customJdbcJobExecutionDao.findLastJobExecutions(jobInstanceIds);

        final Map<Long, JobExecution> jobExecutionMap = new LinkedHashMap<>();
        result.values().stream()
            .filter(Objects::nonNull)
            .forEach(jobExecution -> {
            jobExecutionMap.put(jobExecution.getId(), jobExecution);
        });

        if (!loadSteps || CollectionUtils.isEmpty(jobExecutionMap)) {
            return result;
        }

        // Fill executions with steps.
        customJdbcStepExecutionDao.fillStepExecutions(jobExecutionMap.values());

//        final Map<Long, StepExecution> stepExecutionMap = new HashMap<>();
//
//        jobExecutionMap.values().forEach(jobExecution -> {
//            if (!CollectionUtils.isEmpty(jobExecution.getStepExecutions())) {
//                jobExecution.getStepExecutions()
//                    .forEach(stepExecution -> {
//                        stepExecutionMap.put(stepExecution.getId(), stepExecution);
//                    });
//            }
//        });
//
//        if (CollectionUtils.isEmpty(stepExecutionMap)) {
//            return result;
//        }

        return result;
    }

    public Date getLastSuccessJobExecutionsDate(Collection<Long> jobInstanceIds) {
        Map<Long, JobExecution> result = customJdbcJobExecutionDao.findLastJobSuccessExecutions(jobInstanceIds);
        Date previousSuccessStartTime = null;
        Optional<JobExecution> jobExecution = result.values()
                .stream()
                .filter(Objects::nonNull)
                .findFirst();
        if(jobExecution.isPresent()){
            previousSuccessStartTime = jobExecution.get().getStartTime();
        }
        return previousSuccessStartTime;
    }

    public Map<Long, Long> getLastJobExecutionIds(Collection<Long> jobInstanceIds) {
        return customJdbcJobExecutionDao.findLastJobExecutionIds(jobInstanceIds);
    }
}
