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

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.MatchingAlgorithmService;
import com.unidata.mdm.backend.service.matching.algorithms.Algorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingField;
import com.unidata.mdm.match.MatchingFieldDef;
import com.unidata.mdm.match.UserMatchingAlgorithmDef;

@ConverterQualifier
@Component
public class UserMatchingAlgorithmConverterBO implements Converter<UserMatchingAlgorithmDef, MatchingAlgorithm> {

    @Autowired
    private Converter<MatchingFieldDef, MatchingField> converter;

    @Autowired
    private MatchingAlgorithmService matchingAlgorithmService;


    @Override
    public MatchingAlgorithm convert(UserMatchingAlgorithmDef source) {
        MatchingAlgorithm matchingAlgorithm = new MatchingAlgorithm();
        Integer algorithmId = source.getAlgorithmId() == null ? null : source.getAlgorithmId().intValue();
        Algorithm algorithm = matchingAlgorithmService.getAlgorithmById(algorithmId);
        if (algorithm != null) {
      
        matchingAlgorithm.setName(algorithm.getAlgorithmName());
        matchingAlgorithm.setDescription(algorithm.getAlgorithmDescription());
        matchingAlgorithm.setId(algorithm.getAlgorithmId());
        matchingAlgorithm.setMatchingFields(source.getFields() == null ?
                Collections.emptyList() :
                source.getFields().stream().map(converter::convert).collect(Collectors.toList()));
        }
        return matchingAlgorithm;
    }
}
