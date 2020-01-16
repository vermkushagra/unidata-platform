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

package org.unidata.mdm.meta.service.impl;

import static java.util.Collections.emptyList;
import static java.util.Collections.frequency;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.unidata.mdm.meta.util.ModelUtils.findModelAttribute;
import static org.unidata.mdm.meta.util.ModelUtils.isComplexAttribute;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.ArrayAttributeDef;
import org.unidata.mdm.meta.AttributeGroupDef;
import org.unidata.mdm.meta.CodeAttributeDef;
import org.unidata.mdm.meta.ComplexAttributesHolderEntityDef;
import org.unidata.mdm.meta.CustomPropertyDef;
import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.MergeAttributeDef;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.PeriodBoundaryDef;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.SimpleAttributeDef;
import org.unidata.mdm.meta.SimpleAttributesHolderEntityDef;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.VersionedObjectDef;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext.ModelUpsertType;
import org.unidata.mdm.meta.dto.FullModelDTO;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.meta.exception.MetaModelValidationException;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.service.MetaModelValidationComponent;
import org.unidata.mdm.meta.service.impl.facades.AbstractModelElementFacade;
import org.unidata.mdm.meta.service.impl.facades.EntitiesGroupModelElementFacade;
import org.unidata.mdm.meta.service.impl.facades.ModelElementElementFacade;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.meta.type.ie.MetaEdge;
import org.unidata.mdm.meta.type.ie.MetaEdgeFactory;
import org.unidata.mdm.meta.type.ie.MetaExistence;
import org.unidata.mdm.meta.type.ie.MetaGraph;
import org.unidata.mdm.meta.type.ie.MetaType;
import org.unidata.mdm.meta.type.ie.MetaVertex;
import org.unidata.mdm.meta.util.ModelContextUtils;
import org.unidata.mdm.meta.util.ModelUtils;
import org.unidata.mdm.meta.util.ValidityPeriodUtils;
import org.unidata.mdm.system.exception.PlatformBusinessException;
import org.unidata.mdm.system.exception.ValidationResult;
import org.unidata.mdm.system.util.AbstractJaxbUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * The Class MetaModelValidationComponentImpl.
 */
@Component
public class MetaModelValidationComponentImpl implements MetaModelValidationComponent {

    /**
     * The Constant INITIAL_ERROR_MESSAGE.
     */
    private static final String INITIAL_ERROR_MESSAGE = "Model is incorrect.";
    private static final String DUPLICATE_NAMES = "Model contains duplicate names";

    /**
     * Message's code
     */
    private static final String UNKNOWN_ATTR_IN_DISPLAY_GROUP = "app.meta.display.group.contain.absent.attr";
    private static final String DISPLAY_GROUP_CONTAINS_UNAVAILABLE_ATTR = "app.meta.display.group.contain.incorrect.attr";
    private static final String MERGE_SOURCE_SYSTEM_ABSENT = "app.meta.merge.source.system.absent";
    private static final String MERGE_SOURCE_SYSTEM_INCORRECT = "app.meta.merge.source.system.incorrect";
    private static final String MERGE_ATTR_ABSENT = "app.meta.merge.attr.absent";
    private static final String MERGE_ATTR_INCORRECT = "app.meta.merge.attr.incorrect";
    private static final String SOURCE_SYSTEM_INCORRECT = "app.meta.source.system.incorrect";
    private static final String ENUM_ABSENT = "app.meta.enum.absent";
    private static final String LOOKUP_ENTITY_ABSENT = "app.meta.lookup.absent";
    private static final String NODE_GROUP_TREE_ABSENT = "app.meta.group.node.absent";
    private static final String DUPL_ATTRIBUTE = "app.meta.attr.dupl";
    private static final String NO_SEARCHABLE_ATTRIBUTE = "app.meta.attr.noSearchable";
    private static final String INCORRECT_ADMIN_SOURCE_SYSTEM = "app.meta.ss.admin.incorrect";
    private static final String INCORRECT_WEIGHT_FOR_SOURCE_SYSTEM = "app.meta.ss.weight";
    private static final String CONNECTIVITY_MISSING_ELEMENT = "app.meta.connectivity.missing.element";
    private static final String LINK_ATTRIBUTE_INCORRECT = "app.meta.attr.link.incorrect";
    private static final String TIMELINE_NOT_OVERLAPPING = "app.meta.timeline.overlapping";
    private static final String MODIFIED_RELATION_TYPE_ON_ELEMENT_WITH_DATA = "app.meta.relation.type.modified";
    private static final String CUSTOM_PROPERTY_INVALID_NAMES = "app.custom.property.invalid.names.on.object";
    private static final String CUSTOM_PROPERTY_DUPLICATED_NAMES = "app.custom.property.duplicated.names.on.object";
    /**
     * The meta model service.
     */
    // todo separate metaModelService to modelCache and service!
    @Autowired
    private MetaModelService metaModelService;

