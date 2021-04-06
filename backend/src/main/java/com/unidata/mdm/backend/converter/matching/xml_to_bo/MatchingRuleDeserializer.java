package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.match.MatchingRuleDef;

@ConverterQualifier
@Component
public class MatchingRuleDeserializer implements Converter<String, MatchingRule> {

    @Autowired
    private Converter<MatchingRuleDef, MatchingRule> ruleConverter;

    @Autowired
    private Converter<String, MatchingRuleDef> deserializer;

    @Override
    public MatchingRule convert(String source) {
        MatchingRuleDef matchingRuleDef = deserializer.convert(source);
        return ruleConverter.convert(matchingRuleDef);
    }
}
