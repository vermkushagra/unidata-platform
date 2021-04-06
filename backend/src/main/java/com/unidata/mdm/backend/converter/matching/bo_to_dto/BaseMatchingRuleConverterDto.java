package com.unidata.mdm.backend.converter.matching.bo_to_dto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.matching.BaseMatchingRuleRO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;

@ConverterQualifier
@Component
public class BaseMatchingRuleConverterDto implements Converter<MatchingRule, BaseMatchingRuleRO> {

    @Override
    public BaseMatchingRuleRO convert(MatchingRule source) {
        BaseMatchingRuleRO matchingRule = new BaseMatchingRuleRO();
        matchingRule.setDescription(source.getDescription());
        matchingRule.setId(source.getId());
        matchingRule.setName(source.getName());
        matchingRule.setEntityName(source.getEntityName());
        matchingRule.setActive(source.isActive());
        return matchingRule;
    }
}
