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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.model.ModelSearchObject;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.meta.ArrayAttributeDef;
import org.unidata.mdm.meta.ArrayValueType;
import org.unidata.mdm.meta.CodeAttributeDef;
import org.unidata.mdm.meta.ComplexAttributeDef;
import org.unidata.mdm.meta.ComplexAttributesHolderEntityDef;
import org.unidata.mdm.meta.CustomPropertyDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.SimpleAttributeDef;
import org.unidata.mdm.meta.SimpleDataType;
import org.unidata.mdm.meta.dto.GetEntityDTO;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.meta.service.MetaCustomPropertiesConstants;
import org.unidata.mdm.meta.service.MetaModelMappingService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.meta.type.search.ModelHeaderField;
import org.unidata.mdm.meta.type.search.ModelIndexType;
import org.unidata.mdm.meta.type.search.RecordHeaderField;
import org.unidata.mdm.meta.type.search.RelationHeaderField;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.context.MappingRequestContext;
import org.unidata.mdm.search.context.SearchRequestContext;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.form.FormField;
import org.unidata.mdm.search.type.form.FormFieldsGroup;
import org.unidata.mdm.search.type.indexing.Indexing;
import org.unidata.mdm.search.type.indexing.IndexingField;
import org.unidata.mdm.search.type.mapping.Mapping;
import org.unidata.mdm.search.type.mapping.MappingField;
import org.unidata.mdm.search.type.mapping.impl.BooleanMappingField;
import org.unidata.mdm.search.type.mapping.impl.DateMappingField;
import org.unidata.mdm.search.type.mapping.impl.DoubleMappingField;
import org.unidata.mdm.search.type.mapping.impl.LongMappingField;
import org.unidata.mdm.search.type.mapping.impl.StringMappingField;
import org.unidata.mdm.search.type.mapping.impl.TimeMappingField;
import org.unidata.mdm.search.type.mapping.impl.TimestampMappingField;
import org.unidata.mdm.search.util.SearchUtils;
import org.unidata.mdm.system.exception.PlatformFailureException;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

/**
 * @author Mikhail Mikhailov on Oct 14, 2019
 */
