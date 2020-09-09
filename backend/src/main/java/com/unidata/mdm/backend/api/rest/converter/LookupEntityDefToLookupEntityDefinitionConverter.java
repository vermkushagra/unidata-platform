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

import java.util.List;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.meta.AttributeGroupsRO;
import com.unidata.mdm.backend.api.rest.dto.meta.CodeAttributeDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.LookupEntityDefinition;
import com.unidata.mdm.meta.AttributeGroupDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * @author Mikhail Mikhailov
 *
 */
public class LookupEntityDefToLookupEntityDefinitionConverter extends AbstractEntityDefinitionConverter {

    /**
     * Instantiation disabled.
     */
    private LookupEntityDefToLookupEntityDefinitionConverter() {
        super();
    }

    /**
     * Converts lookup entity from internal to REST.
     * @param source the source
     * @return REST
     */
    public static LookupEntityDefinition convert(LookupEntityDef source) {

        LookupEntityDefinition target = new LookupEntityDefinition();

        target.setDashboardVisible(source.isDashboardVisible());
        target.setGroupName(source.getGroupName());

        convertAttributeGroups(source.getAttributeGroups(), target.getAttributeGroups());
        copyAbstractEntityData(source, target);
        copyCodeAttribute(source.getCodeAttribute(), target.getCodeAttribute(), source.getName());
        target.setCustomProperties(to(source.getCustomProperties()));

        target.getSimpleAttributes().addAll(toSimpleAttrs(source.getSimpleAttribute(), source.getName()));
        target.getArrayAttributes().addAll(toArrayAttrs(source.getArrayAttribute(), source.getName()));

        for (CodeAttributeDef codeAttributeDef : source.getAliasCodeAttributes()) {
            CodeAttributeDefinition codeAttributeDefinition = new CodeAttributeDefinition();
            copyCodeAttribute(codeAttributeDef, codeAttributeDefinition, source.getName());
            target.getAliasCodeAttributes().add(codeAttributeDefinition);
        }
        source.getAliasCodeAttributes().stream().map(f -> new CodeAttributeDefinition()).collect(Collectors.toList());

        target.setValidityPeriod(PeriodBoundaryConverter.to(source.getValidityPeriod()));
        target.setMergeSettings(MergeSettingsConverter.to(source.getMergeSettings()));
        target.setExternalIdGenerationStrategy(ExternalIdGenerationStrategyConverter.to(source.getExternalIdGenerationStrategy()));
        target.setClassifiers(source.getClassifiers());

        DQRuleDefToDQRuleDefinitionConverter.convertList(source.getDataQualities(), target.getDataQualityRules());

        return target;
    }

    /**
     * Convert Model object to Request object.
     * @param sourceAttributeGroups - will be used for filling
     * @param targetAttributeGroups - will be filled
     */
    private static void convertAttributeGroups(List<AttributeGroupDef> sourceAttributeGroups, List<AttributeGroupsRO> targetAttributeGroups) {
        for (AttributeGroupDef attributeGroup : sourceAttributeGroups) {
            AttributeGroupsRO attributeGroupRo = new AttributeGroupsRO()
                    .withRow(attributeGroup.getRow())
                    .withTitle(attributeGroup.getTitle())
                    .withColumn(attributeGroup.getColumn())
                    .withAttributes(attributeGroup.getAttributes());
            targetAttributeGroups.add(attributeGroupRo);
        }
    }

    /**
     * Copy code attribute.
     * @param source the source
     * @param target target
     */
    private static void copyCodeAttribute(CodeAttributeDef source, CodeAttributeDefinition target, String securityPath) {

        copyAbstractAttributeData(source, target, securityPath);

        target.setNullable(source.isNullable());
        target.setUnique(source.isUnique());
        target.setSearchable(source.isSearchable());
        target.setDisplayable(source.isDisplayable());
        target.setMainDisplayable(source.isMainDisplayable());
        target.setMask(source.getMask());
        target.setExternalIdGenerationStrategy(ExternalIdGenerationStrategyConverter.to(source.getExternalIdGenerationStrategy()));

        if (source.getSimpleDataType() != null) {
            target.setSimpleDataType(source.getSimpleDataType() != null
                    ? SimpleDataType.fromValue(source.getSimpleDataType().name())
                    : null);
        }
    }
}
