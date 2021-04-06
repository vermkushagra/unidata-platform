package com.unidata.mdm.backend.api.rest.converter;

import java.math.BigInteger;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.meta.SourceSystemDef;
import com.unidata.mdm.backend.api.rest.dto.meta.SourceSystemDefinition;

/**
 * 
 * Converts {@see SourceSystemDefinition} to {@see SourceSystemDef}.
 */
@ConverterQualifier
@Component
public class SourceSystemDefinitionToDefConverter implements Converter<SourceSystemDefinition, SourceSystemDef> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.core.convert.converter.Converter#convert(java.lang
     * .Object)
     */
    @Override
    public SourceSystemDef convert(SourceSystemDefinition source) {
        if (source == null) {
            return null;
        }
        SourceSystemDef target = new SourceSystemDef();
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setWeight(BigInteger.valueOf(source.getWeight()));
        target.withCustomProperties(ToCustomPropertyDefConverter.convert(source.getCustomProperties()));
        return target;
    }

}