@Component
public class MetaModelMappingServiceImpl implements MetaModelMappingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaModelMappingServiceImpl.class);

    private static final String FROM_TO_TIMESTAMP_TARGET_FORMAT = "date_time||epoch_millis";

    private static final String CREATE_INDEX_LOCK_NAME = "createMetaIndexLock";
    // FIXME Create mappings dynamically on the heap
    private static final Mapping MODEL_INDEX_MAPPING = new Mapping(ModelIndexType.MODEL)
        .withFields(
            new StringMappingField(ModelHeaderField.FIELD_NAME.getName())
                .withNonAnalyzable(true)
                .withIndexType(ModelIndexType.MODEL),
            new StringMappingField(ModelHeaderField.FIELD_DISPLAY_NAME.getName())
                .withNonAnalyzable(true)
                .withIndexType(ModelIndexType.MODEL),
            new StringMappingField(ModelHeaderField.FIELD_SEARCH_OBJECTS.getName())
                .withNonAnalyzable(true)
                .withIndexType(ModelIndexType.MODEL),
            new StringMappingField(ModelHeaderField.FIELD_VALUE.getName())
                .withIndexType(ModelIndexType.MODEL)
        );
    // FIXME Create mappings dynamically on the heap
    private static final List<MappingField> RECORD_HEADER = Arrays.asList(
        new StringMappingField(RecordHeaderField.FIELD_ETALON_ID.getName())
            .withDocValue(true)
            .withNonAnalyzable(true),
        new StringMappingField(RecordHeaderField.FIELD_PERIOD_ID.getName())
            .withDocValue(true)
            .withNonAnalyzable(true),
        new StringMappingField(RecordHeaderField.FIELD_ORIGINATOR.getName())
            .withNonAnalyzable(true),
        new TimestampMappingField(RecordHeaderField.FIELD_FROM.getName())
            .withFormat(FROM_TO_TIMESTAMP_TARGET_FORMAT)
            .withDefaultValue(SearchUtils.ES_MIN_FROM),
        new TimestampMappingField(RecordHeaderField.FIELD_TO.getName())
            .withFormat(FROM_TO_TIMESTAMP_TARGET_FORMAT)
            .withDefaultValue(SearchUtils.ES_MAX_TO),
        new TimestampMappingField(RecordHeaderField.FIELD_CREATED_AT.getName()),
        new TimestampMappingField(RecordHeaderField.FIELD_UPDATED_AT.getName()),
        new BooleanMappingField(RecordHeaderField.FIELD_PENDING.getName())
            .withDefaultValue(Boolean.FALSE),
        new BooleanMappingField(RecordHeaderField.FIELD_PUBLISHED.getName())
            .withDefaultValue(Boolean.TRUE),
        new BooleanMappingField(RecordHeaderField.FIELD_DELETED.getName())
            .withDefaultValue(Boolean.FALSE),
        new BooleanMappingField(RecordHeaderField.FIELD_INACTIVE.getName())
            .withDefaultValue(Boolean.FALSE),
        new StringMappingField(RecordHeaderField.FIELD_OPERATION_TYPE.getName())
            .withDocValue(true)
            .withNonAnalyzable(true),
        new StringMappingField(RecordHeaderField.FIELD_EXTERNAL_KEYS.getName())
            .withDocValue(true)
            .withNonAnalyzable(true)
    );
    // FIXME Create mappings dynamically on the heap
    private static final List<MappingField> RELATION_HEADER = Arrays.asList(
        new StringMappingField(RelationHeaderField.FIELD_ETALON_ID.getName())
            .withDocValue(true)
            .withNonAnalyzable(true),
        new StringMappingField(RelationHeaderField.FIELD_FROM_ETALON_ID.getName())
            .withDocValue(true)
            .withNonAnalyzable(true),
        new StringMappingField(RelationHeaderField.FIELD_TO_ETALON_ID.getName())
            .withDocValue(true)
            .withNonAnalyzable(true),
        new StringMappingField(RelationHeaderField.FIELD_PERIOD_ID.getName())
            .withDocValue(true)
            .withNonAnalyzable(true),
        new StringMappingField(RelationHeaderField.REL_NAME.getName())
            .withDocValue(true)
            .withNonAnalyzable(true),
        new StringMappingField(RelationHeaderField.REL_TYPE.getName())
            .withDocValue(true)
            .withNonAnalyzable(true),
        new TimestampMappingField(RelationHeaderField.FIELD_FROM.getName())
            .withFormat(FROM_TO_TIMESTAMP_TARGET_FORMAT)
            .withDefaultValue(SearchUtils.ES_MIN_FROM),
        new TimestampMappingField(RelationHeaderField.FIELD_TO.getName())
            .withFormat(FROM_TO_TIMESTAMP_TARGET_FORMAT)
            .withDefaultValue(SearchUtils.ES_MAX_TO),
        new TimestampMappingField(RelationHeaderField.FIELD_CREATED_AT.getName()),
        new TimestampMappingField(RelationHeaderField.FIELD_UPDATED_AT.getName()),
        new BooleanMappingField(RelationHeaderField.FIELD_PENDING.getName())
            .withDefaultValue(Boolean.FALSE),
        new BooleanMappingField(RelationHeaderField.FIELD_PUBLISHED.getName())
            .withDefaultValue(Boolean.TRUE),
        new BooleanMappingField(RelationHeaderField.FIELD_DELETED.getName())
            .withDefaultValue(Boolean.FALSE),
        new BooleanMappingField(RelationHeaderField.FIELD_INACTIVE.getName())
            .withDefaultValue(Boolean.FALSE),
        new BooleanMappingField(RelationHeaderField.FIELD_DIRECT.getName())
            .withDefaultValue(Boolean.FALSE)
    );

    @Autowired
    private MetaModelService modelService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private HazelcastInstance hazelcastInstance;
    /**
     * Constructor.
     */
    public MetaModelMappingServiceImpl() {
        super();
    }

    @Override
    public void ensureMetaModelIndex() {

        final ILock createIndexLock = hazelcastInstance.getLock(CREATE_INDEX_LOCK_NAME);
        try {
            if (createIndexLock.tryLock(5, TimeUnit.SECONDS)) {
                try {
                    MappingRequestContext mCtx = MappingRequestContext.builder()
                            .entity(ModelIndexType.INDEX_NAME)
                            .storageId(SecurityUtils.getCurrentUserStorageId())
                            .mapping(MODEL_INDEX_MAPPING)
                            .build();

                    searchService.process(mCtx);
                } finally {
                    createIndexLock.unlock();
                }
            } else {
                final String message = "Cannot aquire model index create lock.";
                LOGGER.error(message);
                throw new PlatformFailureException(message, MetaExceptionIds.EX_META_INDEX_LOCK_TIME_OUT);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            final String message = "Cannot aquire model index create lock.";
            LOGGER.error(message);
            throw new PlatformFailureException(message, e, MetaExceptionIds.EX_META_INDEX_LOCK_TIME_OUT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanMetaModelIndex() {
        searchService.deleteFoundResult(
            SearchRequestContext.builder(ModelIndexType.MODEL, ModelIndexType.INDEX_NAME)
                .onlyQuery(true)
                .skipEtalonId(true)
                .fetchAll(true)
                .storageId(SecurityUtils.getCurrentUserStorageId())
                .build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putToMetaModelIndex(Collection<ModelSearchObject> objects) {

        if (CollectionUtils.isEmpty(objects)) {
            return;
        }

        List<Indexing> indexings = objects.stream()
                .filter(Objects::nonNull)
                .map(element -> {
                    removeFromMetaModelIndex(element.getEntityName());
                    return element.getSearchElements().entries().stream()
                        .map(entry ->
                            new Indexing(ModelIndexType.MODEL, null)
                                .withFields(
                                        IndexingField.of(ModelIndexType.MODEL, ModelHeaderField.FIELD_SEARCH_OBJECTS.getName(), entry.getKey()),
                                        IndexingField.of(ModelIndexType.MODEL, ModelHeaderField.FIELD_VALUE.getName(), entry.getValue()),
                                        IndexingField.of(ModelIndexType.MODEL, ModelHeaderField.FIELD_NAME.getName(), element.getEntityName()),
                                        IndexingField.of(ModelIndexType.MODEL, ModelHeaderField.FIELD_DISPLAY_NAME.getName(), element.getDisplayName())
                                ))
                        .collect(Collectors.toList());
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        IndexRequestContext irc = IndexRequestContext.builder()
                .entity(ModelIndexType.INDEX_NAME)
                .storageId(SecurityUtils.getCurrentUserStorageId())
                .refresh(true)
                .index(indexings)
                .build();

        searchService.process(irc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dropAllEntityIndexes(String storageId) {

        String selectedStorageId = Objects.isNull(storageId) ? SecurityUtils.getCurrentUserStorageId() : storageId;
        Stream<String> entities = modelService.getEntitiesList().stream().map(EntityDef::getName);
        Stream<String> lookupEntities = modelService.getLookupEntitiesList().stream().map(LookupEntityDef::getName);
        Stream.concat(entities, lookupEntities).forEach(ent ->
            searchService.dropIndex(
                MappingRequestContext.builder()
                    .storageId(selectedStorageId)
                    .entity(ent)
                    .drop(true)
                    .build()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromMetaModelIndex(String... entityNames) {

        if (Objects.isNull(entityNames)) {
            return;
        }

        String storageId = SecurityUtils.getCurrentUserStorageId();
        List<FormField> fields = Stream.of(entityNames)
            .map(name -> FormField.strictValue(ModelHeaderField.FIELD_NAME.getFieldType(), ModelHeaderField.FIELD_NAME.getName(), name))
            .collect(Collectors.toList());

        if (fields.isEmpty()) {
            return;
        }

        searchService.deleteFoundResult(SearchRequestContext.builder(ModelIndexType.MODEL, ModelIndexType.INDEX_NAME)
                .onlyQuery(true)
                .skipEtalonId(true)
                .form(FormFieldsGroup.createOrGroup(fields))
                .storageId(storageId)
                .build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEntityMappings(String storageId, boolean force, List<String> names) {

        if (CollectionUtils.isEmpty(names)) {
            return;
        }

        updateEntityMappings(storageId, force, names.toArray(new String[names.size()]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateRelationMappings(String storageId, String entityName, List<String> relationNames) {

        if (CollectionUtils.isEmpty(relationNames)) {
            return;
        }

        updateRelationMappings(storageId, entityName, relationNames.toArray(new String[relationNames.size()]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEntityMappings(String storageId, boolean force, String... names) {

        String selectedStorageId = Objects.isNull(storageId) ? SecurityUtils.getCurrentUserStorageId() : storageId;
        for (String entityName : names) {

            boolean isEntity = modelService.isEntity(entityName);
            boolean isLookup = !isEntity && modelService.isLookupEntity(entityName);

            if (!isEntity && !isLookup) {
                LOGGER.info("Meta oject with name {} not found. Skipping.", entityName);
                continue;
            }

            if (isEntity) {
                GetEntityDTO dto = modelService.getEntityById(entityName);
                updateEntityMapping(selectedStorageId, force, dto.getEntity(), dto.getRefs());
            } else {
                LookupEntityDef entity = modelService.getLookupEntityById(entityName);
                updateLookupMapping(selectedStorageId, force, entity);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateRelationMappings(String storageId, String entityName, String... relationNames) {

        String selectedStorageId = Objects.isNull(storageId) ? SecurityUtils.getCurrentUserStorageId() : storageId;
        for (String relationName : relationNames) {

            RelationDef relation = modelService.getRelationById(entityName);

            if (Objects.isNull(relation)) {
                LOGGER.info("Meta oject with name {} not found. Skipping.", relationName);
                continue;
            }

            updateRelationMapping(selectedStorageId, entityName, relation);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateRelationMapping(String storageId, String entityName, RelationDef relation) {

        if (Objects.isNull(relation)) {
            return;
        }

        String selectedStorageId = Objects.isNull(storageId) ? SecurityUtils.getCurrentUserStorageId() : storageId;

        boolean processFromSide = Objects.isNull(entityName) || relation.getFromEntity().equals(entityName);
        boolean processToSide = Objects.isNull(entityName) || relation.getToEntity().equals(entityName);

        MappingRequestContext ctx;
        if (processFromSide) {
            ctx = processRelationMappings(selectedStorageId, relation, relation.getFromEntity());
            if (Objects.nonNull(ctx)) {
                searchService.process(ctx);
            }
        }

        if (processToSide) {
            ctx = processRelationMappings(selectedStorageId, relation, relation.getToEntity());
            if (Objects.nonNull(ctx)) {
                searchService.process(ctx);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEntityMapping(String storageId, boolean force, EntityDef entity, List<NestedEntityDef> refs) {

        if (Objects.isNull(entity)) {
            return;
        }

        String selectedStorageId = Objects.isNull(storageId) ? SecurityUtils.getCurrentUserStorageId() : storageId;
        MappingRequestContext ctx = processEntityMappings(selectedStorageId, force, entity, Objects.isNull(refs) ? Collections.emptyList() : refs);

        if (Objects.nonNull(ctx)) {
            searchService.process(ctx);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLookupMapping(String storageId, boolean force, LookupEntityDef entity) {

        if (Objects.isNull(entity)) {
            return;
        }

        String selectedStorageId = Objects.isNull(storageId) ? SecurityUtils.getCurrentUserStorageId() : storageId;
        MappingRequestContext ctx = processLookupMappings(selectedStorageId, force, entity);

        if (Objects.nonNull(ctx)) {
            searchService.process(ctx);
        }
    }

    private MappingRequestContext processEntityMappings(String storageId, boolean force, EntityDef entity, List<NestedEntityDef> refs) {

        String shards = entity.getCustomProperties().stream()
                .filter(cp -> MetaCustomPropertiesConstants.SEARCH_SHARDS_NUMBER.equals(cp.getName()))
                .map(CustomPropertyDef::getValue)
                .findAny()
                .orElse(null);

        // after upgrade elasticsearch to 6.4 refactoring this place.
        String tokenize = entity.getCustomProperties().stream()
                .filter(cp -> MetaCustomPropertiesConstants.CUSTOM_TOKENIZE_PROPERTY.equals(cp.getName()))
                .map(CustomPropertyDef::getValue)
                .findAny()
                .orElse(null);

        String replicas = entity.getCustomProperties().stream()
                .filter(cp -> MetaCustomPropertiesConstants.SEARCH_REPLICAS_NUMBER.equals(cp.getName()))
                .map(CustomPropertyDef::getValue)
                .findAny()
                .orElse(null);

        return MappingRequestContext.builder()
                .storageId(storageId)
                .entity(entity.getName())
                .shards(StringUtils.isNotBlank(shards) ? Integer.parseInt(shards) : 0)
                .replicas(StringUtils.isNotBlank(replicas) ? Integer.parseInt(replicas) : 0)
                .whitespace(StringUtils.equals(tokenize, "whitespace"))
                .forceCreate(force)
                .mappings(
                        new Mapping(EntityIndexType.RECORD).withFields(() -> processEntityMappingFields(entity, refs)),
                        new Mapping(EntityIndexType.ETALON))
                .build();
    }

    private MappingRequestContext processLookupMappings(String storageId, boolean force, LookupEntityDef entity) {

        String shards = entity.getCustomProperties().stream()
                .filter(cp -> MetaCustomPropertiesConstants.SEARCH_SHARDS_NUMBER.equals(cp.getName()))
                .map(CustomPropertyDef::getValue)
                .findAny()
                .orElse(null);

        String replicas = entity.getCustomProperties().stream()
                .filter(cp -> MetaCustomPropertiesConstants.SEARCH_REPLICAS_NUMBER.equals(cp.getName()))
                .map(CustomPropertyDef::getValue)
                .findAny()
                .orElse(null);

        return MappingRequestContext.builder()
                .storageId(storageId)
                .entity(entity.getName())
                .shards(StringUtils.isNotBlank(shards) ? Integer.parseInt(shards) : 0)
                .replicas(StringUtils.isNotBlank(replicas) ? Integer.parseInt(replicas) : 0)
                .forceCreate(force)
                .mappings(
                        new Mapping(EntityIndexType.RECORD).withFields(() -> processLookupMappingFields(entity)),
                        new Mapping(EntityIndexType.ETALON))
                .build();
    }

    private MappingRequestContext processRelationMappings(String storageId, RelationDef def, String sideEntityName) {

        String shards = def.getCustomProperties().stream()
                .filter(cp -> MetaCustomPropertiesConstants.SEARCH_SHARDS_NUMBER.equals(cp.getName()))
                .map(CustomPropertyDef::getValue)
                .findAny()
                .orElse(null);

        String replicas = def.getCustomProperties().stream()
                .filter(cp -> MetaCustomPropertiesConstants.SEARCH_REPLICAS_NUMBER.equals(cp.getName()))
                .map(CustomPropertyDef::getValue)
                .findAny()
                .orElse(null);

        return MappingRequestContext.builder()
                .storageId(storageId)
                .entity(sideEntityName)
                .shards(StringUtils.isNotBlank(shards) ? Integer.parseInt(shards) : 0)
                .replicas(StringUtils.isNotBlank(replicas) ? Integer.parseInt(replicas) : 0)
                .mappings(new Mapping(EntityIndexType.RELATION).withFields(() -> processRelationMappingFields(def)))
                .build();
    }

    private List<MappingField> processEntityMappingFields(EntityDef def, List<NestedEntityDef> refs) {

        List<MappingField> result = new ArrayList<>(RECORD_HEADER.size()
                + def.getSimpleAttribute().size()
                + def.getArrayAttribute().size()
                + def.getComplexAttribute().size());

        result.addAll(RECORD_HEADER);
        result.addAll(processComplexEntityMappingFields(EntityIndexType.RECORD, def, refs));

        return result;
    }

    private List<MappingField> processLookupMappingFields(LookupEntityDef entity) {

        List<MappingField> result = new ArrayList<>(RECORD_HEADER.size()
                + entity.getSimpleAttribute().size()
                + entity.getArrayAttribute().size()
                + entity.getAliasCodeAttributes().size()
                + 1);

        result.addAll(RECORD_HEADER);

        // 1. Code
        Collection<CodeAttributeDef> codeAttributes = new ArrayList<>();
        codeAttributes.add(entity.getCodeAttribute());
        codeAttributes.addAll(entity.getAliasCodeAttributes());
        for (CodeAttributeDef codeAttribute : codeAttributes) {

            switch (codeAttribute.getSimpleDataType()) {
            case STRING:
                result.add(new StringMappingField(codeAttribute.getName())
                        .withIndexType(EntityIndexType.RECORD)
                        .withNonAnalyzable(true));
                break;
            case INTEGER:
                result.add(new LongMappingField(codeAttribute.getName())
                        .withIndexType(EntityIndexType.RECORD));
                break;
            default:
                break;
            }
        }

        // 2. Simple attributes
        for (SimpleAttributeDef simpleAttr : entity.getSimpleAttribute()) {

            MappingField f = processSimpleAttribute(EntityIndexType.RECORD, simpleAttr);
            if (Objects.isNull(f)) {
                continue;
            }

            result.add(f);
        }

        // 3. Array attributes
        for (ArrayAttributeDef arrayAttr : entity.getArrayAttribute()) {

            MappingField f = processArrayAttribute(EntityIndexType.RECORD, arrayAttr);
            if (Objects.isNull(f)) {
                continue;
            }

            result.add(f);
        }

        return result;
    }

    private List<MappingField> processRelationMappingFields(RelationDef def) {

        List<MappingField> result = new ArrayList<>(RELATION_HEADER.size()
                + def.getArrayAttribute().size()
                + def.getSimpleAttribute().size()
                + def.getComplexAttribute().size());

        result.addAll(RELATION_HEADER);
        result.addAll(processComplexEntityMappingFields(EntityIndexType.RELATION, def, Collections.emptyList()));

        return result;
    }

    private List<MappingField> processComplexEntityMappingFields(IndexType indexType, ComplexAttributesHolderEntityDef def, List<NestedEntityDef> refs) {

        List<MappingField> result = new ArrayList<>(
                  def.getArrayAttribute().size()
                + def.getSimpleAttribute().size()
                + def.getComplexAttribute().size());

        // 1. Simple attributes
        for (SimpleAttributeDef simpleAttr : def.getSimpleAttribute()) {

            MappingField f = processSimpleAttribute(indexType, simpleAttr);
            if (Objects.isNull(f)) {
                continue;
            }

            result.add(f);
        }

        // 2. Array attributes
        for (ArrayAttributeDef arrayAttr : def.getArrayAttribute()) {

            MappingField f = processArrayAttribute(indexType, arrayAttr);
            if (Objects.isNull(f)) {
                continue;
            }

            result.add(f);
        }

        // 3. Complex attributes
        for (ComplexAttributeDef complexAttr : def.getComplexAttribute()) {

            String nestedEntityName = complexAttr.getNestedEntityName();
            NestedEntityDef nested = refs.stream()
                    .filter(nestedEntityDef -> nestedEntityDef.getName().equals(nestedEntityName))
                    .findFirst()
                    .orElse(null);

            if (nested == null) {
                String message = "Invalid model. Nested entity [{}] from complex attribute [{}] not found.";
                LOGGER.warn(message, nestedEntityName, complexAttr.getName());
                throw new PlatformFailureException(message,
                        MetaExceptionIds.EX_META_MAPPING_NESTED_ENTITY_NOT_FOUND,
                        complexAttr.getNestedEntityName(), complexAttr.getName());
            }

            result.addAll(processComplexEntityMappingFields(indexType, nested, refs));
        }

        return result;
    }

    /**
     * Processes an array attribute from EntityDef.
     *
     * @param builder the builder
     * @param attr    the attribute
     * @return the builder
     * @throws IOException
     */
    private MappingField processArrayAttribute(IndexType indexType, ArrayAttributeDef attr) {

        if (attr == null) {
            return null;
        }

        String name = attr.getName();
        ArrayValueType originalType = null;
        if (StringUtils.isNotBlank(attr.getLookupEntityType())) {
            originalType = attr.getLookupEntityCodeAttributeType();
        } else if (attr.getArrayValueType() != null) {
            originalType = attr.getArrayValueType();
        } else {
            originalType = ArrayValueType.STRING;
        }

        switch (originalType) {
        case STRING:
            return new StringMappingField(name)
                    .withMorphologicalAnalysis(attr.isSearchMorphologically())
                    .withIndexType(indexType)
                    .withCaseInsensitive(attr.isSearchCaseInsensitive())
                    .withMorphologicalAnalysis(attr.isSearchMorphologically());
        case INTEGER:
            return new LongMappingField(name)
                    .withIndexType(indexType);
        case NUMBER:
            return new DoubleMappingField(name)
                    .withIndexType(indexType);
        case DATE:
            return new DateMappingField(name)
                    .withIndexType(indexType);
        case TIME:
            return new TimeMappingField(name)
                    .withIndexType(indexType);
        case TIMESTAMP:
            return new TimestampMappingField(name)
                    .withIndexType(indexType);
        default:
            break;
        }

        return null;
    }

    /**
     * Processes a simple attribute from EntityDef.
     *
     * @param builder the builder
     * @param attr    the attribute
     * @return the builder
     * @throws IOException
     */
    private MappingField processSimpleAttribute(IndexType indexType, SimpleAttributeDef attr) {

        if (attr == null) {
            return null;
        }

        String name = attr.getName();
        SimpleDataType originalType = null;
        if (attr.getSimpleDataType() != null) {
            originalType = attr.getSimpleDataType();
        } else if (StringUtils.isNotBlank(attr.getEnumDataType())
                || StringUtils.isNotBlank(attr.getLinkDataType())) {
            originalType = SimpleDataType.STRING;
        } else if (StringUtils.isNotBlank(attr.getLookupEntityType())) {
            originalType = attr.getLookupEntityCodeAttributeType();
        } else {
            originalType = SimpleDataType.STRING;
        }

        switch (originalType) {
        case BLOB:
        case CLOB:
        case STRING:
            return new StringMappingField(name)
                    .withMorphologicalAnalysis(attr.isSearchMorphologically())
                    .withIndexType(indexType)
                    .withCaseInsensitive(attr.isSearchCaseInsensitive())
                    .withMorphologicalAnalysis(attr.isSearchMorphologically());
        case BOOLEAN:
            return new BooleanMappingField(name)
                    .withIndexType(indexType);
        case INTEGER:
            return new LongMappingField(name)
                    .withIndexType(indexType);
        case MEASURED:
        case NUMBER:
            return new DoubleMappingField(name)
                    .withIndexType(indexType);
        case DATE:
            return new DateMappingField(name)
                    .withIndexType(indexType);
        case TIME:
            return new TimeMappingField(name)
                    .withIndexType(indexType);
        case TIMESTAMP:
            return new TimestampMappingField(name)
                    .withIndexType(indexType);
        default:
            break;
        }

        return null;
    }
}
