package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingField;
import com.unidata.mdm.match.MatchingAlgorithmDef;
import com.unidata.mdm.match.MatchingFieldDef;

@ConverterQualifier
@Component
public class MatchingAlgorithmConverterDef implements Converter<MatchingAlgorithm, MatchingAlgorithmDef> {

    @Autowired
    private Converter<MatchingField, MatchingFieldDef> fieldConverter;

    @Override
    public MatchingAlgorithmDef convert(MatchingAlgorithm source) {
        if (source.getMatchingFields() == null || source.getMatchingFields().isEmpty()) {
            throw new RuntimeException();
        }
        MatchingAlgorithmDef matchingAlgorithm = new MatchingAlgorithmDef();
        matchingAlgorithm.setName(source.getName());
        matchingAlgorithm.setDescription(source.getDescription());
        matchingAlgorithm.withFields(source.getMatchingFields() == null ? Collections.emptyList() : source.getMatchingFields().stream().map(fieldConverter::convert).collect(Collectors.toList()));
        return matchingAlgorithm;
    }
}
