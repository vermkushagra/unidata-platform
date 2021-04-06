package com.unidata.mdm.backend.converter.matching.bo_to_po;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.ClusterPO;
import com.unidata.mdm.backend.po.matching.ClusterRecordPO;

@ConverterQualifier
@Component
public class ClusterConverterPO implements Converter<Cluster, ClusterPO> {

    @Autowired
    private Converter<ClusterRecord, ClusterRecordPO> converter;

    @Override
    public ClusterPO convert(Cluster source) {
        ClusterPO clusterPO = new ClusterPO();
        clusterPO.setClusterId(source.getClusterId());
        clusterPO.setClusterHash(null);
        clusterPO.setEntityName(source.getMetaData().getEntityName());
        clusterPO.setGroupId(source.getMetaData().getGroupId());
        clusterPO.setRuleId(source.getMetaData().getRuleId());
        clusterPO.setMatchingDate(source.getMatchingDate());
        clusterPO.setStorage(source.getMetaData().getStorage());
        Map<String, ClusterRecordPO> records = source.getClusterRecords()
                                                     .stream()
                                                     .map(converter::convert)
                                                     .collect(Collectors.toMap(ClusterRecordPO::getEtalonId, (r) -> r));
        clusterPO.setClusterRecordPOs(records);
        if (clusterPO.getClusterId() != null) {
            clusterPO.getClusterRecordPOs().values().forEach(re -> re.setClusterId(clusterPO.getClusterId()));
        }
        return clusterPO;
    }

}
