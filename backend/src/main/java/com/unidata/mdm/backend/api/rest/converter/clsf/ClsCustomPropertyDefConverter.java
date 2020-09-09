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

package com.unidata.mdm.backend.api.rest.converter.clsf;

import java.util.Collection;
import java.util.List;

import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import com.unidata.mdm.classifier.CustomPropertyDef;


import static java.util.stream.Collectors.toList;

public class ClsCustomPropertyDefConverter {

    public static List<CustomPropertyDef> convert(final Collection<CustomPropertyDefinition> customProperties) {
        return customProperties == null
                ? null
                : customProperties.stream().map(ClsCustomPropertyDefConverter::convert).collect(toList());
    }

    public static CustomPropertyDef convert(final CustomPropertyDefinition customPropertyDefinition) {
        return new CustomPropertyDef()
                .withName(customPropertyDefinition.getName())
                .withValue(customPropertyDefinition.getValue());
    }


    public static List<CustomPropertyDefinition> convertTo(final Collection<CustomPropertyDef> customProperties) {
        return customProperties == null
                ? null
                : customProperties.stream().map(ClsCustomPropertyDefConverter::convertTo).collect(toList());
    }

    public static CustomPropertyDefinition convertTo(final CustomPropertyDef customPropertyDefinition) {
        return new CustomPropertyDefinition(customPropertyDefinition.getName(), customPropertyDefinition.getValue());

    }

}
