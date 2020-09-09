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

package com.unidata.mdm.backend.service.job.duplicates;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.service.job.JobParameterValidator;
import com.unidata.mdm.backend.service.matching.MatchingRulesService;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.util.MessageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Job parameter validator for matching rule
 * Validate matching rule with check preprocessing mode and entity name parameter
 *
 * @author Dmitry Kopin on 23.08.2018.
 */
public class DuplicateJobMatchingRuleParameterValidator implements JobParameterValidator {

    private final static String INCORRECT_PREPROCESSING = "app.job.merge.duplicates.parameters.matchingRule.incorrectPreprocessing";

    private final static String INCORRECT_MATCHING_RULE = "app.job.merge.duplicates.parameters.matchingRule.incorrect";

    private final static String INCORRECT_ENTITY_NAME = "app.job.merge.duplicates.parameters.entityName.incorrect";

    @Autowired
    private MatchingRulesService matchingRulesService;

    @Override
    public List<String> validate(String paramName, Collection<JobParameterDTO> jobParameters) {
        if (CollectionUtils.isNotEmpty(jobParameters)) {
            JobParameterDTO jobParameter = jobParameters.stream()
                    .filter(param -> param.getName().equals(paramName))
                    .findFirst()
                    .orElse(null);
            if (jobParameter == null) {
                return Collections.emptyList();
            }

            if (StringUtils.isEmpty(jobParameter.getStringValue())) {
                return Collections.singletonList(MessageUtils.getMessage(INCORRECT_MATCHING_RULE));
            }
            String matchingRuleName = jobParameter.getStringValue();

            JobParameterDTO entityNameParam = jobParameters.stream()
                    .filter(param -> param.getName().equals("entityName"))
                    .findFirst()
                    .orElseGet(null);
            String entityName = entityNameParam == null ? null : entityNameParam.getStringValue();

            if (StringUtils.isEmpty(entityName) || entityName.equals("ALL")) {
                return Collections.singletonList(MessageUtils.getMessage(INCORRECT_ENTITY_NAME));
            }

            MatchingRule rule = matchingRulesService.getMatchingRule(entityName, matchingRuleName);
            if (rule == null) {
                return Collections.singletonList(MessageUtils.getMessage(INCORRECT_MATCHING_RULE));
            }

            JobParameterDTO usePreprocessing = jobParameters.stream()
                    .filter(param -> param.getName().equals("usePreprocessing"))
                    .findFirst()
                    .orElseGet(null);
            if (usePreprocessing != null
                    && BooleanUtils.isTrue(usePreprocessing.getBooleanValue())
                    && !rule.isWithPreprocessing()) {
                return Collections.singletonList(MessageUtils.getMessage(INCORRECT_PREPROCESSING));
            }

        }
        return Collections.emptyList();
    }
}
