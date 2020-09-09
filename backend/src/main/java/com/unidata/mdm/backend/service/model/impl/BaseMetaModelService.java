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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesByRelationSideDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.MetadataException;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.ModelHeaderField;
import com.unidata.mdm.backend.common.types.RelationSide;
import com.unidata.mdm.backend.dao.MetaModelDao;
import com.unidata.mdm.backend.notification.notifiers.ModelChangesNotifier;
import com.unidata.mdm.backend.po.MetaModelPO;
import com.unidata.mdm.backend.po.MetaStoragePO;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.cleanse.DQUtils;
import com.unidata.mdm.backend.service.matching.MatchingRulesService;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ModelSearchObject;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.service.model.RecordsCleaner;
import com.unidata.mdm.backend.service.model.util.ModelCache;
import com.unidata.mdm.backend.service.model.util.ModelCacheUtils;
import com.unidata.mdm.backend.service.model.util.ModelContextUtils;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.facades.AbstractModelElementFacade;
import com.unidata.mdm.backend.service.model.util.facades.EntitiesGroupModelElementFacade;
import com.unidata.mdm.backend.service.model.util.facades.ModelElementElementFacade;
import com.unidata.mdm.backend.service.model.util.parsers.CleanseFunctionRootGroupParser;
import com.unidata.mdm.backend.service.model.util.parsers.CleanseParser;
import com.unidata.mdm.backend.service.model.util.parsers.EntitiesGroupParser;
import com.unidata.mdm.backend.service.model.util.parsers.EntitiesParser;
import com.unidata.mdm.backend.service.model.util.parsers.EnumerationsParser;
import com.unidata.mdm.backend.service.model.util.parsers.LookupEntitiesParser;
import com.unidata.mdm.backend.service.model.util.parsers.NestedEntitiesParser;
import com.unidata.mdm.backend.service.model.util.parsers.RelationsParser;
import com.unidata.mdm.backend.service.model.util.parsers.SourceSystemsParser;
import com.unidata.mdm.backend.service.model.util.wrappers.AbstractEntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.AttributeWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.BVTMapWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.CleanseFunctionRootGroupWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntitiesGroupWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EnumerationWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.ModelWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.NestedEntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.RelationWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.SourceSystemWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.ValueWrapper;
import com.unidata.mdm.backend.service.registration.Registration;
import com.unidata.mdm.backend.service.registration.RegistrationService;
import com.unidata.mdm.backend.service.registration.keys.AttributeRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.ClassifierRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.EntityRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.LookupEntityRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.MeasurementValueRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.RelationRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.AbstractSimpleAttributeDef;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ArrayValueType;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.ComplexAttributesHolderEntityDef;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.DefaultClassifier;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.ListOfCleanseFunctions;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleAttributesHolderEntityDef;
import com.unidata.mdm.meta.SimpleDataType;
import com.unidata.mdm.meta.SourceSystemDef;
import com.unidata.mdm.meta.VersionedObjectDef;
import org.springframework.util.Assert;

/**
 * The Class BaseMetaModelService.
 *
 * @author Michael Yashin. Created on 26.05.2015.
 */
public abstract class BaseMetaModelService implements MetaModelServiceExt {

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
    private SearchServiceExt searchService;

    /**
     * Meta model DAO.
     */
    @Autowired
    private MetaModelDao metaModelDao;

    /**
     * Model changes notifier.
     */
    @Autowired(required = false)
    private ModelChangesNotifier modelChangesNotifier;

    /**
     * Registration service
     */
    @Autowired
    private RegistrationService registrationService;
    /**
     * Classifier model service.
     */
    @Autowired
    private ClsfService clsfService;
    /**
     * Records Cleaner
     */
    @Autowired
    private Collection<RecordsCleaner> recordsCleaners = Collections.emptyList();

    @Autowired
    private MatchingRulesService matchingRulesService;



    /**
     * Constructor.
     */
    public BaseMetaModelService() {
        super();
    }

