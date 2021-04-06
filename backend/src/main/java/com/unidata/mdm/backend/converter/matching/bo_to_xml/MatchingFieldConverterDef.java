package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingField;
import com.unidata.mdm.match.MatchingFieldDef;

@ConverterQualifier
@Component
public class MatchingFieldConverterDef implements Converter<MatchingField, MatchingFieldDef> {

    @Override
    public MatchingFieldDef convert(MatchingField source) {
        MatchingFieldDef matchingField = new MatchingFieldDef();
        matchingField.setIdentifier(source.getId());
        matchingField.setAttributeName(source.getAttrName());
        matchingField.setDescription(source.getDescription());
        matchingField.setConstantField(source.isConstantField());
        return matchingField;
    }
}
