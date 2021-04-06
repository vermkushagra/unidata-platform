package com.unidata.mdm.backend.converter.matching.bo_to_po;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.MatchingGroupPO;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;

@ConverterQualifier
@Component
public class MatchingGroupConverterPo implements Converter<MatchingGroup, MatchingGroupPO> {

    @Override
    public MatchingGroupPO convert(MatchingGroup source) {
        MatchingGroupPO matchingGroup = new MatchingGroupPO();
        matchingGroup.setName(source.getName());
        matchingGroup.setEntityName(source.getEntityName());
        matchingGroup.setId(source.getId());
        matchingGroup.setActive(source.isActive());
        matchingGroup.setAutoMerge(source.isAutoMerge());
        matchingGroup.setDescription(source.getDescription());
        matchingGroup.setRuleIds(source.getRulesIds());
        return matchingGroup;
    }
}
