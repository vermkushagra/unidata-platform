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

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.ClassifierDef;

@ConverterQualifier
@Component
public class ClassifierToDefConverter implements Converter<ClsfDTO, ClassifierDef> {

    @Override
    public ClassifierDef convert(ClsfDTO source) {
        ClassifierDef classifierDef = new ClassifierDef();
        classifierDef.setCodePattern(source.getCodePattern());
        classifierDef.setName(source.getName());
        classifierDef.setDisplayName(source.getDisplayName());
        classifierDef.setDescription(source.getDescription());
        classifierDef.setValidateCodeByLevel(source.isValidateCodeByLevel());
        return classifierDef;
    }
}
