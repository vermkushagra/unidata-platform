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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.core.type.model.ModelElement;
import org.unidata.mdm.core.type.model.ModelSearchObject;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.meta.AbstractAttributeDef;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.ArrayAttributeDef;
import org.unidata.mdm.meta.ArrayValueType;
import org.unidata.mdm.meta.CodeAttributeDef;
import org.unidata.mdm.meta.ComplexAttributeDef;
import org.unidata.mdm.meta.ComplexAttributesHolderEntityDef;
import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.RelType;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.SimpleAttributeDef;
import org.unidata.mdm.meta.SimpleAttributesHolderEntityDef;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.VersionedObjectDef;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import org.unidata.mdm.meta.dao.MetaModelDao;
import org.unidata.mdm.meta.dto.GetEntitiesByRelationSideDTO;
import org.unidata.mdm.meta.dto.GetEntityDTO;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.meta.po.MetaModelPO;
import org.unidata.mdm.meta.po.MetaStoragePO;
import org.unidata.mdm.meta.service.MetaModelMappingService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.service.impl.facades.AbstractModelElementFacade;
import org.unidata.mdm.meta.service.impl.facades.EntitiesGroupModelElementFacade;
import org.unidata.mdm.meta.service.impl.facades.ModelElementElementFacade;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.meta.type.RelationSide;
import org.unidata.mdm.meta.type.event.ModelReloadEvent;
import org.unidata.mdm.meta.type.event.ModelUpdateEvent;
import org.unidata.mdm.meta.type.info.impl.AbstractBvtMapInfoHolder;
import org.unidata.mdm.meta.type.info.impl.AttributeInfoHolder;
import org.unidata.mdm.meta.type.info.impl.EntitiesGroupWrapper;
import org.unidata.mdm.meta.type.info.impl.EntityInfoHolder;
import org.unidata.mdm.meta.type.info.impl.EnumerationInfoHolder;
import org.unidata.mdm.meta.type.info.impl.LookupInfoHolder;
import org.unidata.mdm.meta.type.info.impl.NestedInfoHolder;
import org.unidata.mdm.meta.type.info.impl.RelationInfoHolder;
import org.unidata.mdm.meta.type.info.impl.SourceSystemInfoHolder;
import org.unidata.mdm.meta.type.parse.EntitiesGroupParser;
import org.unidata.mdm.meta.type.parse.EntitiesParser;
import org.unidata.mdm.meta.type.parse.EnumerationsParser;
import org.unidata.mdm.meta.type.parse.LookupEntitiesParser;
import org.unidata.mdm.meta.type.parse.NestedEntitiesParser;
import org.unidata.mdm.meta.type.parse.RelationsParser;
import org.unidata.mdm.meta.type.parse.SourceSystemsParser;
import org.unidata.mdm.meta.util.MetaJaxbUtils;
import org.unidata.mdm.meta.util.ModelCacheUtils;
import org.unidata.mdm.meta.util.ModelContextUtils;
import org.unidata.mdm.meta.util.ModelUtils;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.service.EventService;
import org.unidata.mdm.system.type.event.Event;
import org.unidata.mdm.system.type.event.EventReceiver;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;
import org.unidata.mdm.system.util.AbstractJaxbUtils;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * The Class BaseMetaModelService.
 *
 * @author Michael Yashin. Created on 26.05.2015.
 */
