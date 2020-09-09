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

package com.unidata.mdm.backend.api.rest.converter;

import java.util.stream.Collectors;

import com.unidata.mdm.backend.api.rest.dto.matching.ClusterRecordRO;
import com.unidata.mdm.backend.api.rest.dto.matching.RecordsClusterRO;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterRecord;

public class ClusterConverter {

    public static RecordsClusterRO convert(Cluster cluster) {
        RecordsClusterRO recordsClusterDto = new RecordsClusterRO();
        recordsClusterDto.setRuleId(cluster.getMetaData().getRuleId());
        recordsClusterDto.setClusterOwnerId(cluster.getClusterOwnerRecord());
        recordsClusterDto.setRecordsCount(cluster.getClusterRecords().size());
        recordsClusterDto.setEntityName(cluster.getMetaData().getEntityName());
        recordsClusterDto.setMatchingDate(cluster.getMatchingDate());
        recordsClusterDto.setClusterId(cluster.getClusterId());
        recordsClusterDto.setRecords(cluster.getClusterRecords()
                                            .stream()
                                            .map(ClusterConverter::convert)
                                            .collect(Collectors.toList()));
        return recordsClusterDto;
    }

    public static ClusterRecordRO convert(ClusterRecord clusterRecord) {
        ClusterRecordRO clusterRecordDto = new ClusterRecordRO();
        clusterRecordDto.setEtalonDate(clusterRecord.getMatchingDate());
        clusterRecordDto.setEtalonId(clusterRecord.getEtalonId());
        clusterRecordDto.setMatchingRate(clusterRecord.getMatchingRate());
        return clusterRecordDto;
    }
}
