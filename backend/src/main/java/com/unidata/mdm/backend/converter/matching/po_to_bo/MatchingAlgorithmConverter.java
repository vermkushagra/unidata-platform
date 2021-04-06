package com.unidata.mdm.backend.converter.matching.po_to_bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.MatchingAlgorithmPO;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;

@ConverterQualifier
@Component
public class MatchingAlgorithmConverter implements Converter<MatchingAlgorithmPO, MatchingAlgorithm> {

    @Autowired
    private Converter<String, MatchingAlgorithm> deserializer;

    @Override
    public MatchingAlgorithm convert(MatchingAlgorithmPO source) {
        MatchingAlgorithm matchingAlgorithm = deserializer.convert(source.getData());
        matchingAlgorithm.setId(source.getAlgorithmId());
        return matchingAlgorithm;
    }
}
