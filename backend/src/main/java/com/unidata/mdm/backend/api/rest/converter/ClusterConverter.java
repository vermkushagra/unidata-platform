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
        recordsClusterDto.setRecordsCount(cluster.getClusterRecords().size());
        recordsClusterDto.setEntityName(cluster.getMetaData().getEntityName());
        recordsClusterDto.setGroupId(cluster.getMetaData().getGroupId());
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
