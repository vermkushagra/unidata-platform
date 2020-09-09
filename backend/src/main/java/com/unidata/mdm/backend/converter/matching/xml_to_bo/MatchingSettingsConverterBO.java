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
import com.unidata.mdm.backend.service.matching.data.MatchingKey;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import com.unidata.mdm.match.MatchingRuleDef;
import com.unidata.mdm.match.MatchingSettingsDef;

@ConverterQualifier
@Component
public class MatchingSettingsConverterBO implements Converter<MatchingSettingsDef, MatchingUserSettings> {

    @Autowired
    private Converter<MatchingRuleDef, MatchingRule> ruleConverter;

    @Override
    public MatchingUserSettings convert(MatchingSettingsDef source) {
        MatchingUserSettings matchingUserSettings = new MatchingUserSettings();
        matchingUserSettings.setMatchingRules(
                source.getUserRules().stream().map(ruleConverter::convert).collect(toList()));

        Map<String, Collection<MatchingKey>> keys = new HashMap<>();
        matchingUserSettings.setMatchingKeys(keys);
        return matchingUserSettings;
    }


}
