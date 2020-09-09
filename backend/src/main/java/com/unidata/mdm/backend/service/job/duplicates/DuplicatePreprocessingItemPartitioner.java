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

import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.service.job.JobUtil;
import com.unidata.mdm.backend.service.matching.MatchingRulesService;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.unidata.mdm.backend.service.search.util.SearchUtils.DEFAULT_NUMBER_OF_SHARDS;

/**
 * @author Dmitry Kopin on 03.04.2018.
 */
public class DuplicatePreprocessingItemPartitioner implements Partitioner {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicatePreprocessingItemPartitioner.class);

    /**
     * Job util
     */
    @Autowired
    private JobUtil jobUtil;

    @Autowired
    private MatchingRulesService matchingRulesService;

    /**
     * Number of shards
     */
    @Value("${" + ConfigurationConstants.SEARCH_SHARDS_NUMBER_PROPERTY + ":" + SearchUtils.DEFAULT_NUMBER_OF_SHARDS + "}")
    public String numberOfShards = DEFAULT_NUMBER_OF_SHARDS;
    /**
     * Operation id
     */
    private String operationId;
    /**
     * Matching rule name.
     */
    private String matchingName;
    /**
     * Entity name.
     */
    private String entityName;

    /**
     * Split all records as separate partition.
     * For some reason another way to use chunks with few records per partition.
     * Note, that parameter gridSize not used here.
     *
     * @param gridSize
     * @return
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        final Map<String, ExecutionContext> result = new HashMap<>();
        result.putAll(collectExecutionContexts(Integer.parseInt(numberOfShards)));
        return result;
    }

    private Map<String, ExecutionContext> collectExecutionContexts(Integer shardNumbers) {

        ClusterMetaData clusterMetaData = jobUtil.getMatchingSettings(entityName, matchingName);

        MatchingRule rule = matchingRulesService.getMatchingRule(clusterMetaData.getRuleId());

        if(rule == null || !rule.isWithPreprocessing()){
            throw new JobException("Matching rule can't be used in preprocessing mode",
                    ExceptionId.EX_MATCHING_INCORRECT_PREPROCESSING_RULE, matchingName);
        }

        Date now = new Date();
        final Map<String, ExecutionContext> result = new HashMap<>();

        for(int i = 0; i < shardNumbers; i++){
            final ExecutionContext value = new ExecutionContext();
            result.put(JobUtil.partitionName(i), value);
            value.putString("entityName", entityName);
            value.putString("partition", "partition" + i);
            value.putString("operationId", operationId);
            value.put("clusterMetaData", clusterMetaData);
            value.put("shardNumber", i);
            value.put("jobTime", now);
        }
        return result;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
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