    /**
     * Constructor for tools support.
     *
     * @param dao          the DAO to use
     * @param initialModel initial model
     */
    public BaseMetaModelService(MetaModelDao dao, Model initialModel) {
        super();

        metaModelDao = dao;
        if (initialModel != null || dao != null) {
            try {
                if (initialModel != null) {
                    storageCache.put(initialModel.getStorageId(), initCache(initialModel));
                } else {
                    afterContextRefresh();
                }
            } catch (Exception e) {
                LOGGER.error("can't initialize meta model service", e);
            }
        }
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

            Model model = assembleModel(selectedStorageId, false);

            //Classifiers shouldn't be stored in metamodel storage.
            model.withDefaultClassifiers(collectDefaultClassifiers());
            return model;
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
        Collection<EntityWrapper> entities = getValues(EntityWrapper.class);
        return entities == null ? Collections.emptyList() : entities.stream().map(EntityWrapper::getEntity).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<EntityDef> getUnfilteredEntitiesList() {
        return getEntitiesList();
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
        EntityWrapper w = getValueById(id, EntityWrapper.class);
        if (w != null) {
            final EntityDef entity = w.getEntity();

            List<NestedEntityDef> refs = getReferences(w.getAttributes().values());

            List<RelationDef> relations = w.getRelationsFrom().keySet().stream()
                    .map(RelationWrapper::getRelation)
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

        EntityWrapper w = getValueById(id, EntityWrapper.class);
        if (w != null) {

            final EntityDef entity = w.getEntity();
            final Map<EntityDef, Pair<List<NestedEntityDef>, List<RelationDef>>> result
                    = new HashMap<>();

            List<RelationDef> relations = getValues(RelationWrapper.class)
                    .parallelStream()
                    .filter(el -> side == RelationSide.TO
                            ? el.getRelation()
                            .getFromEntity()
                            .equals(entity.getName())
                            : el.getRelation()
                            .getToEntity()
                            .equals(entity.getName()))
                    .map(RelationWrapper::getRelation)
                    .collect(Collectors.toList());

            for (RelationDef rel : relations) {
                EntityDef opposite = getEntityByIdNoDeps(side == RelationSide.TO
                        ? rel.getToEntity()
                        : rel.getFromEntity());
                if (!result.containsKey(opposite)) {
                    List<NestedEntityDef> complexAttrs = getNestedEntitiesByTopLevelId(opposite.getName());
                    result.put(opposite,
                            new ImmutablePair<>(
                                    complexAttrs == null ? Collections.emptyList() : complexAttrs,
                                    new ArrayList<>()
                            )
                    );

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
            EntityWrapper w = getValueById(entityName, EntityWrapper.class);
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

        BVTMapWrapper w = getValueById(entityName, EntityWrapper.class);
        if (w != null) {
            return (T) (w.getAttributes().get(path) != null
                    ? w.getAttributes().get(path).getAttribute()
                    : null);
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
        Collection<LookupEntityWrapper> entities = getValues(LookupEntityWrapper.class);
        return entities == null ? Collections.emptyList() : entities.stream().map(LookupEntityWrapper::getEntity).collect(Collectors.toList());
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
            LookupEntityWrapper w = getValueById(id, LookupEntityWrapper.class);
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

        LookupEntityWrapper w = getValueById(entityName, LookupEntityWrapper.class);
        if (w != null) {
            return (T) (w.getAttributes().get(path) != null
                    ? w.getAttributes().get(path).getAttribute()
                    : null);
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
        BVTMapWrapper w = getValueById(id, EntityWrapper.class);
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
        NestedEntityWrapper w = getValueById(id, NestedEntityWrapper.class);
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
    private List<NestedEntityDef> getReferences(Collection<AttributeInfoHolder> attributeInfoHolders) {
        List<NestedEntityDef> refs = new ArrayList<>();
        for (AttributeInfoHolder holder : attributeInfoHolders) {
            if (!holder.isComplex()) {
                continue;
            }

            ComplexAttributeDef complexAttribute = (ComplexAttributeDef) holder.getAttribute();
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
            NestedEntityWrapper w = getValueById(id, NestedEntityWrapper.class);
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

        RelationWrapper w = getValueById(relationName, RelationWrapper.class);
        if (w != null) {
            return (T) (w.getAttributes().get(path) != null
                    ? w.getAttributes().get(path).getAttribute()
                    : null);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getRelationById(java.lang.String)
     */
    @Override
    public RelationDef getRelationById(String id) {
        RelationWrapper w = getValueById(id, RelationWrapper.class);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getClassifiersForEntity(String entityName) {
        List<String> classifierNames;
        if (isEntity(entityName)) {
            classifierNames = getValueById(entityName, EntityWrapper.class).getEntity().getClassifiers();
        } else {
            classifierNames = getValueById(entityName, LookupEntityWrapper.class).getEntity().getClassifiers();
        }
        return classifierNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractEntityDef> getClassifiedEntities(String classifierName) {

        return Stream.of(getValues(EntityWrapper.class), getValues(LookupEntityWrapper.class))
            .filter(CollectionUtils::isNotEmpty)
            .flatMap(Collection::stream)
            .map(w -> (AbstractEntityWrapper) w)
            .map(AbstractEntityWrapper::getAbstractEntity)
            .filter(ae -> CollectionUtils.isNotEmpty(ae.getClassifiers()))
            .filter(ae -> ae.getClassifiers().contains(classifierName))
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
        Collection<RelationWrapper> relations = getValues(RelationWrapper.class);
        List<RelationDef> result = new ArrayList<>();
        for (RelationWrapper w : relations) {
            result.add(w.getRelation());
        }
        return Collections.unmodifiableList(result);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEnumerationById(java.lang.String)
     */
    @Override
    public EnumerationDataType getEnumerationById(String id) {
        EnumerationWrapper w = getValueById(id, EnumerationWrapper.class);
        return w != null ? w.getEnumeration() : null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEnumerationsList()
     */
    @Override
    public List<EnumerationDataType> getEnumerationsList() {
        Collection<EnumerationWrapper> enumerations = getValues(EnumerationWrapper.class);
        List<EnumerationDataType> result = new ArrayList<>();
        for (EnumerationWrapper w : enumerations) {
            result.add(w.getEnumeration());
        }
        return Collections.unmodifiableList(result);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getSourceSystemsList()
     */
    @Override
    public List<SourceSystemDef> getSourceSystemsList() {
        Collection<SourceSystemWrapper> sourceSystems = getValues(SourceSystemWrapper.class);
        List<SourceSystemDef> result = sourceSystems.stream().map(SourceSystemWrapper::getSourceSystem).collect(Collectors.toList());
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
        SourceSystemWrapper w = getValueById(id, SourceSystemWrapper.class);
        return w != null ? w.getSourceSystem() : null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getCleanseFunctionRootGroup()
     */
    @Override
    public CleanseFunctionGroupDef getCleanseFunctionRootGroup() {
        Collection<CleanseFunctionRootGroupWrapper> groups = getValues(CleanseFunctionRootGroupWrapper.class);
        return groups != null && groups.size() == 1 ? groups.iterator().next()
                .getCleanseFunctionRootGroup() : null;
    }

    /* (non-Javadoc)
    * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#synchronizationUpsertModel(com.unidata.mdm.backend.service.model.UpdateModelRequestContext)
    */
    @Override
    public void synchronizationUpsertModel(UpdateModelRequestContext ctx) {
        //TODO check DB for correct versions!?
        adjustClassifiers(ctx);
        updateCache(ctx);
        updateSystemRules(ctx);
        updateDataQualityRules(ctx);
    }

    /**
     * Adjust classifiers.
     * If classifier mentioned in metamodel not exist create it.
     *
     * @param ctx the ctx
     */
    private void adjustClassifiers(UpdateModelRequestContext ctx) {
        List<DefaultClassifier> defaultClassifiers = ctx.getFromStorage(StorageId.DEFAULT_CLASSIFIERS);
        if (CollectionUtils.isEmpty(defaultClassifiers)) {
            return;
        }

        for (DefaultClassifier defaultClassifier : defaultClassifiers) {
            List<ClsfDTO> clsfs = clsfService.getAllClassifiersWithoutDescendants();
            if (CollectionUtils.isEmpty(clsfs)
                    || clsfs.stream().noneMatch(clsf -> StringUtils.equals(clsf.getName(), defaultClassifier.getName()))) {

                ClsfDTO toSave = new ClsfDTO();
                toSave.setName(defaultClassifier.getName());
                toSave.setDisplayName(defaultClassifier.getDisplayName());
                toSave.setDescription(defaultClassifier.getDescription());
                toSave.setCodePattern(defaultClassifier.getCodePattern());
                clsfService.createClassifier(toSave, true);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#upsertModel(com.unidata.mdm.backend.service.model.UpdateModelRequestContext)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertModel(UpdateModelRequestContext ctx) {
        boolean isRecreate = ctx.getUpsertType() == UpdateModelRequestContext.UpsertType.FULLY_NEW;
        final DeleteModelRequestContext deleteModelRequestContext = getRemovedElements(ctx);
        adjustClassifiers(ctx);
        updateDatabase(ctx);
        updateRegistry(ctx.getEntityUpdate(), ctx.getLookupEntityUpdate(), ctx.getNestedEntityUpdate(),
                ctx.getRelationsUpdate(), isRecreate);
        if (!ctx.isSkipRemoveElements()) {
        recordsCleaners.forEach(cl -> cl.cleanRelatedRecords(deleteModelRequestContext));
        }
        updateCache(ctx);
        updateSystemRules(ctx);
        updateDataQualityRules(ctx);
        updateSearchIndexMapping(ctx);
        updateSearchInfo(ctx);

        if (modelChangesNotifier != null) {
            modelChangesNotifier.notifyOtherNodesAboutUpsertModel(ctx);
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
        if (ctx.getUpsertType() != UpdateModelRequestContext.UpsertType.ADDITION) {
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
        if (ctx.getUpsertType() == UpdateModelRequestContext.UpsertType.FULLY_NEW) {
            // 1.1 Remove everything
            boolean wasDeleted = metaModelDao.deleteModel(storageId);
            LOGGER.debug("Full recreate meta model requested, {} records were deleted.", wasDeleted ? "some" : "no");
        } else {
            // 1.2 Remove relations, because bellow we recreate it again.
            List<String> entitiesNames = ctx.getEntityUpdate().stream().map(EntityDef::getName).collect(Collectors.toList());

            if (!ctx.isSkipRemoveElements()) {
                List<String> relations = getValues(RelationWrapper.class).stream()
                        .filter(el -> entitiesNames.contains(el.getRelation().getFromEntity()))
                        .map(RelationWrapper::getRelation)
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

    private void updateSearchIndexMapping(@Nonnull UpdateModelRequestContext ctx) {
        String storageId = SecurityUtils.getStorageId(ctx);
        if (ctx.getUpsertType() == UpdateModelRequestContext.UpsertType.FULLY_NEW) {
            //drop old mappings
            Stream<String> entities = getEntitiesList().stream().map(EntityDef::getName);
            Stream<String> lookupEntities = getLookupEntitiesList().stream().map(LookupEntityDef::getName);
            Stream.concat(entities, lookupEntities).forEach(ent -> searchService.dropIndex(ent, storageId));
        }

        ctx.getEntityUpdate().forEach(entityDef -> searchService.updateEntityMapping(entityDef, ctx.getNestedEntityUpdate(), storageId));
        ctx.getLookupEntityUpdate().forEach(entityDef -> searchService.updateLookupEntityMapping(entityDef, storageId));
        ctx.getRelationsUpdate().forEach(entityDef -> searchService.updateRelationMapping(entityDef, storageId));
    }

    private void updateSearchInfo(@Nonnull UpdateModelRequestContext ctx) {
        final String storageId = SecurityUtils.getStorageId(ctx);
        if (ctx.getUpsertType() == UpdateModelRequestContext.UpsertType.FULLY_NEW) {
            searchService.deleteFoundResult(SearchRequestContext.forModelElements().fetchAll(true).storageId(storageId).build());
        }
        if (CollectionUtils.isEmpty(ctx.getEntityUpdate()) && CollectionUtils.isEmpty(ctx.getLookupEntityUpdate())) {
            //nothing to update
            return;
        }

        Map<String, String> entityLocations = createEntityLocationMap(ctx);

        Collection<ModelSearchObject> modelSearchObjects = getSearchElements(ctx, entityLocations);

        boolean isModelIndexExist = searchService.modelIndexExists(storageId);
        if (!isModelIndexExist) {
            searchService.createModelIndex(storageId);
        }
        //remove prev state
        if (ctx.getUpsertType() != UpdateModelRequestContext.UpsertType.FULLY_NEW) {
            List<FormField> fields = modelSearchObjects.stream()
                    .map(ModelSearchObject::getEntityName)
                    .map(name -> FormField.strictValue(SimpleDataType.INTEGER, ModelHeaderField.ENTITY_NAME.getField(), name))
                    .collect(Collectors.toList());
            searchService.deleteFoundResult(SearchRequestContext.forModelElements().form(FormFieldsGroup.createOrGroup(fields)).storageId(storageId).build());
        }
        //index new data
        searchService.indexModelSearchElements(storageId, modelSearchObjects);
    }

    @Nonnull
    private Map<String, String> createEntityLocationMap(@Nonnull UpdateModelRequestContext ctx) {
        Map<String, String> entityLocations = new HashMap<>();

        ctx.getEntityUpdate().stream()
                .sequential()
                .map(EntityDef::getName)
                .map(name -> getValueById(name, EntityWrapper.class))
                .filter(Objects::nonNull)
                .map(entityWrapper -> mapEntityToGroup(entityWrapper.getEntity().getGroupName(), entityWrapper.getUniqueIdentifier()))
                .forEach(pair -> entityLocations.put(pair.getKey(), pair.getValue()));

        ctx.getLookupEntityUpdate().stream()
                .sequential()
                .map(LookupEntityDef::getName)
                .map(name -> getValueById(name, LookupEntityWrapper.class))
                .filter(Objects::nonNull)
                .map(entityWrapper -> mapEntityToGroup(entityWrapper.getEntity().getGroupName(), entityWrapper.getUniqueIdentifier()))
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
                .map(name -> getValueById(name, EntityWrapper.class))
                .filter(Objects::nonNull)
                .map(EntityWrapper::getModelSearchElement)
                .collect(Collectors.toCollection(() -> modelSearchObjects));

        ctx.getLookupEntityUpdate().stream()
                .sequential()
                .map(LookupEntityDef::getName)
                .map(name -> getValueById(name, LookupEntityWrapper.class))
                .filter(Objects::nonNull)
                .map(LookupEntityWrapper::getModelSearchElement)
                .collect(Collectors.toCollection(() -> modelSearchObjects));
        //add group to search objects, because wrapper doesn't know anything about real group name!
        modelSearchObjects
                .forEach(modelSearchObject -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.groupDisplayName, entityLocation.get(modelSearchObject.getEntityName())));
        return modelSearchObjects;
    }

    private void updateRegistry(Collection<EntityDef> entityDefs,
                                Collection<LookupEntityDef> lookupEntityDefs,
                                List<NestedEntityDef> nestedEntityDefs,
                                Collection<RelationDef> relationDefs,
                                boolean isRecreate) {
        if (isRecreate) {
            List<UniqueRegistryKey> keys = new ArrayList<>();
            getEntitiesList().stream()
                    .map(entity -> new EntityRegistryKey(entity.getName()))
                    .collect(Collectors.toCollection(() -> keys));
            getLookupEntitiesList().stream()
                    .map(entity -> new LookupEntityRegistryKey(entity.getName()))
                    .collect(Collectors.toCollection(() -> keys));
            registrationService.batchRemove(keys);
        }
        Map<UniqueRegistryKey, Registration> registrationMap = new HashMap<>();

        for (RelationDef relationDef : relationDefs) {
            String relName = relationDef.getName();
            UniqueRegistryKey relKey = new RelationRegistryKey(relName);
            Set<UniqueRegistryKey> contains = relationDef.getSimpleAttribute()
                    .stream()
                    .map(attr -> new AttributeRegistryKey(attr.getName(),
                            relName))
                    .collect(Collectors.toSet());
            Set<UniqueRegistryKey> references = new HashSet<>();

            if (CollectionUtils.isNotEmpty(relationDef.getToEntityDefaultDisplayAttributes())){
                relationDef.getToEntityDefaultDisplayAttributes()
                        .forEach(attr -> references.add(new AttributeRegistryKey(attr, relationDef.getToEntity())));
            }

//            if (CollectionUtils.isNotEmpty(relationDef.getToEntitySearchAttributes())) {
//                relationDef.getToEntitySearchAttributes()
//                        .forEach(attr -> references.a);
//            }

            Registration registration = new Registration(relKey, references, contains);
            registrationMap.put(relKey, registration);
            relationDef.getSimpleAttribute()
                    .stream()
                    .map(attr -> createRegistrationForAttribute(relName, attr.getName(), attr))
                    .forEach(reg -> registrationMap.put(reg.getKey(), reg));
        }

        for (EntityDef entityDef : entityDefs) {
            EntityRegistryKey entityRegistryKey = new EntityRegistryKey(entityDef.getName());
            Set<UniqueRegistryKey> references = entityDef.getClassifiers().stream()
                    .map(ClassifierRegistryKey::new)
                    .collect(Collectors.toSet());
            Map<String, AttributeInfoHolder> attrMap = ModelUtils.createAttributesMap(entityDef, nestedEntityDefs);
            Set<UniqueRegistryKey> contains = attrMap.values().stream()
                    .map(attr -> new AttributeRegistryKey(attr.getPath(), entityDef.getName()))
                    .collect(Collectors.toSet());
            relationDefs.stream()
                    .filter(rel -> rel.getFromEntity().equals(entityDef.getName()))
                    .map(rel -> new RelationRegistryKey(rel.getName()))
                    .collect(Collectors.toCollection(() -> contains));
            Registration registration = new Registration(entityRegistryKey, references, contains);
            registrationMap.put(entityRegistryKey, registration);
            getAttributeKeys(entityDef.getName(), attrMap).forEach(reg -> registrationMap.put(reg.getKey(), reg));
        }

        for (LookupEntityDef entityDef : lookupEntityDefs) {
            LookupEntityRegistryKey entityRegistryKey = new LookupEntityRegistryKey(entityDef.getName());
            Set<UniqueRegistryKey> references = entityDef.getClassifiers().stream().map(ClassifierRegistryKey::new).collect(Collectors.toSet());
            Map<String, AttributeInfoHolder> attrMap = ModelUtils.createAttributesMap(entityDef, nestedEntityDefs);
            Set<UniqueRegistryKey> contains = attrMap.values().stream()
                    .map(attr -> new AttributeRegistryKey(attr.getPath(), entityDef.getName()))
                    .collect(Collectors.toSet());
            Registration registration = new Registration(entityRegistryKey, references, contains);
            registrationMap.put(entityRegistryKey, registration);
            getAttributeKeys(entityDef.getName(), attrMap).forEach(reg -> registrationMap.put(reg.getKey(), reg));
        }

        registrationService.batchRegistry(registrationMap.values());
    }

    private Collection<Registration> getAttributeKeys(@Nonnull String entityName,
                                                      @Nonnull Map<String, AttributeInfoHolder> attrMap) {
        return attrMap.values()
                .stream()
                .filter(holder -> !holder.isComplex())
                .map(holder -> createRegistrationForAttribute(entityName, holder.getPath(),
                        holder.getAttribute()))
                .collect(Collectors.toList());
    }

    private Registration createRegistrationForAttribute(@Nonnull String entityName, @Nonnull String fullAttrKey,
                                                        @Nonnull AbstractAttributeDef attributeDef) {
        UniqueRegistryKey attrKey = new AttributeRegistryKey(fullAttrKey, entityName);
        Set<UniqueRegistryKey> references = Collections.emptySet();
        if (attributeDef instanceof SimpleAttributeDef) {
            references = new HashSet<>();
            SimpleAttributeDef simpleAttributeDef = (SimpleAttributeDef) attributeDef;
            if (simpleAttributeDef.getMeasureSettings() != null) {
                references.add(new MeasurementValueRegistryKey(simpleAttributeDef.getMeasureSettings().getValueId()));
            }
            if (!StringUtils.isBlank(simpleAttributeDef.getLookupEntityType())) {
                references.add(new LookupEntityRegistryKey(simpleAttributeDef.getLookupEntityType()));
                if(CollectionUtils.isNotEmpty(simpleAttributeDef.getLookupEntityDisplayAttributes())){
                    Set<UniqueRegistryKey> lookupReferences = simpleAttributeDef.getLookupEntityDisplayAttributes()
                            .stream()
                            .map(attr -> new AttributeRegistryKey(attr, simpleAttributeDef.getLookupEntityType()))
                            .collect(Collectors.toSet());
                    references.addAll(lookupReferences);
                }


            }
        } else if (attributeDef instanceof ArrayAttributeDef) {
            ArrayAttributeDef array = (ArrayAttributeDef) attributeDef;
            if (!StringUtils.isBlank(array.getLookupEntityType())) {
                references = new HashSet<>();
                references.add(new LookupEntityRegistryKey(array.getLookupEntityType()));
                if(CollectionUtils.isNotEmpty(array.getLookupEntityDisplayAttributes())){
                    Set<UniqueRegistryKey> lookupReferences = array.getLookupEntityDisplayAttributes()
                            .stream()
                            .map(attr -> new AttributeRegistryKey(attr, array.getLookupEntityType()))
                            .collect(Collectors.toSet());
                    references.addAll(lookupReferences);
                }
            }
        }
        return new Registration(attrKey, references, Collections.emptySet());
    }

    /**
     * Creates system DQ rules. Must be called after reference / cache rebuilt.
     * All affected entities/lookups are expected to be present in the context (also the delete case).
     *
     * @param ctx the context to process
     */
    private void updateSystemRules(@Nonnull UpdateModelRequestContext ctx) {

        if (ctx.hasEntityUpdate()) {
            ctx.getEntityUpdate().forEach(entity -> {
                // UN-4368 cascade code attr removal to references + recreate sys rules
                EntityWrapper ew = getValueById(entity.getName(), EntityWrapper.class);
                List<DQRuleDef> rules = entity.getDataQualities();
                DQUtils.removeSystemRules(ew, rules);
                DQUtils.addSystemRules(ew, rules);
            });
        }
        if (ctx.hasLookupEntityUpdate()) {
            ctx.getLookupEntityUpdate().forEach(lookup -> {
                // UN-4368 cascade code attr removal to references + recreate sys rules
                LookupEntityWrapper ew = getValueById(lookup.getName(), LookupEntityWrapper.class);
                List<DQRuleDef> rules = lookup.getDataQualities();
                DQUtils.removeSystemRules(ew, rules);
                DQUtils.addSystemRules(ew, rules);
            });
        }
    }

    /**
     * Reassign / reorder DQ rules
     */
    private void updateDataQualityRules(@Nonnull UpdateModelRequestContext ctx) {

        // Lookups
        if (ctx.hasLookupEntityUpdate()) {
            ctx.getLookupEntityUpdate().forEach(lookup -> {
                LookupEntityWrapper ew = getValueById(lookup.getName(), LookupEntityWrapper.class);
                DQUtils.prepareDataQualityWrapper(ew);
            });
        }

        // Entities
        if (ctx.hasEntityUpdate()) {
            ctx.getEntityUpdate().forEach(entity -> {
                EntityWrapper ew = getValueById(entity.getName(), EntityWrapper.class);
                DQUtils.prepareDataQualityWrapper(ew);
            });
        }
    }

    private void updateCache(@Nonnull UpdateModelRequestContext ctx) {
        String storageId = SecurityUtils.getStorageId(ctx);
        if (ctx.getUpsertType() == UpdateModelRequestContext.UpsertType.FULLY_NEW) {
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
        // 5 . Rebuild references in teh cache -> TODO replace in after update actions!
        //TODO use storage id for reducing number of entities which will be used for rebuild.
        rebuildReferences();

        // 6. Add cached source system maps and recalc BVT maps.
        addCachedSourceSystemMaps();

        if (ctx.hasSourceSystemsUpdate()) {
            final List<SourceSystemDef> sourceSystems = getSourceSystemsList();

            for (EntityWrapper ew : getValues(EntityWrapper.class)) {
                ew.setBvtMap(ModelUtils.createBvtMap(ew.getEntity(), sourceSystems, ew.getAttributes()));
            }

            for (LookupEntityWrapper lew : getValues(LookupEntityWrapper.class)) {
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

            for (AttributeInfoHolder attributeInfoHolder : getAttributesInfoMap(entityDef.getName()).values()) {
                attributeInfoHolder.getAttribute().getCustomProperties().stream()
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
    private <W extends ModelWrapper, E extends VersionedObjectDef> void beforeUpdateCacheAction(Class<E> modelElementClass, Class<W> wrapperClass, UpdateModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        String storageId = SecurityUtils.getStorageId(ctx);
        ModelCache modelCache = storageCache.get(storageId);
        if (modelFacade == null || modelCache == null || !ModelType.isRelatedClasses(modelElementClass, wrapperClass)) {
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
    private <W extends ModelWrapper, E extends VersionedObjectDef> void putWrappersToCache(Class<E> modelElementClass, Class<W> wrapperClass, UpdateModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        String storageId = SecurityUtils.getStorageId(ctx);
        ModelCache modelCache = storageCache.get(storageId);
        if (modelFacade == null || modelCache == null || !ModelType.isRelatedClasses(modelElementClass, wrapperClass)) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, ValueWrapper> cache = (Map<String, ValueWrapper>) modelCache.getCache().get(wrapperClass);
        Collection<E> modelElements = ModelContextUtils.getUpdateByModelType(ctx, modelElementClass);

        Map<String, ? extends ValueWrapper> wrappers = modelElements.stream()
                .filter(Objects::nonNull)
                .map(element -> modelFacade.convertToWrapper(element, ctx))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ModelWrapper::getUniqueIdentifier, Function.identity()));
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
    private <W extends ModelWrapper, E extends VersionedObjectDef> void afterUpdateCacheAction(Class<E> modelElementClass, Class<W> wrapperClass, UpdateModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        String storageId = SecurityUtils.getStorageId(ctx);
        ModelCache modelCache = storageCache.get(storageId);
        if (modelFacade == null || modelCache == null || !ModelType.isRelatedClasses(modelElementClass, wrapperClass)) {
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
    private <W extends ModelWrapper, E extends VersionedObjectDef> void updateModelElementVersions(Class<E> modelElementClass, Class<W> wrapperClass, UpdateModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        if (modelFacade == null || !ModelType.isRelatedClasses(modelElementClass, wrapperClass)) {
            return;
        }
        Collection<E> modelElements = ModelContextUtils.getUpdateByModelType(ctx, modelElementClass);
        if (ctx.getUpsertType() == UpdateModelRequestContext.UpsertType.FULLY_NEW) {
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
    private <W extends ModelWrapper, E extends VersionedObjectDef> Collection<MetaModelPO>  getMetaModelPos(Class<E> modelElementClass, Class<W> wrapperClass, UpdateModelRequestContext ctx, String user, String storageId) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        if (modelFacade == null || !ModelType.isRelatedClasses(modelElementClass, wrapperClass)) {
            return Collections.emptyList();
        }
        Collection<E> modelElements = ModelContextUtils.getUpdateByModelType(ctx, modelElementClass);
        return modelElements.stream()
                .filter(element -> element != null)
                .map(element -> modelFacade.convertToPersistObject(element, storageId, user))
                .filter(po -> po != null)
                .collect(Collectors.toList());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getModelFacade(java.lang.Class)
     */
    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <W extends ModelWrapper, E extends VersionedObjectDef> AbstractModelElementFacade<W, E> getModelFacade(Class<E> processedModelElement) {
        return elementFacades.get(processedModelElement);
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
            updateRegistry(updateContext.getEntityUpdate(), updateContext.getLookupEntityUpdate(),
                    updateContext.getNestedEntityUpdate(), updateContext.getRelationsUpdate(), false);
        }
        dropRegistrations(ctx);

        recordsCleaners.forEach(cl -> cl.cleanRelatedRecords(ctx));
        if (updateContext != null) {
            updateSearchIndexMapping(updateContext);
        }

        removeFromCache(enrichDeleteContext);
        if (updateContext != null) {
            updateCache(updateContext);
            updateSystemRules(updateContext);
            updateDataQualityRules(updateContext);
        }

        removeSearchInfo(enrichDeleteContext);
        if (updateContext != null) {
            updateSearchInfo(updateContext);
        }
        ctx.getLookupEntitiesIds().stream().map(LookupEntityRegistryKey::new).forEach(key -> registrationService.remove(key));
        ctx.getEntitiesIds().stream().map(EntityRegistryKey::new).forEach(key -> registrationService.remove(key));
        updateMatching(ctx);

        removeClassifierAttributesWithLookupLinks(ctx.getLookupEntitiesIds());

        if (modelChangesNotifier != null) {
            modelChangesNotifier.notifyOtherNodesAboutDeleteModel(ctx);
        }
    }

    private void removeClassifierAttributesWithLookupLinks(List<String> lookupEntitiesIds) {
        clsfService.removeCodeAttrsWithLookupsLinks(lookupEntitiesIds);
    }

    private void dropRegistrations(DeleteModelRequestContext ctx) {
        Collection<UniqueRegistryKey> keys = new ArrayList<>();
        ctx.getEntitiesIds().stream().map(EntityRegistryKey::new).collect(Collectors.toCollection(() -> keys));
        ctx.getLookupEntitiesIds().stream().map(LookupEntityRegistryKey::new).collect(Collectors.toCollection(() -> keys));
        ctx.getRelationIds().stream().map(RelationRegistryKey::new).collect(Collectors.toCollection(() -> keys));
        registrationService.batchRemove(keys);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#synchronizationDeleteModel(com.unidata.mdm.backend.service.model.DeleteModelRequestContext)
     */
    @Override
    public void synchronizationDeleteModel(DeleteModelRequestContext ctx) {
        UpdateModelRequestContext updateContext = getRelatedUpdateCtx(ctx);
        DeleteModelRequestContext enrichDeleteContext = enrichDeleteContext(ctx);
        removeFromCache(enrichDeleteContext);
        if (updateContext != null) {
            updateCache(updateContext);
            updateSystemRules(updateContext);
            updateDataQualityRules(updateContext);
        }
        updateMatching(ctx);
    }


    private UpdateModelRequestContext getRelatedUpdateCtx(DeleteModelRequestContext ctx) {
        // 2. Delete links from deleting lookup entities
        if (!ctx.hasLookupEntitiesIds()) {
            return null;
        }

        //TODO create a clones of entities because now we modify cache! and it can be problem in case when transaction will be rolled back.
        //find lookup entities for deleting
        Collection<LookupEntityWrapper> lookupEntityDefs = getStorageCache().getCache().get(ModelType.LOOKUP_ENTITY.getWrapperClass()).entrySet().stream()
                .filter(entity -> ctx.getLookupEntitiesIds().contains(entity.getKey()))
                .map(entity -> (LookupEntityWrapper) entity.getValue()).collect(Collectors.toSet());

        //find entities linked with lookup entities for deleting
        List<EntityDef> linkedEntities = lookupEntityDefs.stream()
                .map(entity -> entity.getEntityFromReferences().keySet())
                .flatMap(Collection::stream)
                .map(EntityWrapper::getEntity)
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
                .map(LookupEntityWrapper::getEntity).collect(Collectors.toList());

        for (LookupEntityDef lookupEntityDef : linkedLookupEntities) {

            Collection<SimpleAttributeDef> simpleAttributeDefs = lookupEntityDef.getSimpleAttribute().stream()
                    .filter(attr -> ctx.getLookupEntitiesIds().contains(attr.getLookupEntityType()) && attr.getSimpleDataType() == null).collect(Collectors.toList());
            Collection<ArrayAttributeDef> arrayAttributeDefs = lookupEntityDef.getArrayAttribute().stream()
                    .filter(attr -> ctx.getLookupEntitiesIds().contains(attr.getLookupEntityType()) && attr.getArrayValueType() == null).collect(Collectors.toList());

            lookupEntityDef.getSimpleAttribute().removeAll(simpleAttributeDefs);
            lookupEntityDef.getArrayAttribute().removeAll(arrayAttributeDefs);
        }

        return new UpdateModelRequestContext.UpdateModelRequestContextBuilder()
                .entityUpdate(linkedEntities)
                .lookupEntityUpdate(linkedLookupEntities)
                .nestedEntityUpdate(new ArrayList<>(nestedEntities))
                .relationsUpdate(rels)
                .storageId(SecurityUtils.getStorageId(ctx)).build();
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
            Collection<EntityWrapper> entityDefs = getStorageCache().getCache().get(ModelType.ENTITY.getWrapperClass()).entrySet().stream()
                    .filter(entity -> entitiesIds.contains(entity.getKey()))
                    .map(entity -> (EntityWrapper) entity.getValue()).collect(Collectors.toSet());
            Set<String> entitiesRelations = new HashSet<>(initialCtx.getRelationIds());

            //collect from relations
            entityDefs.stream()
                    .map(entity -> entity.getRelationsFrom().keySet())
                    .flatMap(Collection::stream)
                    .map(RelationWrapper::getRelation)
                    .map(RelationDef::getName)
                    .collect(Collectors.toCollection(() -> entitiesRelations));
            //collect to relations
            entityDefs.stream()
                    .map(entity -> entity.getRelationsTo().keySet())
                    .flatMap(Collection::stream)
                    .map(RelationWrapper::getRelation)
                    .map(RelationDef::getName)
                    .collect(Collectors.toCollection(() -> entitiesRelations));

            //collect nested entities ids.
            Set<String> nestedEntitiesIds = new HashSet<>(initialCtx.getNestedEntitiesIds());
            Collection<String> sharedNestedNames = getSharedNestedEntities();
            entityDefs.stream()
                    .map(EntityWrapper::getNestedEntitiesNames)
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
        getValues(EntityWrapper.class).stream()
                .map(EntityWrapper::getEntity)
                .map(ComplexAttributesHolderEntityDef::getComplexAttribute)
                .flatMap(Collection::stream)
                .map(ComplexAttributeDef::getNestedEntityName)
                .collect(Collectors.toCollection(() -> allLinkedNestedEntitiesNames));

        getValues(NestedEntityWrapper.class).stream()
                .map(NestedEntityWrapper::getEntity)
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
    private void updateMatching(@Nonnull DeleteModelRequestContext ctx) {
        List<String> forUpdateMatching = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(ctx.getEntitiesIds())){
            forUpdateMatching.addAll(ctx.getEntitiesIds());
        }
        if(CollectionUtils.isNotEmpty(ctx.getLookupEntitiesIds())){
            forUpdateMatching.addAll(ctx.getLookupEntitiesIds());
        }
        if(CollectionUtils.isNotEmpty(forUpdateMatching)){
            matchingRulesService.loadRules(forUpdateMatching, true);
        }
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

        String storageId = SecurityUtils.getStorageId(ctx);
        List<FormField> fields = Arrays.asList(ModelType.ENTITY, ModelType.LOOKUP_ENTITY)
                .stream()
                .filter(modelType -> ModelContextUtils.hasIdsForModelType(ctx, modelType))
                .map(modelType -> ModelContextUtils.getIdsForModelType(ctx, modelType))
                .flatMap(Collection::stream)
                .map(name -> FormField.strictValue(SimpleDataType.INTEGER, ModelHeaderField.ENTITY_NAME.getField(), name))
                .collect(Collectors.toList());

        if (fields.isEmpty()) {
            return;
        }

        searchService.deleteFoundResult(SearchRequestContext.forModelElements()
                .form(FormFieldsGroup.createOrGroup(fields))
                .storageId(storageId).build());
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
    private <W extends ModelWrapper, E extends VersionedObjectDef> void removeFromModelCache(Class<E> modelElementClass, Class<W> wrapperClass, DeleteModelRequestContext ctx) {
        ModelElementElementFacade<W, E> modelFacade = getModelFacade(modelElementClass);
        String storageId = SecurityUtils.getStorageId(ctx);
        ModelCache modelCache = storageCache.get(storageId);
        if (modelFacade == null || modelCache == null || !ModelType.isRelatedClasses(modelElementClass, wrapperClass)) {
            return;
        }
        Collection<String> modelElements = ModelContextUtils.getIdsByClass(ctx, modelElementClass);
        modelElements.stream().forEach(modelElement -> modelFacade.removeFromCache(modelElement, ctx, modelCache));
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
                    .map(entity -> ((NestedEntityWrapper) entity.getValue()).getEntity()).collect(Collectors.toList());
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
            // Try to assemble known model from the DB
            List<String> storageIds = getStorageIdsList();
            for (String storageId : storageIds) {
                Model model = assembleModel(storageId, true);
                storageCache.put(storageId, initCache(model));
                initCustomPropertiesCaches(model);
                rebuildReferences();
                assignDataQualityRules();
                addCachedSourceSystemMaps();
                // UN-6722
                /*
                model.getEntities().forEach(ent -> searchService.updateEntityMapping(ent, model.getNestedEntities(),
                        storageId));
                model.getLookupEntities().forEach(ent -> searchService.updateLookupEntityMapping(ent, storageId));
                model.getRelations().forEach(ent -> searchService.updateRelationMapping(ent, storageId));
                */
                updateRegistry(model.getEntities(), model.getLookupEntities(), model.getNestedEntities(),
                        Collections.emptyList(), false);
            }
        } catch (Exception e) {
            final String message = "Metadata service failed to initialize [{}].";
            LOGGER.error(message, e);
            throw new MetadataException(message, e,
                    ExceptionId.EX_META_INIT_METADATA_FAILED);
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
                    new CleanseParser(),
                    new NestedEntitiesParser(),
                    new LookupEntitiesParser(),
                    new EntitiesParser(),
                    new RelationsParser(),
                    new CleanseFunctionRootGroupParser());
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

        Model model = JaxbUtils.getMetaObjectFactory().createModel();
        if (storageId == null) {
            return model;
        }

        MeasurementPoint.start();
        Lock initLock = hazelcastInstance.getLock(ModelUtils.MODEL_INIT_LOCK_NAME);
        initLock.lock();

        try {

            model.setStorageId(storageId);
            CleanseFunctionGroupDef defaultCleanseFunctionsGroup = null;
            SourceSystemDef defaultSourceSystem = null;
            EntitiesGroupDef defaultEntitiesGroup = null;

            List<NestedEntityDef> nestedEntities = new ArrayList<>();

            for (ModelType type : ModelType.values()) {
                List<MetaModelPO> objects;
                switch (type) {
                    case CLEANSE_FUNCTION_GROUP:
                        ListOfCleanseFunctions root = JaxbUtils.getMetaObjectFactory()
                                .createListOfCleanseFunctions();
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        if (objects != null && objects.size() == 1) {
                            CleanseFunctionGroupDef rootGroup = JaxbUtils
                                    .unmarshalCleanseFunctionGroup(objects.get(0)
                                            .getData());
                            rootGroup.setVersion(objects.get(0).getVersion());
                            rootGroup.setCreateAt(JaxbUtils.dateToXMGregorianCalendar(objects.get(0).getCreateDate()));
                            rootGroup.setUpdatedAt(JaxbUtils.dateToXMGregorianCalendar(objects.get(0).getUpdateDate()));
                            root.withGroup(rootGroup);
                        }

                        // UN-4287, Create minimal working set of objects at very first startup.
                        if (Objects.isNull(root.getGroup()) && systemInit) {
                            defaultCleanseFunctionsGroup = ModelUtils.createDefaultCleanseFunctionGroup();
                            root.withGroup(defaultCleanseFunctionsGroup);
                        }

                        model.withCleanseFunctions(root);
                        break;
                    case ENUMERATION:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        List<EnumerationDataType> enumerations = new ArrayList<>();
                        for (MetaModelPO po : objects) {
                            EnumerationDataType e = JaxbUtils.unmarshalEnumeration(po
                                    .getData());
                            e.setVersion(po.getVersion());
                            e.setCreateAt(JaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()));
                            e.setUpdatedAt(JaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate()));
                            enumerations.add(e);
                        }
                        model.withEnumerations(enumerations);
                        break;
                    case LOOKUP_ENTITY:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        List<LookupEntityDef> lookupEntities = new ArrayList<>();
                        for (MetaModelPO po : objects) {
                            LookupEntityDef led = JaxbUtils.unmarshalLookupEntity(po
                                    .getData());
                            led.setVersion(po.getVersion());
                            led.setCreateAt(JaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()));
                            led.setUpdatedAt(JaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate()));
                            lookupEntities.add(led);

                        }
                        model.withLookupEntities(lookupEntities);
                        break;
                    case NESTED_ENTITY:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        for (MetaModelPO po : objects) {
                            NestedEntityDef ned = JaxbUtils.unmarshalNestedEntity(po
                                    .getData());
                            ned.setVersion(po.getVersion());
                            ned.setCreateAt(JaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()));
                            ned.setUpdatedAt(JaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate()));
                            nestedEntities.add(ned);
                        }
                        break;
                    case ENTITY:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        List<EntityDef> entities = new ArrayList<>();
                        for (MetaModelPO po : objects) {
                            EntityDef entity = JaxbUtils.unmarshalEntity(po.getData());
                            entities.add(entity.withVersion(po.getVersion())
                                    .withCreateAt(JaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()))
                                    .withUpdatedAt(JaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate())));
                        }
                        model.withEntities(entities);
                        break;
                    case RELATION:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        List<RelationDef> relations = new ArrayList<>();
                        for (MetaModelPO po : objects) {
                            RelationDef rel = JaxbUtils.unmarshalRelation(po.getData());
                            relations.add(rel.withVersion(po.getVersion())
                                    .withCreateAt(JaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()))
                                    .withUpdatedAt(JaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate())));
                        }
                        model.withRelations(relations);
                        break;
                    case SOURCE_SYSTEM:
                        objects = metaModelDao.findRecordsByType(storageId, type);
                        List<SourceSystemDef> sourceSystems = new ArrayList<>();
                        for (MetaModelPO po : objects) {
                            SourceSystemDef sourceSystem = JaxbUtils
                                    .unmarshalSourceSystem(po.getData());
                            sourceSystem.setVersion(po.getVersion());
                            sourceSystem.setCreateAt(JaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()));
                            sourceSystem.setUpdatedAt(JaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate()));
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
                            throw new IllegalStateException("More than one root group!"); // TODO convert to UD exceptions
                        }

                        EntitiesGroupDef rootGroup;
                        MetaModelPO po = objects.stream().findFirst().orElse(null);

                        // UN-4287, Create minimal working set of objects at very first startup.
                        if (po == null) {
                            if (systemInit) {
                                defaultEntitiesGroup = ModelUtils.createDefaultEntitiesGroup();
                                rootGroup = defaultEntitiesGroup;
                            } else {
                                throw new IllegalStateException("Not a sys init and no root group!"); // TODO convert to UD exceptions
                            }

                        } else {
                            rootGroup = JaxbUtils.unmarshalGroup(po.getData());
                            rootGroup.setVersion(po.getVersion());
                            rootGroup.setCreateAt(JaxbUtils.dateToXMGregorianCalendar(po.getCreateDate()));
                            rootGroup.setUpdatedAt(JaxbUtils.dateToXMGregorianCalendar(po.getUpdateDate()));
                        }

                        model.withEntitiesGroup(rootGroup);
                        break;
                    default:
                        break;
                }
            }

            // Collect nestedEntities which referenced in entities (complex attributes).
            List<NestedEntityDef> usedNestedEntities = ModelUtils.filterNestedEntities(nestedEntities, model);

            model.withNestedEntities(usedNestedEntities);

            // UN-4287, save defaults, if needed
            if (defaultCleanseFunctionsGroup != null
                    || defaultEntitiesGroup != null
                    || defaultSourceSystem != null) {

                UpdateModelRequestContext uCtx = new UpdateModelRequestContextBuilder()
                        .cleanseFunctionsUpdate(defaultCleanseFunctionsGroup)
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
     * Collect default classifiers.
     *
     * @return list with default classifiers.
     */
    private List<DefaultClassifier> collectDefaultClassifiers() {
        MeasurementPoint.start();
        try {
            List<ClsfDTO> clsfs = clsfService.getAllClassifiersWithoutDescendants();
            if (clsfs == null || clsfs.size() == 0) {
                return null;
            }

            List<DefaultClassifier> result = new ArrayList<>();
            for (ClsfDTO clsf : clsfs) {
                result.add(new DefaultClassifier().withName(clsf.getName()).withDisplayName(clsf.getDisplayName()));
            }
            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getValueById(java.lang.String, java.lang.Class)
     */
    @Override
    public <T extends ValueWrapper> T getValueById(String id, Class<T> cachedType) {
        return ModelCacheUtils.getValueById(getStorageCache(), id, cachedType);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#removeValueById(java.lang.String, java.lang.Class)
     */
    @Override
    public <T extends ValueWrapper> void removeValueById(String id, Class<T> cachedType) {
        ModelCacheUtils.removeValueById(getStorageCache(), id, cachedType);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getValues(java.lang.Class)
     */
    @Override
    public <T extends ValueWrapper> Collection<T> getValues(Class<T> cachedType) {
        return ModelCacheUtils.getValues(getStorageCache(), cachedType);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#putValue(java.lang.String, com.unidata.mdm.backend.service.model.util.wrappers.ValueWrapper, java.lang.Class)
     */
    @Override
    public <T extends ValueWrapper> void putValue(String id, T cached,
                                                  Class<T> cachedType) {
        ModelCacheUtils.putValue(getStorageCache(), id, cached, cachedType);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.model.MetaModelServiceExt#getEntityRelations(java.lang.String, boolean, boolean)
     */
    @Override
    public Map<RelationDef, EntityDef> getEntityRelations(String entityName, boolean includeTo, boolean includeFrom) {
        Map<RelationDef, EntityDef> result = new HashMap<>();
        EntityWrapper entity = getValueById(entityName, EntityWrapper.class);
        if (entity != null) {
            if (includeFrom) {
                for (Entry<RelationWrapper, EntityWrapper> w : entity.getRelationsFrom().entrySet()) {
                    result.put(w.getKey().getRelation(), w.getValue().getEntity());
                }
            }
            if (includeTo) {
                for (Entry<RelationWrapper, EntityWrapper> w : entity.getRelationsTo().entrySet()) {
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
        EntityWrapper entity = getValueById(entityName, EntityWrapper.class);
        if (entity != null) {
            if (includeFrom) {
                for (Entry<RelationWrapper, EntityWrapper> w : entity.getRelationsFrom().entrySet()) {
                    if (types.contains(w.getKey().getRelation().getRelType())) {
                        result.put(w.getKey().getRelation(), w.getValue().getEntity());
                    }
                }
            }
            if (includeTo) {
                for (Entry<RelationWrapper, EntityWrapper> w : entity.getRelationsTo().entrySet()) {
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
    public Map<LookupEntityDef, Set<AttributeInfoHolder>> getEntityToLinkedLookups(String entityName) {
        EntityWrapper entity = getValueById(entityName, EntityWrapper.class);
        if (entity != null && MapUtils.isNotEmpty(entity.getLookupToReferences())) {
            Map<LookupEntityDef, Set<AttributeInfoHolder>> result = new HashMap<>();
            for (Entry<LookupEntityWrapper, Set<AttributeWrapper>> w : entity.getLookupToReferences().entrySet()) {
                result.put(w.getKey().getEntity(), new HashSet<>());
                for (AttributeWrapper attributeWrapper : w.getValue()) {
                    result.get(w.getKey().getEntity()).add(attributeWrapper.getAttribute());
                }
            }

            return result;
        }

        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<LookupEntityDef, Set<AttributeInfoHolder>> getLookupEntityToLinkedLookups(String lookupName) {
        LookupEntityWrapper entity = getValueById(lookupName, LookupEntityWrapper.class);
        if (entity != null && MapUtils.isNotEmpty(entity.getLookupToReferences())) {
            Map<LookupEntityDef, Set<AttributeInfoHolder>> result = new HashMap<>();
            for (Entry<LookupEntityWrapper, Set<AttributeWrapper>> w : entity.getLookupToReferences().entrySet()) {
                result.put(w.getKey().getEntity(), new HashSet<>());
                for (AttributeWrapper attributeWrapper : w.getValue()) {
                    result.get(w.getKey().getEntity()).add(attributeWrapper.getAttribute());
                }
            }

            return result;
        }

        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<EntityDef, Set<AttributeInfoHolder>> getEntitiesReferencingThisLookup(String lookupName) {
        Map<EntityDef, Set<AttributeInfoHolder>> result = new HashMap<>();
        LookupEntityWrapper lookup = getValueById(lookupName, LookupEntityWrapper.class);
        if (lookup != null) {
            for (Entry<EntityWrapper, Set<AttributeWrapper>> w : lookup.getEntityFromReferences().entrySet()) {
                result.put(w.getKey().getEntity(), new HashSet<>());
                for (AttributeWrapper attributeWrapper : w.getValue()) {
                    result.get(w.getKey().getEntity()).add(attributeWrapper.getAttribute());
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<LookupEntityDef, Set<AttributeInfoHolder>> getLookupsReferencingThisLookup(String lookupName) {
        Map<LookupEntityDef, Set<AttributeInfoHolder>> result = new HashMap<>();
        LookupEntityWrapper lookup = getValueById(lookupName, LookupEntityWrapper.class);
        if (lookup != null) {
            for (Entry<LookupEntityWrapper, Set<AttributeWrapper>> w : lookup.getLookupFromReferences().entrySet()) {
                result.put(w.getKey().getEntity(), new HashSet<>());
                for (AttributeWrapper attributeWrapper : w.getValue()) {
                    result.get(w.getKey().getEntity()).add(attributeWrapper.getAttribute());
                }
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
     * Reassign / reorder DQ rules
     */
    private void assignDataQualityRules() {

        // Lookups
        for (LookupEntityWrapper lookup : getValues(LookupEntityWrapper.class)) {
            DQUtils.prepareDataQualityWrapper(lookup);
        }

        // Entities
        for (EntityWrapper entity : getValues(EntityWrapper.class)) {
            DQUtils.prepareDataQualityWrapper(entity);
        }
    }

    /**
     * Rebuild references in cache.
     */
    private void rebuildReferences() {
        // cleanup
        for (LookupEntityWrapper lookup : getValues(LookupEntityWrapper.class)) {
            lookup.getEntityFromReferences().clear();
            lookup.getLookupFromReferences().clear();
        }
        for (EntityWrapper entity : getValues(EntityWrapper.class)) {
            entity.getRelationsFrom().clear();
            entity.getRelationsTo().clear();
        }

        // process lookups
        for (LookupEntityWrapper fromLookup : getValues(LookupEntityWrapper.class)) {
            // process relations lookup -> lookup
            for (Entry<String, AttributeInfoHolder> e : fromLookup.getAttributes().entrySet()) {
                AbstractAttributeDef attrDef = e.getValue().getAttribute();
                if (e.getValue().isLookupLink()) {

                    LookupEntityWrapper toLookup = e.getValue().isSimple()
                            ? (LookupEntityWrapper) getStorageCache().getCache()
                            .get(LookupEntityWrapper.class)
                            .get(((SimpleAttributeDef) attrDef).getLookupEntityType())
                            : (LookupEntityWrapper) getStorageCache().getCache()
                            .get(LookupEntityWrapper.class)
                            .get(((ArrayAttributeDef) attrDef).getLookupEntityType());

                    LOGGER.debug("Processing lookup: {}, attr: {}", fromLookup.getEntity().getName(), e.getKey());

                    if (!toLookup.getLookupFromReferences().containsKey(fromLookup)) {
                        toLookup.getLookupFromReferences().put(fromLookup, new HashSet<>());
                    }
                    toLookup.getLookupFromReferences().get(fromLookup).add(new AttributeWrapper(e.getKey(), e.getValue()));

                    if (!fromLookup.getLookupToReferences().containsKey(toLookup)) {
                        fromLookup.getLookupToReferences().put(toLookup, new HashSet<>());
                    }
                    fromLookup.getLookupToReferences().get(toLookup).add(new AttributeWrapper(e.getKey(), e.getValue()));

                    // the last one - fix ref type
                    CodeAttributeDef cAttrDef = toLookup.getEntity().getCodeAttribute();
                    if (e.getValue().isArray()) {
                        ((ArrayAttributeDef) e.getValue().getAttribute()).setLookupEntityCodeAttributeType(ArrayValueType.fromValue(cAttrDef.getSimpleDataType()));
                    } else if (e.getValue().isSimple()) {
                        ((SimpleAttributeDef) e.getValue().getAttribute()).setLookupEntityCodeAttributeType(cAttrDef.getSimpleDataType());
                    }
                }
            }
        }
        // process Relations: entity -> entity
        for (RelationWrapper rel : getValues(RelationWrapper.class)) {
            EntityWrapper fromEntity = ((EntityWrapper) getStorageCache().getCache().get(EntityWrapper.class)
                    .get(rel.getRelation().getFromEntity()));
            EntityWrapper toEntity = ((EntityWrapper) getStorageCache().getCache().get(EntityWrapper.class)
                    .get(rel.getRelation().getToEntity()));
            LOGGER.debug("Processing relation: " + rel.getId());
            toEntity.getRelationsTo().put(rel, fromEntity);
            fromEntity.getRelationsFrom().put(rel, toEntity);
        }
        // process entity
        for (EntityWrapper fromEntity : getValues(EntityWrapper.class)) {
            // process relations entity -> lookups
            for (Entry<String, AttributeInfoHolder> e : fromEntity.getAttributes().entrySet()) {

                AbstractAttributeDef attrDef = e.getValue().getAttribute();
                if (e.getValue().isLookupLink()) {

                    LookupEntityWrapper toLookup = e.getValue().isSimple()
                            ? (LookupEntityWrapper) getStorageCache().getCache()
                            .get(LookupEntityWrapper.class)
                            .get(((SimpleAttributeDef) attrDef).getLookupEntityType())
                            : (LookupEntityWrapper) getStorageCache().getCache()
                            .get(LookupEntityWrapper.class)
                            .get(((ArrayAttributeDef) attrDef).getLookupEntityType());

                    LOGGER.debug("Processing lookup: " + fromEntity.getEntity().getName() + ", attr: " + e.getKey());
                    if (!toLookup.getEntityFromReferences().containsKey(fromEntity)) {
                        toLookup.getEntityFromReferences().put(fromEntity, new HashSet<>());
                    }

                    toLookup.getEntityFromReferences().get(fromEntity).add(new AttributeWrapper(e.getKey(), e.getValue()));
                    if (!fromEntity.getLookupToReferences().containsKey(toLookup)) {
                        fromEntity.getLookupToReferences().put(toLookup, new HashSet<>());
                    }

                    fromEntity.getLookupToReferences().get(toLookup).add(new AttributeWrapper(e.getKey(), e.getValue()));

                    // the last one - fix ref type
                    CodeAttributeDef cAttrDef = toLookup.getEntity().getCodeAttribute();
                    if (e.getValue().isArray()) {
                        ((ArrayAttributeDef) e.getValue().getAttribute()).setLookupEntityCodeAttributeType(ArrayValueType.fromValue(cAttrDef.getSimpleDataType()));
                    } else if (e.getValue().isSimple()) {
                        ((SimpleAttributeDef) e.getValue().getAttribute()).setLookupEntityCodeAttributeType(cAttrDef.getSimpleDataType());
                    }
                }
            }
            // ToDo implement process DQ -> attribute processing
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
		Collection<NestedEntityWrapper> entities = getValues(NestedEntityWrapper.class);
		return entities == null ? Collections.emptyList()
				: entities.stream().map(NestedEntityWrapper::getEntity).collect(Collectors.toList());
	}

	  /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Map<String, AttributeInfoHolder> getAttributesInfoMap(String id) {

        if (isLookupEntity(id)) {
            LookupEntityWrapper lew = getValueById(id, LookupEntityWrapper.class);
            if (Objects.nonNull(lew)) {
                return lew.getAttributes();
            }
        } else if (isEntity(id)) {
            EntityWrapper ew = getValueById(id, EntityWrapper.class);
            if (Objects.nonNull(ew)) {
                return ew.getAttributes();
            }
        } else if (isRelation(id)) {
            RelationWrapper rw = getValueById(id, RelationWrapper.class);
            if (Objects.nonNull(rw)) {
                return rw.getAttributes();
            }
        }

        return Collections.emptyMap();
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
                .filter(sa -> sa.getAttribute() instanceof AbstractSimpleAttributeDef
                        && ((AbstractSimpleAttributeDef) sa.getAttribute()).isMainDisplayable())
                .sorted(Comparator.comparingInt(AttributeInfoHolder::getOrder))
                .map(AttributeInfoHolder::getPath)
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

    private Model convertContextToModel(UpdateModelRequestContext context) {
        String storageId = SecurityUtils.getStorageId(context);
        return new Model().withStorageId(storageId)
                .withCleanseFunctions(new ListOfCleanseFunctions().withGroup(context.getCleanseFunctionsUpdate()))
                .withEntitiesGroup(context.getEntitiesGroupsUpdate())
                .withEntities(context.getEntityUpdate())
                .withLookupEntities(context.getLookupEntityUpdate())
                .withEnumerations(context.getEnumerationsUpdate())
                .withRelations(context.getRelationsUpdate())
                .withNestedEntities(context.getNestedEntityUpdate())
                .withSourceSystems(context.getSourceSystemsUpdate());
    }
}