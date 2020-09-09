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
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.collections.Maps;
import com.unidata.mdm.classifier.AbstractClassifierAttributeDef;
import com.unidata.mdm.classifier.ArrayAttributeWithOptionalValueDef;
import com.unidata.mdm.classifier.ClassifierLookupValueType;
import com.unidata.mdm.classifier.ClassifierValueType;
import com.unidata.mdm.classifier.SimpleAttributeWithOptionalValueDef;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

@ConverterQualifier
@Component
public class ClassifierAttrDefConvert implements Converter<ClsfNodeAttrDTO, AbstractClassifierAttributeDef> {

    private static final Map<Class<? extends ClsfNodeAttrDTO>, Supplier<AbstractClassifierAttributeDef>> ATTR_SUPPLIER_MAPPING = Maps.of(
            ClsfNodeArrayAttrDTO.class, ArrayAttributeWithOptionalValueDef::new,
            ClsfNodeSimpleAttrDTO.class, SimpleAttributeWithOptionalValueDef::new
    );

    @Override
    public AbstractClassifierAttributeDef convert(ClsfNodeAttrDTO source) {
        AbstractClassifierAttributeDef attr = ATTR_SUPPLIER_MAPPING.get(source.getClass()).get();
        attr.setName(source.getAttrName());
        attr.setDisplayName(source.getDisplayName());
        attr.setDescription(source.getDescription());
        attr.setDisplayable(false);
        attr.setHidden(source.isHidden());
        attr.setMainDisplayable(false);
        attr.setReadOnly(source.isReadOnly());
        attr.setSearchable(source.isSearchable());
        if (source.getDataType() != null) {
            attr.setValueType(ClassifierValueType.valueOf(source.getDataType().name()));

        }
        attr.setLookupEntityType(source.getLookupEntityType());
        if (source.getLookupEntityCodeAttributeType() != null) {
            attr.setLookupEntityCodeAttributeType(
                    ClassifierLookupValueType.valueOf(source.getLookupEntityCodeAttributeType().name())
            );
        }
        if (CollectionUtils.isNotEmpty(source.getCustomProperties())) {
            attr.withCustomProperties(ClsCustomPropertyDefConverter.convert(source.getCustomProperties()));
        }
        return attr;
    }
}
