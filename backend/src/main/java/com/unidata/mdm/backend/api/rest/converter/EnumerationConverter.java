package com.unidata.mdm.backend.api.rest.converter;

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.meta.EnumerationDefinitionRO;
import com.unidata.mdm.backend.api.rest.dto.meta.EnumerationValueRO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.meta.EnumerationDataType;

/**
 * The Class EnumerationConverter.
 */
@ConverterQualifier
@Component
public class EnumerationConverter implements Converter<EnumerationDataType, EnumerationDefinitionRO> {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.core.convert.converter.Converter#convert(java.lang
     * .Object)
     */
    @Override
    public EnumerationDefinitionRO convert(EnumerationDataType source) {
        if (source == null) {
            return null;
        }
        EnumerationDefinitionRO target = new EnumerationDefinitionRO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        List<com.unidata.mdm.meta.EnumerationValue> enumerationValues = source.getEnumVal();
        for (com.unidata.mdm.meta.EnumerationValue enumerationValue : enumerationValues) {
            target.addValue(convertEnumerationValue(enumerationValue));
        }
        return target;
    }

    /**
     * Convert from {@link com.unidata.mdm.meta.EnumerationValue} to
     * {@link EnumerationValueRO}.
     *
     * @param source
     *            convert from
     * @return converted value
     */
    private EnumerationValueRO convertEnumerationValue(com.unidata.mdm.meta.EnumerationValue source) {
        if (source == null) {
            return null;
        }
        EnumerationValueRO target = new EnumerationValueRO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        return target;
    }
}
