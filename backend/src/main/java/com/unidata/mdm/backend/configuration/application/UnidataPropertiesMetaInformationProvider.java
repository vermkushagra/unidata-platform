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

package com.unidata.mdm.backend.configuration.application;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.dto.configuration.ConfigurationPropertyAvailableValueDTO;
import com.unidata.mdm.backend.common.dto.configuration.ConfigurationPropertyMetaDTO;
import com.unidata.mdm.backend.common.configuration.application.PropertiesMetaInformationProvider;
import com.unidata.mdm.backend.util.MessageUtils;
import org.springframework.stereotype.Service;

@Service
public class UnidataPropertiesMetaInformationProvider implements PropertiesMetaInformationProvider {

    @Override
    public Collection<ConfigurationPropertyMetaDTO> properties() {
        return Arrays.stream(UnidataConfigurationProperty.values())
                .map(property ->
                        new ConfigurationPropertyMetaDTO<>(
                                property.getKey(),
                                () -> MessageUtils.getMessage(property.getKey()),
                                property.getGroupKey(),
                                () -> MessageUtils.getMessage(property.getGroupKey()),
                                property.getPropertyType(),
                                property.getDefaultValue().orElse(null),
                                property.getAvailableValues().stream()
                                        .map(value ->
                                                new ConfigurationPropertyAvailableValueDTO<>(
                                                        value.getLeft(),
                                                        MessageUtils.getMessage(value.getRight())
                                                )
                                        )
                                        .collect(Collectors.toList()),
                                property.isRequired(),
                                property.isReadonly(),
                                property.getValidator(),
                                property.getDeserializer()
                        )
                )
                .collect(Collectors.toList());
    }
}
