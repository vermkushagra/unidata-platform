package com.unidata.mdm.backend.converter.matching.bo_to_po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.MatchingAlgorithmPO;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;

@ConverterQualifier
@Component
public class MatchingAlgorithmConverterPo implements Converter<MatchingAlgorithm, MatchingAlgorithmPO> {


    @Autowired
    private Converter<MatchingAlgorithm, String> serializer;

    @Override
    public MatchingAlgorithmPO convert(MatchingAlgorithm source) {
        MatchingAlgorithmPO matchingAlgorithmPO = new MatchingAlgorithmPO();
        matchingAlgorithmPO.setAlgorithmId(source.getId());
        matchingAlgorithmPO.setData(serializer.convert(source));
        return matchingAlgorithmPO;
    }
}
