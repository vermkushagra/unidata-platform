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

package com.unidata.mdm.backend.service.job.matching;

import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.service.job.JobUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Dmitry Kopin on 02.04.2018.
 */
public class MatchingJobExecutionListener implements JobExecutionListener {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingJobExecutionListener.class);

    @Autowired
    private ClusterService clusterService;
    /**
     * Job util
     */
    @Autowired
    private JobUtil jobUtil;
    /**
     * Matching rule name.
     */
    private String matchingName;
    /**
     * Entity name.
     */
    private String entityName;


    @Override
    public void beforeJob(JobExecution jobExecution){
        try{
            ClusterMetaData clusterMetaData = jobUtil.getMatchingSettings(entityName, matchingName);
            clusterService.removeAllClusters(clusterMetaData);
        } catch (Exception e){
            LOGGER.error("Can't start job", e);
            jobExecution.addFailureException(e);
            jobExecution.setExitStatus(ExitStatus.FAILED);
            jobExecution.setStatus(BatchStatus.FAILED);
         }

    }

    @Override
    public void afterJob(JobExecution jobExecution){

    }

    @Required
    public void setMatchingName(String matchingName) {
        this.matchingName = matchingName;
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

}
