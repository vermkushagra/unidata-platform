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

package com.unidata.mdm.backend.converter.classifiers;

import com.unidata.mdm.backend.api.rest.converter.clsf.ClsCustomPropertyDefConverter;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.classifier.ClassifierValueDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.SimpleAttributeWithOptionalValueDef;

@ConverterQualifier
@Component
public class SimpleAttrDefConverter implements Converter<SimpleAttributeWithOptionalValueDef, ClsfNodeSimpleAttrDTO> {

    @Autowired
    private Converter<ClassifierValueDef, Object> attrConverter;

    @Override
    public ClsfNodeSimpleAttrDTO convert(SimpleAttributeWithOptionalValueDef source) {
        ClsfNodeSimpleAttrDTO attr = new ClsfNodeSimpleAttrDTO();
        attr.setAttrName(source.getName());
        attr.setDisplayName(source.getDisplayName());
        attr.setDescription(source.getDescription());
        attr.setUnique(source.isUnique());
        attr.setHidden(source.isHidden());
        attr.setReadOnly(source.isReadOnly());
        attr.setNullable(source.isNullable());
        attr.setSearchable(source.isSearchable());
        if(source.getValueType() != null) {
            attr.setDataType(DataType.valueOf(source.getValueType().name()));
        }
        Object value = attrConverter.convert(source.getValue());
        attr.setDefaultValue(value);
        attr.setEnumDataType(source.getEnumDataType());
        if (source.getOrder() != null) {
            attr.setOrder(source.getOrder().intValue());
        }

        attr.setLookupEntityType(source.getLookupEntityType());
        if (source.getLookupEntityCodeAttributeType() != null) {
            attr.setLookupEntityCodeAttributeType(CodeAttribute.CodeDataType
                    .valueOf(source.getLookupEntityCodeAttributeType().name()));
        }
        attr.setCustomProperties(ClsCustomPropertyDefConverter.convertTo(source.getCustomProperties()));
        return attr;
    }
}
