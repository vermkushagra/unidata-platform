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

import com.unidata.mdm.backend.api.rest.dto.meta.RelationDefinition;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import org.apache.commons.collections.CollectionUtils;

public class RelationDefinitionConverter {

    public static RelationDef convert(RelationDefinition source) {
        if (source == null) {
            return null;
        }

        RelationDef target = new RelationDef();
        target.setFromEntity(source.getFromEntity());
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setRelType(RelType.fromValue(source.getRelType().value()));
        target.setRequired(source.isRequired());
        target.setToEntity(source.getToEntity());
        target.setUseAttributeNameForDisplay(source.isUseAttributeNameForDisplay());

        if (CollectionUtils.isNotEmpty(source.getToEntityDefaultDisplayAttributes())){
            target.getToEntityDefaultDisplayAttributes().addAll(source.getToEntityDefaultDisplayAttributes());
        }

        if (CollectionUtils.isNotEmpty(source.getToEntitySearchAttributes())) {
            target.getToEntitySearchAttributes().addAll(source.getToEntitySearchAttributes());
        }

        List<SimpleAttributeDef> attrs = new ArrayList<>();
        SimpleAttributeDefConverter.copySimpleAttributeDataList(source.getSimpleAttributes(), attrs);
        target.withCustomProperties(ToCustomPropertyDefConverter.convert(source.getCustomProperties()));

        target.withSimpleAttribute(attrs);
        return target;
    }

    public static List<RelationDef> convert(List<RelationDefinition> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<RelationDef> result = new ArrayList<>();
        for (RelationDefinition def : source) {
            if (Objects.isNull(def)) {
                continue;
            }

            result.add(convert(def));
        }

        return result;
    }
}
