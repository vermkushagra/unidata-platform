package com.unidata.mdm.backend.converter.matching.bo_to_dto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.matching.MatchingFieldRO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingField;

@ConverterQualifier
@Component
public class MatchingFieldConverterDto implements Converter<MatchingField, MatchingFieldRO> {

    @Override
    public MatchingFieldRO convert(MatchingField source) {
        MatchingFieldRO matchingField = new MatchingFieldRO();
        matchingField.setId(source.getId());
        matchingField.setName(source.getAttrName());
        matchingField.setConstantField(source.isConstantField());
        matchingField.setDescription(source.getDescription());
        return matchingField;
    }
}