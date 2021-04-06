package com.unidata.mdm.backend.converter.matching.dto_to_bo;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.matching.MatchingFieldRO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingField;

@ConverterQualifier
@Component
public class MatchingFieldConverterBo implements Converter<MatchingFieldRO, MatchingField> {

    @Override
    public MatchingField convert(MatchingFieldRO source) {
        MatchingField matchingField = new MatchingField();
        matchingField.setId(source.getId());
        matchingField.setAttrName(source.getName());
        matchingField.setConstantField(source.isConstantField());
        matchingField.setDescription(source.getDescription());
        return matchingField;
    }
}
