package com.unidata.mdm.backend.converter.matching.po_to_bo;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.ClusterPO;
import com.unidata.mdm.backend.po.matching.ClusterRecordPO;

@ConverterQualifier
@Component
public class ClusterConverter implements Converter<ClusterPO, Cluster> {

    @Autowired
    private Converter<ClusterRecordPO, ClusterRecord> converter;

    @Override
    public Cluster convert(ClusterPO source) {
        Cluster cluster = new Cluster(source.getMatchingDate());
        cluster.setClusterId(source.getClusterId());
        Set<ClusterRecord> records = source.getClusterRecordPOs()
                                           .values()
                                           .stream()
                                           .map(converter::convert)
                                           .collect(Collectors.toSet());
        cluster.setClusterRecords(records);
        ClusterMetaData clusterMetaData = ClusterMetaData.builder()
                .entityName(source.getEntityName())
                .groupId(source.getGroupId())
                .ruleId(source.getRuleId())
                .build();
        cluster.setMetaData(clusterMetaData);
        return cluster;
    }

}
