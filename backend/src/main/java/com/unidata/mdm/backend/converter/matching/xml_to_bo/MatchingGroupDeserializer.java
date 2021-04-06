package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;
import com.unidata.mdm.match.MatchingGroupDef;

@ConverterQualifier
@Component
public class MatchingGroupDeserializer implements Converter<String, MatchingGroup> {

    @Autowired
    private Converter<MatchingGroupDef, MatchingGroup> ruleConverter;

    @Autowired
    private Converter<String, MatchingGroupDef> deserializer;

    @Override
    public MatchingGroup convert(String source) {
        MatchingGroupDef def = deserializer.convert(source);
        return ruleConverter.convert(def);
    }
}