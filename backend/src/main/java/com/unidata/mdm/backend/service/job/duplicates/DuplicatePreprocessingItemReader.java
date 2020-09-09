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

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.common.service.ClusterService;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;


import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


public class DuplicatePreprocessingItemReader extends AbstractPaginatedDataItemReader<List<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicatePreprocessingItemReader.class);

    private ClusterMetaData clusterMetaData;

    /**
     * Block size
     */
    private Long blockSize;

    private Integer shardNumber;

    private Integer lastPageSize = Integer.MAX_VALUE;

    @Autowired
    private ClusterService clusterService;

    public DuplicatePreprocessingItemReader() throws MalformedURLException {
        super();
        setName(ClassUtils.getShortName(DuplicatePreprocessingItemReader.class));
    }

    @Override
    public Iterator<List<String>> doPageRead(){
        if(lastPageSize < blockSize) {
            return Collections.emptyIterator();
        }
        Collection<Cluster> clusters = clusterService.getClustersPreprocessing(clusterMetaData, blockSize.intValue(), 0, shardNumber);
        lastPageSize = clusters.size();
        if(CollectionUtils.isNotEmpty(clusters)){
            return clusters.stream()
                    .map(cluster -> cluster.getClusterRecords().stream()
                            .map(ClusterRecord::getEtalonId)
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList()).iterator();
        }
        return Collections.emptyIterator();
    }



    @Required
    public void setClusterMetaData(ClusterMetaData clusterMetaData) {
        this.clusterMetaData = clusterMetaData;
    }

    @Required
    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }


    @Required
    public void setShardNumber(Integer shardNumber) {
        this.shardNumber = shardNumber;
    }

}
