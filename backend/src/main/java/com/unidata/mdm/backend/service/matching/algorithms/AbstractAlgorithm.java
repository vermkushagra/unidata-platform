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

package com.unidata.mdm.backend.service.matching.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Required;

import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingField;
import com.unidata.mdm.backend.util.MessageUtils;

public abstract class AbstractAlgorithm implements Algorithm {

    private String algorithmName;
    private String algorithmDescription;
    private Map<Integer, String> matchingFieldMap;
    private Map<Integer, String> matchingConstantMap;

    @Nonnull
    @Override
    public MatchingAlgorithm getTemplate() {
        MatchingAlgorithm template = new MatchingAlgorithm();
        template.setId(getAlgorithmId());
        template.setName(getAlgorithmName());
        template.setDescription(getAlgorithmDescription());
        template.setAlgorithmImpl(this);
        Collection<MatchingField> matchingFields = new ArrayList<>(matchingFieldMap.size());

        matchingFieldMap.forEach((key, value) -> {
            MatchingField matchingField = new MatchingField();
            matchingField.setId(key);
            matchingField.setDescription(MessageUtils.getMessage(value));
            matchingField.setConstantField(false);
            matchingFields.add(matchingField);
        });
        if (MapUtils.isNotEmpty(matchingConstantMap)) {
            matchingConstantMap.forEach((key, value) -> {
                MatchingField matchingField = new MatchingField();
                matchingField.setId(key);
                matchingField.setDescription(MessageUtils.getMessage(value));
                matchingField.setConstantField(true);
                matchingFields.add(matchingField);
            });
        }

        template.setMatchingFields(matchingFields);
        return template;
    }


    @Nonnull
    @Override
    public String getAlgorithmName() {
        return MessageUtils.getMessage(algorithmName);
    }

    @Required
    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    @Nonnull
    @Override
    public String getAlgorithmDescription() {
        return MessageUtils.getMessage(algorithmDescription);
    }

    @Required
    public void setAlgorithmDescription(String algorithmDescription) {
        this.algorithmDescription = algorithmDescription;
    }

    @Required
    public void setMatchingFieldMap(Map<Integer, String> matchingFieldMap) {
        this.matchingFieldMap = matchingFieldMap;
    }

    public Map<Integer, String> getMatchingFieldMap() {
        return matchingFieldMap;
    }

    public void setMatchingConstantMap(Map<Integer, String> matchingConstantMap) {
        this.matchingConstantMap = matchingConstantMap;
    }

    public Map<Integer, String> getMatchingConstantMap() {
        return matchingConstantMap;
    }
}
