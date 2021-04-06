package com.unidata.mdm.backend.service.model.draft;

import static com.unidata.mdm.backend.service.cleanse.CFUtils.createCleanseFunction;
import static com.unidata.mdm.backend.service.cleanse.CFUtils.getFunction;
import static com.unidata.mdm.backend.service.model.util.ModelUtils.findModelAttribute;
import static com.unidata.mdm.backend.service.model.util.ModelUtils.isComplexAttribute;
import static com.unidata.mdm.meta.SimpleDataType.ANY;
import static com.unidata.mdm.meta.SimpleDataType.MEASURED;
import static com.unidata.mdm.meta.SimpleDataType.NUMBER;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.meta.AbstractEntityDef;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpsertType;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.MetaModelValidationException;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.common.service.MetaDraftService;
import com.unidata.mdm.backend.service.cleanse.CleanseFunctionServiceExt;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.model.ie.GraphCreator;
import com.unidata.mdm.backend.service.model.ie.dto.FullModelDTO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdge;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdgeFactory;
import com.unidata.mdm.backend.service.model.ie.dto.MetaExistence;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
import com.unidata.mdm.backend.service.model.ie.dto.MetaMessage;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.facades.AbstractModelElementFacade;
import com.unidata.mdm.backend.service.model.util.facades.EntitiesGroupModelElementFacade;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AbstractSimpleAttributeDef;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.AttributeGroupDef;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.ComplexAttributesHolderEntityDef;
import com.unidata.mdm.meta.ConstantValueDef;
import com.unidata.mdm.meta.ConstantValueType;
import com.unidata.mdm.meta.CustomPropertyDef;
import com.unidata.mdm.meta.DQApplicableType;
import com.unidata.mdm.meta.DQREnrichDef;
import com.unidata.mdm.meta.DQRMappingDef;
import com.unidata.mdm.meta.DQRRaiseDef;
import com.unidata.mdm.meta.DQRSourceSystemRef;
import com.unidata.mdm.meta.DQRuleClass;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.DQRuleType;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.ListOfCleanseFunctions;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.MergeAttributeDef;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.Port;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleAttributesHolderEntityDef;
import com.unidata.mdm.meta.SimpleDataType;
import com.unidata.mdm.meta.SourceSystemDef;

/**
 * The Class MetaDraftValidationComponent.
 */
@Component
public class MetaDraftValidationComponentImpl implements MetaDraftValidationComponent {
    /**
     * The Constant INITIAL_ERROR_MESSAGE.
     */
    private static final String INITIAL_ERROR_MESSAGE = "Model is incorrect.";

    /**
     * Message's code
     */
    private static final String DQ_ATTR_TYPES_ARE_NOT_MATCH = "app.meta.dq.attr.type.mismatch";
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
    private static final String FUNCTION_ABSENT = "app.meta.cleanse.function.absent";
    private static final String FUNCTION_DO_NOTHING = "app.meta.cleanse.function.do.nothing";
    private static final String VALIDATION_DQ_INCORRECT = "app.meta.dq.validation.incorrect";
    private static final String DQ_DOESNT_CONTAIN_REQUIRED_PORT = "app.meta.dq.doesnt.contain.port";
    private static final String ENRICH_DQ_INCORRECT = "app.meta.dq.enrich.incorrect";
    private static final String ENRICH_SOURCE_SYSTEM_ABSENT = "app.meta.dq.enrich.source.system.absent";
    private static final String ORIGIN_SOURCE_SYSTEM_ABSENT = "app.meta.dq.origin.source.system.absent";
    private static final String DQ_ATTR_ABSENT = "app.meta.dq.attr.absent";
    private static final String DQ_ATTR_HAS_INCORRECT_TYPE = "app.meta.dq.attr.incorrect.type";
    private static final String CONSTANT_IS_ABSENT = "app.meta.dq.constant.absent";
    private static final String CONSTANT_HAS_INCORRECT_TYPE = "app.meta.dq.constant.incorrect.type";
    private static final String META_DUPLICATED_NAMES = "app.meta.import.elDuplicate";
    private static final String MODIFIED_RELATION_TYPE_ON_ELEMENT_WITH_DATA = "app.meta.relation.type.modified";
    private static final String CUSTOM_PROPERTY_INVALID_NAMES = "app.custom.property.invalid.names.on.object";
    private static final String CUSTOM_PROPERTY_DUPLICATED_NAMES = "app.custom.property.duplicated.names.on.object";
    private static final String MEASUREMENT_REMOVING_FORBIDDEN = "app.measurement.removing.forbidden";
    private static final String MEASUREMENT_UNITS_INCORRECT = "app.meta.measurement.settings.refer.undefine.unit";

    /**
     * The cleanse function service.
     */
    @Autowired
    private CleanseFunctionServiceExt cleanseFunctionService;

    /**
     * The meta model service.
     */
    // todo separate metaModelService to modelCache and service!
    @Autowired
    private MetaDraftService metaDraftService;
    @Autowired
    private GraphCreator graphCreator;

