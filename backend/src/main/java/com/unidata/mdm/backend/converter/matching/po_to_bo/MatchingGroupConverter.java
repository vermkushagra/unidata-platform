package com.unidata.mdm.backend.converter.matching.po_to_bo;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.MatchingGroupPO;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;

@ConverterQualifier
@Component
public class MatchingGroupConverter implements Converter<MatchingGroupPO, MatchingGroup> {

    @Override
    public MatchingGroup convert(MatchingGroupPO source) {
        MatchingGroup matchingGroup = new MatchingGroup();
        matchingGroup.setActive(source.isActive());
        matchingGroup.setAutoMerge(source.isAutoMerge());
        matchingGroup.setEntityName(source.getEntityName());
        matchingGroup.setName(source.getName());
        matchingGroup.setId(source.getId());
        matchingGroup.setDescription(source.getDescription());
        matchingGroup.setRulesIds(source.getRuleIds());
        return matchingGroup;
    }
}
