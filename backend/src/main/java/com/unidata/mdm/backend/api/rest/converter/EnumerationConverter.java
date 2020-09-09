/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
