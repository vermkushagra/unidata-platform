package com.unidata.mdm.backend.converter.matching.bo_to_dto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.matching.MatchingGroupRO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;

@ConverterQualifier
@Component
public class MatchingGroupConverterDto implements Converter<MatchingGroup, MatchingGroupRO> {

    @Override
    public MatchingGroupRO convert(MatchingGroup source) {
        MatchingGroupRO matchingRulesGroup = new MatchingGroupRO();
        matchingRulesGroup.setId(source.getId());
        matchingRulesGroup.setName(source.getName());
        matchingRulesGroup.setDescription(source.getDescription());
        matchingRulesGroup.setAutoMerge(source.isAutoMerge());
        matchingRulesGroup.setEntityName(source.getEntityName());
        matchingRulesGroup.setRuleIds(source.getRulesIds());
        return matchingRulesGroup;
    }
}