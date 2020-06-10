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

package com.unidata.mdm.backend.common.dto.configuration;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.unidata.mdm.backend.common.configuration.application.ValueValidators;
import org.apache.commons.lang3.StringUtils;

public enum ConfigurationPropertyTypeDTO {
    STRING("String", ValueValidators.STRING_VALIDATOR, Function.identity()),
    INTEGER("Integer", ValueValidators.INT_VALIDATOR, Integer::valueOf),
    NUMBER("Number", ValueValidators.DOUBLE_VALIDATOR, Double::valueOf),
    BOOLEAN("Boolean", ValueValidators.BOOLEAN_VALIDATOR, Boolean::valueOf);

    private final String value;

    private final Predicate<Optional<String>> validator;

    private final Function<String, ? extends Serializable> deserializer;

    ConfigurationPropertyTypeDTO(
            final String value,
            final Predicate<Optional<String>> validator,
            final Function<String, ? extends Serializable> deserializer
    ) {
        this.value = value;
        this.validator = validator;
        this.deserializer = deserializer;
    }

    public String value() {
        return value;
    }

    public Predicate<Optional<String>> getValidator() {
        return validator;
    }

    public Function<String, ? extends Serializable> getDeserializer() {
        return deserializer;
    }

    public static ConfigurationPropertyTypeDTO fromValue(String v) {
        for (ConfigurationPropertyTypeDTO c : ConfigurationPropertyTypeDTO.values()) {
            if (StringUtils.equalsIgnoreCase(v, c.value())) {
                return c;
            }
        }
        return null;
    }
}
