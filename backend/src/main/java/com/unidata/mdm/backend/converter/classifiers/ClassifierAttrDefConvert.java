package com.unidata.mdm.backend.converter.classifiers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.ClassifierValueDef;
import com.unidata.mdm.classifier.ClassifierValueType;
import com.unidata.mdm.classifier.SimpleAttributeWithOptionalValueDef;

@ConverterQualifier
@Component
public class ClassifierAttrDefConvert implements Converter<ClsfNodeAttrDTO, SimpleAttributeWithOptionalValueDef> {

    private final Converter<ClsfNodeAttrDTO, ClassifierValueDef> attrConverter;

    @Autowired
    public ClassifierAttrDefConvert(final Converter<ClsfNodeAttrDTO, ClassifierValueDef> attrConverter) {
        this.attrConverter = attrConverter;
    }

    @Override
    public SimpleAttributeWithOptionalValueDef convert(ClsfNodeAttrDTO source) {
        SimpleAttributeWithOptionalValueDef attr = new SimpleAttributeWithOptionalValueDef();
        attr.setName(source.getAttrName());
        attr.setDisplayName(source.getDisplayName());
        attr.setDescription(source.getDescription());
        attr.setUnique(source.isUnique());
        attr.setDisplayable(false);
        attr.setHidden(source.isHidden());
        attr.setMainDisplayable(false);
        attr.setReadOnly(source.isReadOnly());
        attr.setSearchable(source.isSearchable());
        attr.setNullable(source.isNullable());
        attr.setMask(null);
        attr.setOrder(null);
        if (source.getDataType() != null) {
            attr.setValueType(ClassifierValueType.valueOf(source.getDataType().name()));
            attr.setValue(attrConverter.convert(source));
        }
        return attr;
    }
}
