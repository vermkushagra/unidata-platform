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

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.AbstractClassifierAttributeDef;
import com.unidata.mdm.classifier.ClassifierValueDef;
import com.unidata.mdm.classifier.SimpleAttributeWithOptionalValueDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@ConverterQualifier
@Component
public class ClassifierSimpleAttrDefConvert implements Converter<ClsfNodeSimpleAttrDTO, SimpleAttributeWithOptionalValueDef> {

    private final Converter<ClsfNodeSimpleAttrDTO, ClassifierValueDef> attrConverter;

    @Autowired
    private Converter<ClsfNodeAttrDTO, AbstractClassifierAttributeDef> superConverter;

    @Autowired
    public ClassifierSimpleAttrDefConvert(final Converter<ClsfNodeSimpleAttrDTO, ClassifierValueDef> attrConverter) {
        this.attrConverter = attrConverter;
    }

    @Override
    public SimpleAttributeWithOptionalValueDef convert(ClsfNodeSimpleAttrDTO source) {
        SimpleAttributeWithOptionalValueDef attr = (SimpleAttributeWithOptionalValueDef) superConverter.convert(source);
        attr.setUnique(source.isUnique());
        attr.setNullable(source.isNullable());
        attr.setEnumDataType(source.getEnumDataType());
        attr.setMask(null);
        attr.setOrder(null);

        if (source.getDefaultValue() != null) {
            attr.setValue(attrConverter.convert(source));
        }
        return attr;
    }
}
