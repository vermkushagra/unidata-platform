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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierCustomProperty;
import com.unidata.mdm.backend.util.JsonUtils;

/**
 * @author Dmitry Kopin on 25.05.2018.
 */
public class ClsfCustomPropertyPOToCachedClassifierPropertyConverter {

    private ClsfCustomPropertyPOToCachedClassifierPropertyConverter() {
        super();
    }

    public static String convert(final CachedClassifierCustomProperty[] customProperties) {
        return JsonUtils.write(customProperties);
    }

    public static CachedClassifierCustomProperty[] convert(final String customProperties) {
        return JsonUtils.read(customProperties, CachedClassifierCustomProperty[].class);
    }

    public static List<CustomPropertyDefinition> convertToDTO(CachedClassifierCustomProperty[] source) {

        if (source == null || source.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.stream(source)
                .map(s -> new CustomPropertyDefinition(s.getName(), s.getValue()))
                .collect(Collectors.toList());
    }

    public static CachedClassifierCustomProperty[] convert(List<CustomPropertyDefinition> properties) {

        if (CollectionUtils.isEmpty(properties)) {
            return null;
        }

        return properties.stream()
            .map(p -> new CachedClassifierCustomProperty(p.getName(), p.getValue()))
            .toArray(sz -> new CachedClassifierCustomProperty[sz]);
    }
}
