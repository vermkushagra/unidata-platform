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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.unidata.mdm.backend.common.service.ClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.service.job.JobUtil;


public class DuplicateItemPartitioner implements Partitioner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateItemPartitioner.class);

    private String entityName;
    private Long blockSize;
    private String matchingName;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private JobUtil jobUtil;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        ClusterMetaData clusterMetaData = jobUtil.getMatchingSettings(entityName, matchingName);
        LOGGER.info("Start partitioning of clusters between workers. Entity name {} , {}:{}", entityName, matchingName);

        Collection<Long> clusterIds = clusterService.getClusterIds(clusterMetaData);
        Long[] arrayIds = clusterIds.toArray(new Long[clusterIds.size()]);

        Map<String, ExecutionContext> map = new HashMap<>(gridSize);
        int size = blockSize.intValue();
        int partitions = arrayIds.length / size;
        for (int i = 0; i < (partitions + 1); i++) {
            final ExecutionContext value = new ExecutionContext();
            int start = i * size;
            int finish = i == partitions ? arrayIds.length : (i + 1) * size - 1;
            map.put("partition" + i, value);
            value.put("ids", Arrays.copyOfRange(arrayIds, start, finish));
            value.putString("partition", "partition" + i);
        }
        LOGGER.info("Partitioning was finished");
        return map;
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Required
    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }

    @Required
    public void setMatchingName(String matchingName) {
        this.matchingName = matchingName;
    }
}
