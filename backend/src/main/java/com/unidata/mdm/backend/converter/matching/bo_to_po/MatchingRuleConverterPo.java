package com.unidata.mdm.backend.converter.matching.bo_to_po;

import static java.util.stream.Collectors.toList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.MatchingAlgorithmPO;
import com.unidata.mdm.backend.po.matching.MatchingRulePO;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;

@ConverterQualifier
@Component
public class MatchingRuleConverterPo implements Converter<MatchingRule, MatchingRulePO> {

    @Autowired
    private Converter<MatchingAlgorithm, MatchingAlgorithmPO> converter;

    @Override
    public MatchingRulePO convert(MatchingRule source) {
        MatchingRulePO matchingRulePO = new MatchingRulePO();
        matchingRulePO.setId(source.getId());
        matchingRulePO.setName(source.getName());
        matchingRulePO.setEntityName(source.getEntityName());
        matchingRulePO.setActive(source.isActive());
        matchingRulePO.setDescription(source.getDescription());
        //there can be serializing
        matchingRulePO.setSettings(null);
        matchingRulePO.setMatchingAlgorithms(source.getMatchingAlgorithms().stream().map(converter::convert).collect(toList()));
        return matchingRulePO;
    }
}

