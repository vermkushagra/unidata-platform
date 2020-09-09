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
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.meta.AttributeGroupsRO;
import com.unidata.mdm.backend.api.rest.dto.meta.EntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.RelType;
import com.unidata.mdm.backend.api.rest.dto.meta.RelationDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.RelationGroupsRO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.meta.AttributeGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.RelationGroupDef;

/**
 * @author Michael Yashin. Created on 26.05.2015.
 */
public class EntityDefinitionConverter extends AbstractEntityDefinitionConverter {

    /**
     * Converts internal representation of an entity - {@link EntityDef} to a REST capable {@link EntityDefinition}.
     * @param input {@link GetEntityDTO} object
     * @return REST capable entity definition
     */
    public static EntityDefinition convert(GetEntityDTO input) {

        EntityDefinition result = new EntityDefinition();
        EntityDef source = input.getEntity();

        List<NestedEntityDef> refs = input.getRefs();
        List<RelationDef> relationDefs = input.getRelations();

        result.setGroupName(source.getGroupName());
        result.setValidityPeriod(PeriodBoundaryConverter.to(source.getValidityPeriod()));

        copyAbstractEntityData(source, result);

        result.getSimpleAttributes().addAll(toSimpleAttrs(source.getSimpleAttribute(), source.getName()));
        result.getArrayAttributes().addAll(toArrayAttrs(source.getArrayAttribute(), source.getName()));
        result.getComplexAttributes().addAll(to(source.getComplexAttribute(), refs, source.getName()));
        result.setCustomProperties(to(source.getCustomProperties()));

        DQRuleDefToDQRuleDefinitionConverter.convertList(source.getDataQualities(), result.getDataQualityRules());
        convertAttributeGroups(input.getEntity().getAttributeGroups(), result.getAttributeGroups());
        convertRelationGroups(input.getEntity().getRelationGroups(), result.getRelationGroups());

        result.setDashboardVisible(source.isDashboardVisible());
        result.setMergeSettings(MergeSettingsConverter.to(source.getMergeSettings()));
        result.setExternalIdGenerationStrategy(ExternalIdGenerationStrategyConverter.to(source.getExternalIdGenerationStrategy()));
        result.setClassifiers(source.getClassifiers());

        List<RelationDefinition> targetRelationDefs = new ArrayList<>();
        for (RelationDef relationDef : relationDefs) {
            targetRelationDefs.add(RelationDefConverter.convert(relationDef));
        }

        result.setRelations(targetRelationDefs);

        return result;
    }

    /**
     * Convert Model object to Request object.
     * @param sourceAttributeGroups - will be used for filling
     * @param targetAttributeGroups - will be filled
     */
    private static void convertAttributeGroups(List<AttributeGroupDef> sourceAttributeGroups, List<AttributeGroupsRO> targetAttributeGroups) {
        for (AttributeGroupDef attributeGroup : sourceAttributeGroups) {
            AttributeGroupsRO attributeGroupRo = new AttributeGroupsRO()
                    .withColumn(attributeGroup.getColumn())
                    .withRow(attributeGroup.getRow())
                    .withTitle(attributeGroup.getTitle())
                    .withAttributes(attributeGroup.getAttributes());
            targetAttributeGroups.add(attributeGroupRo);
        }
    }

    /**
     * Convert Model object to Request object.
     * @param sourceRelationGroups - will be used for filling
     * @param targetRelationGroups - will be filled
     */
    private static void convertRelationGroups(List<RelationGroupDef> sourceRelationGroups, List<RelationGroupsRO> targetRelationGroups) {
        for (RelationGroupDef relationGroup : sourceRelationGroups) {
            RelationGroupsRO relationGroupRo = new RelationGroupsRO()
                    .withColumn(relationGroup.getColumn())
                    .withRow(relationGroup.getRow())
                    .withTitle(relationGroup.getTitle())
                    .withRelType(RelType.fromValue(relationGroup.getRelType().value()))
                    .withRelations(relationGroup.getRelations());
            targetRelationGroups.add(relationGroupRo);
        }
    }
}
