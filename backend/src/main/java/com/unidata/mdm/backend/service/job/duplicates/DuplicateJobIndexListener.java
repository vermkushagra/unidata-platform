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

package com.unidata.mdm.backend.service.job.duplicates;


import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.service.job.JobUtil;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.Map;

/**
 * @author Dmitry Kopin on 16.03.2018.
 */
public class DuplicateJobIndexListener implements JobExecutionListener{

    @Autowired
    private SearchServiceExt searchService;

    @Autowired
    private ClusterService clusterService;

    /**
     * Job util
     */
    @Autowired
    private JobUtil jobUtil;

    private String entityName;

    private String matchingName;

    @Override
    public void beforeJob(JobExecution jobExecution){
        Map<String, Object> indexParams = Collections.singletonMap("index.refresh_interval", "-1");
        searchService.setIndexSettings(entityName, SecurityUtils.getCurrentUserStorageId(), indexParams);
        searchService.refreshIndex(entityName, SecurityUtils.getCurrentUserStorageId(), false);
    }


    @Override
    public void afterJob(JobExecution jobExecution){
        Map<String, Object> indexParams = Collections.singletonMap("index.refresh_interval", "1s");
        searchService.setIndexSettings(entityName, SecurityUtils.getCurrentUserStorageId(), indexParams);
        searchService.refreshIndex(entityName, SecurityUtils.getCurrentUserStorageId(), true);
        ClusterMetaData clusterMetaData = jobUtil.getMatchingSettings(entityName, matchingName);
        clusterService.removeAllClusters(clusterMetaData);
    }


    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Required
    public void setMatchingName(String matchingName) {
        this.matchingName = matchingName;
    }
}
