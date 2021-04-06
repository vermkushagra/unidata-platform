package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.match.MatchingAlgorithmDef;

@ConverterQualifier
@Component
public class MatchingAlgorithmSerializer implements Converter<MatchingAlgorithm, String> {

    @Autowired
    private Converter<MatchingAlgorithmDef, String> serializer;

    @Autowired
    private Converter<MatchingAlgorithm, MatchingAlgorithmDef> convert;

    @Override
    public String convert(MatchingAlgorithm matchingAlgorithm) {
        MatchingAlgorithmDef source = convert.convert(matchingAlgorithm);
        return serializer.convert(source);
    }
}
