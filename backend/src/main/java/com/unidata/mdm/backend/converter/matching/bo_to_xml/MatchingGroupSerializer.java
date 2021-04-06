package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;
import com.unidata.mdm.match.MatchingGroupDef;

@ConverterQualifier
@Component
public class MatchingGroupSerializer implements Converter<MatchingGroup, String> {

    @Autowired
    private Converter<MatchingGroupDef, String> serializer;

    @Autowired
    private Converter<MatchingGroup, MatchingGroupDef> convert;

    @Override
    public String convert(MatchingGroup matchingRule) {
        MatchingGroupDef source = convert.convert(matchingRule);
        return serializer.convert(source);
    }
}
