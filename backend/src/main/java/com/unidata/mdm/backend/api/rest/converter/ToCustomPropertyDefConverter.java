package com.unidata.mdm.backend.api.rest.converter;

import java.util.Collection;

import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import com.unidata.mdm.meta.CustomPropertyDef;

import static java.util.stream.Collectors.toList;

public class ToCustomPropertyDefConverter {

    public static Collection<CustomPropertyDef> convert(final Collection<CustomPropertyDefinition> customProperties) {
        return customProperties.stream().map(ToCustomPropertyDefConverter::convert).collect(toList());
    }

    public static CustomPropertyDef convert(final CustomPropertyDefinition customPropertyDefinition) {
        return new CustomPropertyDef()
                .withName(customPropertyDefinition.getName())
                .withValue(customPropertyDefinition.getValue());
    }

}
