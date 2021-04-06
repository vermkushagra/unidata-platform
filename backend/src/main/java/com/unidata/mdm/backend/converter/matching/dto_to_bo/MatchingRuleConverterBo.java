package com.unidata.mdm.backend.converter.matching.dto_to_bo;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.matching.MatchingAlgorithmRO;
import com.unidata.mdm.backend.api.rest.dto.matching.MatchingRuleRO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;

@ConverterQualifier
@Component
public class MatchingRuleConverterBo implements Converter<MatchingRuleRO, MatchingRule> {

    @Autowired
    private Converter<MatchingAlgorithmRO, MatchingAlgorithm> algorithmConverter;

    @Override
    public MatchingRule convert(MatchingRuleRO source) {
        MatchingRule matchingRule = new MatchingRule();
        matchingRule.setDescription(source.getDescription());
        matchingRule.setId(source.getId());
        matchingRule.setName(source.getName());
        matchingRule.setEntityName(source.getEntityName());
        matchingRule.setActive(source.isActive());
        matchingRule.setMatchingAlgorithms(source.getMatchingAlgorithms() == null ? Collections.emptyList() : source.getMatchingAlgorithms().stream().map(algorithmConverter::convert).collect(Collectors.toList()));
        return matchingRule;
    }
}
