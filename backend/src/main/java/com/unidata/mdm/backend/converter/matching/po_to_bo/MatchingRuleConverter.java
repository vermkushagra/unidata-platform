package com.unidata.mdm.backend.converter.matching.po_to_bo;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.Collection;

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
public class MatchingRuleConverter implements Converter<MatchingRulePO, MatchingRule> {

    @Autowired
    private Converter<MatchingAlgorithmPO, MatchingAlgorithm> converter;

    @Override
    public MatchingRule convert(MatchingRulePO source) {
        MatchingRule matchingRule = new MatchingRule();
        matchingRule.setId(source.getId());
        matchingRule.setName(source.getName());
        matchingRule.setEntityName(source.getEntityName());
        matchingRule.setActive(source.isActive());
        matchingRule.setDescription(source.getDescription());
        Collection<MatchingAlgorithmPO> algos = source.getMatchingAlgorithms();
        matchingRule.setMatchingAlgorithms(isEmpty(algos) ? null : algos.stream().map(converter::convert).collect(toList()));
        //set settings!
        return matchingRule;
    }

}
