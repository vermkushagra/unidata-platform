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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;

import com.unidata.mdm.backend.common.service.ClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterRecord;


public class DuplicateItemReader implements ItemReader<Collection<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateItemReader.class);

    @Autowired
    private ClusterService clusterService;

    /**
     * Collection of cluster ids
     */
    private Long[] ids;
    /**
     * Stateful cursor for arrays of ids.
     */
    private int cursor = -1;
    /**
     * Partition name
     */
    private String partition;

    @Override
    public Collection<String> read() throws Exception {
        cursor++;

        if (cursor == 0) {
            LOGGER.info("Duplicate reader was started for {}", partition);
        }

        if (cursor >= ids.length) {
            return null;
        }

        Long id = ids[cursor];

        Cluster cluster = clusterService.getCluster(id);
        Collection<String> result = cluster == null ? emptyList() : cluster.getClusterRecords().stream().map(ClusterRecord::getEtalonId).collect(toList());
        //if (cluster != null) {
        //    clusterService.excludeFromClusters(cluster.getMetaData().getEntityName(), result);
        //}
        return result;
    }

    @Required
    public void setIds(Long[] ids) {
        this.ids = ids;
    }

    @Required
    public void setPartition(String partition) {
        this.partition = partition;
    }
}
