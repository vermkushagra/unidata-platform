package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.match.MatchingAlgorithmDef;

@ConverterQualifier
@Component
public class MatchingAlgorithmDeserializer implements Converter<String, MatchingAlgorithm> {

    @Autowired
    private Converter<MatchingAlgorithmDef, MatchingAlgorithm> converter;

    @Autowired
    private Converter<String, MatchingAlgorithmDef> deserializer;

    @Override
    public MatchingAlgorithm convert(String source) {
        MatchingAlgorithmDef def = deserializer.convert(source);
        return converter.convert(def);
    }

}
