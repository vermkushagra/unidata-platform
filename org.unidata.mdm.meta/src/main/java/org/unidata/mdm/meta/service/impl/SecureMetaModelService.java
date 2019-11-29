package org.unidata.mdm.meta.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.core.dto.SecuredResourceDTO;
import org.unidata.mdm.core.service.SecurityService;
import org.unidata.mdm.core.service.impl.RoleServiceExt;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.type.security.SecuredResourceCategory;
import org.unidata.mdm.core.type.security.SecuredResourceType;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.GetModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.dto.GetEntitiesGroupsDTO;
import org.unidata.mdm.meta.dto.GetEntityDTO;
import org.unidata.mdm.meta.dto.GetModelLookupDTO;
import org.unidata.mdm.meta.dto.GetModelDTO;
import org.unidata.mdm.meta.dto.GetModelRelationDTO;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.meta.service.MetaDraftService;
import org.unidata.mdm.meta.service.MetaModelValidationComponent;
import org.unidata.mdm.meta.type.info.impl.EntitiesGroupWrapper;
import org.unidata.mdm.meta.util.ModelUtils;
import org.unidata.mdm.system.exception.PlatformFailureException;

@Service("metaModelService")
public class SecureMetaModelService extends BaseMetaModelService  {
    /**
     * The MDS.
     */
    @Autowired
    private MetaDraftService metaDraftService;
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMetaModelService.class);

    /**
     * Security service.
     */
    @Autowired(required = false)
    private SecurityService securityService;

    /**
     * The validation service.
     */
    @Autowired(required = false)
    private MetaModelValidationComponent validationComponent;

    /**
     * Role service. Contains methods for role management.
     */
    @Autowired
    private RoleServiceExt roleServiceExt;
    /**
     * {@inheritDoc}
     */
    @Override
    public GetModelDTO getModel(GetModelRequestContext ctx) {

        GetModelDTO result = new GetModelDTO();

        processEntities(ctx, result);
        processLookups(ctx, result);
        processRelations(ctx, result);
        processEnumerations(ctx, result);
        processSourceSystems(ctx, result);
        processMeasuredValues(ctx, result);
        processEntityGroups(ctx, result);

        return result;
    }

    private void processEntities(GetModelRequestContext ctx, GetModelDTO dto) {

        if (!ctx.isAllEntities() && CollectionUtils.isEmpty(ctx.getEntityIds())) {
            return;
        }

        List<GetEntityDTO> entities;
        if (ctx.isAllEntities()) {
            entities = ctx.isDraft()
                ? metaDraftService.getEntitiesList().stream()
                        .map(entity -> {
                            if (ctx.isReduced()) {
                                return new GetEntityDTO(entity, null, null);
                            }

                            return metaDraftService.getEntityById(entity.getName());
                        })
                        .collect(Collectors.toList())
                : getEntitiesList().stream()
                        .map(entity -> {
                            if (ctx.isReduced()) {
                                return new GetEntityDTO(entity, null, null);
                            }

                            return getEntityById(entity.getName());
                        })
                        .collect(Collectors.toList());
        } else {
            entities = ctx.getEntityIds().stream()
                    .map(name -> {

                        if (ctx.isReduced()) {
                            EntityDef entity = ctx.isDraft()
                                    ? metaDraftService.getEntityByIdNoDeps(name)
                                    : getEntityByIdNoDeps(name);
                            return Objects.isNull(entity) ? null : new GetEntityDTO(entity, null, null);
                        }

                        return ctx.isDraft() ?  metaDraftService.getEntityById(name) : getEntityById(name);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        dto.setEntities(entities);
    }

    private void processLookups(GetModelRequestContext ctx, GetModelDTO dto) {

        if (!ctx.isAllLookups() && CollectionUtils.isEmpty(ctx.getLookupIds())) {
            return;
        }

        List<GetModelLookupDTO> entities;
        if (ctx.isAllEntities()) {
            entities = ctx.isDraft()
                ? metaDraftService.getLookupEntitiesList().stream().map(GetModelLookupDTO::new).collect(Collectors.toList())
                : getLookupEntitiesList().stream().map(GetModelLookupDTO::new).collect(Collectors.toList());
        } else {
            entities = ctx.getLookupIds().stream()
                    .map(name -> {

                        LookupEntityDef lookup = ctx.isDraft()
                                ? metaDraftService.getLookupEntityById(name)
                                : getLookupEntityById(name);

                        return Objects.isNull(lookup) ? null : new GetModelLookupDTO(lookup);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        dto.setLookups(entities);
    }

    private void processRelations(GetModelRequestContext ctx, GetModelDTO dto) {

        if (!ctx.isAllRelations() && CollectionUtils.isEmpty(ctx.getRelationIds())) {
            return;
        }

        List<GetModelRelationDTO> relations;
        if (ctx.isAllEntities()) {
            relations = ctx.isDraft()
                ? metaDraftService.getRelationsList().stream().map(GetModelRelationDTO::new).collect(Collectors.toList())
                : getRelationsList().stream().map(GetModelRelationDTO::new).collect(Collectors.toList());
        } else {
            relations = ctx.getRelationIds().stream()
                    .map(name -> {

                        RelationDef relation;
                        if (ctx.isDraft()) {
                            relation = metaDraftService.getRelationById(name);
                        } else {
                            relation = getRelationById(name);
                        }

                        return Objects.nonNull(relation) ? new GetModelRelationDTO(relation) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        dto.setRelations(relations);
    }

    private void processEnumerations(GetModelRequestContext ctx, GetModelDTO dto) {

        if (!ctx.isAllEnumerations() && CollectionUtils.isEmpty(ctx.getEnumerationIds())) {
            return;
        }
    }

    private void processSourceSystems(GetModelRequestContext ctx, GetModelDTO dto) {

        if (!ctx.isAllSourceSystems() && CollectionUtils.isEmpty(ctx.getSourceSystemIds())) {
            return;
        }
    }

    private void processMeasuredValues(GetModelRequestContext ctx, GetModelDTO dto) {

    }

    private void processEntityGroups(GetModelRequestContext ctx, GetModelDTO dto) {

        if (!ctx.isAllEntityGroups() && CollectionUtils.isEmpty(ctx.getEntityGroupIds())) {
            return;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(DeleteModelRequestContext ctx) {
        roleServiceExt.deleteResources(ctx.getLookupEntitiesIds());
        roleServiceExt.deleteResources(ctx.getEntitiesIds());
        super.deleteModel(ctx);
//        throw new RuntimeException("vk");
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertModel(UpdateModelRequestContext ctx) {
        if (ctx.getUpsertType() == UpdateModelRequestContext.ModelUpsertType.ADDITION) {
            mergeGroups(ctx);
        }

        validationComponent.validateUpdateModelContext(ctx);

        if (ctx.getUpsertType() == UpdateModelRequestContext.ModelUpsertType.FULLY_NEW) {
            roleServiceExt.dropResources(SecuredResourceCategory.META_MODEL);
        }

        for(EntityDef entityDef : ctx.getEntityUpdate()){
            Map<String, AttributeModelElement> oldAttributes = new HashMap<>(getAttributesInfoMap(entityDef.getName()));
            Map<String, AttributeModelElement> newAttributes = ModelUtils.createAttributesMap(entityDef, ctx.getNestedEntityUpdate());
            oldAttributes.entrySet().removeIf(
                    oldAttr -> newAttributes.keySet()
                            .stream()
                            .anyMatch(newAttr -> newAttr.equals(oldAttr.getKey())));
            dropAttributeResourceFromEntity(entityDef, oldAttributes);
            createResourceFromEntity(entityDef, newAttributes);
        }

        for(LookupEntityDef lookupEntityDef : ctx.getLookupEntityUpdate()){
            Map<String, AttributeModelElement> oldAttributes = new HashMap<>(getAttributesInfoMap(lookupEntityDef.getName()));
            Map<String, AttributeModelElement> newAttributes = ModelUtils.createAttributesMap(lookupEntityDef, Collections.emptyList());
            oldAttributes.entrySet().removeIf(
                    oldAttr -> newAttributes.keySet()
                            .stream()
                            .anyMatch(newAttr -> newAttr.equals(oldAttr.getKey())));
            dropAttributeResourceFromEntity(lookupEntityDef, oldAttributes);
            createResourceFromEntity(lookupEntityDef, newAttributes);
        }

        super.upsertModel(ctx);
    }

    @Nonnull
    @Override
    public List<EntityDef> getEntitiesList() {
        List<EntityDef> unSecureResult = super.getEntitiesList();
        Collection<String> names = unSecureResult.stream().map(EntityDef::getName).collect(Collectors.toList());
        Collection<String> filtredNames = filter(names);
        List<EntityDef> result = unSecureResult.stream().filter(entity -> filtredNames.contains(entity.getName())).collect(Collectors.toList());
        return Collections.unmodifiableList(result);
    }

    @Nonnull
    @Override
    public List<LookupEntityDef> getLookupEntitiesList() {
        List<LookupEntityDef> unSecureResult = super.getLookupEntitiesList();
        Collection<String> names = unSecureResult.stream().map(LookupEntityDef::getName).collect(Collectors.toList());
        Collection<String> filtredNames = filter(names);
        List<LookupEntityDef> result = unSecureResult.stream().filter(entity -> filtredNames.contains(entity.getName())).collect(Collectors.toList());
        return Collections.unmodifiableList(result);
    }

    @Nonnull
    @Override
    public List<LookupEntityDef> getUnfilteredLookupEntitiesList() {
        return super.getLookupEntitiesList();
    }

    @Override
    @Nullable
    public GetEntityDTO getEntityById(String id) {
        return super.getEntityById(id);
//        if (entityDTO == null) {
//            return null;
//        }
//        Collection<String> filteredResult = filter(Collections.singletonList(entityDTO.getEntity().getName()));
//        if (filteredResult.isEmpty()) {
//            return null;
//        } else {
//            return entityDTO;
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetEntitiesGroupsDTO getEntitiesGroups() {

        Collection<EntitiesGroupWrapper> entitiesGroupWrappers = super.getAllGroupWrappers();
        Collection<String> entitiesNames = new ArrayList<>();

        entitiesGroupWrappers.stream()
                .map(EntitiesGroupWrapper::getNestedEntites)
                .flatMap(Collection::stream)
                .map(EntityDef::getName)
                .collect(Collectors.toCollection(() -> entitiesNames));

        entitiesGroupWrappers.stream()
                .map(EntitiesGroupWrapper::getNestedLookupEntities)
                .flatMap(Collection::stream)
                .map(LookupEntityDef::getName)
                .collect(Collectors.toCollection(() -> entitiesNames));

        Collection<String> filteredResult = filter(entitiesNames);

        Map<String, EntitiesGroupDef> defs = new HashMap<>(entitiesGroupWrappers.size());
        Map<EntitiesGroupDef, Pair<List<EntityDef>, List<LookupEntityDef>>> nested = new HashMap<>(entitiesGroupWrappers.size());
        for (EntitiesGroupWrapper entitiesGroupWrapper : entitiesGroupWrappers) {

            List<EntityDef> entities =
                entitiesGroupWrapper.getNestedEntites()
                        .stream()
                        .filter(entityDef -> filteredResult.contains(entityDef.getName()))
                        .collect(Collectors.toList());

            List<LookupEntityDef> lookupEntities =
                entitiesGroupWrapper.getNestedLookupEntities()
                        .stream()
                        .filter(entityDef -> filteredResult.contains(entityDef.getName()))
                        .collect(Collectors.toList());

            defs.put(entitiesGroupWrapper.getWrapperId(), entitiesGroupWrapper.getEntitiesGroupDef());
            nested.put(entitiesGroupWrapper.getEntitiesGroupDef(),
                    new ImmutablePair<>(entities, lookupEntities));
        }

        return new GetEntitiesGroupsDTO(defs, nested);
    }

    @Override
    @Nullable
    public LookupEntityDef getLookupEntityById(String id) {
        return super.getLookupEntityById(id);
//        if (lookupEntityDef == null) {
//            return null;
//        }
//        Collection<String> filteredResult = filter(Collections.singletonList(lookupEntityDef.getName()));
//        if (filteredResult.isEmpty()) {
//            return null;
//        } else {
//            return lookupEntityDef;
//        }
    }


    @Nonnull
    private Collection<String> filter(@Nullable Collection<String> uniqueResourcesNames) {
        if (uniqueResourcesNames == null || uniqueResourcesNames.isEmpty()) {
            return Collections.emptyList();
        }

        String token = SecurityUtils.getCurrentUserToken();
        // TODO: hack for import utility
        try {
            if (token == null || securityService.getUserByToken(token).isAdmin()) {
                return uniqueResourcesNames;
            }

            List<? extends Right> srds = securityService.getRightsByToken(token);
            if (srds.stream().anyMatch(srd ->
            	StringUtils.equals(srd.getSecuredResource().getName(), SecurityUtils.ADMIN_SYSTEM_MANAGEMENT)
            	||StringUtils.equals(srd.getSecuredResource().getName(), SecurityUtils.ADMIN_DATA_MANAGEMENT_RESOURCE_NAME))) {
                return uniqueResourcesNames;
            }

            List<String> filteredResult = new ArrayList<>();
            srds.forEach(
                    srd -> {
                        Optional<String> opt = uniqueResourcesNames.stream()
                                .filter(r -> (r.equals(srd.getSecuredResource().getName())
                                )).findFirst();
                        opt.ifPresent(filteredResult::add);
                    });
            return filteredResult;
        } catch (Exception e) {
            final String message = "Metadata service failed to retrieve data [{}].";
            LOGGER.error(message, e);
            throw new PlatformFailureException(message, e, MetaExceptionIds.EX_META_ENTITY_NOT_FOUND);
        }
    }

    @Override
	public void applyDraft(String draftId) {
		// TODO Auto-generated method stub

	}

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#createResourceFromLookup(java.util.List)
     */
    protected void createResourceFromLookup(List<LookupEntityDef> lookupEntityUpdate) {
        if (CollectionUtils.isEmpty(lookupEntityUpdate)) {
            return;
        }
        List<SecuredResourceDTO> resources = new ArrayList<>();
        for (LookupEntityDef def : lookupEntityUpdate) {

            Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(def, Collections.emptyList());
            SecuredResourceDTO resource = createResources(def.getName(), def.getDisplayName(),
                    SecuredResourceCategory.META_MODEL, attrs);
            resources.add(resource);
        }

        roleServiceExt.createResources(resources);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#createResourceFromEntity(java.util.List)
     */
    protected void createResourceFromEntity(List<EntityDef> entityUpdate, List<NestedEntityDef> refs) {
        if (CollectionUtils.isEmpty(entityUpdate)) {
            return;
        }
        List<SecuredResourceDTO> resources = new ArrayList<>();
        for (EntityDef def : entityUpdate) {

            Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(def, refs);
            SecuredResourceDTO resource = createResources(def.getName(), def.getDisplayName(),
                    SecuredResourceCategory.META_MODEL, attrs);
            resources.add(resource);
        }

        roleServiceExt.createResources(resources);
    }

    protected void createResourceFromEntity(AbstractEntityDef entityDef, Map<String, AttributeModelElement> attrs) {
        SecuredResourceDTO resource = createResources(entityDef.getName(), entityDef.getDisplayName(),
                SecuredResourceCategory.META_MODEL, attrs);
        roleServiceExt.createResources(Collections.singletonList(resource));
    }

    protected void dropAttributeResourceFromEntity(AbstractEntityDef entityDef, Map<String, AttributeModelElement> deletedAttrs) {
        List<String> deleteResources = deletedAttrs.values()
                .stream()
                .map(attr -> String.join(".", entityDef.getName(), attr.getPath()))
                .collect(Collectors.toList());
        deleteResources(deleteResources);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#deleteResources(java.util.List)
     */
    protected void deleteResources(List<String> resources) {

        if (CollectionUtils.isEmpty(resources)) {
            return;
        }

        for (String resource : resources) {
            roleServiceExt.deleteResource(resource);
        }
    }

    /**
     * TODO move to own service.
     * Creates a resource.
     *
     * @param holder the name of the attribute
     * @param topLevelName name of the top level object
     * @param parent parent
     * @return {@link SecuredResourceDTO}
     */
    private SecuredResourceDTO createResource(AttributeModelElement holder, String topLevelName, SecuredResourceDTO parent) {

        SecuredResourceDTO resource = new SecuredResourceDTO();
        resource.setName(String.join(".", topLevelName, holder.getPath()));
        resource.setDisplayName(holder.getDisplayName());
        resource.setParent(parent);
        resource.setCreatedAt(new Date());
        resource.setCreatedBy(SecurityUtils.getCurrentUserName());
        resource.setType(SecuredResourceType.USER_DEFINED);
        resource.setCategory(parent.getCategory());
        resource.setUpdatedAt(new Date());
        resource.setUpdatedBy(SecurityUtils.getCurrentUserName());

        if (holder.hasChildren()) {
            List<SecuredResourceDTO> children = new ArrayList<>();
            for (AttributeModelElement child : holder.getChildren()) {
                children.add(createResource(child, topLevelName, resource));
            }

            resource.setChildren(children);
        }

        return resource;
    }

    /**
     * Create resource
     *
     * @param name - name of resource
     * @param displayName - display name of resource
     * @param category the category to set
     * @param attrs attributes
     * @return resource
     */
    private SecuredResourceDTO createResources(String name, String displayName, SecuredResourceCategory category,
                                               Map<String, AttributeModelElement> attrs) {

        // 1. Top level object
        SecuredResourceDTO resource = new SecuredResourceDTO();
        resource.setName(name);
        resource.setDisplayName(displayName);
        resource.setCreatedAt(new Date());
        resource.setCreatedBy(SecurityUtils.getCurrentUserName());
        resource.setType(SecuredResourceType.USER_DEFINED);
        resource.setCategory(category);
        resource.setUpdatedAt(new Date());
        resource.setUpdatedBy(SecurityUtils.getCurrentUserName());

        // 2. Attributes. Only top level are processed
        List<SecuredResourceDTO> children = new ArrayList<>();
        for (Entry<String, AttributeModelElement> entry : attrs.entrySet()) {

            if (entry.getValue().hasParent()) {
                continue;
            }

            children.add(createResource(entry.getValue(), name, resource));
        }


        resource.setChildren(children);
        return resource;
    }
}
