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

package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.meta.EntityInfoDefinition;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.meta.AbstractEntityDef;

/**
 * @author Michael Yashin. Created on 26.05.2015.
 */
@ConverterQualifier
@Component
public class AbstractEntityDefToEntityListElementConverter implements Converter<AbstractEntityDef, EntityInfoDefinition> {

    public static<T extends AbstractEntityDef> List<EntityInfoDefinition> to(List<T> source) {

        if (CollectionUtils.isEmpty(source)) {
            Collections.emptyList();
        }

        List<EntityInfoDefinition> result = new ArrayList<>();
        for (AbstractEntityDef a : source) {
            result.add(to(a));
        }

        return result;
    }

    public static EntityInfoDefinition to(AbstractEntityDef source) {

        if (Objects.isNull(source)) {
            return null;
        }

        EntityInfoDefinition element = new EntityInfoDefinition();
        element.setName(source.getName());
        element.setDisplayName(source.getDisplayName());
        element.setDescription(source.getDescription());

        return element;
    }

    @Override
    public EntityInfoDefinition convert(AbstractEntityDef source) {
        return to(source);
    }
}