    // TODO: @Modules
//    @Autowired
//    private GraphCreator graphCreator;
//
//    @Autowired
//    private RelationsServiceComponent relationsServiceComponent;

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.model.MetaModelValidationService#
     * validateUpdateModelContext(com.unidata.mdm.backend.service.model.
     * UpdateModelRequestContext)
     */
    @Override
    //todo rewrite all list in context to maps! (and this component at all)
    public void validateUpdateModelContext(@Nonnull final UpdateModelRequestContext ctx) {
        // validate each element
        Arrays.stream(ModelType.values())
                .filter(modelType -> ModelContextUtils.hasUpdateForModelType(ctx, modelType))
                .forEach(modelType -> verifyModelElements(modelType.getModelElementClass(), modelType.getWrapperClass(), ctx));

        // validate full model.
        boolean isFullModelCtx = ctx.getUpsertType() == ModelUpsertType.FULLY_NEW;
        if (ctx.getEntitiesGroupsUpdate() == null
                && (isFullModelCtx || metaModelService.getRootGroup(SecurityUtils.getStorageId(ctx)) == null)) {
            throw new PlatformBusinessException("Root group is absent", MetaExceptionIds.EX_META_ROOT_GROUP_IS_ABSENT);
        }

        // check unique names in nested entities.
        Collection<String> duplicateNames = getDuplicateNames(ctx);
        if (!isFullModelCtx && duplicateNames.isEmpty()) {
            duplicateNames = Arrays.stream(ModelType.values())
                    .filter(modelType -> ModelContextUtils.hasUpdateForModelType(ctx, modelType))
                    .map(modelType -> checkUniqueNames(modelType.getModelElementClass(), modelType.getWrapperClass(), ctx))
                    .flatMap(Collection::stream).collect(Collectors.toSet());
        }
        if (!duplicateNames.isEmpty()) {
            throw new PlatformBusinessException(DUPLICATE_NAMES, MetaExceptionIds.EX_META_NESTED_ENTITIES_DUPLICATE2, duplicateNames);
        }

        //todo replace some checks to verifyModelElements!
        Collection<ValidationResult> validations = new ArrayList<>();

        // check attributes groups
        validations.addAll(checkDisplayGroups(ctx));
        //check attributes
        validations.addAll(checkAttributes(ctx));
        //check source systems
        validations.addAll(checkSourceSystems(ctx));
        // check merge settings
        validations.addAll(checkMergeSettings(ctx));

        //check source systems
        validations.addAll(checkTopLevelSourceSystem(ctx));

        // check references
        validations.addAll(checkReferencesToEnumerations(ctx));
        validations.addAll(checkReferencesToLookupEntities(ctx));
        validations.addAll(checkReferencesToGroups(ctx));

        // relations
        validations.addAll(checkRelations(ctx));

        // check groups
        validations.addAll(checkGroups(ctx));

        // validate timelines
        validations.addAll(checkTimelines(ctx));
        // validate connectivity
        validations.addAll(checkConnectivity(ctx));

        // check modification of relation type on elements with data
        validations.addAll(checkModificationRelationTypeOnElementsWithData(ctx));

        if (!validations.isEmpty()) {
            throw new MetaModelValidationException(INITIAL_ERROR_MESSAGE, MetaExceptionIds.EX_META_IS_INCORRECT, validations);
        }
    }

    private Collection<ValidationResult> checkRelations(UpdateModelRequestContext ctx) {
        return ctx.getRelationsUpdate().stream()
                .flatMap(relation -> {
                    final List<ValidationResult> validationResults = new ArrayList<>();
                    validationResults.addAll(validateCustomProperties(relation.getName(), relation.getCustomProperties()));
                    validationResults.addAll(
                            relation.getSimpleAttribute().stream()
                                    .flatMap(attr -> validateCustomProperties(attr.getName(), attr.getCustomProperties()).stream())
                                    .collect(toList())
                    );
                    validationResults.addAll(
                            relation.getComplexAttribute().stream()
                                    .flatMap(attr -> validateCustomProperties(attr.getName(), attr.getCustomProperties()).stream())
                                    .collect(toList())
                    );
                    validationResults.addAll(
                            relation.getArrayAttribute().stream()
                                    .flatMap(attr -> validateCustomProperties(attr.getName(), attr.getCustomProperties()).stream())
                                    .collect(toList())
                    );
                    return validationResults.stream();
                })
                .collect(toList());

    }

    /**
     * Check modification of relation type on elements with data.
     *
     * @param ctx context.
     * @return list with errors(if any).
     */
    private Collection<? extends ValidationResult> checkModificationRelationTypeOnElementsWithData(final UpdateModelRequestContext ctx) {
        final List<RelationDef> modifiedRelations = ctx.getRelationsUpdate().stream()
                .filter(relationDef -> true)//relationsServiceComponent.checkExistDataByRelName(relationDef.getName()))// TODO: @Modules
                .filter(relationDef -> {
                            RelationDef oldRelationDef = metaModelService.getRelationById(relationDef.getName());
                            return oldRelationDef != null && relationDef.getRelType() != oldRelationDef.getRelType();
                        }
                )
                .collect(toList());
        final String message = "Relation type of [{}] can't be modified, because element has data";
        return modifiedRelations.isEmpty() ?
                Collections.emptyList() :
                modifiedRelations.stream()
                        .map(relationDef -> new ValidationResult(message, MODIFIED_RELATION_TYPE_ON_ELEMENT_WITH_DATA, relationDef.getName()))
                        .collect(toList());
    }

