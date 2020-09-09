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

package com.unidata.mdm.backend.service.classifier.converters;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import com.unidata.mdm.backend.po.MetaModelCustomPropertyPO;
import com.unidata.mdm.backend.util.JsonUtils;
import com.unidata.mdm.classifier.CustomPropertyDef;

/**
 * @author Dmitry Kopin on 25.05.2018.
 */
public class ClsfCustomPropertyToPOConverter {

    private ClsfCustomPropertyToPOConverter() {
        super();
    }

    private static final TypeReference<List<CustomPropertyDefinition>> PROPERTIES_DTO_TYPE_REFERENCE
        = new TypeReference<List<CustomPropertyDefinition>>(){};

    private static final TypeReference<List<CustomPropertyDef>> PROPERTIES_DEF_TYPE_REFERENCE
        = new TypeReference<List<CustomPropertyDef>>(){};

    public static String convert(final Collection<CustomPropertyDefinition> customProperties) {
        return JsonUtils.write(customProperties);
    }

    public static List<CustomPropertyDefinition> convert(final String customProperties) {
        return JsonUtils.read(customProperties, PROPERTIES_DTO_TYPE_REFERENCE);
    }

    public static MetaModelCustomPropertyPO convert(final CustomPropertyDefinition customPropertyDefinition) {
        return new MetaModelCustomPropertyPO(customPropertyDefinition.getName(), customPropertyDefinition.getValue());
    }

    public static List<CustomPropertyDef> convertToDef(final String customProperties) {
        return JsonUtils.read(customProperties, PROPERTIES_DEF_TYPE_REFERENCE);
    }

}
