package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.match.MatchingRuleDef;

@ConverterQualifier
@Component
public class MatchingRuleSerializer implements Converter<MatchingRule, String> {

    @Autowired
    private Converter<MatchingRuleDef, String> serialezer;

    @Autowired
    private Converter<MatchingRule, MatchingRuleDef> convert;

    @Override
    public String convert(MatchingRule matchingRule) {
        MatchingRuleDef source = convert.convert(matchingRule);
        return serialezer.convert(source);
    }
}
