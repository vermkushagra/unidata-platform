package com.unidata.mdm.backend.api.rest.converter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.api.rest.dto.meta.AbstractAttributeDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.AbstractEntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.AttributeGroupsRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ComplexAttributeDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.EntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.NestedEntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.RelationDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.RelationGroupsRO;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.AttributeGroupDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.RelationGroupDef;

/**
 * @author Michael Yashin. Created on 26.05.2015.
 */
public class EntityDefinitionToEntityDefConverter {

    /**
     * Converts a REST entity definition DTO into internal format.
     *
     * @param source
     *            REST DTO
     * @return request context
     */
    public static UpdateModelRequestContext convert(EntityDefinition source) {

        EntityDef result = new EntityDef();
        Map<String, NestedEntityDef> nestedEntities = new HashMap<>();

        copyAbstractEntityData(source, result);
        SimpleAttributeDefConverter.copySimpleAttributeDataList(source.getSimpleAttributes(), result.getSimpleAttribute());
        ArrayAttributeDefConverter.copySimpleAttributeDataList(source.getArrayAttributes(), result.getArrayAttribute());
        copyComplexAttributeDataList(source.getComplexAttributes(), result.getComplexAttribute(), nestedEntities);

        DQRuleDefinitionToDQRuleDefConverter.convertList(source.getDataQualityRules(), result.getDataQualities());

        result.setDashboardVisible(source.isDashboardVisible());
        result.setGroupName(source.getGroupName());
        result.setValidityPeriod(PeriodBoundaryConverter.from(source.getValidityPeriod()));
        result.setMergeSettings(MergeSettingsConverter.from(source.getMergeSettings()));
        result.setExternalIdGenerationStrategy(ExternalIdGenerationStrategyConverter.from(source.getExternalIdGenerationStrategy()));

        if (source.getClassifiers() != null) {
            result.getClassifiers().addAll(source.getClassifiers());
        }

        if (source.getAttributeGroups() != null) {
            convertAttributeGroups(source.getAttributeGroups(), result.getAttributeGroups());
        }

        if (source.getRelationGroups() != null) {
            convertRelationGroups(source.getRelationGroups(), result.getRelationGroups());
        }

        List<RelationDefinition> sourceDefs = source.getRelations();
        List<RelationDef> targetDefs = new ArrayList<>();

        for (RelationDefinition relationDefinition : sourceDefs) {
            targetDefs.add(RelationDefinitionConverter.convert(relationDefinition));
        }

        return new UpdateModelRequestContextBuilder()
                .entityUpdate(Collections.singletonList(result))
                .nestedEntityUpdate(new ArrayList<>(nestedEntities.values()))
                .relationsUpdate(targetDefs)
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
     * Convert Request object to Model object.
     * @param sourceRelationGroups - will be used for filling
     * @param targetRelationGroups - will be filled
     */
    private static void convertRelationGroups(List<RelationGroupsRO> sourceRelationGroups, List<RelationGroupDef> targetRelationGroups) {
        for (RelationGroupsRO relationGroup : sourceRelationGroups) {
            RelationGroupDef relationGroupDef = new RelationGroupDef()
                    .withColumn(relationGroup.getColumn())
                    .withRow(relationGroup.getRow())
                    .withTitle(relationGroup.getTitle())
                    .withRelType(RelType.fromValue(relationGroup.getRelType().value()))
                    .withRelations(relationGroup.getRelations());
            targetRelationGroups.add(relationGroupDef);
        }
    }

    /**
     * Converts complex attributes from REST to intenal.
     *
     * @param source
     *            REST type
     * @param targetList
     * @param nestedEntities the model
     */
    private static void convertComplexAttribute(ComplexAttributeDefinition source, List<ComplexAttributeDef> targetList,
            Map<String, NestedEntityDef> nestedEntities) {


        ComplexAttributeDef result = new ComplexAttributeDef();

        copyAbstractAttributeData(source, result);

        if (source.getMinCount() != null) {
            result.setMinCount(BigInteger.valueOf(source.getMinCount()));
        }
        if (source.getMaxCount() != null) {
            result.setMaxCount(BigInteger.valueOf(source.getMaxCount()));
        }

        result.setOrder(BigInteger.valueOf(source.getOrder()));
        result.setSubEntityKeyAttribute(source.getSubEntityKeyAttribute());

        if (source.getNestedEntity() != null
        && !nestedEntities.containsKey(source.getNestedEntity().getName())) {
            NestedEntityDef nested = convertNestedEntity(source.getNestedEntity(), nestedEntities);
            nestedEntities.put(nested.getName(), nested);
            result.setNestedEntityName(nested.getName());
        }

        targetList.add(result);
    }

    /**
     * Converts nested entity from REST to internal
     * @param source REST
     * @param nestedEntities model
     * @return internal
     */
    private static NestedEntityDef convertNestedEntity(NestedEntityDefinition source, Map<String, NestedEntityDef> nestedEntities) {
        NestedEntityDef result = new NestedEntityDef();

        copyAbstractEntityData(source, result);
        SimpleAttributeDefConverter.copySimpleAttributeDataList(source.getSimpleAttributes(), result.getSimpleAttribute());
        ArrayAttributeDefConverter.copySimpleAttributeDataList(source.getArrayAttributes(), result.getArrayAttribute());
        copyComplexAttributeDataList(source.getComplexAttributes(), result.getComplexAttribute(), nestedEntities);

        return result;
    }

    /**
     * Copy abstract entity definition from REST to internal.
     *
     * @param source
     *            REST source
     * @param target
     *            internal
     */
    private static void copyAbstractEntityData(AbstractEntityDefinition source, AbstractEntityDef target) {
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());
        target.withCustomProperties(ToCustomPropertyDefConverter.convert(source.getCustomProperties()));
    }

    /**
     * Copy abstract attribute data from REST to internal.
     *
     * @param source
     *            REST source
     * @param target
     *            internal
     */
    private static void copyAbstractAttributeData(AbstractAttributeDefinition source, AbstractAttributeDef target) {
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());
        target.setHidden(source.isHidden());
        target.setReadOnly(source.isReadOnly());
        target.withCustomProperties(ToCustomPropertyDefConverter.convert(source.getCustomProperties()));
    }

    /**
     * Copy list of REST complex attributes to internal target
     * @param target REST
     * @param source internal
     * @param nestedEntities the model
     */
    private static void copyComplexAttributeDataList(
            List<ComplexAttributeDefinition> source,
            List<ComplexAttributeDef> target,
            Map<String, NestedEntityDef> nestedEntities) {

        if (source == null) {
            return;
        }

        for (ComplexAttributeDefinition attr : source) {
            convertComplexAttribute(attr, target, nestedEntities);
        }
    }
}
