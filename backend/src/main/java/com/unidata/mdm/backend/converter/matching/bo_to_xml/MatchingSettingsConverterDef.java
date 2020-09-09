/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import static java.util.stream.Collectors.toList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import com.unidata.mdm.match.MatchingSettingsDef;
import com.unidata.mdm.match.MatchingRuleDef;

@ConverterQualifier
@Component
public class MatchingSettingsConverterDef implements Converter<MatchingUserSettings, MatchingSettingsDef> {

    @Autowired
    private Converter<MatchingRule, MatchingRuleDef> matchingRuleDefConverter;

    @Override
    public MatchingSettingsDef convert(MatchingUserSettings source) {
        MatchingSettingsDef def = new MatchingSettingsDef();
        def.withUserRules(source.getMatchingRules().stream().map(matchingRuleDefConverter::convert).collect(toList()));
        return def;
    }

}