    /**
     * Check connectivity.
     *
     * @param ctx context.
     * @return list with errors(if any).
     */
    private Collection<? extends ValidationResult> checkConnectivity(UpdateModelRequestContext ctx) {
        List<ValidationResult> errors = new ArrayList<>();
        MetaGraph result = new MetaGraph(new MetaEdgeFactory());
        if (ctx.getUpsertType() != ModelUpsertType.FULLY_NEW) {
            Model model = metaModelService.exportModel(ctx.getStorageId());
            FullModelDTO fullModelDTO = new FullModelDTO().withModel(model);
            // TODO: @Modules
//            graphCreator.enrich(fullModelDTO, result, MetaExistence.EXIST, MetaType.ENTITY, MetaType.NESTED_ENTITY,
//                    MetaType.ENUM, MetaType.LOOKUP, MetaType.SOURCE_SYSTEM, MetaType.RELATION, ctx.hasSourceSystemsUpdate()&&!ctx.isDirect()?MetaType.CLASSIFIER:null);
        }
        Model model = new Model();
        if (ctx.hasEntitiesGroupUpdate()) {
            model.withEntitiesGroup(ctx.getEntitiesGroupsUpdate());
        }
        if (ctx.hasEntityUpdate()) {
            model.withEntities(ctx.getEntityUpdate());
        }
        if (ctx.hasEnumerationUpdate()) {
            model.withEnumerations(ctx.getEnumerationsUpdate());
        }
        if (ctx.hasLookupEntityUpdate()) {
            model.withLookupEntities(ctx.getLookupEntityUpdate());
        }
        if (ctx.hasNestedEntityUpdate()) {
            model.withNestedEntities(ctx.getNestedEntityUpdate());
        }
        if (ctx.hasRelationsUpdate()) {
            model.withRelations(ctx.getRelationsUpdate());
        }
        if (ctx.hasSourceSystemsUpdate()) {
            model.withSourceSystems(ctx.getSourceSystemsUpdate());
        }

        FullModelDTO fullModelDTO = new FullModelDTO().withModel(model);
        // TODO: @Modules
//        graphCreator.enrich(fullModelDTO, result, MetaExistence.EXIST, MetaType.ENTITY, MetaType.NESTED_ENTITY,
//                MetaType.ENUM, MetaType.RELATION, ctx.hasSourceSystemsUpdate()&&!ctx.isDirect()?MetaType.GROUPS:null, MetaType.LOOKUP, MetaType.SOURCE_SYSTEM, ctx.hasSourceSystemsUpdate()&&!ctx.isDirect()?MetaType.CLASSIFIER:null);
        for (MetaVertex to : result.vertexSet()) {
            Set<MetaEdge<MetaVertex>> edges = result.incomingEdgesOf(to);
            if (edges != null) {
                for (MetaEdge<MetaVertex> edge : edges) {
                    MetaVertex from = edge.getFrom();
                    if (to.getStatus() == MetaExistence.NOT_FOUND || to.getStatus() == null) {
                        String message = "Element [{}] with type [{}] refers missing element [{}] with type [{}]";
                        errors.add(new ValidationResult(message, CONNECTIVITY_MISSING_ELEMENT, from.getId(),
                                from.getType().name(), to.getId(), to.getType().name()));
                    }
                }
            }

        }
        return errors;
    }


    private Collection<? extends ValidationResult> checkSourceSystems(UpdateModelRequestContext ctx) {
        List<ValidationResult> errors = new ArrayList<>();
        Set<String> adms = new HashSet<>();
        if (ctx.getUpsertType() != ModelUpsertType.FULLY_NEW) {
            List<SourceSystemDef> fromModel = metaModelService.getSourceSystemsList();
            if (fromModel != null) {
                for (SourceSystemDef ss : fromModel) {
                    if (ss.isAdmin()) {
                        adms.add(ss.getName());
                    }
                }
            }
        }
        if (ctx.hasSourceSystemsUpdate()) {
            for (SourceSystemDef ss : ctx.getSourceSystemsUpdate()) {

                if (ss.getWeight() == null || ss.getWeight().intValue() > 100 || ss.getWeight().intValue() < 0) {
                    String message = "Incorrect weight for source system [{}]";
                    errors.add(new ValidationResult(message, INCORRECT_WEIGHT_FOR_SOURCE_SYSTEM, ss.getName(),
                            ss.getWeight() == null ? null : ss.getWeight().intValue()));
                }
                if (ss.isAdmin()) {
                    adms.add(ss.getName());
                    //if source system type changed from admin to ordinary source system remove it from the set
                } else if (!ss.isAdmin() && adms.contains(ss.getName())) {
                    adms.remove(ss.getName());
                }
            }
            if (adms.size() != 1) {
                String message = "Admin source system must be defined exactly once found [{}] admin source system. Names: [{}]";
                errors.add(new ValidationResult(message, INCORRECT_ADMIN_SOURCE_SYSTEM, adms.size(), adms.toString()));
            }
        }

        return errors;
    }


    /**
     * Check consistency of model over attribute groups(display groups).
     *
     * @param ctx - context which contains all necessary info about model.
     * @return collection of validation results
     */
    private Collection<ValidationResult> checkDisplayGroups(UpdateModelRequestContext ctx) {
        Collection<ValidationResult> validationResults = new HashSet<>();

        for (EntityDef entity : ctx.getEntityUpdate()) {
            Collection<String> requiredAttr = entity.getAttributeGroups()
                    .stream()
                    .map(AttributeGroupDef::getAttributes)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            Collection<String> notPresentedAttrs = allAbsentAttr(entity, requiredAttr,
                    ctx.getNestedEntityUpdate());
            notPresentedAttrs.stream()
                    .map(name -> new ValidationResult("Display group of entity [{}] contain absent attr [{}]",
                            UNKNOWN_ATTR_IN_DISPLAY_GROUP, entity.getDisplayName(), name))
                    .collect(Collectors.toCollection(() -> validationResults));

            //complex attributes can't be in attribute's group settings!
            Collection<String> complexAndNestedAttributes = findComplexAndNestedAttributes(entity, requiredAttr);
            complexAndNestedAttributes.stream()
                    .map(name -> new ValidationResult("Display group of entity [{}] contains complex or nested attr [{}]",
                            DISPLAY_GROUP_CONTAINS_UNAVAILABLE_ATTR, entity.getDisplayName(), name))
                    .collect(Collectors.toCollection(() -> validationResults));
        }

        for (LookupEntityDef entity : ctx.getLookupEntityUpdate()) {
            Collection<String> requiredAttrs = entity.getAttributeGroups()
                    .stream()
                    .map(AttributeGroupDef::getAttributes)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            Collection<String> notPresentedAttrs = allAbsentAttr(entity, requiredAttrs, emptyList());
            notPresentedAttrs.stream()
                    .map(name -> new ValidationResult("Unknown attr [{}] in display group of entity [{}]",
                            UNKNOWN_ATTR_IN_DISPLAY_GROUP, name, entity.getDisplayName()))
                    .collect(Collectors.toCollection(() -> validationResults));
        }

        return validationResults;
    }

