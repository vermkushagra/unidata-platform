package com.unidata.mdm.backend.converter.matching.bo_to_dto;

import java.util.Collections;
import java.util.stream.Collectors;

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
        if (source.getMatchingFields() == null || source.getMatchingFields().isEmpty()) {
            throw new RuntimeException();
        }
        MatchingAlgorithmRO matchingAlgorithm = new MatchingAlgorithmRO();
        matchingAlgorithm.setId(source.getId());
        matchingAlgorithm.setName(source.getName());
        matchingAlgorithm.setDescription(source.getDescription());
        matchingAlgorithm.setMatchingFields(source.getMatchingFields() == null ? Collections.emptyList() : source.getMatchingFields().stream().map(fieldConverter::convert).collect(Collectors.toList()));
        return matchingAlgorithm;
    }
}