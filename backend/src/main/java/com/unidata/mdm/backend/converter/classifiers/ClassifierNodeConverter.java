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

import java.util.List;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.api.rest.converter.clsf.ClsCustomPropertyDefConverter;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.classifier.ArrayAttributeWithOptionalValueDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.ClassifierNodeDef;
import com.unidata.mdm.classifier.SimpleAttributeWithOptionalValueDef;


/**
 * The Class ClassifierNodeConverter.
 */
@ConverterQualifier
@Component
public class ClassifierNodeConverter implements Converter<ClsfNodeDTO, ClassifierNodeDef> {

    /** The simple attr converter. */
    @Autowired
    private Converter<ClsfNodeSimpleAttrDTO, SimpleAttributeWithOptionalValueDef> simpleAttrConverter;

    /** The array attr converter. */
    @Autowired
    private Converter<ClsfNodeArrayAttrDTO, ArrayAttributeWithOptionalValueDef> arrayAttrConverter;

    /* (non-Javadoc)
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public ClassifierNodeDef convert(ClsfNodeDTO source) {
        ClassifierNodeDef nodeDef = new ClassifierNodeDef();
        nodeDef.setDescription(source.getDescription());
        nodeDef.setName(source.getName());
        nodeDef.setCode(source.getCode());
        nodeDef.setClassifierName(source.getClsfName());
        nodeDef.setId(source.getNodeId());
        nodeDef.setParentId(source.getParentId());
        List<SimpleAttributeWithOptionalValueDef> attrs = source.getNodeSimpleAttrs().stream()
                .map(simpleAttrConverter::convert)
                .collect(Collectors.toList());
        nodeDef.withAttributes(attrs);
        List<ArrayAttributeWithOptionalValueDef> arrayAttrs = source.getNodeArrayAttrs().stream()
                .map(arrayAttrConverter::convert)
                .collect(Collectors.toList());
        nodeDef.withArrayAttributes(arrayAttrs);
        nodeDef.withCustomProperties(ClsCustomPropertyDefConverter.convert(source.getCustomProperties()));
        return nodeDef;
    }
}
