package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;
import com.unidata.mdm.match.MatchingGroupDef;
import com.unidata.mdm.match.MatchingRuleKey;

@ConverterQualifier
@Component
public class MatchingGroupConverterDef implements Converter<MatchingGroup, MatchingGroupDef> {

    @Override
    public MatchingGroupDef convert(MatchingGroup source) {
        MatchingGroupDef matchingGroupDef = new MatchingGroupDef();
        matchingGroupDef.setName(source.getName());
        matchingGroupDef.setDescription(source.getDescription());
        matchingGroupDef.setActive(source.isActive());
        String entityName = source.getEntityName();
        matchingGroupDef.setEntityName(entityName);
        Collection<MatchingRuleKey> keys = source.getRules().stream().map(rule -> new MatchingRuleKey().withEntityName(rule.getEntityName()).withName(rule.getName())).collect(Collectors.toList());
        matchingGroupDef.withRuleKeys(keys);
        return matchingGroupDef;
    }
}
