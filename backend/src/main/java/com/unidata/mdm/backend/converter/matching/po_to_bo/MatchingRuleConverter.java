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

package com.unidata.mdm.backend.converter.matching.po_to_bo;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.MatchingAlgorithmPO;
import com.unidata.mdm.backend.po.matching.MatchingRulePO;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;

@ConverterQualifier
@Component
public class MatchingRuleConverter implements Converter<MatchingRulePO, MatchingRule> {

    @Autowired
    private Converter<MatchingAlgorithmPO, MatchingAlgorithm> converter;

    @Override
    public MatchingRule convert(MatchingRulePO source) {
        MatchingRule matchingRule = new MatchingRule();
        matchingRule.setId(source.getId());
        matchingRule.setName(source.getName());
        matchingRule.setEntityName(source.getEntityName());
        matchingRule.setActive(source.isActive());
        matchingRule.setWithPreprocessing(source.isWithPreprocessing());
        matchingRule.setAutoMerge(source.isAutoMerge());
        matchingRule.setDescription(source.getDescription());
        Collection<MatchingAlgorithmPO> algos = source.getMatchingAlgorithms();
        matchingRule.setMatchingAlgorithms(isEmpty(algos) ? null : algos.stream().map(converter::convert).collect(toList()));
        //set settings!
        return matchingRule;
    }

}
