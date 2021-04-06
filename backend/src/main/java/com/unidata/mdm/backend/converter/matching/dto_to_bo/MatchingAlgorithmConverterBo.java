package com.unidata.mdm.backend.converter.matching.dto_to_bo;

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
public class MatchingAlgorithmConverterBo implements Converter<MatchingAlgorithmRO, MatchingAlgorithm> {

    @Autowired
    private Converter<MatchingFieldRO, MatchingField> fieldConverter;

    @Override
    public MatchingAlgorithm convert(MatchingAlgorithmRO source) {
        MatchingAlgorithm matchingAlgorithm = new MatchingAlgorithm();
        matchingAlgorithm.setName(source.getName());
        matchingAlgorithm.setId(source.getId());
        matchingAlgorithm.setDescription(source.getDescription());
        matchingAlgorithm.setMatchingFields(source.getMatchingFields() == null ? Collections.emptyList() : source.getMatchingFields().stream().map(fieldConverter::convert).collect(Collectors.toList()));
        return matchingAlgorithm;
    }
}
