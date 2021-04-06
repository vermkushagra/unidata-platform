package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;
import com.unidata.mdm.match.MatchingGroupDef;
import com.unidata.mdm.match.MatchingRuleKey;

@ConverterQualifier
@Component
public class MatchingGroupConverterBO implements Converter<MatchingGroupDef, MatchingGroup> {

    @Override
    public MatchingGroup convert(MatchingGroupDef source) {

        MatchingGroup matchingGroup = new MatchingGroup();
        matchingGroup.setActive(source.isActive());
        matchingGroup.setDescription(source.getDescription());
        matchingGroup.setEntityName(source.getEntityName());
        matchingGroup.setName(source.getName());
        matchingGroup.setRulesNames(source.getRuleKeys().stream()
                .map(MatchingRuleKey::getName)
                .collect(Collectors.toList()));

        return matchingGroup;
    }

}
