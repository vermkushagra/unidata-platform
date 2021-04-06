package com.unidata.mdm.backend.converter.classifiers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.SimpleAttributeWithOptionalValueDef;

@ConverterQualifier
@Component
public class SimpleAttrDefConverter implements Converter<SimpleAttributeWithOptionalValueDef, ClsfNodeAttrDTO> {

    @Autowired
    private Converter<SimpleAttributeWithOptionalValueDef, Object> attrConverter;

    @Override
    public ClsfNodeAttrDTO convert(SimpleAttributeWithOptionalValueDef source) {
    	ClsfNodeAttrDTO attr = new ClsfNodeAttrDTO();
        attr.setAttrName(source.getName());
        attr.setDisplayName(source.getDisplayName());
        attr.setDescription(source.getDescription());
        attr.setUnique(source.isUnique());
        attr.setHidden(source.isHidden());
        attr.setReadOnly(source.isReadOnly());
        attr.setNullable(source.isNullable());
        attr.setSearchable(source.isSearchable());
        attr.setDataType(DataType.valueOf(source.getValueType().name()));
        Object value = attrConverter.convert(source);
        attr.setDefaultValue(value);
        return attr;
    }
}
