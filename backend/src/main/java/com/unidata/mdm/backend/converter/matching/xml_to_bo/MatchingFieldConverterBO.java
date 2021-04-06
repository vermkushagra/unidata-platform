package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingField;
import com.unidata.mdm.match.MatchingFieldDef;

@ConverterQualifier
@Component
public class MatchingFieldConverterBO implements Converter<MatchingFieldDef, MatchingField> {

    @Override
    public MatchingField convert(MatchingFieldDef source) {
        MatchingField matchingField = new MatchingField();
        matchingField.setId(source.getIdentifier());
        matchingField.setAttrName(source.getAttributeName());
        matchingField.setConstantField(source.isConstantField());
        matchingField.setDescription(source.getDescription());
        return matchingField;
    }
}