public abstract class BaseMetaModelService implements MetaModelService, EventReceiver {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMetaModelService.class);

    private static final String SCORE_ENABLE_PROPERTY = "unidata_score_enable";

    private static final String SCORE_BOOST_FACTOR = "unidata_search_boost";

    /**
     * The cache.
     */
    private ConcurrentHashMap<String, ModelCache> storageCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Map<String, Float>> entityCustomScores = new ConcurrentHashMap<>();
    /**
     * Hazelcast instance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;
    /**
     * The element facades.
     */
    @SuppressWarnings("rawtypes")
    private Map<Class<? extends VersionedObjectDef>, AbstractModelElementFacade> elementFacades;

    @Autowired
    private MetaModelMappingService mappingService;
    /**
     * Meta model DAO.
     */
    @Autowired
    private MetaModelDao metaModelDao;
    /**
     * Event service - 'update model', 'reload model' are sent.
     */
    @Autowired
    private EventService eventService;
    /**
     * Constructor.
     */
    public BaseMetaModelService() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#exportModel(java.lang.String)
     */
    @Override
    public Model exportModel(String storageId) {

        MeasurementPoint.start();
        try {
            String selectedStorageId = StringUtils.isBlank(storageId)
                    ? SecurityUtils.getCurrentUserStorageId()
                    : storageId;

            return assembleModel(selectedStorageId, false);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getStorageIdsList()
     */
    @Override
    public List<String> getStorageIdsList() {
        List<MetaStoragePO> records = metaModelDao.findStorageRecords();
        List<String> result = new ArrayList<>();
        for (int i = 0; records != null && i < records.size(); i++) {
            result.add(records.get(i).getId());
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEntitiesList()
     */
    @Override
    @Nonnull
    public List<EntityDef> getEntitiesList() {
        Collection<EntityInfoHolder> entities = getValues(EntityInfoHolder.class);
        return entities == null ? Collections.emptyList() : entities.stream().map(EntityInfoHolder::getEntity).collect(Collectors.toList());
    }

    /* (non-Javadoc)
         * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getRootGroup(java.lang.String)
         */
    @Override
    public EntitiesGroupDef getRootGroup(String storageId) {

        String selectedStorageId = StringUtils.isBlank(storageId)
                ? SecurityUtils.getCurrentUserStorageId()
                : storageId;

        ModelCache modelCache = storageCache.get(selectedStorageId);
        return modelCache.getCache().get(EntitiesGroupWrapper.class).values().stream()
                .map(wrapper -> (EntitiesGroupWrapper) wrapper)
                .filter(wrapper -> EntitiesGroupModelElementFacade.getSplitPath(wrapper.getWrapperId()).length == 1)
                .map(EntitiesGroupWrapper::getEntitiesGroupDef)
                .findAny().orElseGet(null);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEntityById(java.lang.String)
     */
    @Override
    public GetEntityDTO getEntityById(String id) {

        EntityInfoHolder w = getValueById(id, EntityInfoHolder.class);
        if (w != null) {
            final EntityDef entity = w.getEntity();

            List<NestedEntityDef> refs = getReferences(w.getAttributes().values());

            List<RelationDef> relations = w.getRelationsFrom().keySet().stream()
                    .map(RelationInfoHolder::getRelation)
                    .collect(Collectors.toList());

            return new GetEntityDTO(entity, refs, relations);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEntitiesFilteredByRelationSide(java.lang.String, com.unidata.mdm.backend.service.model.RelationSide)
     */
    @Override
    public GetEntitiesByRelationSideDTO getEntitiesFilteredByRelationSide(String id, RelationSide side) {

        EntityInfoHolder w = getValueById(id, EntityInfoHolder.class);
        if (w != null) {

            final EntityDef entity = w.getEntity();
            final Map<EntityDef, Pair<List<NestedEntityDef>, List<RelationDef>>> result
                    = new HashMap<>();

            List<RelationDef> relations = getValues(RelationInfoHolder.class)
                    .parallelStream()
                    .filter(el -> side == RelationSide.TO
                            ? el.getRelation()
                            .getFromEntity()
                            .equals(entity.getName())
                            : el.getRelation()
                            .getToEntity()
                            .equals(entity.getName()))
                    .map(RelationInfoHolder::getRelation)
                    .collect(Collectors.toList());

            for (RelationDef rel : relations) {
                EntityDef opposite = getEntityByIdNoDeps(side == RelationSide.TO
                        ? rel.getToEntity()
                        : rel.getFromEntity());
                if (!result.containsKey(opposite)) {
                    List<NestedEntityDef> complexAttrs = getNestedEntitiesByTopLevelId(opposite.getName());
                    result.put(opposite,
                            new ImmutablePair<List<NestedEntityDef>, List<RelationDef>>(
                                    complexAttrs == null ? Collections.emptyList() : complexAttrs,
                                    new ArrayList<>()));

                }

                result.get(opposite).getRight().add(rel);
            }

            return new GetEntitiesByRelationSideDTO(result);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEntityByIdNoDeps(java.lang.String)
     */
    @Override
    public EntityDef getEntityByIdNoDeps(String entityName) {

        MeasurementPoint.start();
        try {
            EntityInfoHolder w = getValueById(entityName, EntityInfoHolder.class);
            return w == null ? null : w.getEntity();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEntityAttributeByPath(java.lang.String, java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends AbstractAttributeDef> T getEntityAttributeByPath(
            String entityName, String path) {

        AbstractBvtMapInfoHolder w = getValueById(entityName, EntityInfoHolder.class);
        if (w != null) {
            AttributeInfoHolder h = ((AttributeInfoHolder) w.getAttributes().get(path));
            return (T) (h != null ? h.getAttribute() : null);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getAttributeByPath(java.lang.String, java.lang.String)
     */
    @Override
    public <T extends AbstractAttributeDef> T getAttributeByPath(String id,
                                                                 String path) {
        if (isEntity(id)) {
            return getEntityAttributeByPath(id, path);
        }
        if (isLookupEntity(id)) {
            return getLookupEntityAttributeByPath(id, path);
        }
        if (isRelation(id)) {
            return getRelationAttributeByPath(id, path);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#isEntity(java.lang.String)
     */
    @Override
    public boolean isEntity(String entityId) {
        return getEntityByIdNoDeps(entityId) != null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#isNestedEntity(java.lang.String)
     */
    @Override
    public boolean isNestedEntity(String entityId) {
        return getNestedEntityByNoDeps(entityId) != null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getLookupEntitiesList()
     */
    @Override
    @Nonnull
    public List<LookupEntityDef> getLookupEntitiesList() {
        Collection<LookupInfoHolder> entities = getValues(LookupInfoHolder.class);
        return entities == null ? Collections.emptyList() : entities.stream().map(LookupInfoHolder::getEntity).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<LookupEntityDef> getUnfilteredLookupEntitiesList() {
        return getLookupEntitiesList();
    }

    /* (non-Javadoc)
         * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getLookupEntityById(java.lang.String)
         */
    @Override
    public LookupEntityDef getLookupEntityById(String id) {
        MeasurementPoint.start();
        try {
            LookupInfoHolder w = getValueById(id, LookupInfoHolder.class);
            return w != null ? w.getEntity() : null;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getLookupEntityAttributeByPath(java.lang.String, java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends AbstractAttributeDef> T getLookupEntityAttributeByPath(
            String entityName, String path) {

        LookupInfoHolder w = getValueById(entityName, LookupInfoHolder.class);
        if (w != null) {
            AttributeInfoHolder h = ((AttributeInfoHolder) w.getAttributes().get(path));
            return (T) (h != null ? h.getAttribute() : null);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#isLookupEntity(java.lang.String)
     */
    @Override
    public boolean isLookupEntity(String entityName) {
        return getLookupEntityById(entityName) != null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getNestedEntitiesByTopLevelId(java.lang.String)
     */
    @Override
    public List<NestedEntityDef> getNestedEntitiesByTopLevelId(String id) {
        AbstractBvtMapInfoHolder w = getValueById(id, EntityInfoHolder.class);
        if (w != null) {
            return getReferences(w.getAttributes().values());
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getNestedEntityById(java.lang.String)
     */
    @Override
    public Map<NestedEntityDef, List<NestedEntityDef>> getNestedEntityById(
            String id) {
        NestedInfoHolder w = getValueById(id, NestedInfoHolder.class);
        if (w != null) {
            NestedEntityDef entity = w.getEntity();
            List<NestedEntityDef> refs = getReferences(w.getAttributes().values());
            return Collections.singletonMap(entity, refs);
        }

        return null;
    }

    /**
     * Gets the references.
     *
     * @param attributeInfoHolders the attribute info holders
     * @return the references
     */
    protected List<NestedEntityDef> getReferences(Collection<AttributeModelElement> attributeInfoHolders) {
        List<NestedEntityDef> refs = new ArrayList<>();
        for (AttributeModelElement holder : attributeInfoHolders) {
            if (!holder.isComplex()) {
                continue;
            }

            AttributeInfoHolder cast = (AttributeInfoHolder) holder;
            ComplexAttributeDef complexAttribute = (ComplexAttributeDef) cast.getAttribute();
            Map<NestedEntityDef, List<NestedEntityDef>> nested = getNestedEntityById(complexAttribute.getNestedEntityName());

            // Provoke NPE, if null
            for (Entry<NestedEntityDef, List<NestedEntityDef>> e : nested.entrySet()) {
                if (!refs.contains(e.getKey())) {
                    refs.add(e.getKey());
                }

                refs.addAll(e.getValue().stream()
                        .filter(v -> !refs.contains(v))
                        .collect(Collectors.toList()));
            }
        }
        return refs;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getNestedEntityByNoDeps(java.lang.String)
     */
    @Override
    public NestedEntityDef getNestedEntityByNoDeps(String id) {
        MeasurementPoint.start();
        try {
            NestedInfoHolder w = getValueById(id, NestedInfoHolder.class);
            return w != null ? w.getEntity() : null;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getRelationAttributeByPath(java.lang.String, java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends AbstractAttributeDef> T getRelationAttributeByPath(
            String relationName, String path) {

        RelationInfoHolder w = getValueById(relationName, RelationInfoHolder.class);
        if (w != null) {
            AttributeInfoHolder h = ((AttributeInfoHolder) w.getAttributes().get(path));
            return (T) (h != null ? h.getAttribute() : null);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getRelationById(java.lang.String)
     */
    @Override
    public RelationDef getRelationById(String id) {
        RelationInfoHolder w = getValueById(id, RelationInfoHolder.class);
        return w != null ? w.getRelation() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<RelationDef> getRelationsByFromEntityName(String entityName) {
        return getRelationsList().stream()
                .filter(rd -> rd.getFromEntity().equals(entityName))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<RelationDef> getRelationsByToEntityName(String entityName) {
        return getRelationsList().stream()
                .filter(rd -> rd.getToEntity().equals(entityName))
                .collect(Collectors.toList());
    }

    /* (non-Javadoc)
         * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#isRelation(java.lang.String)
         */
    @Override
    public boolean isRelation(String id) {
        return getRelationById(id) != null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getRelationsList()
     */
    @Override
    public List<RelationDef> getRelationsList() {
        Collection<RelationInfoHolder> relations = getValues(RelationInfoHolder.class);
        List<RelationDef> result = new ArrayList<>();
        for (RelationInfoHolder w : relations) {
            result.add(w.getRelation());
        }
        return Collections.unmodifiableList(result);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEnumerationById(java.lang.String)
     */
    @Override
    public EnumerationDataType getEnumerationById(String id) {
        EnumerationInfoHolder w = getValueById(id, EnumerationInfoHolder.class);
        return w != null ? w.getEnumeration() : null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEnumerationsList()
     */
    @Override
    public List<EnumerationDataType> getEnumerationsList() {
        Collection<EnumerationInfoHolder> enumerations = getValues(EnumerationInfoHolder.class);
        List<EnumerationDataType> result = new ArrayList<>();
        for (EnumerationInfoHolder w : enumerations) {
            result.add(w.getEnumeration());
        }
        return Collections.unmodifiableList(result);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getSourceSystemsList()
     */
    @Override
    public List<SourceSystemDef> getSourceSystemsList() {
        Collection<SourceSystemInfoHolder> sourceSystems = getValues(SourceSystemInfoHolder.class);
        List<SourceSystemDef> result = sourceSystems.stream().map(SourceSystemInfoHolder::getSourceSystem).collect(Collectors.toList());
        return Collections.unmodifiableList(result);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getReversedSourceSystems()
     */
    @Override
    public Map<String, Integer> getReversedSourceSystems() {
        ModelCache cache = getStorageCache();
        return cache != null ? cache.getReversedSourceSystemsMap() : null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getStraightSourceSystems()
     */
    @Override
    public Map<String, Integer> getStraightSourceSystems() {
        ModelCache cache = getStorageCache();
        return cache != null ? cache.getStraightSourceSystemsMap() : null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getAdminSourceSystem()
     */
    @Override
    public SourceSystemDef getAdminSourceSystem() {

        Map<String, Integer> topDownMap = getReversedSourceSystems();
        for (Entry<String, Integer> entry : topDownMap.entrySet()) {
            SourceSystemDef def = getSourceSystemById(entry.getKey());
            if (def.isAdmin()) {
                return def;
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#isAdminSourceSystem(java.lang.String)
     */
    @Override
    public boolean isAdminSourceSystem(String id) {
        SourceSystemDef def = getSourceSystemById(id);
        return def != null && def.isAdmin();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getSourceSystemById(java.lang.String)
     */
    @Override
    public SourceSystemDef getSourceSystemById(String id) {
        SourceSystemInfoHolder w = getValueById(id, SourceSystemInfoHolder.class);
        return w != null ? w.getSourceSystem() : null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#upsertModel(com.unidata.mdm.backend.service.model.UpdateModelRequestContext)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertModel(UpdateModelRequestContext ctx) {

        updateDatabase(ctx);
        updateCache(ctx);
        updateMappings(ctx);
        updateSearchInfo(ctx);

        eventService.fire(ModelUpdateEvent.of(ctx));
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#deleteModel(com.unidata.mdm.backend.service.model.DeleteModelRequestContext)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(DeleteModelRequestContext ctx) {

        UpdateModelRequestContext updateContext = getRelatedUpdateCtx(ctx);
        DeleteModelRequestContext enrichDeleteContext = enrichDeleteContext(ctx);

        removeFromDatabase(enrichDeleteContext);
        if (updateContext != null) {
            updateDatabase(updateContext);
        }

        if (updateContext != null) {
            updateMappings(updateContext);
        }

        removeFromCache(enrichDeleteContext);
        if (updateContext != null) {
            updateCache(updateContext);
        }

        removeSearchInfo(enrichDeleteContext);
        if (updateContext != null) {
            updateSearchInfo(updateContext);
        }

        eventService.fire(ModelUpdateEvent.of(ctx));
    }

    // TODO @Modules Use UpdateEvent to read changes from DB
    /* (non-Javadoc)
    * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#synchronizationUpsertModel(com.unidata.mdm.backend.service.model.UpdateModelRequestContext)
    */
    private void reloadModel() {

        try {
            // Try to assemble last known model from the DB
            List<String> storageIds = getStorageIdsList();
            for (String storageId : storageIds) {

                Model model = assembleModel(storageId, true);
                storageCache.put(storageId, initCache(model));

                initCustomPropertiesCaches(model);
                rebuildReferences();
                addCachedSourceSystemMaps();
            }

        } catch (Exception e) {
            final String message = "Metadata service failed to reload model(s).";
            LOGGER.error(message, e);
            throw new PlatformFailureException(message, e, MetaExceptionIds.EX_META_RELOAD_METADATA_FAILED);
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#synchronizationDeleteModel(com.unidata.mdm.backend.service.model.DeleteModelRequestContext)
     */
    private void synchronizationDeleteModel(DeleteModelRequestContext ctx) {

        UpdateModelRequestContext updateContext = getRelatedUpdateCtx(ctx);
        DeleteModelRequestContext enrichDeleteContext = enrichDeleteContext(ctx);

        removeFromCache(enrichDeleteContext);
        if (updateContext != null) {
            updateCache(updateContext);
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getModelFacade(java.lang.Class)
     */
    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <W extends IdentityModelElement, E extends VersionedObjectDef> AbstractModelElementFacade<W, E> getModelFacade(Class<E> processedModelElement) {
        return elementFacades.get(processedModelElement);
    }

    @Override
    public void receive(Event event) {
        if (event instanceof ModelReloadEvent) {
            reloadModel();
        }
    }

    private DeleteModelRequestContext getRemovedElements(UpdateModelRequestContext ctx) {
        if (ctx.isSkipRemoveElements()) {
            return new DeleteModelRequestContext.DeleteModelRequestContextBuilder().build();
        }
        List<String> removedRels = ctx.getEntityUpdate()
                .stream()
                .map(EntityDef::getName)
                .map(this::getRelationsByFromEntityName)
                .flatMap(Collection::stream)
                .filter(rel -> ctx.getRelationsUpdate()
                        .stream()
                        .noneMatch(rel2 -> rel2.getName().contains(rel.getName())))
                .map(AbstractEntityDef::getName)
                .collect(Collectors.toList());
        // collect nested entities without links to another entities and removed from current entity
        Collection<String> sharedNested = getSharedNestedEntities();
        List<String> removedNested = ctx.getEntityUpdate()
                .stream()
                .map(EntityDef::getName)
                .map(this::getComplexAttributes)
                .flatMap(Collection::stream)
                .filter(nested -> ctx.getNestedEntityUpdate()
                        .stream()
                        .noneMatch(existNested -> existNested.getName().equals(nested.getName())))
                .filter(nested -> sharedNested.stream().noneMatch(sharedName -> sharedName.equals(nested.getName())))
                .map(ComplexAttributeDef::getName)
                .collect(Collectors.toList());

        return new DeleteModelRequestContext.DeleteModelRequestContextBuilder()
                .relationIds(removedRels)
                .nestedEntiesIds(removedNested)
                .storageId(SecurityUtils.getStorageId(ctx))
                .build();
    }

    /**
     * Merge cached group and new from context.
     *
     * @param ctx - new context.
     */
    protected void mergeGroups(UpdateModelRequestContext ctx) {
        if (ctx.getUpsertType() != UpdateModelRequestContext.ModelUpsertType.ADDITION) {
            return;
        }
        EntitiesGroupDef additionGroup = ctx.getEntitiesGroupsUpdate();
        if (additionGroup == null) {
            return;
        }
        EntitiesGroupDef currentGroup = getRootGroup(SecurityUtils.getStorageId(ctx));
        String additionGroupName = additionGroup.getGroupName();
        String currentGroupName = currentGroup.getGroupName();
        //set current root group as root group in addition
        additionGroup.setGroupName(currentGroupName);
        additionGroup.setTitle(currentGroup.getTitle());
        additionGroup.setVersion(currentGroup.getVersion());

        mergeGroups(Collections.singletonList(additionGroup), Collections.singletonList(currentGroup));

        if (Objects.equals(additionGroupName, currentGroupName)) {
            return;
        }

        //change group name in entities and lookup entities.
        for (EntityDef entity : ctx.getEntityUpdate()) {
            String oldGroupName = entity.getGroupName();
            String newGroupName = oldGroupName.replaceFirst(additionGroupName, currentGroupName);
            entity.setGroupName(newGroupName);
        }
        for (LookupEntityDef lookupEntity : ctx.getLookupEntityUpdate()) {
            String oldGroupName = lookupEntity.getGroupName();
            String newGroupName = oldGroupName.replaceFirst(additionGroupName, currentGroupName);
            lookupEntity.setGroupName(newGroupName);
        }
    }

    /**
     * Method merge two groups.
     *
     * @param to   - modified collection of groups.
     * @param from - unmodified collection of groups
     * @return merged collection of groups.
     */
    private Collection<EntitiesGroupDef> mergeGroups(Collection<EntitiesGroupDef> to, Collection<EntitiesGroupDef> from) {
        Collection<EntitiesGroupDef> result = new ArrayList<>(to);
        for (EntitiesGroupDef groupFrom : from) {
            boolean found = false;
            for (EntitiesGroupDef groupTo : to) {
                if (Objects.equals(groupFrom.getGroupName(), groupTo.getGroupName())) {
                    Collection<EntitiesGroupDef> innerGroups = mergeGroups(groupTo.getInnerGroups(), groupFrom.getInnerGroups());
                    groupTo.getInnerGroups().clear();
                    groupTo.withInnerGroups(innerGroups);
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(groupFrom);
            }
        }
        return result;
    }

    private void updateDatabase(@Nonnull UpdateModelRequestContext ctx) {

        String storageId = SecurityUtils.getStorageId(ctx);

        // 1. Truncate previous state
        if (ctx.getUpsertType() == UpdateModelRequestContext.ModelUpsertType.FULLY_NEW) {
            // 1.1 Remove everything
            boolean wasDeleted = metaModelDao.deleteModel(storageId);
            LOGGER.debug("Full recreate meta model requested, {} records were deleted.", wasDeleted ? "some" : "no");
        } else {
            // 1.2 Remove relations, because bellow we recreate it again.
            List<String> entitiesNames = ctx.getEntityUpdate().stream().map(EntityDef::getName).collect(Collectors.toList());

            if (!ctx.isSkipRemoveElements()) {
                List<String> relations = getValues(RelationInfoHolder.class).stream()
                        .filter(el -> entitiesNames.contains(el.getRelation().getFromEntity()))
                        .map(RelationInfoHolder::getRelation)
                        .map(RelationDef::getName)
                        .collect(Collectors.toList());

                metaModelDao.deleteRecords(storageId, ModelType.RELATION, relations);
            }
        }

        // 2. Change model element versions.
        Arrays.stream(ModelType.values())
                .filter(modelType -> ModelContextUtils.hasUpdateForModelType(ctx, modelType))
                .forEach(modelType -> updateModelElementVersions(modelType.getModelElementClass(), modelType.getWrapperClass(), ctx));

        // 3. Read updates.
        String userName = SecurityUtils.getCurrentUserName();
        List<MetaModelPO> updates = Arrays.stream(ModelType.values())
                .filter(modelType -> ModelContextUtils.hasUpdateForModelType(ctx, modelType))
                .map(modelType -> getMetaModelPos(modelType.getModelElementClass(), modelType.getWrapperClass(), ctx, userName, storageId))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        // 4. Merge to DB
        metaModelDao.upsertRecords(storageId, updates);
    }

    @Nonnull
    private Map<String, String> createEntityLocationMap(@Nonnull UpdateModelRequestContext ctx) {
        Map<String, String> entityLocations = new HashMap<>();

        ctx.getEntityUpdate().stream()
                .sequential()
                .map(EntityDef::getName)
                .map(name -> getValueById(name, EntityInfoHolder.class))
                .filter(Objects::nonNull)
                .map(entityWrapper -> mapEntityToGroup(entityWrapper.getEntity().getGroupName(), entityWrapper.getId()))
                .forEach(pair -> entityLocations.put(pair.getKey(), pair.getValue()));

        ctx.getLookupEntityUpdate().stream()
                .sequential()
                .map(LookupEntityDef::getName)
                .map(name -> getValueById(name, LookupInfoHolder.class))
                .filter(Objects::nonNull)
                .map(entityWrapper -> mapEntityToGroup(entityWrapper.getEntity().getGroupName(), entityWrapper.getId()))
                .forEach(pair -> entityLocations.put(pair.getKey(), pair.getValue()));
        return entityLocations;
    }

    private Pair<String, String> mapEntityToGroup(String groupName, String entityName) {
        EntitiesGroupWrapper entitiesGroupWrapper = getValueById(groupName, EntitiesGroupWrapper.class);
        String title = entitiesGroupWrapper == null || entitiesGroupWrapper.getEntitiesGroupDef() == null ? "" : entitiesGroupWrapper.getEntitiesGroupDef().getTitle();
        return Pair.of(entityName, title);
    }

    @Nonnull
    private Collection<ModelSearchObject> getSearchElements(@Nonnull UpdateModelRequestContext ctx, @Nonnull Map<String, String> entityLocation) {

        Collection<ModelSearchObject> modelSearchObjects = new ArrayList<>();

        ctx.getEntityUpdate().stream()
                .sequential()
                .map(EntityDef::getName)
                .map(name -> getValueById(name, EntityInfoHolder.class))
                .filter(Objects::nonNull)
                .map(EntityInfoHolder::getModelSearchElement)
                .collect(Collectors.toCollection(() -> modelSearchObjects));

        ctx.getLookupEntityUpdate().stream()
                .sequential()
                .map(LookupEntityDef::getName)
                .map(name -> getValueById(name, LookupInfoHolder.class))
                .filter(Objects::nonNull)
                .map(LookupInfoHolder::getModelSearchElement)
                .collect(Collectors.toCollection(() -> modelSearchObjects));

        //add group to search objects, because wrapper doesn't know anything about real group name!
        modelSearchObjects
                .forEach(modelSearchObject -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.groupDisplayName, entityLocation.get(modelSearchObject.getEntityName())));
        return modelSearchObjects;
    }

    private void updateCache(@Nonnull UpdateModelRequestContext ctx) {
        String storageId = SecurityUtils.getStorageId(ctx);
        if (ctx.getUpsertType() == UpdateModelRequestContext.ModelUpsertType.FULLY_NEW) {
            // 1. Truncate previous state of cache!
            ModelCache cacheToClean = storageCache.remove(storageId);
            if (cacheToClean != null) {
                cacheToClean.getCache().clear();
                LOGGER.debug("Full recreate meta model requested, old cache discarded.");
            }
            storageCache.put(storageId, initCache(convertContextToModel(ctx)));
        } else {
            // 2. Before update cache actions
            Arrays.stream(ModelType.values())
                    .filter(modelType -> ModelContextUtils.hasUpdateForModelType(ctx, modelType))
                    .forEach(modelType -> beforeUpdateCacheAction(modelType.getModelElementClass(), modelType.getWrapperClass(), ctx));

            // 3. put to cache
            Arrays.stream(ModelType.values())
                    .filter(modelType -> ModelContextUtils.hasUpdateForModelType(ctx, modelType))
                    .forEach(modelType -> putWrappersToCache(modelType.getModelElementClass(), modelType.getWrapperClass(), ctx));

            // 4. After update cache actions
            Arrays.stream(ModelType.values())
                    .filter(modelType -> ModelContextUtils.hasUpdateForModelType(ctx, modelType))
                    .forEach(modelType -> afterUpdateCacheAction(modelType.getModelElementClass(), modelType.getWrapperClass(), ctx));
        }
        // 5 . Rebuild references in the cache -> TODO replace in after update actions!
        //TODO use storage id for reducing number of entities which will be used for rebuild.
        rebuildReferences();

        // 6. Add cached source system maps and recalc BVT maps.
        addCachedSourceSystemMaps();

        if (ctx.hasSourceSystemsUpdate()) {
            final List<SourceSystemDef> sourceSystems = getSourceSystemsList();

            for (EntityInfoHolder ew : getValues(EntityInfoHolder.class)) {
                ew.setBvtMap(ModelUtils.createBvtMap(ew.getEntity(), sourceSystems, ew.getAttributes()));
            }

            for (LookupInfoHolder lew : getValues(LookupInfoHolder.class)) {
                lew.setBvtMap(ModelUtils.createBvtMap(lew.getEntity(), sourceSystems, lew.getAttributes()));
            }
        }

        updateCustomPropertiesCaches(ctx);
    }

    private void initCustomPropertiesCaches(@Nonnull Model model) {
        model.getEntities().forEach(this::calculateCustomScore);
        model.getLookupEntities().forEach(this::calculateCustomScore);
    }

    private void updateCustomPropertiesCaches(@Nonnull UpdateModelRequestContext ctx) {
        if(ctx.hasEntityUpdate()){
            ctx.getEntityUpdate().forEach(this::calculateCustomScore);
        }
        if(ctx.hasLookupEntityUpdate()){
            ctx.getLookupEntityUpdate().forEach(this::calculateCustomScore);
        }
    }

    private void calculateCustomScore(SimpleAttributesHolderEntityDef entityDef) {
        Map<String, Float> scoreMapForUpdate = new HashMap<>();
        if (entityDef.getCustomProperties().stream()
                .anyMatch(customPropertyDef -> SCORE_ENABLE_PROPERTY.equals(customPropertyDef.getName())
                        && Boolean.parseBoolean(customPropertyDef.getValue()))) {

            for (AttributeModelElement attributeInfoHolder : getAttributesInfoMap(entityDef.getName()).values()) {
                ((AttributeInfoHolder) attributeInfoHolder).getAttribute().getCustomProperties().stream()
                        .filter(customPropertyDef -> SCORE_BOOST_FACTOR.equals(customPropertyDef.getName()))
                        .findFirst()
                        .ifPresent(customPropertyDef -> scoreMapForUpdate.put(attributeInfoHolder.getPath(),
                                Float.parseFloat(customPropertyDef.getValue())));
            }
            entityCustomScores.put(entityDef.getName(), scoreMapForUpdate);
        } else {
            entityCustomScores.remove(entityDef.getName());
        }
    }

    /**
     * Before update cache action.
     *
     * @param <W>               the generic type
     * @param <E>               the element type
     * @param modelElementClass the model element class
     * @param wrapperClass      the wrapper class
     * @param ctx               the ctx
     */
    private <W extends IdentityModelElement, E extends VersionedObjectDef> void beforeUpdateCacheAction(Class<E> modelElementClass, Class<W> wrapperClass, UpdateModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        String storageId = SecurityUtils.getStorageId(ctx);
        ModelCache modelCache = storageCache.get(storageId);
        if (modelFacade == null || modelCache == null || !ModelType.isOf(modelElementClass, wrapperClass)) {
            return;
        }
        Collection<E> modelElements = ModelContextUtils.getUpdateByModelType(ctx, modelElementClass);
        modelElements.forEach(modelElement -> modelFacade.changeCacheBeforeUpdate(modelElement, ctx, modelCache));
    }

    /**
     * Gets the wrappers.
     *
     * @param <W>               the generic type
     * @param <E>               the element type
     * @param modelElementClass the model element class
     * @param wrapperClass      the wrapper class
     * @param ctx               the ctx
     */
    private <W extends IdentityModelElement, E extends VersionedObjectDef> void putWrappersToCache(Class<E> modelElementClass, Class<W> wrapperClass, UpdateModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        String storageId = SecurityUtils.getStorageId(ctx);
        ModelCache modelCache = storageCache.get(storageId);
        if (modelFacade == null || modelCache == null || !ModelType.isOf(modelElementClass, wrapperClass)) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, ModelElement> cache = (Map<String, ModelElement>) modelCache.getCache().get(wrapperClass);
        Collection<E> modelElements = ModelContextUtils.getUpdateByModelType(ctx, modelElementClass);

        Map<String, ? extends ModelElement> wrappers = modelElements.stream()
                .filter(Objects::nonNull)
                .map(element -> modelFacade.convertToWrapper(element, ctx))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(IdentityModelElement::getId, Function.identity()));
        cache.putAll(wrappers);
    }

    /**
     * After update cache action.
     *
     * @param <W>               the generic type
     * @param <E>               the element type
     * @param modelElementClass the model element class
     * @param wrapperClass      the wrapper class
     * @param ctx               the ctx
     */
    private <W extends IdentityModelElement, E extends VersionedObjectDef> void afterUpdateCacheAction(Class<E> modelElementClass, Class<W> wrapperClass, UpdateModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        String storageId = SecurityUtils.getStorageId(ctx);
        ModelCache modelCache = storageCache.get(storageId);
        if (modelFacade == null || modelCache == null || !ModelType.isOf(modelElementClass, wrapperClass)) {
            return;
        }
        Collection<E> modelElements = ModelContextUtils.getUpdateByModelType(ctx, modelElementClass);
        modelElements.stream().forEach(modelElement -> modelFacade.changeCacheAfterUpdate(modelElement, ctx, modelCache));
    }

    /**
     * Update model element versions.
     *
     * @param <W>               the generic type
     * @param <E>               the element type
     * @param modelElementClass the model element class
     * @param wrapperClass      the wrapper class
     * @param ctx               the ctx
     */
    private <W extends IdentityModelElement, E extends VersionedObjectDef> void updateModelElementVersions(Class<E> modelElementClass, Class<W> wrapperClass, UpdateModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        if (modelFacade == null || !ModelType.isOf(modelElementClass, wrapperClass)) {
            return;
        }
        Collection<E> modelElements = ModelContextUtils.getUpdateByModelType(ctx, modelElementClass);
        if (ctx.getUpsertType() == UpdateModelRequestContext.ModelUpsertType.FULLY_NEW) {
            modelElements.stream().filter(Objects::nonNull).forEach(modelFacade::setInitialVersion);
        } else {
            modelElements.stream().filter(Objects::nonNull).forEach(modelFacade::updateVersion);
        }
    }

    /**
     * Gets the meta model pos.
     *
     * @param <W>               the generic type
     * @param <E>               the element type
     * @param modelElementClass the model element class
     * @param wrapperClass      the wrapper class
     * @param ctx               the ctx
     * @param user              the user
     * @param storageId         the storage id
     * @return the meta model pos
     */
    @Nonnull
    private <W extends IdentityModelElement, E extends VersionedObjectDef> Collection<MetaModelPO>  getMetaModelPos(Class<E> modelElementClass, Class<W> wrapperClass, UpdateModelRequestContext ctx, String user, String storageId) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        if (modelFacade == null || !ModelType.isOf(modelElementClass, wrapperClass)) {
            return Collections.emptyList();
        }
        Collection<E> modelElements = ModelContextUtils.getUpdateByModelType(ctx, modelElementClass);
        return modelElements.stream()
                .filter(element -> element != null)
                .map(element -> modelFacade.convertToPersistObject(element, storageId, user))
                .filter(po -> po != null)
                .collect(Collectors.toList());
    }

    private UpdateModelRequestContext getRelatedUpdateCtx(DeleteModelRequestContext ctx) {
        // 2. Delete links from deleting lookup entities
        if (!ctx.hasLookupEntitiesIds()) {
            return null;
        }

        //TODO create a clones of entities because now we modify cache! and it can be problem in case when transaction will be rolled back.
        //find lookup entities for deleting
        Collection<LookupInfoHolder> lookupEntityDefs = getStorageCache().getCache().get(ModelType.LOOKUP_ENTITY.getWrapperClass()).entrySet().stream()
                .filter(entity -> ctx.getLookupEntitiesIds().contains(entity.getKey()))
                .map(entity -> (LookupInfoHolder) entity.getValue()).collect(Collectors.toSet());

        //find entities linked with lookup entities for deleting
        List<EntityDef> linkedEntities = lookupEntityDefs.stream()
                .map(entity -> entity.getEntityFromReferences().keySet())
                .flatMap(Collection::stream)
                .map(EntityInfoHolder::getEntity)
                .collect(Collectors.toList());

        Set<NestedEntityDef> nestedEntities = removeLinkToLookupEntity(linkedEntities, ctx.getLookupEntitiesIds());

        List<RelationDef> rels = linkedEntities.stream()
                .map(ent -> getRelationsByFromEntityName(ent.getName()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        //find lookup entities linked with lookup entities for deleting
        List<LookupEntityDef> linkedLookupEntities = lookupEntityDefs.stream()
                .map(entity -> entity.getLookupFromReferences().keySet())
                .flatMap(Collection::stream)
                .filter(entity -> !ctx.getLookupEntitiesIds().contains(entity.getId()))
                .map(LookupInfoHolder::getEntity).collect(Collectors.toList());

        for (LookupEntityDef lookupEntityDef : linkedLookupEntities) {

            Collection<SimpleAttributeDef> simpleAttributeDefs = lookupEntityDef.getSimpleAttribute().stream()
                    .filter(attr -> ctx.getLookupEntitiesIds().contains(attr.getLookupEntityType()) && attr.getSimpleDataType() == null).collect(Collectors.toList());
            Collection<ArrayAttributeDef> arrayAttributeDefs = lookupEntityDef.getArrayAttribute().stream()
                    .filter(attr -> ctx.getLookupEntitiesIds().contains(attr.getLookupEntityType()) && attr.getArrayValueType() == null).collect(Collectors.toList());

            lookupEntityDef.getSimpleAttribute().removeAll(simpleAttributeDefs);
            lookupEntityDef.getArrayAttribute().removeAll(arrayAttributeDefs);
        }

        return UpdateModelRequestContext.builder()
                .entityUpdate(linkedEntities)
                .lookupEntityUpdate(linkedLookupEntities)
                .nestedEntityUpdate(new ArrayList<>(nestedEntities))
                .relationsUpdate(rels)
                .storageId(SecurityUtils.getStorageId(ctx))
                .build();
    }

    /**
     * Method create new delete ctx which will be enriched for consistent deleting
     *
     * @param initialCtx ctx which doesn't have any information about linked entities
     * @return the delete model request context
     */
    private DeleteModelRequestContext enrichDeleteContext(DeleteModelRequestContext initialCtx) {
        if (!initialCtx.hasEntitiesIds()) {
            return initialCtx;
        } else {
            Collection<String> entitiesIds = initialCtx.getEntitiesIds();
            Collection<EntityInfoHolder> entityDefs = getStorageCache().getCache().get(ModelType.ENTITY.getWrapperClass()).entrySet().stream()
                    .filter(entity -> entitiesIds.contains(entity.getKey()))
                    .map(entity -> (EntityInfoHolder) entity.getValue()).collect(Collectors.toSet());
            Set<String> entitiesRelations = new HashSet<>(initialCtx.getRelationIds());

            //collect from relations
            entityDefs.stream()
                    .map(entity -> entity.getRelationsFrom().keySet())
                    .flatMap(Collection::stream)
                    .map(RelationInfoHolder::getRelation)
                    .map(RelationDef::getName)
                    .collect(Collectors.toCollection(() -> entitiesRelations));
            //collect to relations
            entityDefs.stream()
                    .map(entity -> entity.getRelationsTo().keySet())
                    .flatMap(Collection::stream)
                    .map(RelationInfoHolder::getRelation)
                    .map(RelationDef::getName)
                    .collect(Collectors.toCollection(() -> entitiesRelations));

            //collect nested entities ids.
            Set<String> nestedEntitiesIds = new HashSet<>(initialCtx.getNestedEntitiesIds());
            Collection<String> sharedNestedNames = getSharedNestedEntities();
            entityDefs.stream()
                    .map(EntityInfoHolder::getNestedEntitiesNames)
                    .flatMap(Collection::stream)
                    .filter(name -> !sharedNestedNames.contains(name))
                    .collect(Collectors.toCollection(() -> nestedEntitiesIds));
            return new DeleteModelRequestContext.DeleteModelRequestContextBuilder()
                    .relationIds(new ArrayList<>(entitiesRelations))
                    .nestedEntiesIds(new ArrayList<>(nestedEntitiesIds))
                    .entitiesIds(initialCtx.getEntitiesIds())
                    .enumerationIds(initialCtx.getEnumerationIds())
                    .lookupEntitiesIds(initialCtx.getLookupEntitiesIds())
                    .sourceSystemIds(initialCtx.getSourceSystemIds())
                    .storageId(SecurityUtils.getStorageId(initialCtx))
                    .build();
        }
    }

    @Nonnull
    //UN-4452  Ошибка при удалении некоторых справочников
    //todo remove when management of complex attrs and entities will be separated.
    private Collection<String> getSharedNestedEntities() {
        Collection<String> allLinkedNestedEntitiesNames = new ArrayList<>();
        getValues(EntityInfoHolder.class).stream()
                .map(EntityInfoHolder::getEntity)
                .map(ComplexAttributesHolderEntityDef::getComplexAttribute)
                .flatMap(Collection::stream)
                .map(ComplexAttributeDef::getNestedEntityName)
                .collect(Collectors.toCollection(() -> allLinkedNestedEntitiesNames));

        getValues(NestedInfoHolder.class).stream()
                .map(NestedInfoHolder::getEntity)
                .map(ComplexAttributesHolderEntityDef::getComplexAttribute)
                .flatMap(Collection::stream)
                .map(ComplexAttributeDef::getNestedEntityName)
                .collect(Collectors.toCollection(() -> allLinkedNestedEntitiesNames));

        return allLinkedNestedEntitiesNames.stream()
                .filter(name -> Collections.frequency(allLinkedNestedEntitiesNames, name) > 1)
                .collect(Collectors.toSet());
    }

    /**
     * Removes model info from cache
     *
     * @param ctx the ctx
     */
    private void removeFromCache(@Nonnull DeleteModelRequestContext ctx) {
        Arrays.stream(ModelType.values())
                .filter(modelType -> ModelContextUtils.hasIdsForModelType(ctx, modelType))
                .forEach(modelType -> removeFromModelCache(modelType.getModelElementClass(), modelType.getWrapperClass(), ctx));
    }

    /**
     * Removes search info.
     *
     * @param ctx the ctx
     */
    private void removeSearchInfo(@Nonnull DeleteModelRequestContext ctx) {

        String[] names = Stream.of(ModelType.ENTITY, ModelType.LOOKUP_ENTITY)
                .filter(modelType -> ModelContextUtils.hasIdsForModelType(ctx, modelType))
                .map(modelType -> ModelContextUtils.getIdsForModelType(ctx, modelType))
                .flatMap(Collection::stream)
                .toArray(n -> new String[n]);

        mappingService.removeFromMetaModelIndex(names);
    }

    /**
     * Removes the from model cache.
     *
     * @param <W>               the generic type
     * @param <E>               the element type
     * @param modelElementClass the model element class
     * @param wrapperClass      the wrapper class
     * @param ctx               the ctx
     */
    private <W extends IdentityModelElement, E extends VersionedObjectDef> void removeFromModelCache(Class<E> modelElementClass, Class<W> wrapperClass, DeleteModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        String storageId = SecurityUtils.getStorageId(ctx);
        ModelCache modelCache = storageCache.get(storageId);
        if (modelFacade == null || modelCache == null || !ModelType.isOf(modelElementClass, wrapperClass)) {
            return;
        }
        Collection<String> modelElements = ModelContextUtils.getIdsByClass(ctx, modelElementClass);
        modelElements.forEach(modelElement -> modelFacade.removeFromCache(modelElement, ctx, modelCache));
    }

    /**
     * Removes the from db.
     *
     * @param ctx the ctx
     */
    private void removeFromDatabase(@Nonnull DeleteModelRequestContext ctx) {
        String storageId = SecurityUtils.getStorageId(ctx);
        Arrays.stream(ModelType.values())
                .filter(modelType -> ModelContextUtils.hasIdsForModelType(ctx, modelType))
                .forEach(modelType -> {
                    List<String> idsForModelType = ModelContextUtils.getIdsForModelType(ctx, modelType);
                    metaModelDao.deleteRecords(storageId, modelType, idsForModelType);
                });
    }

    /**
     * Removes the link to lookup entity.
     *
     * @param topLevelEntities    - collection of nested entities from whom will remove links to
     *                            lookup entities
     * @param lookupEntitiesNames - names of lookup entities
     * @return list of affected during removing nested entities ,exclude
     * topLevelEntities.
     */
    @Nonnull
    private Set<NestedEntityDef> removeLinkToLookupEntity(@Nonnull Collection<? extends NestedEntityDef> topLevelEntities, @Nonnull Collection<String> lookupEntitiesNames) {
        Set<NestedEntityDef> allAffectedEntities = new HashSet<>();
        for (NestedEntityDef entityDef : topLevelEntities) {

            Collection<SimpleAttributeDef> simpleAttributeDefs = entityDef.getSimpleAttribute().stream()
                    .filter(attr -> lookupEntitiesNames.contains(attr.getLookupEntityType()) && attr.getSimpleDataType() == null).collect(Collectors.toList());

            Collection<ArrayAttributeDef> arrayAttributeDefs = entityDef.getArrayAttribute().stream()
                    .filter(attr -> lookupEntitiesNames.contains(attr.getLookupEntityType()) && attr.getArrayValueType() == null).collect(Collectors.toList());

            entityDef.getSimpleAttribute().removeAll(simpleAttributeDefs);
            entityDef.getArrayAttribute().removeAll(arrayAttributeDefs);

            Collection<String> nestedEntities = entityDef.getComplexAttribute().stream().map(ComplexAttributeDef::getNestedEntityName).collect(Collectors.toList());
            if (nestedEntities.isEmpty()) {
                continue;
            }

            Collection<NestedEntityDef> nestedEntityDefs = getStorageCache().getCache().get(ModelType.NESTED_ENTITY.getWrapperClass()).entrySet().stream()
                    .filter(entity -> nestedEntities.contains(entity.getKey()))
                    .map(entity -> ((NestedInfoHolder) entity.getValue()).getEntity()).collect(Collectors.toList());
            allAffectedEntities.addAll(nestedEntityDefs);
            Set<NestedEntityDef> affectedEntities = removeLinkToLookupEntity(nestedEntityDefs, lookupEntitiesNames);
            allAffectedEntities.addAll(affectedEntities);
        }
        return allAffectedEntities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterContextRefresh() {

        try {
            // Try to assemble last known model from the DB
            List<String> storageIds = getStorageIdsList();
            for (String storageId : storageIds) {

                Model model = assembleModel(storageId, true);
                storageCache.put(storageId, initCache(model));

                initCustomPropertiesCaches(model);
                rebuildReferences();
                addCachedSourceSystemMaps();

                // UN-6722
                model.getEntities().forEach(entityDef -> mappingService.updateEntityMapping(storageId, false, entityDef, model.getNestedEntities()));
                model.getLookupEntities().forEach(entityDef -> mappingService.updateLookupMapping(storageId, false, entityDef));
                model.getRelations().forEach(relationDef -> mappingService.updateRelationMapping(storageId, null, relationDef));
            }

            // Subscribe for distributed model changes
            eventService.register(this, ModelReloadEvent.class);

        } catch (Exception e) {
            final String message = "Metadata service failed to initialize.";
            LOGGER.error(message, e);
            throw new PlatformFailureException(message, e, MetaExceptionIds.EX_META_INIT_METADATA_FAILED);
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getStorageCache()
     */
    protected ModelCache getStorageCache() {
        return storageCache.get(SecurityUtils.getCurrentUserStorageId());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getGroupWrappers()
     */
    @Nonnull
    protected Collection<EntitiesGroupWrapper> getAllGroupWrappers() {
        Collection<EntitiesGroupWrapper> entitiesGroupWrappers = getValues(EntitiesGroupWrapper.class);
        return entitiesGroupWrappers == null ? Collections.emptyList() : entitiesGroupWrappers;
    }

    /**
     * Inits the cache.
     *
     * @param model the model
     * @return the model cache
     */
    private ModelCache initCache(Model model) {
        if (model != null) {
            return new ModelCache(model, model.getStorageId(),
                    new EntitiesGroupParser(),
                    new SourceSystemsParser(),
                    new EnumerationsParser(),
                    new NestedEntitiesParser(),
                    new LookupEntitiesParser(),
                    new EntitiesParser(),
                    new RelationsParser());
        }
        return null;
    }

    /**
     * Assembles model from database.
     *
     * @param storageId  the storage id
     * @param systemInit flag to mark system initialization procedure
     * @return model
     */
    @Nonnull
    private Model assembleModel(String storageId, boolean systemInit) {

        Model model = MetaJaxbUtils.getMetaObjectFactory().createModel();
        if (storageId == null) {
            return model;
        }

        MeasurementPoint.start();
        Lock initLock = hazelcastInstance.getLock(ModelUtils.MODEL_INIT_LOCK_NAME);

        initLock.lock();
        try {

            model.setStorageId(storageId);

            SourceSystemDef defaultSourceSystem = null;
            EntitiesGroupDef defaultEntitiesGroup = null;

            List<NestedEntityDef> nestedEntities = new ArrayList<>();

            for (ModelType type : ModelType.values()) {
                List<MetaModelPO> objects;
                switch (type) {
                    case ENUMERATION:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        List<EnumerationDataType> enumerations = new ArrayList<>();
                        for (MetaModelPO po : objects) {
                            EnumerationDataType e = MetaJaxbUtils.unmarshalEnumeration(po
                                    .getData());
                            e.setVersion(po.getVersion());
                            e.setCreateAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()));
                            e.setUpdatedAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate()));
                            enumerations.add(e);
                        }
                        model.withEnumerations(enumerations);
                        break;
                    case LOOKUP_ENTITY:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        List<LookupEntityDef> lookupEntities = new ArrayList<>();
                        for (MetaModelPO po : objects) {
                            LookupEntityDef led = MetaJaxbUtils.unmarshalLookupEntity(po
                                    .getData());
                            led.setVersion(po.getVersion());
                            led.setCreateAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()));
                            led.setUpdatedAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate()));
                            lookupEntities.add(led);

                        }
                        model.withLookupEntities(lookupEntities);
                        break;
                    case NESTED_ENTITY:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        for (MetaModelPO po : objects) {
                            NestedEntityDef ned = MetaJaxbUtils.unmarshalNestedEntity(po
                                    .getData());
                            ned.setVersion(po.getVersion());
                            ned.setCreateAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()));
                            ned.setUpdatedAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate()));
                            nestedEntities.add(ned);
                        }
                        break;
                    case ENTITY:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        List<EntityDef> entities = new ArrayList<>();
                        for (MetaModelPO po : objects) {
                            EntityDef entity = MetaJaxbUtils.unmarshalEntity(po.getData());
                            entities.add(entity.withVersion(po.getVersion())
                                    .withCreateAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()))
                                    .withUpdatedAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate())));
                        }
                        model.withEntities(entities);
                        break;
                    case RELATION:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        List<RelationDef> relations = new ArrayList<>();
                        for (MetaModelPO po : objects) {
                            RelationDef rel = MetaJaxbUtils.unmarshalRelation(po.getData());
                            relations.add(rel.withVersion(po.getVersion())
                                    .withCreateAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()))
                                    .withUpdatedAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate())));
                        }
                        model.withRelations(relations);
                        break;
                    case SOURCE_SYSTEM:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        List<SourceSystemDef> sourceSystems = new ArrayList<>();
                        for (MetaModelPO po : objects) {
                            SourceSystemDef sourceSystem = MetaJaxbUtils
                                    .unmarshalSourceSystem(po.getData());
                            sourceSystem.setVersion(po.getVersion());
                            sourceSystem.setCreateAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()));
                            sourceSystem.setUpdatedAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate()));
                            sourceSystems.add(sourceSystem);
                        }

                        // UN-4287, Create minimal working set of objects at very first startup.
                        if (sourceSystems.isEmpty() && systemInit) {
                            defaultSourceSystem = ModelUtils.createDefaultSourceSystem();
                            sourceSystems.add(defaultSourceSystem);
                        }

                        model.withSourceSystems(sourceSystems);
                        break;
                    case ENTITIES_GROUP:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        if (objects.size() > 1) {
                            throw new PlatformFailureException("More than one root group!",
                                    MetaExceptionIds.EX_META_MORE_THEN_ONE_ROOT_GROUP);
                        }

                        EntitiesGroupDef rootGroup;
                        MetaModelPO po = objects.stream().findFirst().orElse(null);

                        // UN-4287, Create minimal working set of objects at very first startup.
                        if (po == null) {
                            if (systemInit) {
                                defaultEntitiesGroup = ModelUtils.createDefaultEntitiesGroup();
                                rootGroup = defaultEntitiesGroup;
                            } else {
                                throw new PlatformFailureException("Not a sys init and no root group!",
                                        MetaExceptionIds.EX_META_NOT_SYS_NO_ROOT_GROUP);
                            }

                        } else {
                            rootGroup = MetaJaxbUtils.unmarshalGroup(po.getData());
                            rootGroup.setVersion(po.getVersion());
                            rootGroup.setCreateAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()));
                            rootGroup.setUpdatedAt(AbstractJaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate()));
                        }

                        model.withEntitiesGroup(rootGroup);
                        break;
                    default:
                        break;
                }
            }

            // Collect nestedEntities which referenced in entities (complex attributes).
            model.withNestedEntities(ModelUtils.filterUsageNestedEntities(nestedEntities, model.getEntities()));

            // UN-4287, save defaults, if needed
            if (defaultEntitiesGroup != null || defaultSourceSystem != null) {

                UpdateModelRequestContext uCtx = new UpdateModelRequestContextBuilder()
                        .entitiesGroupsUpdate(defaultEntitiesGroup)
                        .sourceSystemsUpdate(Collections.singletonList(defaultSourceSystem))
                        .storageId(storageId)
                        .build();

                updateDatabase(uCtx);
            }

        } finally {
            initLock.unlock();
            MeasurementPoint.stop();
        }

        return model;
    }

    /**
     * Filter and collect nested entities which used in existing entities (as complex attributes).
     * It will be useful to avoid issues with model export where old non-deleted nested entities exists.
     * See UDSUE-387.
     *
     * @param nestedEntities nested entities collection.
     * @param allEntityDefs all entities meta model data.
     * @return nested entities referenced from entities.
     */
    @Override
    public List<NestedEntityDef> filterUsageNestedEntities(final List<NestedEntityDef> nestedEntities,
                                                           List<EntityDef> allEntityDefs) {
        return ModelUtils.filterUsageNestedEntities(nestedEntities, allEntityDefs);
    }

    // @Modules
    // START Preferred API.
    /**
     * {@inheritDoc}
     */
    @Override
    public EntityModelElement getEntityModelElementById(String id) {

        // 1. Try entity type first
        EntityModelElement element = getValueById(id, EntityInfoHolder.class);
        if (Objects.isNull(element)) {
            // 2. Try lookup secondly.
            element = getValueById(id, LookupInfoHolder.class);
            if (Objects.isNull(element)) {
                // 3. Relation
                return getValueById(id, RelationInfoHolder.class);
            }
        }

        return element;
    }
    // END Preferred API.
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getValueById(java.lang.String, java.lang.Class)
     */
    @Override
    public <T extends ModelElement> T getValueById(String id, Class<T> cachedType) {
        return ModelCacheUtils.getValueById(getStorageCache(), id, cachedType);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#removeValueById(java.lang.String, java.lang.Class)
     */
    @Override
    public <T extends ModelElement> void removeValueById(String id, Class<T> cachedType) {
        ModelCacheUtils.removeValueById(getStorageCache(), id, cachedType);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getValues(java.lang.Class)
     */
    @Override
    public <T extends ModelElement> Collection<T> getValues(Class<T> cachedType) {
        return ModelCacheUtils.getValues(getStorageCache(), cachedType);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#putValue(java.lang.String, com.unidata.mdm.backend.service.model.util.wrappers.ValueWrapper, java.lang.Class)
     */
    @Override
    public <T extends ModelElement> void putValue(String id, T cached,
                                                  Class<T> cachedType) {
        ModelCacheUtils.putValue(getStorageCache(), id, cached, cachedType);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEntityRelations(java.lang.String, boolean, boolean)
     */
    @Override
    public Map<RelationDef, EntityDef> getEntityRelations(String entityName, boolean includeTo, boolean includeFrom) {
        Map<RelationDef, EntityDef> result = new HashMap<>();
        EntityInfoHolder entity = getValueById(entityName, EntityInfoHolder.class);
        if (entity != null) {
            if (includeFrom) {
                for (Entry<RelationInfoHolder, EntityInfoHolder> w : entity.getRelationsFrom().entrySet()) {
                    result.put(w.getKey().getRelation(), w.getValue().getEntity());
                }
            }
            if (includeTo) {
                for (Entry<RelationInfoHolder, EntityInfoHolder> w : entity.getRelationsTo().entrySet()) {
                    result.put(w.getKey().getRelation(), w.getValue().getEntity());
                }
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEntityRelations(java.lang.String, boolean, boolean)
     */
    @Override
    public Map<RelationDef, EntityDef> getEntityRelationsByType(String entityName, List<RelType> types, boolean includeTo, boolean includeFrom) {
        Map<RelationDef, EntityDef> result = new HashMap<>();
        EntityInfoHolder entity = getValueById(entityName, EntityInfoHolder.class);
        if (entity != null) {
            if (includeFrom) {
                for (Entry<RelationInfoHolder, EntityInfoHolder> w : entity.getRelationsFrom().entrySet()) {
                    if (types.contains(w.getKey().getRelation().getRelType())) {
                        result.put(w.getKey().getRelation(), w.getValue().getEntity());
                    }
                }
            }
            if (includeTo) {
                for (Entry<RelationInfoHolder, EntityInfoHolder> w : entity.getRelationsTo().entrySet()) {
                    if (types.contains(w.getKey().getRelation().getRelType())) {
                        result.put(w.getKey().getRelation(), w.getValue().getEntity());
                    }
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<LookupEntityDef, Set<AttributeModelElement>> getEntityToLinkedLookups(String entityName) {
        EntityInfoHolder entity = getValueById(entityName, EntityInfoHolder.class);
        if (entity != null && MapUtils.isNotEmpty(entity.getLookupToReferences())) {
            Map<LookupEntityDef, Set<AttributeModelElement>> result = new HashMap<>();
            for (Entry<LookupInfoHolder, Set<AttributeModelElement>> w : entity.getLookupToReferences().entrySet()) {
                result.put(w.getKey().getEntity(), new HashSet<>(w.getValue()));
            }

            return result;
        }

        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<LookupEntityDef, Set<AttributeModelElement>> getLookupEntityToLinkedLookups(String lookupName) {
        LookupInfoHolder entity = getValueById(lookupName, LookupInfoHolder.class);
        if (entity != null && MapUtils.isNotEmpty(entity.getLookupToReferences())) {
            Map<LookupEntityDef, Set<AttributeModelElement>> result = new HashMap<>();
            for (Entry<LookupInfoHolder, Set<AttributeModelElement>> w : entity.getLookupToReferences().entrySet()) {
                result.put(w.getKey().getEntity(), new HashSet<>(w.getValue()));
            }

            return result;
        }

        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<EntityDef, Set<AttributeModelElement>> getEntitiesReferencingThisLookup(String lookupName) {
        Map<EntityDef, Set<AttributeModelElement>> result = new HashMap<>();
        LookupInfoHolder lookup = getValueById(lookupName, LookupInfoHolder.class);
        if (lookup != null) {
            for (Entry<EntityInfoHolder, Set<AttributeModelElement>> w : lookup.getEntityFromReferences().entrySet()) {
                result.put(w.getKey().getEntity(), new HashSet<>(w.getValue()));
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<LookupEntityDef, Set<AttributeModelElement>> getLookupsReferencingThisLookup(String lookupName) {
        Map<LookupEntityDef, Set<AttributeModelElement>> result = new HashMap<>();
        LookupInfoHolder lookup = getValueById(lookupName, LookupInfoHolder.class);
        if (lookup != null) {
            for (Entry<LookupInfoHolder, Set<AttributeModelElement>> w : lookup.getLookupFromReferences().entrySet()) {
                result.put(w.getKey().getEntity(), new HashSet<>(w.getValue()));
            }
        }
        return result;
    }

    /**
     * Adds cached source system maps to a particular cache.
     */
    private void addCachedSourceSystemMaps() {

        ModelCache cache = getStorageCache();
        List<SourceSystemDef> sourceSystems = getSourceSystemsList();
        Map<String, Integer> reversedSourceSystemsMap = ModelUtils.createSourceSystemsMap(sourceSystems, true);
        Map<String, Integer> straightSourceSystemsMap = ModelUtils.createSourceSystemsMap(sourceSystems, false);

        cache.setReversedSourceSystemsMap(reversedSourceSystemsMap);
        cache.setStraightSourceSystemsMap(straightSourceSystemsMap);
    }

    /**
     * Rebuild references in cache.
     */
    private void rebuildReferences() {
        // cleanup
        for (LookupInfoHolder lookup : getValues(LookupInfoHolder.class)) {
            lookup.getEntityFromReferences().clear();
            lookup.getLookupFromReferences().clear();
        }
        for (EntityInfoHolder entity : getValues(EntityInfoHolder.class)) {
            entity.getRelationsFrom().clear();
            entity.getRelationsTo().clear();
        }

        // process lookups
        for (LookupInfoHolder fromLookup : getValues(LookupInfoHolder.class)) {
            // process relations lookup -> lookup
            for (Entry<String, AttributeModelElement> e : fromLookup.getAttributes().entrySet()) {

                AttributeInfoHolder aih = ((AttributeInfoHolder) e.getValue());
                if (aih.isLookupLink()) {

                    LookupInfoHolder toLookup = getValueById(aih.getLookupLinkName(), LookupInfoHolder.class);
                    LOGGER.debug("Processing lookup: {}, attr: {}", fromLookup.getEntity().getName(), e.getKey());

                    if (!toLookup.getLookupFromReferences().containsKey(fromLookup)) {
                        toLookup.getLookupFromReferences().put(fromLookup, new HashSet<>());
                    }
                    toLookup.getLookupFromReferences().get(fromLookup).add(e.getValue());

                    if (!fromLookup.getLookupToReferences().containsKey(toLookup)) {
                        fromLookup.getLookupToReferences().put(toLookup, new HashSet<>());
                    }
                    fromLookup.getLookupToReferences().get(toLookup).add(e.getValue());

                    // the last one - fix ref type
                    CodeAttributeDef cAttrDef = toLookup.getEntity().getCodeAttribute();
                    if (aih.isArray()) {
                        ((ArrayAttributeDef) aih.getAttribute())
                            .setLookupEntityCodeAttributeType(ArrayValueType.fromValue(cAttrDef.getSimpleDataType()));
                    } else if (e.getValue().isSimple()) {
                        ((SimpleAttributeDef) aih.getAttribute()).setLookupEntityCodeAttributeType(cAttrDef.getSimpleDataType());
                    }
                }
            }
        }
        // process Relations: entity -> entity
        for (RelationInfoHolder rel : getValues(RelationInfoHolder.class)) {
            EntityInfoHolder fromEntity = ((EntityInfoHolder) getStorageCache().getCache().get(EntityInfoHolder.class)
                    .get(rel.getRelation().getFromEntity()));
            EntityInfoHolder toEntity = ((EntityInfoHolder) getStorageCache().getCache().get(EntityInfoHolder.class)
                    .get(rel.getRelation().getToEntity()));
            LOGGER.debug("Processing relation: {}.", rel.getId());
            toEntity.getRelationsTo().put(rel, fromEntity);
            fromEntity.getRelationsFrom().put(rel, toEntity);
        }
        // process entity
        for (EntityInfoHolder fromEntity : getValues(EntityInfoHolder.class)) {
            // process relations entity -> lookups
            for (Entry<String, AttributeModelElement> e : fromEntity.getAttributes().entrySet()) {

                AttributeInfoHolder aih = ((AttributeInfoHolder) e.getValue());
                if (aih.isLookupLink()) {

                    LookupInfoHolder toLookup = getValueById(aih.getLookupLinkName(), LookupInfoHolder.class);

                    LOGGER.debug("Processing lookup: {}, attr: {}", fromEntity.getEntity().getName(), e.getKey());
                    if (!toLookup.getEntityFromReferences().containsKey(fromEntity)) {
                        toLookup.getEntityFromReferences().put(fromEntity, new HashSet<>());
                    }
                    toLookup.getEntityFromReferences().get(fromEntity).add(e.getValue());

                    if (!fromEntity.getLookupToReferences().containsKey(toLookup)) {
                        fromEntity.getLookupToReferences().put(toLookup, new HashSet<>());
                    }
                    fromEntity.getLookupToReferences().get(toLookup).add(e.getValue());

                    // the last one - fix ref type
                    CodeAttributeDef cAttrDef = toLookup.getEntity().getCodeAttribute();
                    if (aih.isArray()) {
                        ((ArrayAttributeDef) aih.getAttribute()).setLookupEntityCodeAttributeType(ArrayValueType.fromValue(cAttrDef.getSimpleDataType()));
                    } else if (e.getValue().isSimple()) {
                        ((SimpleAttributeDef) aih.getAttribute()).setLookupEntityCodeAttributeType(cAttrDef.getSimpleDataType());
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getSimpleAttributes(java.lang.String)
     */
    @Override
    public List<AbstractAttributeDef> getSimpleAttributes(String id) {
        if (isLookupEntity(id)) {
            LookupEntityDef def = getLookupEntityById(id);
            List<AbstractAttributeDef> result
                    = new ArrayList<>(def.getSimpleAttribute());
            result.add(def.getCodeAttribute());
            result.addAll(def.getAliasCodeAttributes());
            return result;
        } else if (isEntity(id)) {
            return new ArrayList<>(getEntityByIdNoDeps(id).getSimpleAttribute());
        } else if (isRelation(id)) {
            return new ArrayList<>(getRelationById(id).getSimpleAttribute());
        } else if (isNestedEntity(id)) {
            return new ArrayList<>(getNestedEntityByNoDeps(id).getSimpleAttribute());
        } else {
            return Collections.emptyList();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getComplexAttributes(java.lang.String)
     */
    @Override
    public List<ComplexAttributeDef> getComplexAttributes(String id) {
        if (isEntity(id)) {
            return getEntityByIdNoDeps(id).getComplexAttribute();
        } else if (isNestedEntity(id)) {
            return new ArrayList<>(getNestedEntityByNoDeps(id).getComplexAttribute());
        } else {
            return Collections.emptyList();
        }
    }
	  /**
     * {@inheritDoc}
     */
	@Override
	public List<NestedEntityDef> getNestedEntitiesList() {
		Collection<NestedInfoHolder> entities = getValues(NestedInfoHolder.class);
		return entities == null ? Collections.emptyList()
				: entities.stream().map(NestedInfoHolder::getEntity).collect(Collectors.toList());
	}

	  /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Map<String, AttributeModelElement> getAttributesInfoMap(String id) {

        if (isLookupEntity(id)) {
            LookupInfoHolder lew = getValueById(id, LookupInfoHolder.class);
            if (Objects.nonNull(lew)) {
                return lew.getAttributes();
            }
        } else if (isEntity(id)) {
            EntityInfoHolder ew = getValueById(id, EntityInfoHolder.class);
            if (Objects.nonNull(ew)) {
                return ew.getAttributes();
            }
        } else if (isRelation(id)) {
            RelationInfoHolder rw = getValueById(id, RelationInfoHolder.class);
            if (Objects.nonNull(rw)) {
                return rw.getAttributes();
            }
        } else if (isNestedEntity(id)) {
            NestedInfoHolder rw = getValueById(id, NestedInfoHolder.class);
            if (Objects.nonNull(rw)) {
                return rw.getAttributes();
            }
        }

        return Collections.emptyMap();
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends AttributeModelElement> T getEntityAttributeInfoByPath(String entityName, String path) {

        EntityInfoHolder ew = getValueById(entityName, EntityInfoHolder.class);
        if (Objects.nonNull(ew)) {
            return (T) ew.getAttributes().get(path);
        }

        return null;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends AttributeModelElement> T getRelationAttributeInfoByPath(String relationName, String path) {

        RelationInfoHolder rw = getValueById(relationName, RelationInfoHolder.class);
        if (Objects.nonNull(rw)) {
            return (T) rw.getAttributes().get(path);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends AttributeModelElement> T getLookupAttributeInfoByPath(String lookupName, String path) {

        LookupInfoHolder lew = getValueById(lookupName, LookupInfoHolder.class);
        if (Objects.nonNull(lew)) {
            return (T) lew.getAttributes().get(path);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends AttributeModelElement> T getNestedAttributeInfoByPath(String nestedName, String path) {

        NestedInfoHolder rw = getValueById(nestedName, NestedInfoHolder.class);
        if (Objects.nonNull(rw)) {
            return (T) rw.getAttributes().get(path);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends AttributeModelElement> T getAttributeInfoByPath(String id, String path) {

        if (isLookupEntity(id)) {
            return getLookupAttributeInfoByPath(id, path);
        } else if (isEntity(id)) {
            return getEntityAttributeInfoByPath(id, path);
        } else if (isRelation(id)) {
            return getRelationAttributeInfoByPath(id, path);
        } else if (isNestedEntity(id)) {
            return getNestedAttributeInfoByPath(id, path);
        }

        return null;
    }

    /**
     * Shutdown hazelcast instance.
     */
    @PreDestroy
    public void cleanUp() {
        Hazelcast.shutdownAll();
    }

    @SuppressWarnings("rawtypes")
    @Autowired
    public void setElementFacades(List<AbstractModelElementFacade> elementFacades) {
        if (elementFacades == null) {
            this.elementFacades = Collections.emptyMap();
        } else {
            Map<Class<? extends VersionedObjectDef>, AbstractModelElementFacade> elements = new HashMap<>(elementFacades.size(), 1);
            for (AbstractModelElementFacade facade : elementFacades) {
                elements.put(facade.getModelType().getModelElementClass(), facade);
            }
            this.elementFacades = elements;
        }
    }


    @Override
    public List<String> findMainDisplayableAttrNamesSorted(@Nonnull String entityName) {
        return getAttributesInfoMap(entityName).values()
                .stream()
                .filter(AttributeModelElement::isMainDisplayable)
                .sorted(Comparator.comparingInt(AttributeModelElement::getOrder))
                .map(AttributeModelElement::getPath)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Float> getBoostScoreForEntity(final String entityName, List<String> searchFields) {
        Map<String, Float> entityCustomScore = entityCustomScores.get(entityName);
        if(MapUtils.isEmpty(entityCustomScore)){
            return Collections.emptyMap();
        }

        return searchFields.stream()
                .collect(Collectors.toMap(o -> o, o -> entityCustomScore.getOrDefault(o, 1f)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyEntitySearchInfo(String entityName) {

        boolean isEntity = isEntity(entityName);
        final ModelSearchObject modelSearchElement;
        if (isEntity) {
            EntityInfoHolder wrapper = getValueById(entityName, EntityInfoHolder.class);
            modelSearchElement = wrapper.getModelSearchElement();
        } else {
            LookupInfoHolder wrapper = getValueById(entityName, LookupInfoHolder.class);
            modelSearchElement = wrapper != null ? wrapper.getModelSearchElement() : null;
        }

        if (modelSearchElement == null) {
            return;
        }

        mappingService.putToMetaModelIndex(Collections.singletonList(modelSearchElement));
    }

    private Model convertContextToModel(UpdateModelRequestContext context) {
        String storageId = SecurityUtils.getStorageId(context);
        return new Model().withStorageId(storageId)
                .withEntitiesGroup(context.getEntitiesGroupsUpdate())
                .withEntities(context.getEntityUpdate())
                .withLookupEntities(context.getLookupEntityUpdate())
                .withEnumerations(context.getEnumerationsUpdate())
                .withRelations(context.getRelationsUpdate())
                .withNestedEntities(context.getNestedEntityUpdate())
                .withSourceSystems(context.getSourceSystemsUpdate());
    }

    private void updateMappings(@Nonnull UpdateModelRequestContext ctx) {

        String storageId = SecurityUtils.getStorageId(ctx);
        if (ctx.getUpsertType() == UpdateModelRequestContext.ModelUpsertType.FULLY_NEW) {
            //drop old mappings
            mappingService.dropAllEntityIndexes(storageId);
        }

        ctx.getEntityUpdate().forEach(entityDef -> mappingService.updateEntityMapping(storageId, false, entityDef, ctx.getNestedEntityUpdate()));
        ctx.getLookupEntityUpdate().forEach(entityDef -> mappingService.updateLookupMapping(storageId, true, entityDef));
        ctx.getRelationsUpdate().forEach(relationDef -> mappingService.updateRelationMapping(storageId, null, relationDef));
    }

    private void updateSearchInfo(@Nonnull UpdateModelRequestContext ctx) {

        if (ctx.getUpsertType() == UpdateModelRequestContext.ModelUpsertType.FULLY_NEW) {
            mappingService.cleanMetaModelIndex();
        }

        if (CollectionUtils.isEmpty(ctx.getEntityUpdate()) && CollectionUtils.isEmpty(ctx.getLookupEntityUpdate())) {
            //nothing to update
            return;
        }

        Map<String, String> entityLocations = createEntityLocationMap(ctx);

        //remove prev state and create new one
        Collection<ModelSearchObject> modelSearchObjects = getSearchElements(ctx, entityLocations);
        mappingService.putToMetaModelIndex(modelSearchObjects);
    }
}