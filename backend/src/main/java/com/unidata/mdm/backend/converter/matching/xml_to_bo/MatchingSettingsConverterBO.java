package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;
import com.unidata.mdm.backend.service.matching.data.MatchingKey;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import com.unidata.mdm.match.MatchingGroupDef;
import com.unidata.mdm.match.MatchingRuleDef;
import com.unidata.mdm.match.MatchingSettingsDef;

@ConverterQualifier
@Component
public class MatchingSettingsConverterBO implements Converter<MatchingSettingsDef, MatchingUserSettings> {

    @Autowired
    private Converter<MatchingRuleDef, MatchingRule> ruleConverter;

    @Autowired
    private Converter<MatchingGroupDef, MatchingGroup> groupConverter;

    @Override
    public MatchingUserSettings convert(MatchingSettingsDef source) {
        MatchingUserSettings matchingUserSettings = new MatchingUserSettings();
        matchingUserSettings.setMatchingRules(
                source.getUserRules().stream().map(ruleConverter::convert).collect(toList()));
        matchingUserSettings.setMatchingGroups(
                source.getUserGroups().stream().map(groupConverter::convert).collect(toList()));
        Map<String, Collection<MatchingKey>> keys = new HashMap<>();
        for (MatchingGroupDef groupDef : source.getUserGroups()) {
            keys.put(groupDef.getName() + groupDef.getEntityName(), getKey(groupDef));
        }
        matchingUserSettings.setMatchingKeys(keys);
        return matchingUserSettings;
    }

    private List<MatchingKey> getKey(MatchingGroupDef groupDef) {
        return groupDef.getRuleKeys()
                       .stream()
                       .map(key -> new MatchingKey().setEntityName(key.getEntityName())
                                                    .setMatchingGroupName(groupDef.getName())
                                                    .setMatchingRuleName(key.getName()))
                       .collect(Collectors.toList());
    }

}
