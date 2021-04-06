package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import static java.util.stream.Collectors.toList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import com.unidata.mdm.match.MatchingSettingsDef;
import com.unidata.mdm.match.MatchingGroupDef;
import com.unidata.mdm.match.MatchingRuleDef;

@ConverterQualifier
@Component
public class MatchingSettingsConverterDef implements Converter<MatchingUserSettings, MatchingSettingsDef> {

    @Autowired
    private Converter<MatchingRule, MatchingRuleDef> matchingRuleDefConverter;

    @Autowired
    private Converter<MatchingGroup, MatchingGroupDef> matchingGroupDefConverter;

    @Override
    public MatchingSettingsDef convert(MatchingUserSettings source) {
        MatchingSettingsDef def = new MatchingSettingsDef();
        def.withUserGroups(source.getMatchingGroups().stream().map(matchingGroupDefConverter::convert).collect(toList()));
        def.withUserRules(source.getMatchingRules().stream().map(matchingRuleDefConverter::convert).collect(toList()));
        return def;
    }

}
