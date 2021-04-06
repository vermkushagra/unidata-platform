package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import static java.util.stream.Collectors.toList;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingField;
import com.unidata.mdm.match.MatchingFieldDef;
import com.unidata.mdm.match.UserMatchingAlgorithmDef;

@ConverterQualifier
@Component
public class UserMatchingAlgorithmConverterDef implements Converter<MatchingAlgorithm, UserMatchingAlgorithmDef> {

    @Autowired
    private Converter<MatchingField, MatchingFieldDef> fieldConverter;

    @Override
    public UserMatchingAlgorithmDef convert(MatchingAlgorithm source) {
        UserMatchingAlgorithmDef algorithmDef = new UserMatchingAlgorithmDef();
        algorithmDef.setAlgorithmId(BigInteger.valueOf(source.getId()));
        algorithmDef.withFields(source.getMatchingFields().stream().map(fieldConverter::convert).collect(toList()));
        return algorithmDef;
    }
}
