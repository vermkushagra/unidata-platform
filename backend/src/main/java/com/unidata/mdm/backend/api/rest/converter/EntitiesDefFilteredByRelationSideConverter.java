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

/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.api.rest.dto.meta.EntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.RelationDefinition;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesByRelationSideDTO;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 *
 */
public class EntitiesDefFilteredByRelationSideConverter {

    /**
     * Constructor.
     */
    private EntitiesDefFilteredByRelationSideConverter() {
        super();
    }

    /**
     * Converter method.
     * @param dto the dto to convert
     * @return list of entity definitions
     */
    public static List<EntityDefinition> convert(GetEntitiesByRelationSideDTO dto) {

        if (dto == null || dto.getEntities() == null || dto.getEntities().isEmpty()) {
            return Collections.emptyList();
        }

        List<EntityDefinition> result = new ArrayList<>();
        for (Entry<EntityDef, Pair<List<NestedEntityDef>, List<RelationDef>>> e
                : dto.getEntities().entrySet()) {

            EntityDef source = e.getKey();
            EntityDefinition target = new EntityDefinition();
            List<NestedEntityDef> refs = e.getValue().getLeft();

            EntityDefinitionConverter.copyAbstractEntityData(source, target);
            target.getSimpleAttributes().addAll(
                    EntityDefinitionConverter.toSimpleAttrs(source.getSimpleAttribute(), source.getName()));
            target.getArrayAttributes().addAll(
                    EntityDefinitionConverter.toArrayAttrs(source.getArrayAttribute(), source.getName()));
            target.getComplexAttributes().addAll(
                    EntityDefinitionConverter.to(source.getComplexAttribute(), refs, source.getName()));

            target.setDashboardVisible(source.isDashboardVisible());
            target.setMergeSettings(MergeSettingsConverter.to(source.getMergeSettings()));

            List<RelationDefinition> relations = new ArrayList<>();
            for (RelationDef relationDef : e.getValue().getRight()) {
                relations.add(RelationDefConverter.convert(relationDef));
            }

            target.setRelations(relations);

            result.add(target);
        }

        return result;
    }
}
