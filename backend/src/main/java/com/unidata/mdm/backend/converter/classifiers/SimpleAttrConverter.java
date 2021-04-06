package com.unidata.mdm.backend.converter.classifiers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.classifier.ClassifierValueDef;
import com.unidata.mdm.classifier.SimpleAttributeWithOptionalValueDef;

@ConverterQualifier
@Component
public class SimpleAttrConverter implements Converter<SimpleAttributeWithOptionalValueDef, Object> {

    @Override
    public Object convert(SimpleAttributeWithOptionalValueDef source) {
        ClassifierValueDef simpleAttribute = source.getValue();
        if (simpleAttribute == null || simpleAttribute.getType() == null) {
            return null;
        }

        switch (simpleAttribute.getType()) {
            case BOOLEAN:
                return simpleAttribute.isBoolValue();
            case DATE:
                return ConvertUtils.localDate2Date(simpleAttribute.getDateValue());
            case TIME:
                return ConvertUtils.localTime2Date(simpleAttribute.getTimeValue());
            case TIMESTAMP:
                return ConvertUtils.localDateTime2Date(simpleAttribute.getTimestampValue());
            case INTEGER:
                return simpleAttribute.getIntValue();
            case NUMBER:
                return simpleAttribute.getNumberValue();
            case STRING:
                return simpleAttribute.getStringValue();
            default:
                return null;
        }
    }
}