    @Autowired
    private RelationsServiceComponent relationsServiceComponent;

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.model.MetaModelValidationService#
     * validateUpdateModelContext(com.unidata.mdm.backend.service.model.
     * UpdateModelRequestContext)
     */
    @Override
    // todo rewrite all list in context to maps! (and this component at all)
    public void validateUpdateModelContext(@Nonnull final UpdateModelRequestContext ctx) {

        // validate full model.
        boolean isFullModelCtx = ctx.getUpsertType() == UpsertType.FULLY_NEW;
        if (ctx.getEntitiesGroupsUpdate() == null
                && (isFullModelCtx || metaDraftService.getRootGroup(SecurityUtils.getStorageId(ctx)) == null)) {
            throw new BusinessException("Root group is absent", ExceptionId.EX_META_ROOT_GROUP_IS_ABSENT);
        }

        // todo replace some checks to verifyModelElements!
        Collection<ValidationResult> validations = new ArrayList<>();

        // check attributes groups
        validations.addAll(checkDisplayGroups(ctx));
        // check attributes
        validations.addAll(checkAttributes(ctx));
        // check source systems
        validations.addAll(checkSourceSystems(ctx));
        // check merge settings
        validations.addAll(checkMergeSettings(ctx));

        // check source systems
        validations.addAll(checkTopLevelSourceSystem(ctx));

        // check dq
        validations.addAll(checkDqRules(ctx));

        // check references
        validations.addAll(checkReferencesToEnumerations(ctx));
        validations.addAll(checkReferencesToLookupEntities(ctx));
        validations.addAll(checkReferencesToGroups(ctx));

        // relations
        validations.addAll(checkRelations(ctx));

        // check groups
        validations.addAll(checkGroups(ctx));

        // // validate timelines
        // validations.addAll(checkTimelines(ctx));
        // validate connectivity
        validations.addAll(checkConnectivity(ctx));

        // check modification of relation type on elements with data
        validations.addAll(checkModificationRelationTypeOnElementsWithData(ctx));

        if (!validations.isEmpty()) {
            throw new MetaModelValidationException(INITIAL_ERROR_MESSAGE, ExceptionId.EX_DRAFT_IS_INCORRECT,
                    validations);
        }
    }

    private Collection<ValidationResult> checkRelations(UpdateModelRequestContext ctx) {
        return ctx.getRelationsUpdate().stream().flatMap(relation -> {
            final List<ValidationResult> validationResults = new ArrayList<>();
            validationResults.addAll(validateCustomProperties(relation.getName(), relation.getCustomProperties()));
            validationResults.addAll(relation.getSimpleAttribute().stream()
                    .flatMap(attr -> validateCustomProperties(attr.getName(), attr.getCustomProperties()).stream())
                    .collect(toList()));
            validationResults.addAll(relation.getComplexAttribute().stream()
                    .flatMap(attr -> validateCustomProperties(attr.getName(), attr.getCustomProperties()).stream())
                    .collect(toList()));
            validationResults.addAll(relation.getArrayAttribute().stream()
                    .flatMap(attr -> validateCustomProperties(attr.getName(), attr.getCustomProperties()).stream())
                    .collect(toList()));
            return validationResults.stream();
        }).collect(toList());

    }

