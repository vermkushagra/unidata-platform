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

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.meta.RelType;
import com.unidata.mdm.backend.api.rest.dto.meta.RelationDefinition;
import com.unidata.mdm.meta.RelationDef;

public class RelationDefConverter extends AbstractEntityDefinitionConverter {

    public static RelationDefinition convert(RelationDef source) {
        if (source == null) {
            return null;
        }
        RelationDefinition target = new RelationDefinition();
        target.setFromEntity(source.getFromEntity());
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setRelType(RelType.fromValue(source.getRelType().value()));
        target.setRequired(source.isRequired());
        target.setToEntity(source.getToEntity());
        target.setToEntityDefaultDisplayAttributes(source.getToEntityDefaultDisplayAttributes());
        target.setToEntitySearchAttributes(source.getToEntitySearchAttributes());
        target.setSimpleAttributes(toSimpleAttrs(source.getSimpleAttribute(), target.getName()));
        target.setUseAttributeNameForDisplay(source.isUseAttributeNameForDisplay());

        target.setCustomProperties(to(source.getCustomProperties()));

        return target;
    }

    public static List<RelationDefinition> convert(List<RelationDef> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<RelationDefinition> result = new ArrayList<>();
        for (RelationDef def : source) {
            if (Objects.isNull(def)) {
                continue;
            }

            result.add(convert(def));
        }

        return result;
    }
}
