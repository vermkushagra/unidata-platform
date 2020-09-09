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

package com.unidata.mdm.backend.converter.matching.bo_to_dto;

import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.matching.MatchingAlgorithmRO;
import com.unidata.mdm.backend.api.rest.dto.matching.MatchingFieldRO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingField;

@ConverterQualifier
@Component
public class MatchingAlgorithmConverterDto implements Converter<MatchingAlgorithm ,MatchingAlgorithmRO> {

    @Autowired
    private Converter<MatchingField ,MatchingFieldRO> fieldConverter;

    @Override
    public MatchingAlgorithmRO convert(MatchingAlgorithm source) {
        if (CollectionUtils.isEmpty(source.getMatchingFields())) {
            throw new RuntimeException();
        }
        MatchingAlgorithmRO matchingAlgorithm = new MatchingAlgorithmRO();
        matchingAlgorithm.setId(source.getId());
        matchingAlgorithm.setName(source.getName());
        matchingAlgorithm.setDescription(source.getDescription());
        matchingAlgorithm.setPreprocessingAvailable(source.isExactMatching());
        matchingAlgorithm.setMatchingFields(source.getMatchingFields() == null ? Collections.emptyList() : source.getMatchingFields().stream().map(fieldConverter::convert).collect(Collectors.toList()));
        return matchingAlgorithm;
    }
}