    /**
     * Check consistency of model over merge settings.
     *
     * @param ctx - context which contains all necessary info about model.
     * @return collection of validation results
     */
    private Collection<ValidationResult> checkMergeSettings(UpdateModelRequestContext ctx) {
        Collection<ValidationResult> validationResults = new ArrayList<>();
        for (EntityDef entity : ctx.getEntityUpdate()) {
            if (entity.getMergeSettings() == null) {
                continue;
            }
            //check attrs
            List<MergeAttributeDef> attributes = entity.getMergeSettings().getBvtSettings() == null ?
                    Collections.emptyList() :
                    entity.getMergeSettings().getBvtSettings().getAttributes();

            Collection<String> requiredAttrs = attributes.stream()
                    .map(MergeAttributeDef::getName)
                    .collect(Collectors.toSet());

            requiredAttrs.stream()
                    .filter(attr -> StringUtils.isBlank(attr) || ModelUtils.isCompoundPath(attr))
                    .map(name -> new ValidationResult("Merge attr [{}] is incorrect in entity [{}]", MERGE_ATTR_INCORRECT, name,
                            entity.getDisplayName()))
                    .collect(Collectors.toCollection(() -> validationResults));

            Collection<String> absentAttrs = allAbsentAttr(entity, requiredAttrs, ctx.getNestedEntityUpdate());
            absentAttrs.stream()
                    .map(name -> new ValidationResult("Entity [{}] contain merge attr [{}] which is absent", MERGE_ATTR_ABSENT,
                            entity.getDisplayName(), name))
                    .collect(Collectors.toCollection(() -> validationResults));

            //check merge source system
            Collection<SourceSystemDef> sourceSystems = attributes.stream()
                    .map(MergeAttributeDef::getSourceSystemsConfigs)
                    .flatMap(Collection::stream)
                    .collect(toList());
            if (entity.getMergeSettings().getBvrSettings() != null) {
                sourceSystems.addAll(entity.getMergeSettings().getBvrSettings().getSourceSystemsConfigs());
            }

            sourceSystems.stream()
                    .filter(Objects::nonNull)
                    .filter(sourceSystemDef -> !isValidSourceSystem(sourceSystemDef))
                    .map(SourceSystemDef::getName)
                    .distinct()
                    .map(name -> new ValidationResult("Entity [{}] contain Merge source system [{}] with incorrect params",
                            MERGE_SOURCE_SYSTEM_INCORRECT, entity.getDisplayName(), name))
                    .collect(Collectors.toCollection(() -> validationResults));

            sourceSystems.stream()
                    .map(SourceSystemDef::getName)
                    .distinct()
                    .filter(source -> !isSourceSystemPresent(ctx, source))
                    .map(name -> new ValidationResult("Entity [{}] contain Merge source system [{}] which is absent in system",
                            MERGE_SOURCE_SYSTEM_ABSENT, entity.getDisplayName(), name))
                    .collect(Collectors.toCollection(() -> validationResults));
        }
        return validationResults;
    }

    /**
     * Check source system
     *
     * @param ctx - context which contains all necessary info about model.
     * @return collection of validation results
     */
    private Collection<ValidationResult> checkTopLevelSourceSystem(UpdateModelRequestContext ctx) {
        return ctx.getSourceSystemsUpdate()
                .stream()
                .filter(Objects::nonNull)
                .filter(sourceSystemDef -> !isValidSourceSystem(sourceSystemDef))
                .map(SourceSystemDef::getName)
                .distinct()
                .map(name -> new ValidationResult("Source system [{}] is incorrect", SOURCE_SYSTEM_INCORRECT, name))
                .collect(toList());
    }

    /**
     * @param sourceSystemDef - source system
     * @return true if source system is valid
     */
    private boolean isValidSourceSystem(SourceSystemDef sourceSystemDef) {

        if (isBlank(sourceSystemDef.getName())) {
            return false;
        }

        BigInteger weight = sourceSystemDef.getWeight();
        return weight != null && weight.intValue() >= 0 && weight.intValue() <= 100;
    }

    /**
     * @param entityDef - entity
     * @param requiredAttrsNames -
     * @return collection of attribute's names which is nested or complex.
     */
    private Collection<String> findComplexAndNestedAttributes(EntityDef entityDef, Collection<String> requiredAttrsNames) {
        Collection<String> complexAndNestedAttributes = new ArrayList<>();
        Collection<String> nestedAttrs = requiredAttrsNames.stream()
                .filter(ModelUtils::isCompoundPath)
                .collect(Collectors.toCollection(() -> complexAndNestedAttributes));
        requiredAttrsNames.removeAll(nestedAttrs);
        requiredAttrsNames.stream()
                .filter(name -> isComplexAttribute(entityDef, name))
                .collect(Collectors.toCollection(() -> complexAndNestedAttributes));
        return complexAndNestedAttributes;
    }

    /**
     * Check relation timelines.
     *
     * @param ctx the ctx
     * @return the multimap<? extends validation error type,? extends string>
     */
    private Collection<ValidationResult> checkTimelines(UpdateModelRequestContext ctx) {
        //todo get MultiMap from ctx where key is entity and value all lookup attrs.
        Collection<ValidationResult> errors = new ArrayList<>();
        boolean isNewEntitiesPresent = CollectionUtils.isNotEmpty(ctx.getEntityUpdate());
        boolean isNewLookupEntitiesPresent = CollectionUtils.isNotEmpty(ctx.getLookupEntityUpdate());
        boolean isNewNestedEntitiesPresent = CollectionUtils.isNotEmpty(ctx.getNestedEntityUpdate());
        Map<String, PeriodBoundaryDef> periods = collectPeriods(ctx);

        if (!CollectionUtils.isEmpty(ctx.getRelationsUpdate())) {
            ctx.getRelationsUpdate()
                    .stream()
                    .map(rel -> checkOverlaping(periods, rel))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(() -> errors));
        }

