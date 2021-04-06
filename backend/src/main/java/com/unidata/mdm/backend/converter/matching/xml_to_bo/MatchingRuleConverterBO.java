package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.match.MatchingRuleDef;
import com.unidata.mdm.match.MatchingRuleKey;
import com.unidata.mdm.match.UserMatchingAlgorithmDef;

@ConverterQualifier
@Component
public class MatchingRuleConverterBO implements Converter<MatchingRuleDef, MatchingRule> {

    @Autowired
    private Converter<UserMatchingAlgorithmDef, MatchingAlgorithm> converter;

    @Override
    public MatchingRule convert(MatchingRuleDef source) {
        MatchingRule matchingRule = new MatchingRule();
        MatchingRuleKey matchingRuleKey = source.getKey();
        matchingRule.setEntityName(matchingRuleKey.getEntityName());
        matchingRule.setName(matchingRuleKey.getName());
        matchingRule.setDescription(source.getDescription());
        matchingRule.setActive(source.isActive());
        matchingRule.setMatchingAlgorithms(source.getAlgorithms() == null ? Collections.emptyList() : source.getAlgorithms().stream().map(converter::convert).collect(Collectors.toList()));
        return matchingRule;
    }
}