    /**
     * Check modification of relation type on elements with data.
     *
     * @param ctx context.
     * @return list with errors(if any).
     */
    private Collection<? extends ValidationResult> checkModificationRelationTypeOnElementsWithData(
            final UpdateModelRequestContext ctx) {
        final List<RelationDef> modifiedRelations = ctx.getRelationsUpdate().stream().filter(
                relationDef -> relationsServiceComponent.checkExistDataByRelName(relationDef.getName()))
                .filter(relationDef -> {
                    RelationDef oldRelationDef = metaDraftService.getRelationById(relationDef.getName());
                    return oldRelationDef != null && relationDef.getRelType() != oldRelationDef.getRelType();
                }).collect(toList());
        final String message = "Relation type of [{}] can't be modified, because element has data";
        return modifiedRelations
                .isEmpty()
                ? Collections.emptyList()
                : modifiedRelations.stream()
                .map(relationDef -> new ValidationResult(message,
                        MODIFIED_RELATION_TYPE_ON_ELEMENT_WITH_DATA, relationDef.getName()))
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
        if (ctx.getUpsertType() != UpsertType.FULLY_NEW) {
            Model model = metaDraftService.exportModel(ctx.getStorageId());
            FullModelDTO fullModelDTO = new FullModelDTO().withModel(model);
            graphCreator.enrich(fullModelDTO, result, MetaExistence.EXIST, MetaType.ENTITY, MetaType.NESTED_ENTITY,
                    MetaType.ENUM, MetaType.GROUPS, MetaType.LOOKUP, MetaType.SOURCE_SYSTEM);
        }
        Model model = new Model();
        if (ctx.hasCleanseFunctionsUpdate()) {
            model.withCleanseFunctions(new ListOfCleanseFunctions().withGroup(ctx.getCleanseFunctionsUpdate()));
        }
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
        graphCreator.enrich(fullModelDTO, result, MetaExistence.EXIST, MetaType.ENTITY, MetaType.NESTED_ENTITY,
                MetaType.ENUM, MetaType.GROUPS, MetaType.LOOKUP, MetaType.SOURCE_SYSTEM);
        for (MetaVertex to : result.vertexSet()) {
            Set<MetaEdge<MetaVertex>> edges = result.incomingEdgesOf(to);
            if (to.getMessages() != null && to.getMessages().size() != 0) {
                for (MetaMessage metaMessage : to.getMessages()) {
                    errors.add(new ValidationResult(META_DUPLICATED_NAMES, String.join("\n", metaMessage.getMessages()), ""));
                }
            }
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
        if (ctx.getUpsertType() != UpsertType.FULLY_NEW) {
            List<SourceSystemDef> fromModel = metaDraftService.getSourceSystemsList();
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
                    // if source system type changed from admin to ordinary source system remove it
                    // from the set
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
            Collection<String> requiredAttr = entity.getAttributeGroups().stream().map(AttributeGroupDef::getAttributes)
                    .flatMap(Collection::stream).collect(Collectors.toSet());
            Collection<String> notPresentedAttrs = allAbsentAttr(entity, requiredAttr, ctx.getNestedEntityUpdate());
            notPresentedAttrs.stream()
                    .map(name -> new ValidationResult("Display group of entity [{}] contain absent attr [{}]",
                            UNKNOWN_ATTR_IN_DISPLAY_GROUP, entity.getDisplayName(), name))
                    .collect(Collectors.toCollection(() -> validationResults));

            // complex attributes can't be in attribute's group settings!
            Collection<String> complexAndNestedAttributes = findComplexAndNestedAttributes(entity, requiredAttr);
            complexAndNestedAttributes.stream()
                    .map(name -> new ValidationResult(
                            "Display group of entity [{}] contains complex or nested attr [{}]",
                            DISPLAY_GROUP_CONTAINS_UNAVAILABLE_ATTR, entity.getDisplayName(), name))
                    .collect(Collectors.toCollection(() -> validationResults));
        }

        for (LookupEntityDef entity : ctx.getLookupEntityUpdate()) {
            Collection<String> requiredAttrs = entity.getAttributeGroups().stream()
                    .map(AttributeGroupDef::getAttributes).flatMap(Collection::stream).collect(Collectors.toSet());
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
            // check attrs
            List<MergeAttributeDef> attributes = entity.getMergeSettings().getBvtSettings() == null
                    ? Collections.emptyList()
                    : entity.getMergeSettings().getBvtSettings().getAttributes();

            Collection<String> requiredAttrs = attributes.stream().map(MergeAttributeDef::getName)
                    .collect(Collectors.toSet());

            requiredAttrs.stream().filter(attr -> StringUtils.isBlank(attr) || ModelUtils.isCompoundPath(attr))
                    .map(name -> new ValidationResult("Merge attr [{}] is incorrect in entity [{}]",
                            MERGE_ATTR_INCORRECT, name, entity.getDisplayName()))
                    .collect(Collectors.toCollection(() -> validationResults));

            Collection<String> absentAttrs = allAbsentAttr(entity, requiredAttrs, ctx.getNestedEntityUpdate());
            absentAttrs.stream()
                    .map(name -> new ValidationResult("Entity [{}] contain merge attr [{}] which is absent",
                            MERGE_ATTR_ABSENT, entity.getDisplayName(), name))
                    .collect(Collectors.toCollection(() -> validationResults));

            // check merge source system
            Collection<SourceSystemDef> sourceSystems = attributes.stream()
                    .map(MergeAttributeDef::getSourceSystemsConfigs).flatMap(Collection::stream).collect(toList());
            if (entity.getMergeSettings().getBvrSettings() != null) {
                sourceSystems.addAll(entity.getMergeSettings().getBvrSettings().getSourceSystemsConfigs());
            }

            sourceSystems.stream().filter(Objects::nonNull)
                    .filter(sourceSystemDef -> !isValidSourceSystem(sourceSystemDef)).map(SourceSystemDef::getName)
                    .distinct()
                    .map(name -> new ValidationResult(
                            "Entity [{}] contain Merge source system [{}] with incorrect params",
                            MERGE_SOURCE_SYSTEM_INCORRECT, entity.getDisplayName(), name))
                    .collect(Collectors.toCollection(() -> validationResults));

            sourceSystems.stream().map(SourceSystemDef::getName).distinct()
                    .filter(source -> !isSourceSystemPresent(ctx, source))
                    .map(name -> new ValidationResult(
                            "Entity [{}] contain Merge source system [{}] which is absent in system",
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
        return ctx.getSourceSystemsUpdate().stream().filter(Objects::nonNull)
                .filter(sourceSystemDef -> !isValidSourceSystem(sourceSystemDef)).map(SourceSystemDef::getName)
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
        if (weight == null || weight.intValue() < 0 || weight.intValue() > 100) {
            return false;
        }
        return true;
    }

    /**
     * @param entityDef          - entity
     * @param requiredAttrsNames -
     * @return collection of attribute's names which is nested or complex.
     */
    private Collection<String> findComplexAndNestedAttributes(EntityDef entityDef,
                                                              Collection<String> requiredAttrsNames) {
        Collection<String> complexAndNestedAttributes = new ArrayList<>();
        Collection<String> nestedAttrs = requiredAttrsNames.stream().filter(ModelUtils::isCompoundPath)
                .collect(Collectors.toCollection(() -> complexAndNestedAttributes));
        requiredAttrsNames.removeAll(nestedAttrs);
        requiredAttrsNames.stream().filter(name -> isComplexAttribute(entityDef, name))
                .collect(Collectors.toCollection(() -> complexAndNestedAttributes));
        return complexAndNestedAttributes;
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

        if (ctx.getUpsertType() != UpsertType.FULLY_NEW) {
            metaDraftService.getLookupEntitiesList()
                    .forEach(ent -> groupNames.put(ent.getGroupName(), ent.getDisplayName()));

            metaDraftService.getEntitiesList().forEach(ent -> groupNames.put(ent.getGroupName(), ent.getDisplayName()));
        }

        Map<String, String[]> splitGroupNames = groupNames.keySet().stream()
                .collect(Collectors.toMap((group) -> group, EntitiesGroupModelElementFacade::getSplitPath));

        Collection<ValidationResult> errors = new ArrayList<>();
        for (Map.Entry<String, String[]> splitName : splitGroupNames.entrySet()) {
            String fullGroupName = splitName.getKey();
            Collection<EntitiesGroupDef> entitiesGroupDefs = singletonList(root);
            for (String namePath : splitName.getValue()) {
                entitiesGroupDefs = entitiesGroupDefs.stream().filter(group -> group.getGroupName().equals(namePath))
                        .findAny().map(EntitiesGroupDef::getInnerGroups).orElse(null);
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
        boolean iFullUpdate = ctx.getUpsertType() == UpsertType.FULLY_NEW;
        Collection<ValidationResult> errors = new ArrayList<>();
        Collection<SimpleAttributeDef> allLookupLinkAttr = ctx
                .getAttributes(attr -> (attr.getSimpleDataType() == null && !isBlank(attr.getLookupEntityType())));
        for (SimpleAttributeDef link : allLookupLinkAttr) {
            LookupEntityDef lookupEntityById = ctx.getLookupEntityUpdate().stream()
                    .filter(entity -> entity.getName().equals(link.getLookupEntityType())).findAny().orElse(null);
            if (!iFullUpdate && lookupEntityById == null) {
                lookupEntityById = metaDraftService.getLookupEntityById(link.getLookupEntityType());
            }
            if (lookupEntityById == null) {
                errors.add(new ValidationResult("Attr [{}] has link to lookup entity [{}] which is absent",
                        LOOKUP_ENTITY_ABSENT, link.getDisplayName(), link.getLookupEntityType()));
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
        boolean isFullUpdate = ctx.getUpsertType() == UpsertType.FULLY_NEW;
        Collection<ValidationResult> errors = new ArrayList<>();
        Collection<SimpleAttributeDef> allEnumLinkAttr = ctx
                .getAttributes(attr -> (attr.getSimpleDataType() == null && !isBlank(attr.getEnumDataType())));
        for (SimpleAttributeDef enumLink : allEnumLinkAttr) {
            EnumerationDataType enumerationDataType = ctx.getEnumerationsUpdate().stream()
                    .filter(entity -> entity.getName().equals(enumLink.getEnumDataType())).findAny().orElse(null);
            if (!isFullUpdate && enumerationDataType == null) {
                enumerationDataType = metaDraftService.getEnumerationById(enumLink.getEnumDataType());
            }
            if (enumerationDataType == null) {
                errors.add(new ValidationResult("Attr [{}] has link to enum [{}] which is absent", ENUM_ABSENT,
                        enumLink.getDisplayName(), enumLink.getEnumDataType()));
            }
            errors.addAll(validateCustomProperties(enumLink.getName(), enumLink.getCustomProperties()));
        }
        return errors;
    }

    /**
     * Gets the all not presented attr.
     *
     * @param attrHolder     the attr holder
     * @param attrNames      the attr names
     * @param nestedEntities the nested entities
     * @return the all not presented attr
     */
    private Collection<String> allAbsentAttr(SimpleAttributesHolderEntityDef attrHolder, Collection<String> attrNames,
                                             Collection<NestedEntityDef> nestedEntities) {
        return attrNames.stream().filter(attrName -> findModelAttribute(attrName, attrHolder, nestedEntities) == null)
                .collect(toList());
    }

    /**
     * Check dq
     *
     * @param ctx the ctx
     * @return the collection of validation errors
     */
    private Collection<ValidationResult> checkDqRules(UpdateModelRequestContext ctx) {
        Collection<ValidationResult> validations = new ArrayList<>();
        ctx.getEntityUpdate().stream()
                .map(entity -> entity.getDataQualities().stream().filter(dq -> !dq.isSpecial())
                        .map(dq -> checkDqRule(entity, dq, ctx)).collect(toList()))
                .flatMap(Collection::stream).flatMap(Collection::stream)
                .collect(Collectors.toCollection(() -> validations));

        ctx.getLookupEntityUpdate().stream()
                .map(entity -> entity.getDataQualities().stream().filter(dq -> !dq.isSpecial())
                        .map(dq -> checkDqRule(entity, dq, ctx)).collect(toList()))
                .flatMap(Collection::stream).flatMap(Collection::stream)
                .collect(Collectors.toCollection(() -> validations));
        return validations;
    }

    /**
     * Check dq rules.
     *
     * @param attributesHolder the attributes holder
     * @param dqRule           the dq rules
     * @param ctx              - context!
     * @return the collection of validation errors
     */
    private Collection<ValidationResult> checkDqRule(SimpleAttributesHolderEntityDef attributesHolder, DQRuleDef dqRule,
                                                     UpdateModelRequestContext ctx) {
        String entity = attributesHolder.getName();
        CleanseFunctionExtendedDef cleanseFunction = null;
        String functionName = dqRule.getCleanseFunctionName();
        // UN-4707
        // Custom functions can be only on the first level and they are imported
        // separately.
        // Because we don't have special type for them yet,
        // && (StringUtils.contains(functionName, ".")
        // ||(getFunction(EMPTY, functionName,
        // ctx.getCleanseFunctionsUpdate())instanceof CleanseFunctionExtendedDef))
        // was added, it can be removed after introduction of the new type.
        if (ctx.hasCleanseFunctionsUpdate() && (StringUtils.contains(functionName, ".") || (getFunction(EMPTY,
                functionName, ctx.getCleanseFunctionsUpdate()) instanceof CleanseFunctionExtendedDef))) {
            CleanseFunctionGroupDef group = ctx.getCleanseFunctionsUpdate();
            CleanseFunctionDef cleanseFunctionDef = isNull(group) ? null : getFunction(EMPTY, functionName, group);
            // composite cleanse function
            if (cleanseFunctionDef instanceof CleanseFunctionExtendedDef) {
                cleanseFunction = (CleanseFunctionExtendedDef) cleanseFunctionDef;
            } else {
                CleanseFunction function = isNull(cleanseFunctionDef) ? null
                        : createCleanseFunction(cleanseFunctionDef);
                cleanseFunction = isNull(function) ? null : function.getDefinition();
            }
        } else {
            cleanseFunction = cleanseFunctionService.getByID(functionName);
        }

        if (isNull(cleanseFunction)) {
            return singletonList(new ValidationResult("Cleanse Function [{}] is absent in entity [{}]", FUNCTION_ABSENT,
                    functionName, entity));
        }
        // UN-4824
        dqRule.getType().clear();
        if (dqRule.getEnrich() != null) {
            dqRule.getType().add(DQRuleType.ENRICH);
        }
        if (dqRule.getRaise() != null) {
            dqRule.getType().add(DQRuleType.VALIDATE);
        }
        if (dqRule.getRClass() == null) {
            dqRule.setRClass(DQRuleClass.USER_DEFINED);
        }
        //
        if (!dqRule.getType().stream().anyMatch(Objects::nonNull)) {
            return singletonList(new ValidationResult("Cleanse Function [{}] do nothing in entity [{}]",
                    FUNCTION_DO_NOTHING, functionName, entity));
        }

        Collection<ValidationResult> errors = new ArrayList<>();
        if (dqRule.getType().contains(DQRuleType.VALIDATE)) {
            DQRRaiseDef raiseDef = dqRule.getRaise();
            if (isNull(raiseDef) || (isBlank(raiseDef.getMessageText()) && isBlank(raiseDef.getMessagePort()))
                    || isBlank(raiseDef.getFunctionRaiseErrorPort()) || isNull(raiseDef.getSeverityValue())) {
                errors.add(new ValidationResult("Validation Dq [{}] is incorrect in entity [{}]",
                        VALIDATION_DQ_INCORRECT, functionName, entity));
            }
        }

        if (dqRule.getType().contains(DQRuleType.ENRICH)) {
            DQREnrichDef enrich = dqRule.getEnrich();
            if (enrich == null || enrich.getAction() == null || enrich.getPhase() == null) {
                errors.add(new ValidationResult("Enrich DQ  [{}] is incorrect in entity [{}]", ENRICH_DQ_INCORRECT,
                        functionName, entity));
            }
            if (dqRule.getApplicable().contains(DQApplicableType.ETALON) && enrich != null) {
                String source = enrich.getSourceSystem();
                if (!isSourceSystemPresent(ctx, source)) {
                    errors.add(new ValidationResult(
                            "Enrich Etalon DQ [{}] in entity [{}] contain absent source system [{}]",
                            ENRICH_SOURCE_SYSTEM_ABSENT, functionName, entity, source));
                }
            }
        }

        if (dqRule.getApplicable().contains(DQApplicableType.ORIGIN) && !dqRule.getOrigins().isAll()) {
            dqRule.getOrigins().getSourceSystem().stream().map(DQRSourceSystemRef::getName)
                    .filter(sourceName -> !isSourceSystemPresent(ctx, sourceName))
                    .map(name -> new ValidationResult(
                            "DQ [{}] in entity [{}] contain source system [{}] which is absent",
                            ORIGIN_SOURCE_SYSTEM_ABSENT, functionName, entity, name))
                    .collect(Collectors.toCollection(() -> errors));
        }

        // check all necessary fields for validation
        for (Port port : cleanseFunction.getInputPorts()) {
            // check input ports
            DQRMappingDef input = dqRule.getDqrMapping().stream()
                    .filter(map -> port.getName().equals(map.getInputPort())).findAny().orElse(null);

            if (input == null) {
                if (port.isRequired()) {
                    errors.add(new ValidationResult(
                            "Cleanse function [{}] from dq rule [{}] of entity [{}] doesnt contains value for required input port  [{}]",
                            DQ_DOESNT_CONTAIN_REQUIRED_PORT, functionName, dqRule.getName(), entity, port.getName()));
                }
                continue;
            }

            String attrName = input.getAttributeName();
            AbstractAttributeDef portAttr = attrName == null ? null
                    : findModelAttribute(attrName, attributesHolder, ctx.getNestedEntityUpdate());
            ValidationResult mappingValidation = null;
            if (attrName != null) {
                if (portAttr == null) {
                    mappingValidation = new ValidationResult("Dq [{}] in entity [{}] contain attr [{}] which is absent",
                            DQ_ATTR_ABSENT, functionName, dqRule.getName(), entity, attrName);
                } else if (!(portAttr instanceof AbstractSimpleAttributeDef)) {
                    mappingValidation = new ValidationResult(
                            "Dq [{}] in entity [{}] contain attr [{}] which has incorrect type",
                            DQ_ATTR_HAS_INCORRECT_TYPE, functionName, dqRule.getName(), entity, attrName);
                } else {
                    mappingValidation = checkAttributeTypeInMapping(port, portAttr, functionName, entity, attrName);
                }
            } else {
                mappingValidation = checkConstantInMapping(input, port, functionName, entity);
            }
            if (mappingValidation != null) {
                errors.add(mappingValidation);
            }
        }

        for (Port port : cleanseFunction.getOutputPorts()) {
            // check output ports
            DQRMappingDef output = dqRule.getDqrMapping().stream()
                    .filter(map -> port.getName().equals(map.getOutputPort())).findAny().orElse(null);

            if (output == null) {
                continue;
            }
            if (output.getAttributeConstantValue() != null && output.getAttributeConstantValue().getType() == null) {
                output.setAttributeConstantValue(null);
            }
            String attrName = output.getAttributeName();
            AbstractAttributeDef portAttr = attrName == null ? null
                    : findModelAttribute(attrName, attributesHolder, ctx.getNestedEntityUpdate());
            ValidationResult mappingValidation = null;
            if (attrName != null) {
                if (portAttr == null) {
                    mappingValidation = new ValidationResult("Dq [{}] in entity [{}]. Attr [{}] is absent",
                            DQ_ATTR_ABSENT, functionName, dqRule.getName(), entity, attrName);
                } else if (!(portAttr instanceof SimpleAttributeDef)) {
                    mappingValidation = new ValidationResult("Dq [{}] in entity [{}]. Attr [{}] has incorrect type",
                            DQ_ATTR_HAS_INCORRECT_TYPE, functionName, dqRule.getName(), entity, attrName);
                } else {
                    mappingValidation = checkAttributeTypeInMapping(port, portAttr, functionName, entity, attrName);
                }
            } else {
                mappingValidation = checkConstantInMapping(output, port, functionName, entity);
            }
            if (mappingValidation != null) {
                errors.add(mappingValidation);
            }
        }
        return errors;
    }

    @Nullable
    private ValidationResult checkAttributeTypeInMapping(Port port, AbstractAttributeDef portAttr, String functionName,
                                                         String entity, String attrName) {
        SimpleDataType portDefType = port.getDataType();
        if (portDefType == ANY) {
            return null;
        }
        SimpleDataType portAttrType = ((AbstractSimpleAttributeDef) portAttr).getSimpleDataType();
        SimpleDataType convertedType = portAttrType == MEASURED ? NUMBER : portAttrType;
        if (convertedType != portDefType) {
            return new ValidationResult(
                    "Attr [{}] with type [{}] in function [{}] in entity [{}] does not match with port type [{}]",
                    DQ_ATTR_TYPES_ARE_NOT_MATCH, attrName, portAttrType.name(), functionName, entity,
                    portDefType.name());
        } else {
            return null;
        }
    }

    @Nullable
    private ValidationResult checkConstantInMapping(DQRMappingDef input, Port port, String functionName,
                                                    String entity) {
        ConstantValueDef constant = input.getAttributeConstantValue();
        if (constant == null || constant.getType() == null) {
            return new ValidationResult("Dq [{}] in entity [{}]. Constant in port [{}] is absent", CONSTANT_IS_ABSENT,
                    functionName, entity, port.getName());
        }
        SimpleDataType portDefType = port.getDataType();
        if (portDefType == ANY) {
            return null;
        }
        SimpleDataType convertedType = portDefType == MEASURED ? NUMBER : portDefType;
        ConstantValueType constantValueType = constant.getType();
        if (!constantValueType.name().equals(convertedType.name())) {
            return new ValidationResult(
                    "Dq [{}] in entity [{}]. Constant in port [{}] has incorrect type.Has [{}] , need [{}]",
                    CONSTANT_HAS_INCORRECT_TYPE, functionName, entity, port.getName(), constantValueType.name(),
                    portDefType.name());
        }
        // todo add constant type vs value check
        return null;
    }

    private boolean isSourceSystemPresent(UpdateModelRequestContext ctx, String sourceSystem) {
        if (StringUtils.isBlank(sourceSystem)) {
            return false;
        }
        boolean isFullyUpdate = ctx.getUpsertType() == UpsertType.FULLY_NEW;
        boolean present = ctx.getSourceSystemsUpdate().stream().anyMatch(s -> s.getName().equals(sourceSystem));
        SourceSystemDef sourceSystemDef = isFullyUpdate ? null : metaDraftService.getSourceSystemById(sourceSystem);
        return present || sourceSystemDef != null;
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
            ctx.getNestedEntityUpdate()
                    .forEach(el -> checkAttributes(errors, el, el.getName(), MetaType.NESTED_ENTITY));
        }
        return errors;
    }

    /**
     * Search for duplicate attributes.
     *
     * @param errors list with errors.
     * @param el     element to check.
     * @param elName element name.
     * @param type
     */
    private void checkAttributes(Collection<ValidationResult> errors, SimpleAttributesHolderEntityDef el, String elName,
                                 MetaType type) {
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
                CodeAttributeDef cad = lookup.getAliasCodeAttributes().stream().filter(CodeAttributeDef::isSearchable)
                        .findFirst().orElse(null);

                if (Objects.nonNull(cad)) {
                    isSearchable = true;
                }
            }
            errors.addAll(validateCustomProperties(lookup.getCodeAttribute().getName(),
                    lookup.getCodeAttribute().getCustomProperties()));
        }

        if (!isSearchable && type != MetaType.NESTED_ENTITY) {
            String message = "Entity or Lookup [{}] does not contain at least one searchable attribute on the first level.";
            errors.add(new ValidationResult(message, NO_SEARCHABLE_ATTRIBUTE, elName));
        }
        if (dupl.size() > 0) {
            for (String duplName : dupl) {
                String message = "Found more than one attribute with name [{}]  in [{}]";
                errors.add(new ValidationResult(message, DUPL_ATTRIBUTE, duplName, elName));
            }
        }

        el.getSimpleAttribute()
                .forEach(attr -> errors.addAll(validateCustomProperties(attr.getName(), attr.getCustomProperties())));
        el.getArrayAttribute()
                .forEach(attr -> errors.addAll(validateCustomProperties(attr.getName(), attr.getCustomProperties())));
        if (el instanceof ComplexAttributesHolderEntityDef) {
            final ComplexAttributesHolderEntityDef complexAttributesHolder = (ComplexAttributesHolderEntityDef) el;
            complexAttributesHolder.getComplexAttribute().forEach(
                    attr -> errors.addAll(validateCustomProperties(attr.getName(), attr.getCustomProperties())));
        }
    }

    private List<ValidationResult> validateCustomProperties(final String objectName,
                                                            final List<CustomPropertyDef> customProperties) {
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
            validationResults.add(new ValidationResult("Invalid properties names: " + invalidNames,
                    CUSTOM_PROPERTY_INVALID_NAMES, invalidNames, objectName));
        }
        if (!duplicatedNames.isEmpty()) {
            validationResults.add(new ValidationResult("Duplicated properties names: " + duplicatedNames,
                    CUSTOM_PROPERTY_DUPLICATED_NAMES, duplicatedNames, objectName));
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
            rootGroup = metaDraftService.getRootGroup(SecurityUtils.getStorageId(ctx));
        }
        Set<String> groupNames = new HashSet<>();
        groupNames("", rootGroup, groupNames);
        Collection<ValidationResult> errors = new ArrayList<>();
        if (ctx.hasEntityUpdate()) {
            List<EntityDef> entities = ctx.getEntityUpdate();
            for (EntityDef entity : entities) {
                if (!groupNames.contains(entity.getGroupName())) {
                    String message = "Group tree doesn't contains a node [{}] used in [{}]";
                    errors.add(new ValidationResult(message, NODE_GROUP_TREE_ABSENT, entity.getGroupName(),
                            entity.getName()));
                }
            }
        }
        if (ctx.hasLookupEntityUpdate()) {
            List<LookupEntityDef> lookups = ctx.getLookupEntityUpdate();
            for (LookupEntityDef lookup : lookups) {
                if (!groupNames.contains(lookup.getGroupName())) {
                    String message = "Group tree doesn't contains a node [{}] used in [{}]";
                    errors.add(new ValidationResult(message, NODE_GROUP_TREE_ABSENT, lookup.getGroupName(),
                            lookup.getName()));
                }
            }
        }

        return errors;
    }

    @Override
    public void checkReferencesToMeasurementUnits(@Nonnull final MeasurementValue measureValue) {

        java.util.function.Predicate<? super SimpleAttributeDef> predicate = simpleAttributeDef ->
                simpleAttributeDef.getMeasureSettings() != null &&
                        measureValue.getId().equals(simpleAttributeDef.getMeasureSettings().getValueId());
        List<SimpleAttributeDef> linkedElements = new ArrayList<>();

        metaDraftService.getLookupEntitiesList().stream()
                .flatMap(entityDef -> entityDef.getSimpleAttribute().stream())
                .filter(predicate)
                .forEach(linkedElements::add);

        metaDraftService.getEntitiesList().stream()
                .flatMap(entityDef -> entityDef.getSimpleAttribute().stream())
                .filter(predicate)
                .forEach(linkedElements::add);

        metaDraftService.getNestedEntitiesList().stream()
                .flatMap(entityDef -> entityDef.getSimpleAttribute().stream())
                .filter(predicate)
                .forEach(linkedElements::add);

        List<ValidationResult> validationResult = new ArrayList<>();
        for(SimpleAttributeDef simpleAttribute : linkedElements){
            if(measureValue.getMeasurementUnits().stream()
                    .noneMatch(measurementUnitDef -> measurementUnitDef.getId().equals(
                            simpleAttribute.getMeasureSettings().getDefaultUnitId()))){
                validationResult.add(new ValidationResult("Update unavailable: ",
                                MEASUREMENT_UNITS_INCORRECT, simpleAttribute.getName(),
                        simpleAttribute.getMeasureSettings().getDefaultUnitId(), simpleAttribute.getMeasureSettings().getValueId()));
            }
        }

        if(CollectionUtils.isNotEmpty(validationResult)){
            throw new MetaModelValidationException(INITIAL_ERROR_MESSAGE, ExceptionId.EX_DRAFT_IS_INCORRECT,
                    validationResult);
        }
    }

    @Override
    public void checkReferencesToMeasurementValues(@Nonnull final List<String> measureValueIds) {

        java.util.function.Predicate<? super SimpleAttributeDef> predicate = simpleAttributeDef ->
                simpleAttributeDef.getMeasureSettings() != null &&
                        measureValueIds.contains(simpleAttributeDef.getMeasureSettings().getValueId());
        List<String> linkedElements = new ArrayList<>();

        metaDraftService.getLookupEntitiesList().stream()
                .filter(entityDef -> entityDef.getSimpleAttribute().stream().anyMatch(predicate))
                .map(AbstractEntityDef::getName)
                .forEach(linkedElements::add);

        metaDraftService.getEntitiesList().stream()
                .filter(entityDef -> entityDef.getSimpleAttribute().stream().anyMatch(predicate))
                .map(AbstractEntityDef::getName)
                .forEach(linkedElements::add);

        metaDraftService.getNestedEntitiesList().stream()
                .filter(entityDef -> entityDef.getSimpleAttribute().stream().anyMatch(predicate))
                .map(AbstractEntityDef::getName)
                .forEach(linkedElements::add);

        if (CollectionUtils.isNotEmpty(linkedElements)) {
            throw new MetaModelValidationException(INITIAL_ERROR_MESSAGE, ExceptionId.EX_DRAFT_IS_INCORRECT,
                    Collections.singletonList(new ValidationResult("Delete unavailable: " + measureValueIds,
                            MEASUREMENT_REMOVING_FORBIDDEN, linkedElements)));
        }
    }

    /**
     * Create set with group names.
     *
     * @param path       group path.
     * @param group      entity group.
     * @param groupNames group names.
     */
    private void groupNames(String path, EntitiesGroupDef group, Set<String> groupNames) {
        path = StringUtils.isEmpty(path) ? group.getGroupName() : String.join(".", path, group.getGroupName());
        groupNames.add(path);
        if (group.getInnerGroups() != null && group.getInnerGroups().size() != 0) {
            for (EntitiesGroupDef innerGroup : group.getInnerGroups()) {
                groupNames(path, innerGroup, groupNames);
            }
        }
    }

}