        List<RelationDef> oldRels = metaModelService.getRelationsList();
        if (!CollectionUtils.isEmpty(oldRels) && (isNewEntitiesPresent || isNewLookupEntitiesPresent)) {
            oldRels.stream()
                    .map(rel -> checkOverlaping(periods, rel))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(() -> errors));
        }

        if (isNewEntitiesPresent) {
            ctx.getEntityUpdate()
                    .stream()
                    .forEach(entity -> getLinkToLookup(entity).stream()
                            .map(lookupName -> createRel(entity.getName(), lookupName))
                            .map(rel -> checkOverlaping(periods, rel))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toCollection(() -> errors)));
        }

        if (isNewLookupEntitiesPresent) {
            ctx.getLookupEntityUpdate()
                    .forEach(entity -> getLinkToLookup(entity).stream()
                            .map(lookupName -> createRel(entity.getName(), lookupName))
                            .map(rel -> checkOverlaping(periods, rel))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toCollection(() -> errors)));
        }

        if (isNewNestedEntitiesPresent && isNewEntitiesPresent) {
            ctx.getNestedEntityUpdate()
                    .forEach(entity -> getLinkToLookup(entity)
                            .stream()
                            //todo rewrite ctx for simple search
                            .map(lookupName -> createRel(getEntityByNestedEntity(ctx, entity.getName()).map(
                                    AbstractEntityDef::getName).orElse(null), lookupName))
                            .filter(rel -> rel.getFromEntity() != null)
                            .map(rel -> checkOverlaping(periods, rel))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toCollection(() -> errors)));
        }
        return errors;
    }

    private Collection<String> getLinkToLookup(SimpleAttributesHolderEntityDef attrHolder) {
        Collection<String> links = new ArrayList<>();
        attrHolder.getArrayAttribute()
                .stream()
                .filter(sa -> !isEmpty(sa.getLookupEntityType()))
                .map(ArrayAttributeDef::getLookupEntityType)
                .collect(Collectors.toCollection(() -> links));
        attrHolder.getSimpleAttribute()
                .stream()
                .filter(sa -> !isEmpty(sa.getLookupEntityType()))
                .map(SimpleAttributeDef::getLookupEntityType)
                .collect(Collectors.toCollection(() -> links));
        return links;
    }

    private RelationDef createRel(String from, String to) {
        return new RelationDef().withFromEntity(from).withToEntity(to);
    }

    private Optional<EntityDef> getEntityByNestedEntity(UpdateModelRequestContext ctx, String nestedName) {
        return ctx.getEntityUpdate()
                .stream()
                .filter(newEntity -> newEntity.getComplexAttribute()
                        .stream()
                        .anyMatch(ca -> ca.getName().equals(nestedName)))
                .findAny();
    }

    /**
     * Check overlaping between two ends of relation.
     *
     * @param periods all periods.
     * @param r relation def.
     * @return valudation result
     */
    private ValidationResult checkOverlaping(Map<String, PeriodBoundaryDef> periods, RelationDef r) {
        String fromEntity = r.getFromEntity();
        String toEntity = r.getToEntity();
        PeriodBoundaryDef fromPeriod = periods.get(fromEntity);
        PeriodBoundaryDef toPeriod = periods.get(toEntity);

        XMLGregorianCalendar fromCalStart = fromPeriod == null || fromPeriod.getStart() == null ?
                AbstractJaxbUtils.dateToXMGregorianCalendar(new Date(Long.MIN_VALUE)) :
                fromPeriod.getStart();
        XMLGregorianCalendar fromCalEnd = fromPeriod == null || fromPeriod.getEnd() == null ?
                AbstractJaxbUtils.dateToXMGregorianCalendar(new Date(Long.MAX_VALUE)) :
                fromPeriod.getEnd();
        Interval from = new Interval(fromCalStart.toGregorianCalendar().getTime().getTime(),
                fromCalEnd.toGregorianCalendar().getTime().getTime());

        XMLGregorianCalendar toCalStart = toPeriod == null || toPeriod.getStart() == null ?
                AbstractJaxbUtils.dateToXMGregorianCalendar(new Date(Long.MIN_VALUE)) :
                toPeriod.getStart();
        XMLGregorianCalendar toCalEnd = toPeriod == null || toPeriod.getEnd() == null ?
                AbstractJaxbUtils.dateToXMGregorianCalendar(new Date(Long.MAX_VALUE)) :
                toPeriod.getEnd();
        Interval to = new Interval(toCalStart.toGregorianCalendar().getTime().getTime(),
                toCalEnd.toGregorianCalendar().getTime().getTime());

        if (!from.overlaps(to)) {
            return new ValidationResult("Timeline is not overlaping.From entity [{}], To entity [{}]", TIMELINE_NOT_OVERLAPPING, fromEntity, toEntity);
        } else {
            return null;
        }
    }


    /**
     * Collect periods for all entities\lookups.
     *
     * @param ctx update model request context.
     * @return Map with collected periods.
     */
    private Map<String, PeriodBoundaryDef> collectPeriods(UpdateModelRequestContext ctx) {
        // Collect period for all entities/lookups
        final Map<String, PeriodBoundaryDef> periods = new HashMap<>();
        List<EntityDef> entityDefs = ctx.getEntityUpdate();
        if (entityDefs != null) {
            entityDefs.stream()
                    .map(entity -> Pair.of(entity.getName(), getPeriod(entity.getValidityPeriod())))
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (e1, e2) -> e1, () -> periods));
        }
        List<LookupEntityDef> lookupEntityDefs = ctx.getLookupEntityUpdate();
        if (lookupEntityDefs != null) {
            lookupEntityDefs.stream()
                    .map(entity -> Pair.of(entity.getName(), getPeriod(entity.getValidityPeriod())))
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (e1, e2) -> e1, () -> periods));
        }
        metaModelService.getEntitiesList().stream()
                .map(entity -> Pair.of(entity.getName(), getPeriod(entity.getValidityPeriod())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (e1, e2) -> e1, () -> periods));
        metaModelService.getLookupEntitiesList().stream()
                .map(entity -> Pair.of(entity.getName(), getPeriod(entity.getValidityPeriod())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (e1, e2) -> e1, () -> periods));
        return periods;
    }

    /**
     * Check groups.
     *
     * @param ctx the ctx
     * @return the collection of validation errors
     */
    private Collection<ValidationResult> checkGroups(@Nonnull final UpdateModelRequestContext ctx) {
        EntitiesGroupDef root = ctx.getEntitiesGroupsUpdate();
        if (root == null) {
            return Collections.emptyList();
        }

        Multimap<String, String> groupNames = HashMultimap.create();
        ctx.getEntityUpdate().forEach(ent -> groupNames.put(ent.getGroupName(), ent.getDisplayName()));
        ctx.getLookupEntityUpdate().forEach(ent -> groupNames.put(ent.getGroupName(), ent.getDisplayName()));

//        if (ctx.getUpsertType() != UpsertType.FULLY_NEW) {
//            metaModelService.getLookupEntitiesList()
//                    .forEach(ent -> groupNames.put(ent.getGroupName(), ent.getDisplayName()));
//
//            metaModelService.getEntitiesList()
//                    .forEach(ent -> groupNames.put(ent.getGroupName(), ent.getDisplayName()));
//        }

        Map<String, String[]> splitGroupNames = groupNames.keySet()
                .stream()
                .collect(Collectors.toMap(group -> group,
                        EntitiesGroupModelElementFacade::getSplitPath));

        Collection<ValidationResult> errors = new ArrayList<>();
        for (Map.Entry<String, String[]> splitName : splitGroupNames.entrySet()) {
            String fullGroupName = splitName.getKey();
            Collection<EntitiesGroupDef> entitiesGroupDefs = singletonList(root);
            for (String namePath : splitName.getValue()) {
                entitiesGroupDefs = entitiesGroupDefs.stream()
                        .filter(group -> group.getGroupName().equals(namePath))
                        .findAny()
                        .map(EntitiesGroupDef::getInnerGroups)
                        .orElse(null);
                if (entitiesGroupDefs == null) {
                    Collection<String> entities = groupNames.get(fullGroupName);
                    String message = "Group tree doesn't contains a node [{}] used in {}";
                    errors.add(new ValidationResult(message, NODE_GROUP_TREE_ABSENT, namePath, entities));
                    break;
                }
            }
        }
        return errors;
    }

    /**
     * Check references to lookup entities.
     *
     * @param ctx the ctx
     * @return the collection of validation errors
     */
    private Collection<ValidationResult> checkReferencesToLookupEntities(@Nonnull final UpdateModelRequestContext ctx) {
        boolean iFullUpdate = ctx.getUpsertType() == ModelUpsertType.FULLY_NEW;
        Collection<ValidationResult> errors = new ArrayList<>();
        Collection<SimpleAttributeDef> allLookupLinkAttr = ctx.getAttributes(
                attr -> (attr.getSimpleDataType() == null && !isBlank(attr.getLookupEntityType())));
        for (SimpleAttributeDef link : allLookupLinkAttr) {
            LookupEntityDef lookupEntityById = ctx.getLookupEntityUpdate()
                    .stream()
                    .filter(entity -> entity.getName().equals(link.getLookupEntityType()))
                    .findAny()
                    .orElse(null);
            if (!iFullUpdate && lookupEntityById == null) {
                lookupEntityById = metaModelService.getLookupEntityById(link.getLookupEntityType());
            }
            if (lookupEntityById == null) {
                errors.add(new ValidationResult("Attr [{}] has link to lookup entity [{}] which is absent", LOOKUP_ENTITY_ABSENT, link.getDisplayName(), link.getLookupEntityType()));
            }
            errors.addAll(validateCustomProperties(link.getName(), link.getCustomProperties()));
        }
        return errors;
    }

    /**
     * Check references to enumerations.
     *
     * @param ctx the ctx
     * @return the collection of validation errors
     */
    private Collection<ValidationResult> checkReferencesToEnumerations(@Nonnull final UpdateModelRequestContext ctx) {
        boolean isFullUpdate = ctx.getUpsertType() == ModelUpsertType.FULLY_NEW;
        Collection<ValidationResult> errors = new ArrayList<>();
        Collection<SimpleAttributeDef> allEnumLinkAttr = ctx.getAttributes(
                attr -> (attr.getSimpleDataType() == null && !isBlank(attr.getEnumDataType())));
        for (SimpleAttributeDef enumLink : allEnumLinkAttr) {
            EnumerationDataType enumerationDataType = ctx.getEnumerationsUpdate()
                    .stream()
                    .filter(entity -> entity.getName()
                            .equals(enumLink.getEnumDataType()))
                    .findAny()
                    .orElse(null);
            if (!isFullUpdate && enumerationDataType == null) {
                enumerationDataType = metaModelService.getEnumerationById(enumLink.getEnumDataType());
            }
            if (enumerationDataType == null) {
                errors.add(new ValidationResult("Attr [{}] has link to enum [{}] which is absent", ENUM_ABSENT, enumLink.getDisplayName(),
                        enumLink.getEnumDataType()));
            }
            errors.addAll(validateCustomProperties(enumLink.getName(), enumLink.getCustomProperties()));
        }
        return errors;
    }

    /**
     * Gets the duplicate names.
     *
     * @param ctx the ctx
     * @return the duplicate names
     */
    private Collection<String> getDuplicateNames(UpdateModelRequestContext ctx) {
        Collection<String> allNames = ctx.getAllTopModelElementNames();
        Collection<String> duplicateNames = new HashSet<>();

        ctx.getNestedEntityUpdate()
                .stream()
                .map(NestedEntityDef::getName)
                .filter(name -> (frequency(allNames, name)) > 1)
                .collect(Collectors.toCollection(() -> duplicateNames));

        ctx.getLookupEntityUpdate()
                .stream()
                .map(LookupEntityDef::getName)
                .filter(name -> (frequency(allNames, name)) > 1)
                .collect(Collectors.toCollection(() -> duplicateNames));

        ctx.getEntityUpdate()
                .stream()
                .map(EntityDef::getName)
                .filter(name -> (frequency(allNames, name)) > 1)
                .collect(Collectors.toCollection(() -> duplicateNames));

        ctx.getRelationsUpdate()
                .stream()
                .map(RelationDef::getName)
                .filter(name -> (frequency(allNames, name)) > 1)
                .collect(Collectors.toCollection(() -> duplicateNames));

        ctx.getSourceSystemsUpdate()
                .stream()
                .map(SourceSystemDef::getName)
                .filter(name -> (frequency(allNames, name)) > 1)
                .collect(Collectors.toCollection(() -> duplicateNames));

        return duplicateNames;
    }

    /**
     * Gets the all not presented attr.
     *
     * @param attrHolder the attr holder
     * @param attrNames the attr names
     * @param nestedEntities the nested entities
     * @return the all not presented attr
     */
    private Collection<String> allAbsentAttr(SimpleAttributesHolderEntityDef attrHolder, Collection<String> attrNames,
                                             Collection<NestedEntityDef> nestedEntities) {
        return attrNames.stream()
                .filter(attrName -> findModelAttribute(attrName, attrHolder, nestedEntities) == null)
                .collect(toList());
    }

    private boolean isSourceSystemPresent(UpdateModelRequestContext ctx, String sourceSystem) {
        if (StringUtils.isBlank(sourceSystem)) {
            return false;
        }
        boolean isFullyUpdate = ctx.getUpsertType() == ModelUpsertType.FULLY_NEW;
        boolean present = ctx.getSourceSystemsUpdate()
                .stream()
                .anyMatch(s -> s.getName().equals(sourceSystem));
        SourceSystemDef sourceSystemDef = isFullyUpdate ? null : metaModelService.getSourceSystemById(sourceSystem);
        return present || sourceSystemDef != null;
    }

    /**
     * Verify model elements.
     *
     * @param <W> the generic type
     * @param <E> the element type
     * @param modelElementClass the model element class
     * @param wrapperClass the wrapper class
     * @param ctx the ctx
     */
    private <W extends IdentityModelElement, E extends VersionedObjectDef> void verifyModelElements(Class<E> modelElementClass,
                                                                                            Class<W> wrapperClass, UpdateModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = metaModelService.getModelFacade(modelElementClass);
        if (modelFacade == null || !ModelType.isOf(modelElementClass, wrapperClass)) {
            return;
        }
        Collection<E> modelElements = ModelContextUtils.getUpdateByModelType(ctx, modelElementClass);
        modelElements.forEach(modelFacade::verifyModelElement);
    }

    /**
     * Check unique names.
     *
     * @param <W> the generic type
     * @param <E> the element type
     * @param modelElementClass the model element class
     * @param wrapperClass the wrapper class
     * @param ctx the ctx
     * @return the collection
     */
    private <W extends IdentityModelElement, E extends VersionedObjectDef> Collection<String> checkUniqueNames(
            Class<E> modelElementClass, Class<W> wrapperClass, UpdateModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = metaModelService.getModelFacade(modelElementClass);
        if (modelFacade == null || !ModelType.isOf(modelElementClass, wrapperClass)) {
            return emptyList();
        }
        Collection<E> modelElements = ModelContextUtils.getUpdateByModelType(ctx, modelElementClass);
        return modelElements.stream()
                .filter(modelElement -> !modelFacade.isUniqueModelElementId(modelElement))
                .map(modelFacade::getModelElementId).collect(toList());
    }

    private PeriodBoundaryDef getPeriod(PeriodBoundaryDef period) {
        if (isNull(period)) {
            period = new PeriodBoundaryDef();
            period.setStart(AbstractJaxbUtils.dateToXMGregorianCalendar(ValidityPeriodUtils.getGlobalValidityPeriodStart()));
            period.setEnd(AbstractJaxbUtils.dateToXMGregorianCalendar(ValidityPeriodUtils.getGlobalValidityPeriodEnd()));
        } else {
            if (isNull(period.getStart())) {
                period.setStart(AbstractJaxbUtils.dateToXMGregorianCalendar(ValidityPeriodUtils.getGlobalValidityPeriodStart()));
            }
            if (isNull(period.getEnd())) {
                period.setEnd(AbstractJaxbUtils.dateToXMGregorianCalendar(ValidityPeriodUtils.getGlobalValidityPeriodEnd()));
            }
        }
        return period;
    }

    /**
     * Find duplicate attribute names in lookups, entities, nested entities.
     *
     * @param ctx context to check.
     * @return list with errors.
     */
    private Collection<ValidationResult> checkAttributes(@Nonnull final UpdateModelRequestContext ctx) {
        Collection<ValidationResult> errors = new ArrayList<>();
        if (ctx.hasLookupEntityUpdate()) {
            ctx.getLookupEntityUpdate().forEach(el -> checkAttributes(errors, el, el.getName(), MetaType.LOOKUP));
        }
        if (ctx.hasEntityUpdate()) {
            ctx.getEntityUpdate().forEach(el -> checkAttributes(errors, el, el.getName(), MetaType.ENTITY));
        }
        if (ctx.hasNestedEntityUpdate()) {
            ctx.getNestedEntityUpdate().forEach(el -> checkAttributes(errors, el, el.getName(), MetaType.NESTED_ENTITY));
        }
        return errors;
    }

    /**
     * Search for duplicate attributes.
     *
     * @param errors list with errors.
     * @param el element to check.
     * @param elName element name.
     * @param type
     */
    private void checkAttributes(
            Collection<ValidationResult> errors,
            SimpleAttributesHolderEntityDef el,
            String elName,
            MetaType type
    ) {
        Set<String> dupl = new HashSet<>();
        Set<String> names = new HashSet<>();
        boolean isSearchable = false;
        for (SimpleAttributeDef sa : el.getSimpleAttribute()) {
            if (sa.isSearchable()) {
                isSearchable = true;
            }
            if (names.contains(sa.getName())) {
                dupl.add(sa.getName());
            }
            if (sa.getSimpleDataType() == null && !StringUtils.isEmpty(sa.getLinkDataType())) {
                if (sa.isDisplayable() || sa.isSearchable() || sa.isMainDisplayable() || !sa.isNullable()
                        || sa.isUnique()) {
                    String message = "Link attribute shouldn't be displayable, searchable, main displayable, required or unique. Entity name [{}]. Attribute name [{}].";
                    errors.add(new ValidationResult(message, LINK_ATTRIBUTE_INCORRECT, elName, sa.getName()));
                }
            }
            names.add(sa.getName());
        }
        for (ArrayAttributeDef aa : el.getArrayAttribute()) {
            if (names.contains(aa.getName())) {
                dupl.add(aa.getName());
            }
            if (aa.isSearchable()) {
                isSearchable = true;
            }
            names.add(aa.getName());
        }

        if (el instanceof LookupEntityDef) {
            LookupEntityDef lookup = (LookupEntityDef) el;
            if (lookup.getCodeAttribute().isSearchable()) {
                isSearchable = true;
            } else {
                CodeAttributeDef cad = lookup.getAliasCodeAttributes().stream()
                        .filter(CodeAttributeDef::isSearchable)
                        .findFirst()
                        .orElse(null);

                if (Objects.nonNull(cad)) {
                    isSearchable = true;
                }
            }
            errors.addAll(validateCustomProperties(
                    lookup.getCodeAttribute().getName(),
                    lookup.getCodeAttribute().getCustomProperties())
            );
        }

        if (!isSearchable && type != MetaType.NESTED_ENTITY) {
            String message = "Entity or Lookup [{}] does not contain at least one searchable attribute on the first level.";
            errors.add(new ValidationResult(message, NO_SEARCHABLE_ATTRIBUTE, elName));
        }

        if (!dupl.isEmpty()) {
            for (String duplName : dupl) {
                String message = "Found more than one attribute with name [{}]  in [{}]";
                errors.add(new ValidationResult(message, DUPL_ATTRIBUTE, duplName, elName));
            }
        }

        el.getSimpleAttribute().forEach(
                attr -> errors.addAll(validateCustomProperties(attr.getName(), attr.getCustomProperties()))
        );
        el.getArrayAttribute().forEach(
                attr -> errors.addAll(validateCustomProperties(attr.getName(), attr.getCustomProperties()))
        );
        if (el instanceof ComplexAttributesHolderEntityDef) {
            final ComplexAttributesHolderEntityDef complexAttributesHolder = (ComplexAttributesHolderEntityDef) el;
            complexAttributesHolder.getComplexAttribute().forEach(
                    attr -> errors.addAll(validateCustomProperties(attr.getName(), attr.getCustomProperties()))
            );
        }
    }

    private List<ValidationResult> validateCustomProperties(
            final String objectName,
            final List<CustomPropertyDef> customProperties
    ) {
        if (org.springframework.util.CollectionUtils.isEmpty(customProperties)) {
            return Collections.emptyList();
        }
        final Set<String> invalidNames = new HashSet<>();
        final Set<String> duplicatedNames = new HashSet<>();
        final Set<String> propertiesNames = new HashSet<>();
        customProperties.forEach(property -> {
            final String propertyName = property.getName();
            if (!AbstractModelElementFacade.NAME_PATTERN.matcher(propertyName).matches()) {
                invalidNames.add(propertyName);
            }
            if (!propertiesNames.add(propertyName)) {
                duplicatedNames.add(propertyName);
            }
        });
        final List<ValidationResult> validationResults = new ArrayList<>();
        if (!invalidNames.isEmpty()) {
            validationResults.add(
                    new ValidationResult(
                            "Invalid properties names: " + invalidNames,
                            CUSTOM_PROPERTY_INVALID_NAMES,
                            invalidNames,
                            objectName
                    )
            );
        }
        if (!duplicatedNames.isEmpty()) {
            validationResults.add(
                    new ValidationResult(
                            "Duplicated properties names: " + duplicatedNames,
                            CUSTOM_PROPERTY_DUPLICATED_NAMES,
                            duplicatedNames,
                            objectName
                    )
            );
        }
        return validationResults;
    }


    /**
     * Check references to group from entities and lookups.
     *
     * @param ctx context.
     * @return validation errors.
     */
    private Collection<ValidationResult> checkReferencesToGroups(@Nonnull final UpdateModelRequestContext ctx) {
        EntitiesGroupDef rootGroup = null;
        if (ctx.hasEntitiesGroupUpdate()) {
            rootGroup = ctx.getEntitiesGroupsUpdate();
        } else {
            rootGroup = metaModelService.getRootGroup(SecurityUtils.getStorageId(ctx));
        }
        Set<String> groupNames = new HashSet<>();
        groupNames("", rootGroup, groupNames);
        Collection<ValidationResult> errors = new ArrayList<>();
        if (ctx.hasEntityUpdate()) {
            List<EntityDef> entities = ctx.getEntityUpdate();
            for (EntityDef entity : entities) {
                if (!groupNames.contains(entity.getGroupName())) {
                    String message = "Group tree doesn't contains a node [{}] used in [{}]";
                    errors.add(new ValidationResult(message, NODE_GROUP_TREE_ABSENT, entity.getGroupName(), entity.getName()));
                }
            }
        }
        if (ctx.hasLookupEntityUpdate()) {
            List<LookupEntityDef> lookups = ctx.getLookupEntityUpdate();
            for (LookupEntityDef lookup : lookups) {
                if (!groupNames.contains(lookup.getGroupName())) {
                    String message = "Group tree doesn't contains a node [{}] used in [{}]";
                    errors.add(new ValidationResult(message, NODE_GROUP_TREE_ABSENT, lookup.getGroupName(), lookup.getName()));
                }
            }
        }

        return errors;
    }

    /**
     * Create set with group names.
     *
     * @param path group path.
     * @param group entity group.
     * @param groupNames group names.
     */
    private void groupNames(String path, EntitiesGroupDef group, Set<String> groupNames) {
        path = StringUtils.isEmpty(path) ? group.getGroupName() : String.join(".", path, group.getGroupName());
        groupNames.add(path);
        if (CollectionUtils.isNotEmpty(group.getInnerGroups())) {
            for (EntitiesGroupDef innerGroup : group.getInnerGroups()) {
                groupNames(path, innerGroup, groupNames);
            }
        }
    }
}
