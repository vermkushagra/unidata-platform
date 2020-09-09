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

import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.meta.AbstractAttributeDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.AbstractEntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.AttributeGroupsRO;
import com.unidata.mdm.backend.api.rest.dto.meta.CodeAttributeDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.LookupEntityDefinition;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AttributeGroupDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 *
 */
public class LookupEntityDefinitionToLookupEntityDefConverter {

    /**
     * Instantiation disabled.
     */
    private LookupEntityDefinitionToLookupEntityDefConverter() {
        super();
    }

    /**
     * Converts a REST lookup entity definition DTO into internal format.
     * @param source REST DTO
     * @return internal
     */
    public static UpdateModelRequestContext convert(LookupEntityDefinition source) {

        LookupEntityDef target = new LookupEntityDef();

        target.setDashboardVisible(source.isDashboardVisible());
        target.setValidityPeriod(PeriodBoundaryConverter.from(source.getValidityPeriod()));
        target.setMergeSettings(MergeSettingsConverter.from(source.getMergeSettings()));
        target.setExternalIdGenerationStrategy(ExternalIdGenerationStrategyConverter.from(source.getExternalIdGenerationStrategy()));
        target.setGroupName(source.getGroupName());

        copyAbstractEntityData(source, target);
        copyCodeAttribute(source.getCodeAttribute(), target.withCodeAttribute(new CodeAttributeDef()).getCodeAttribute());

        for (CodeAttributeDefinition codeAttributeDefinition : source.getAliasCodeAttributes()) {
            CodeAttributeDef codeAttributeDef = new CodeAttributeDef();
            copyCodeAttribute(codeAttributeDefinition, codeAttributeDef);
            target.getAliasCodeAttributes().add(codeAttributeDef);
        }
        if (source.getClassifiers() != null) {
            target.getClassifiers().addAll(source.getClassifiers());
        }
        if (source.getAttributeGroups() != null) {
            convertAttributeGroups(source.getAttributeGroups(), target.getAttributeGroups());
        }
        DQRuleDefinitionToDQRuleDefConverter.convertList(source.getDataQualityRules(), target.getDataQualities());
        return new UpdateModelRequestContextBuilder()
                .lookupEntityUpdate(Collections.singletonList(target))
                .build();
    }

    /**
     * Convert Request object to Model object.
     * @param sourceAttributeGroups - will be used for filling
     * @param targetAttributeGroups - will be filled
     */
    private static void convertAttributeGroups(List<AttributeGroupsRO> sourceAttributeGroups, List<AttributeGroupDef> targetAttributeGroups) {
        for (AttributeGroupsRO attributeGroup : sourceAttributeGroups) {
            AttributeGroupDef attributeGroupDef = new AttributeGroupDef()
                    .withColumn(attributeGroup.getColumn())
                    .withRow(attributeGroup.getRow())
                    .withTitle(attributeGroup.getTitle())
                    .withAttributes(attributeGroup.getAttributes());
            targetAttributeGroups.add(attributeGroupDef);
        }
    }


    /**
     * Copy code attribute.
     * @param source the source
     * @param target target
     */
    private static void copyCodeAttribute(CodeAttributeDefinition source, CodeAttributeDef target) {
        SimpleAttributeDefConverter.copyAbstractAttributeData(source, target);

        target.setNullable(source.isNullable());
        target.setSimpleDataType(SimpleDataType.valueOf(source.getSimpleDataType().name()));
        target.setUnique(source.isUnique());
        target.setMask(source.getMask());
        target.setSearchable(source.isSearchable());
        target.setDisplayable(source.isDisplayable());
        target.setMainDisplayable(source.isMainDisplayable());
        target.setExternalIdGenerationStrategy(ExternalIdGenerationStrategyConverter.from(source.getExternalIdGenerationStrategy()));
    }

    /**
     * Copy abstract entity definition from REST to internal.
     * @param source REST source
     * @param target internal
     */
    private static void copyAbstractEntityData(AbstractEntityDefinition source, LookupEntityDef target) {
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());

        SimpleAttributeDefConverter.copySimpleAttributeDataList(source.getSimpleAttributes(), target.getSimpleAttribute());
        ArrayAttributeDefConverter.copySimpleAttributeDataList(source.getArrayAttributes(), target.getArrayAttribute());

        target.withCustomProperties(ToCustomPropertyDefConverter.convert(source.getCustomProperties()));
    }
}
