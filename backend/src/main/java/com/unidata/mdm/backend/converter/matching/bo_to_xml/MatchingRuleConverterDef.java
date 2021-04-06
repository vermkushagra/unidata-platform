package com.unidata.mdm.backend.converter.matching.bo_to_xml;

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
public class MatchingRuleConverterDef implements Converter<MatchingRule, MatchingRuleDef> {

    @Autowired
    private Converter<MatchingAlgorithm, UserMatchingAlgorithmDef> algorithmConverter;

    @Override
    public MatchingRuleDef convert(MatchingRule source) {
        MatchingRuleDef matchingRule = new MatchingRuleDef();
        MatchingRuleKey matchingRuleKey = new MatchingRuleKey();
        matchingRuleKey.setName(source.getName());
        matchingRuleKey.setEntityName(source.getEntityName());
        matchingRule.setKey(matchingRuleKey);
        matchingRule.setDescription(source.getDescription());
        matchingRule.setActive(source.isActive());
        matchingRule.withAlgorithms(source.getMatchingAlgorithms() == null ? Collections.emptyList() : source.getMatchingAlgorithms().stream().map(algorithmConverter::convert).collect(Collectors.toList()));
        return matchingRule;
    }
}
