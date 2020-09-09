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

package com.unidata.mdm.backend.service.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesGroupsDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.MetadataException;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.service.model.MetaModelValidationComponent;
import com.unidata.mdm.backend.service.model.util.wrappers.EntitiesGroupWrapper;
import com.unidata.mdm.backend.service.security.SecurityServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

@Service("metaModelService")
@DependsOn(value = "CF_APP_CTX")
public class SecureMetaModelService extends BaseMetaModelService  {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMetaModelService.class);

    /**
     * Security service.
     */
    @Autowired(required = false)
    private SecurityServiceExt securityService;

    /**
     * The validation service.
     */
    @Autowired(required = false)
    private MetaModelValidationComponent validationComponent;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(DeleteModelRequestContext ctx) {
        securityService.deleteResources(ctx.getLookupEntitiesIds());
        securityService.deleteResources(ctx.getEntitiesIds());
        super.deleteModel(ctx);
//        throw new RuntimeException("vk");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertModel(UpdateModelRequestContext ctx) {
        if (ctx.getUpsertType() == UpdateModelRequestContext.UpsertType.ADDITION) {
            mergeGroups(ctx);
        }

        validationComponent.validateUpdateModelContext(ctx);

        if (ctx.getUpsertType() == UpdateModelRequestContext.UpsertType.FULLY_NEW) {
            securityService.dropResources(SecuredResourceCategory.META_MODEL);
        }

        for(EntityDef entityDef : ctx.getEntityUpdate()){
            Map<String, AttributeInfoHolder> oldAttributes = new HashMap<>(getAttributesInfoMap(entityDef.getName()));
            Map<String, AttributeInfoHolder> newAttributes = ModelUtils.createAttributesMap(entityDef, ctx.getNestedEntityUpdate());
            oldAttributes.entrySet().removeIf(
                    oldAttr -> newAttributes.keySet()
                            .stream()
                            .anyMatch(newAttr -> newAttr.equals(oldAttr.getKey())));
            securityService.dropAttributeResourceFromEntity(entityDef, oldAttributes);
            securityService.createResourceFromEntity(entityDef, newAttributes);
        }

        for(LookupEntityDef lookupEntityDef : ctx.getLookupEntityUpdate()){
            Map<String, AttributeInfoHolder> oldAttributes = new HashMap<>(getAttributesInfoMap(lookupEntityDef.getName()));
            Map<String, AttributeInfoHolder> newAttributes = ModelUtils.createAttributesMap(lookupEntityDef, Collections.emptyList());
            oldAttributes.entrySet().removeIf(
                    oldAttr -> newAttributes.keySet()
                            .stream()
                            .anyMatch(newAttr -> newAttr.equals(oldAttr.getKey())));
            securityService.dropAttributeResourceFromEntity(lookupEntityDef, oldAttributes);
            securityService.createResourceFromEntity(lookupEntityDef, newAttributes);
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
    public List<EntityDef> getUnfilteredEntitiesList() {
        return super.getEntitiesList();
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
            	StringUtils.equals(srd.getSecuredResource().getName(), SecurityUtils.ADMIN_SYSTEM_MANAGEMENT_RESOURCE_NAME)
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
            throw new MetadataException(message, e, ExceptionId.EX_META_ENTITY_NOT_FOUND);
        }
    }

	@Override
	public void applyDraft(String draftId) {
		// TODO Auto-generated method stub
		
	}
}
