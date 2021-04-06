package com.unidata.mdm.backend.converter.matching.po_to_bo;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.ClusterRecordPO;

@ConverterQualifier
@Component
public class ClusterRecordConverter implements Converter<ClusterRecordPO, ClusterRecord> {

    @Override
    public ClusterRecord convert(ClusterRecordPO source) {
        return new ClusterRecord(source.getEtalonId(), source.getEtalonDate(), source.getMatchingRate());
    }

}
