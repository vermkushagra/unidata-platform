package com.unidata.mdm.backend.converter.matching.bo_to_po;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.ClusterRecordPO;

@ConverterQualifier
@Component
public class ClusterRecordConverterPO implements Converter<ClusterRecord, ClusterRecordPO> {

    @Override
    public ClusterRecordPO convert(ClusterRecord source) {
        ClusterRecordPO recordPO = new ClusterRecordPO();
        recordPO.setClusterId(null);
        recordPO.setEtalonDate(source.getMatchingDate());
        recordPO.setEtalonId(source.getEtalonId());
        recordPO.setMatchingRate(source.getMatchingRate());
        return recordPO;
    }
}
