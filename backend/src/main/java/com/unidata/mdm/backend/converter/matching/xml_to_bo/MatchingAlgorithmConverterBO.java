package com.unidata.mdm.backend.converter.matching.xml_to_bo;

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
public class MatchingAlgorithmConverterBO implements Converter<MatchingAlgorithmDef, MatchingAlgorithm> {

    @Autowired
    private Converter<MatchingFieldDef, MatchingField> converter;

    @Override
    public MatchingAlgorithm convert(MatchingAlgorithmDef source) {
        if (source.getFields().isEmpty()) {
            throw new RuntimeException();
        }
        MatchingAlgorithm matchingAlgorithm = new MatchingAlgorithm();
        matchingAlgorithm.setName(source.getName());
        matchingAlgorithm.setDescription(source.getDescription());
        matchingAlgorithm.setMatchingFields(source.getFields() == null ? Collections.emptyList() : source.getFields().stream().map(converter::convert).collect(Collectors.toList()));
        return matchingAlgorithm;
    }
}
