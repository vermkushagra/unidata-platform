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

package org.unidata.mdm.data.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.context.DeleteRelationsRequestContext;
import org.unidata.mdm.data.context.GetRelationRequestContext;
import org.unidata.mdm.data.context.GetRelationsDigestRequestContext;
import org.unidata.mdm.data.context.GetRelationsRequestContext;
import org.unidata.mdm.data.context.GetRelationsTimelineRequestContext;
import org.unidata.mdm.data.context.MergeRequestContext;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRelationsRequestContext;
import org.unidata.mdm.data.dao.RelationsDao;
import org.unidata.mdm.data.dto.DeleteRelationDTO;
import org.unidata.mdm.data.dto.DeleteRelationsDTO;
import org.unidata.mdm.data.dto.GetRelationDTO;
import org.unidata.mdm.data.dto.GetRelationsDTO;
import org.unidata.mdm.data.dto.RelationDigestDTO;
import org.unidata.mdm.data.dto.RelationStateDTO;
import org.unidata.mdm.data.dto.UpsertRelationDTO;
import org.unidata.mdm.data.dto.UpsertRelationsDTO;
import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationEtalonRemapFromPO;
import org.unidata.mdm.data.po.data.RelationEtalonRemapToPO;
import org.unidata.mdm.data.po.data.RelationOriginRemapPO;
import org.unidata.mdm.data.po.keys.RelationExternalKeyPO;
import org.unidata.mdm.data.service.DataRelationsService;
import org.unidata.mdm.data.service.RelationBatchSetProcessor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteConnectorExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationGetConnectorExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertConnectorExecutor;
import org.unidata.mdm.data.type.apply.RelationMergeChangeSet;
import org.unidata.mdm.data.type.apply.batch.AbstractBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationDeleteBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSetAccumulator;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RelationEtalonKey;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.RelationSide;
import org.unidata.mdm.meta.type.search.RelationFromIndexId;
import org.unidata.mdm.meta.type.search.RelationToIndexId;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.type.id.ManagedIndexId;
import org.unidata.mdm.system.service.ExecutionService;
import org.unidata.mdm.system.type.batch.BatchIterator;
import org.unidata.mdm.system.type.batch.BatchSetAccumulator;
import org.unidata.mdm.system.type.batch.BatchSetSize;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov Relations service component.
 */
@Component
public class DataRelationsServiceImpl implements DataRelationsService {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataRelationsServiceImpl.class);
    /**
     * Get multiple connector/executor component.
     */
    @Autowired
    @Qualifier(RelationGetConnectorExecutor.SEGMENT_ID)
    private RelationGetConnectorExecutor relationsGetConnectorExecutor;
    /**
     * Upsert multiple connector/executor component.
     */
    @Autowired
    @Qualifier(RelationUpsertConnectorExecutor.SEGMENT_ID)
    private RelationUpsertConnectorExecutor relationsUpsertConnectorExecutor;
    /**
     * Delete multiple connector/executor component.
     */
    @Autowired
    @Qualifier(RelationDeleteConnectorExecutor.SEGMENT_ID)
    private RelationDeleteConnectorExecutor relationsDeleteConnectorExecutor;
    /**
     * Relations vistory DAO.
     */
    @Autowired
    private RelationsDao relationsVistoryDao;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Common relations functionality.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * Batch set processor.
     */
    @Autowired
    private RelationBatchSetProcessor relationBatchSetProcessor;
    /**
     * The ES instance.
     */
    @Autowired
    private ExecutionService executionService;
    /**
     * Constructor.
     */
    public DataRelationsServiceImpl() {
        super();
    }
    /**
     * Loads relevant relations time line for the given relation identities and relation name.
     *
     * @param ctx the context
     * @return timeline
     */

    public List<Timeline<OriginRelation>> loadTimelines(GetRelationsTimelineRequestContext ctx) {

        MeasurementPoint.start();
        try {

            Map<String, List<Timeline<OriginRelation>>> timelines = commonRelationsComponent.loadTimelines(ctx);
            return timelines.entrySet().stream()
                .map(Entry::getValue)
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .filter(tl -> ! tl.isEmpty())
                .collect(Collectors.toList());

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Timeline<OriginRelation>> loadTimelines(GetRelationsRequestContext ctx) {
        return loadTimelines(GetRelationsTimelineRequestContext.builder(ctx).build());
    }
    /**
     * Gets a relation by simple request context.
     *
     * @param ctx the context
     * @return relation DTO
     */
    @Override
    public GetRelationDTO getRelation(GetRelationRequestContext ctx) {
        MeasurementPoint.start();
        try {
            return executionService.execute(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Gets relations by simple request contexts.
     *
     * @param ctxts the contexts
     * @return relations DTO
     */
    @Override
    public List<GetRelationDTO> getRelations(List<GetRelationRequestContext> ctxts) {

        List<GetRelationDTO> result = new ArrayList<>(ctxts.size());
        for (GetRelationRequestContext ctx : ctxts) {
            GetRelationDTO val = getRelation(ctx);
            if (val != null) {
                result.add(val);
            }
        }
        return result;
    }
    /**
     * Gets the relations.
     *
     * @param ctx the context
     * @return relations DTO
     */
    @Override
    public GetRelationsDTO getRelations(GetRelationsRequestContext ctx) {
        return relationsGetConnectorExecutor.execute(ctx, null);
    }
    /**
     * Actually upserts relation.
     *
     * @param ctx the context
     * @return result DTO
     */
    @Override
    @Transactional
    public UpsertRelationDTO upsertRelation(UpsertRelationRequestContext ctx) {
        MeasurementPoint.start();
        try {
            return executionService.execute(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Upsert multiple updating relations call.
     *
     * @param ctxts the contexts to process
     * @return result (inserted/updated record)
     */
    @Override
    @Transactional
    public List<UpsertRelationDTO> upsertRelations(List<UpsertRelationRequestContext> ctxts) {
        List<UpsertRelationDTO> result = new ArrayList<>(ctxts.size());
        for (UpsertRelationRequestContext ctx : ctxts) {
            UpsertRelationDTO val = upsertRelation(ctx);
            if (val != null) {
                result.add(val);
            }
        }
        return result;
    }
    /**
     * Upsert relations call.
     *
     * @param ctx the context
     * @return result (inserted/updated records)
     */
    @Override
    @Transactional
    public UpsertRelationsDTO upsertRelations(UpsertRelationsRequestContext ctx) {
        return relationsUpsertConnectorExecutor.execute(ctx, null);
    }
    /**
     * Actually deletes a relation.
     *
     * @param ctx the context
     * @return result DTO
     */
    @Override
    @Transactional
    public DeleteRelationDTO deleteRelation(DeleteRelationRequestContext ctx) {
        MeasurementPoint.start();
        try {
            return executionService.execute(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Deletes relations.
     *
     * @param ctxts the contexts
     * @return result DTO
     */
    @Override
    @Transactional
    public List<DeleteRelationDTO> deleteRelations(List<DeleteRelationRequestContext> ctxts) {
        List<DeleteRelationDTO> result = new ArrayList<>(ctxts.size());
        for (DeleteRelationRequestContext ctx : ctxts) {
            DeleteRelationDTO val = deleteRelation(ctx);
            if (val != null) {
                result.add(val);
            }
        }
        return result;
    }
    /**
     * Deletes relations.
     *
     * @param ctx the context
     * @return result DTO
     */
    @Override
    @Transactional
    public DeleteRelationsDTO deleteRelations(DeleteRelationsRequestContext ctx) {
        MeasurementPoint.start();
        try {
            return relationsDeleteConnectorExecutor.execute(ctx, null);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Deletes relations in batched fashion.
     *
     * @param accumulator accumulator
     * @return list of results
     */
    @Override
    public List<DeleteRelationsDTO> batchDeleteRelations(BatchSetAccumulator<DeleteRelationsRequestContext, DeleteRelationsDTO> accumulator) {

        // 1. Collect updates
        List<DeleteRelationsDTO> result = new ArrayList<>(accumulator.workingCopy().size());
        for (BatchIterator<DeleteRelationsRequestContext> bi = accumulator.iterator(); bi.hasNext(); ) {

            DeleteRelationsRequestContext ctx = bi.next();
            try {
                result.add(relationsDeleteConnectorExecutor.execute(ctx, accumulator.pipeline()));
            } catch (Exception e) {
                LOGGER.warn("Batch delete relations BEFORE exception caught.", e);

                if (accumulator.isAbortOnFailure()) {
                    throw e;
                }

                bi.remove();
            }
        }

        // 2. Apply updates
        relationBatchSetProcessor.apply((RelationDeleteBatchSetAccumulator) accumulator);

        // 2. Return result
        return result;
    }
    /**
     * batch delete relations with default accumulator context
     *
     * @param ctxs contexts for delete
     * @return delete result
     */
    @Override
    public List<DeleteRelationsDTO> batchDeleteRelations(List<DeleteRelationsRequestContext> ctxs, boolean abortOnFailure) {

        AbstractBatchSetAccumulator<DeleteRelationsRequestContext, DeleteRelationsDTO> accumulator = getDefaultRelationDeleteAccumulator();

        accumulator.setAbortOnFailure(abortOnFailure);
        accumulator.charge(ctxs);

        try {
            return batchDeleteRelations(accumulator);
        } finally {
            accumulator.discharge();
        }
    }
    /**
     * Batch upsert relations.
     *
     * @param accumulator accumulator
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UpsertRelationsDTO> batchUpsertRelations(BatchSetAccumulator<UpsertRelationsRequestContext, UpsertRelationsDTO> accumulator) {

        // 1. Collect updates
        List<UpsertRelationsDTO> result = new ArrayList<>(accumulator.workingCopy().size());
        for (BatchIterator<UpsertRelationsRequestContext> bi = accumulator.iterator(); bi.hasNext(); ) {

            UpsertRelationsRequestContext ctx = bi.next();
            try {
                result.add(relationsUpsertConnectorExecutor.execute(ctx, accumulator.pipeline()));
            } catch (Exception e) {
                LOGGER.warn("Batch upsert relations BEFORE exception caught.", e);

                if (accumulator.isAbortOnFailure()) {
                    throw e;
                }

                bi.remove();
            }
        }

        // 3. Apply updates
        relationBatchSetProcessor.apply((RelationUpsertBatchSetAccumulator) accumulator);

        // 4. Return result
        return result;
    }
    /**
     * batch upsert relations with default accumulator context
     *
     * @param ctxs contexts for update
     * @return update result
     */
    @Override
    @Transactional
    public List<UpsertRelationsDTO> batchUpsertRelations(List<UpsertRelationsRequestContext> ctxs, boolean abortOnFailure) {

        AbstractBatchSetAccumulator<UpsertRelationsRequestContext, UpsertRelationsDTO> accumulator = getDefaultRelationUpsertAccumulator();

        accumulator.setAbortOnFailure(abortOnFailure);
        accumulator.charge(ctxs);

        try {
            return batchUpsertRelations(accumulator);
        } finally {
            accumulator.discharge();
        }
    }
    /**
     * Loads all periods of all relations as list.
     *
     * @param keys        record from keys
     * @param operationId possibly existing operation id
     * @return collection of relations or empty list
     */
    // FIXME Kill this, refs from old backend only!
    // Used by COPY operation
    @Deprecated
    public List<EtalonRelation> loadActiveEtalonsRelationsByFromSideAsList(@Nonnull RecordKeys keys, String operationId) {
        return loadActiveEtalonsRelationsAsList(keys, true, operationId);
    }

    /**
     * Loads all periods of all relations as list by to side.
     *
     * @param keys        record from keys
     * @param operationId possibly existing operation id
     * @return collection of relations or empty list
     */
    // FIXME Kill this, refs from old backend only!
    // Used by COPY operation
    @Deprecated
    public List<EtalonRelation> loadActiveEtalonsRelationsByToSideAsList(@Nonnull RecordKeys keys, String operationId) {
        return loadActiveEtalonsRelationsAsList(keys, false, operationId);
    }

    /**
     * Loads all periods of all relations as list.
     *
     * @param keys        record from keys
     * @param byFromSide  by from side (true) or by to side (false)
     * @param operationId possibly existing operation id
     * @return collection of relations or empty list
     */
    // FIXME Kill this, refs from old backend only!
    // Used by COPY operation
    @Deprecated
    private List<EtalonRelation> loadActiveEtalonsRelationsAsList(@Nonnull RecordKeys keys, boolean byFromSide, String operationId) {

        MeasurementPoint.start();
        try {

            GetRelationsTimelineRequestContext ctx = GetRelationsTimelineRequestContext.builder()
                    .fetchByToSide(!byFromSide)
                    .forOperationId(operationId)
                    .fetchData(true)
                    .reduceReferences(true)
                    .build();

            ctx.keys(keys);

            Map<String, List<Timeline<OriginRelation>>> timelines = commonRelationsComponent.loadTimelines(ctx);
            return timelines.values().stream()
                    .flatMap(Collection::stream)
                    .flatMap(Timeline::stream)
                    .filter(TimeInterval::isActive)
                    .map(TimeInterval::<EtalonRelation>getCalculationResult)
                    .filter(Objects::nonNull)

                    .collect(Collectors.toList());

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Loads etlon relations for etalon id, relation name and date.
     *
     * @param from        the object (left side, from) etalon id
     * @param name        relation name
     * @param asOf        the date
     * @param operationId the operation id
     * @return relations
     */
    public Map<RelationStateDTO, List<GetRelationDTO>> loadActiveEtalonsRelations(
            RecordKeys from,
            String name,
            Date asOf,
            Date lud,
            String operationId
    ) {
        return loadEtalonsRelations(
                from, name, asOf, lud, operationId, Collections.singletonList(RecordStatus.ACTIVE), Collections.singleton(RelationSide.FROM)
        );
    }

    public Map<RelationStateDTO, List<GetRelationDTO>> loadAllEtalonsRelations(
            RecordKeys from,
            String name,
            Date asOf,
            Date lud,
            String operationId
    ) {
        return loadEtalonsRelations(
                from,
                name,
                asOf,
                lud,
                operationId,
                Arrays.asList(RecordStatus.ACTIVE, RecordStatus.INACTIVE, RecordStatus.MERGED),
                EnumSet.allOf(RelationSide.class)
        );
    }

    public List<GetRelationDTO> loadRelationsToEtalon(GetRelationsRequestContext ctx) {
        return ctx.getRelationNames().stream()
                .flatMap(name ->
                        relationsVistoryDao.loadEtalonRelations(
                                UUID.fromString(ctx.getEtalonKey()),
                                name,
                                Collections.singletonList(RecordStatus.ACTIVE),
                                RelationSide.TO
                        ).stream()
                )
                .map(r -> new GetRelationDTO(
                        RelationKeys.builder()
                                .etalonKey(RelationEtalonKey.builder()
                                    .from(RecordEtalonKey.builder()
                                            .id(r.getFromEtalonId())
                                            .build())
                                    .to(RecordEtalonKey.builder()
                                            .id(r.getToEtalonId())
                                            .build())
                                    .id(r.getId())
                                    .status(r.getStatus())
                                    .state(r.getApproval())
                                    .build())
                                .relationName(r.getName())
                                .relationType(r.getRelationType())
                                .build(),
                        r.getName(),
                        null
                ))
                .collect(Collectors.toList());
    }

    private Map<RelationStateDTO, List<GetRelationDTO>> loadEtalonsRelations(
            RecordKeys recordKeys,
            String name,
            Date asOf,
            Date lud,
            String operationId,
            List<RecordStatus> statuses,
            Set<RelationSide> sides
    ) {

        MeasurementPoint.start();
        try {

            List<RelationEtalonPO> toEtalons = new ArrayList<>();
            if (sides.contains(RelationSide.FROM)) {
                toEtalons.addAll(
                        relationsVistoryDao.loadEtalonRelations(
                                UUID.fromString(recordKeys.getEtalonKey().getId()), name, statuses, RelationSide.FROM
                        )
                );
            }
            if (sides.contains(RelationSide.TO)) {
                toEtalons.addAll(
                        relationsVistoryDao.loadEtalonRelations(
                                UUID.fromString(recordKeys.getEtalonKey().getId()), name, statuses, RelationSide.TO
                        )
                );
            }

            if (toEtalons.isEmpty()) {
                return Collections.emptyMap();
            }

            List<GetRelationRequestContext> requestList = toEtalons.stream().map(
                    po -> GetRelationRequestContext.builder()
                            .relationEtalonKey(po.getId())
                            .forDate(asOf)
                            .forLastUpdate(lud)
                            .forOperationId(operationId)
                            .build())
                    .collect(Collectors.toList());

            GetRelationsRequestContext ctx = GetRelationsRequestContext.builder()
                    .relations(Collections.singletonMap(name, requestList))
                    .build();

            ctx.keys(recordKeys);

            GetRelationsDTO result = getRelations(ctx);
            return result.getRelations();

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets relations digest.
     *
     * @param ctx the context
     * @return result
     */
    @Override
    public RelationDigestDTO loadRelatedEtalonIdsForDigest(GetRelationsDigestRequestContext ctx) {
        MeasurementPoint.start();
        try {

            RelationDef relationDef = metaModelService.getRelationById(ctx.getRelName());
            if (relationDef == null) {
                return null;
            }

            Map<String, Integer> sourceSystemsMap = metaModelService.getStraightSourceSystems();
            return relationsVistoryDao.loadDigestDestinationEtalonIds(UUID.fromString(ctx.getEtalonId()), ctx.getRelName(),
                    ctx.getDirection(), sourceSystemsMap, null, ctx.getCount(), ctx.getPage() * ctx.getCount());

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Check exist data relation by relation name
     *
     * @param relName the name
     * @return count
     */
    public boolean checkExistDataByRelName(String relName) {
        return relationsVistoryDao.checkExistDataRelationByName(relName);
    }

    /**
     * Changes etalon state.
     *
     * @param etalonId the etalon id
     * @param state    the state
     * @return true, if successful, false otherwise
     */
    @Transactional
    public boolean changeApproval(String etalonId, ApprovalState state) {
        return commonRelationsComponent.changeApproval(etalonId, state);
    }
    /**
     * Merge support.
     * @param ctx the merge context
     */
    public void mergeRelations(MergeRequestContext ctx) {

        MeasurementPoint.start();
        try {

    		RelationMergeState state = mergeInitState(ctx);
            if (state.fromSideHasNoRelations() && state.toSideHasNoRelations()) {
                return;
            }

            final RecordKeys master = ctx.keys();
            final List<RecordKeys> duplicates = ctx.duplicateKeys();
            final RelationMergeChangeSet set = ctx.relationChangeSet();

            duplicates.forEach(duplicate -> {

                // 1. Process duplicates by 'from' side
                mergeProcessFromSide(state, master, duplicate);

                // 2. Process duplicates by 'to' side
                mergeProcessToSide(state, master, duplicate);
            });

            if (CollectionUtils.isNotEmpty(state.collectedIndexIds())) {
                set.getIndexRequestContexts().add(IndexRequestContext.builder()
                        .drop(true)
                        .delete(state.collectedIndexIds())
                        .build());
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

	private void mergeProcessFromSide(RelationMergeState state, RecordKeys master, RecordKeys duplicate) {

        if (state.fromSideHasNoRelations()) {
            return;
        }

        GetRelationsTimelineRequestContext tCtx = GetRelationsTimelineRequestContext.builder()
                .fetchByToSide(false)
                .etalonKey(duplicate.getEtalonKey())
                .build();

        Map<String, List<Timeline<OriginRelation>>> timelines = commonRelationsComponent.loadTimelines(tCtx);
        timelines.forEach((key, value) -> {

            RelationDef rel = state.fromTypeByName(key);

            // Possibly skip an old rel, already removed from model
            if (Objects.isNull(rel)) {
                return;
            }

            value.forEach(t -> {

                RelationKeys relationKeys = t.getKeys();

                // 1. Drop from index
                t.forEach(interval -> {

                    state.collectedIndexIds().add(
                            RelationFromIndexId.of(
                                    rel.getFromEntity(),
                                    relationKeys.getRelationName(),
                                    relationKeys.getEtalonKey().getFrom().getId(),
                                    relationKeys.getEtalonKey().getTo().getId(),
                                    interval.getPeriodId()));
                    state.collectedIndexIds().add(
                            RelationToIndexId.of(
                                    rel.getToEntity(),
                                    relationKeys.getRelationName(),
                                    relationKeys.getEtalonKey().getFrom().getId(),
                                    relationKeys.getEtalonKey().getTo().getId(),
                                    interval.getPeriodId()));
                });

                // 2. Drop from ref keys tables
                RelationExternalKeyPO wek = new RelationExternalKeyPO();
                wek.setFromRecordEtalonId(UUID.fromString(relationKeys.getEtalonKey().getFrom().getId()));
                wek.setFromShard(StorageUtils.shard(wek.getFromRecordEtalonId()));
                wek.setToRecordEtalonId(UUID.fromString(relationKeys.getEtalonKey().getTo().getId()));
                wek.setToShard(StorageUtils.shard(wek.getToRecordEtalonId()));
                wek.setRelationName(relationKeys.getRelationName());

                state.changeSet().getExternalKeyWipes().add(wek);

                // 3. Decide what to do with origins and etalon object
                RelationKeys masterKeys = state.masterKeysByTypeAndToId(rel, relationKeys.getEtalonKey().getTo().getId());
                if (Objects.nonNull(masterKeys)) {

                    // 3.1. Drop this rel, that we cannot remap.
                    // Master rel points already to the same 'to' side, so drop the original.
                    RelationEtalonPO erpo = new RelationEtalonPO();
                    erpo.setId(relationKeys.getEtalonKey().getId());
                    erpo.setFromEtalonId(duplicate.getEtalonKey().getId());
                    erpo.setOperationId(state.operationId());
                    erpo.setStatus(RecordStatus.MERGED);
                    erpo.setUpdateDate(state.timestamp());
                    erpo.setUpdatedBy(state.userName());
                    erpo.setName(relationKeys.getRelationName());

                    state.changeSet().getEtalonUpdates().add(erpo);

                    // 3.2. Remap its origins to the new target
                    state.changeSet().getOriginRemaps().addAll(
                        relationKeys.getSupplementaryKeys().stream()
                            .map(ok -> {

                                RelationOriginRemapPO po = new RelationOriginRemapPO();
                                po.setId(ok.getId());
                                po.setShard(relationKeys.getShard());
                                po.setNewEtalonId(UUID.fromString(masterKeys.getEtalonKey().getId()));
                                po.setNewShard(masterKeys.getShard());
                                po.setUpdateDate(state.timestamp());
                                po.setUpdatedBy(state.userName());
                                po.setCreateDate(ok.getCreateDate());
                                po.setCreatedBy(ok.getCreatedBy());
                                po.setEnrichment(ok.isEnrichment());
                                po.setEtalonId(relationKeys.getEtalonKey().getId());
                                po.setFromOriginId(ok.getFrom().getId());
                                po.setInitialOwner(ok.getInitialOwner());
                                po.setName(relationKeys.getRelationName());
                                po.setSourceSystem(ok.getSourceSystem());
                                po.setStatus(ok.getStatus());
                                po.setToOriginId(ok.getTo().getId());

                                return po;
                            })
                            .collect(Collectors.toList()));

                    return;
                }

                // 4. Contribute a new relation to new master object
                RelationEtalonRemapFromPO remapFrom = new RelationEtalonRemapFromPO();

                remapFrom.setId(relationKeys.getEtalonKey().getId());
                remapFrom.setShard(relationKeys.getShard());
                remapFrom.setFromEtalonId(duplicate.getEtalonKey().getId());
                remapFrom.setNewEtalonIdFrom(master.getEtalonKey().getId());
                remapFrom.setName(key);
                remapFrom.setOperationId(state.operationId());
                remapFrom.setUpdateDate(state.timestamp());
                remapFrom.setUpdatedBy(state.userName());

                state.changeSet().getEtalonFromRemaps().add(remapFrom);

                RelationExternalKeyPO iek = new RelationExternalKeyPO();

                iek.setFromRecordEtalonId(UUID.fromString(master.getEtalonKey().getId()));
                iek.setFromShard(StorageUtils.shard(wek.getFromRecordEtalonId()));
                iek.setToRecordEtalonId(UUID.fromString(relationKeys.getEtalonKey().getTo().getId()));
                iek.setToShard(StorageUtils.shard(wek.getToRecordEtalonId()));
                iek.setRelationName(relationKeys.getRelationName());
                iek.setRelationEtalonId(UUID.fromString(relationKeys.getEtalonKey().getId()));

                state.changeSet().getExternalKeyInserts().add(iek);
            });
        });
    }

    private void mergeProcessToSide(RelationMergeState state, RecordKeys master, RecordKeys duplicate) {

        if (state.toSideHasNoRelations()) {
            return;
        }

        GetRelationsTimelineRequestContext tCtx = GetRelationsTimelineRequestContext.builder()
                .fetchByToSide(true)
                .etalonKey(duplicate.getEtalonKey())
                .build();

        Map<String, List<Timeline<OriginRelation>>> timelines = commonRelationsComponent.loadTimelines(tCtx);
        timelines.forEach((key, value) -> {

            RelationDef rel = state.toTypeByName(key);

            // Possibly skip an old rel, already removed from model
            if (Objects.isNull(rel)) {
                return;
            }

            value.forEach(t -> {

                RelationKeys relationKeys = t.getKeys();

                // 1. Drop from index
                t.forEach(interval -> {

                    state.collectedIndexIds().add(
                        RelationFromIndexId.of(
                            rel.getFromEntity(),
                            relationKeys.getRelationName(),
                            relationKeys.getEtalonKey().getFrom().getId(),
                            relationKeys.getEtalonKey().getTo().getId(),
                            interval.getPeriodId()));

                    state.collectedIndexIds().add(
                        RelationToIndexId.of(
                            rel.getToEntity(),
                            relationKeys.getRelationName(),
                            relationKeys.getEtalonKey().getFrom().getId(),
                            relationKeys.getEtalonKey().getTo().getId(),
                            interval.getPeriodId()));
                });

                // 2. Drop from ref keys tables
                RelationExternalKeyPO wek = new RelationExternalKeyPO();
                wek.setFromRecordEtalonId(UUID.fromString(relationKeys.getEtalonKey().getFrom().getId()));
                wek.setFromShard(StorageUtils.shard(wek.getFromRecordEtalonId()));
                wek.setToRecordEtalonId(UUID.fromString(relationKeys.getEtalonKey().getTo().getId()));
                wek.setToShard(StorageUtils.shard(wek.getToRecordEtalonId()));
                wek.setRelationName(relationKeys.getRelationName());

                state.changeSet().getExternalKeyWipes().add(wek);

                // 3. Decide what to do with origins and etalon object
                RelationKeys masterKeys = state.masterKeysByTypeAndFromId(rel, relationKeys.getEtalonKey().getFrom().getId());
                if (Objects.nonNull(masterKeys)) {

                    // 3.1. Drop this rel, that we cannot remap.
                    // Master rel points already to the same 'to' side, so drop the original.
                    RelationEtalonPO erpo = new RelationEtalonPO();
                    erpo.setId(relationKeys.getEtalonKey().getId());
                    erpo.setFromEtalonId(duplicate.getEtalonKey().getId());
                    erpo.setOperationId(state.operationId());
                    erpo.setStatus(RecordStatus.MERGED);
                    erpo.setUpdateDate(state.timestamp());
                    erpo.setUpdatedBy(state.userName());
                    erpo.setName(relationKeys.getRelationName());

                    state.changeSet().getEtalonUpdates().add(erpo);

                    // 3.2. Remap its origins to the new target
                    state.changeSet().getOriginRemaps().addAll(
                        relationKeys.getSupplementaryKeys().stream()
                            .map(ok -> {

                                RelationOriginRemapPO po = new RelationOriginRemapPO();
                                po.setId(ok.getId());
                                po.setShard(relationKeys.getShard());
                                po.setNewEtalonId(UUID.fromString(masterKeys.getEtalonKey().getId()));
                                po.setNewShard(masterKeys.getShard());
                                po.setUpdateDate(state.timestamp());
                                po.setUpdatedBy(state.userName());
                                po.setCreateDate(ok.getCreateDate());
                                po.setCreatedBy(ok.getCreatedBy());
                                po.setEnrichment(ok.isEnrichment());
                                po.setEtalonId(relationKeys.getEtalonKey().getId());
                                po.setFromOriginId(ok.getFrom().getId());
                                po.setInitialOwner(ok.getInitialOwner());
                                po.setName(relationKeys.getRelationName());
                                po.setSourceSystem(ok.getSourceSystem());
                                po.setStatus(ok.getStatus());
                                po.setToOriginId(ok.getTo().getId());

                                return po;
                            })
                            .collect(Collectors.toList()));

                    return;
                }

                // 4. Contribute a new relation to new master object
                RelationEtalonRemapToPO remapTo = new RelationEtalonRemapToPO();

                remapTo.setId(relationKeys.getEtalonKey().getId());
                remapTo.setShard(relationKeys.getShard());
                remapTo.setToEtalonId(duplicate.getEtalonKey().getId());
                remapTo.setNewEtalonIdTo(master.getEtalonKey().getId());
                remapTo.setName(relationKeys.getRelationName());
                remapTo.setOperationId(state.operationId());
                remapTo.setUpdateDate(state.timestamp());
                remapTo.setUpdatedBy(state.userName());

                state.changeSet().getEtalonToRemaps().add(remapTo);

                RelationExternalKeyPO iek = new RelationExternalKeyPO();
                iek.setFromRecordEtalonId(UUID.fromString(relationKeys.getEtalonKey().getFrom().getId()));
                iek.setFromShard(StorageUtils.shard(wek.getFromRecordEtalonId()));
                iek.setToRecordEtalonId(UUID.fromString(master.getEtalonKey().getId()));
                iek.setToShard(StorageUtils.shard(wek.getToRecordEtalonId()));
                iek.setRelationName(relationKeys.getRelationName());
                iek.setRelationEtalonId(UUID.fromString(relationKeys.getEtalonKey().getId()));

                state.changeSet().getExternalKeyInserts().add(iek);
            });
        });
    }

    private RelationMergeState mergeInitState(MergeRequestContext ctx) {

        RelationMergeState state = new RelationMergeState(ctx);
        final RecordKeys master = ctx.keys();

        state.fromMap = metaModelService.getRelationsByFromEntityName(master.getEntityName())
                .stream()
                .collect(Collectors.toMap(RelationDef::getName, Function.identity()));

        state.toMap = metaModelService.getRelationsByToEntityName(master.getEntityName())
                .stream()
                .collect(Collectors.toMap(RelationDef::getName, Function.identity()));

        mergeInitMasterFromRelations(master, state);
        mergeInitMasterToRelations(master, state);

        return state;
    }

    private void mergeInitMasterToRelations(RecordKeys master, RelationMergeState state) {

        if (state.toSideHasNoRelations()) {
            return;
        }

        GetRelationsTimelineRequestContext masterCtx = GetRelationsTimelineRequestContext.builder()
                .fetchByToSide(true)
                .etalonKey(master.getEtalonKey())
                .build();

        Map<RelationDef, Map<String, RelationKeys>> result = new IdentityHashMap<>();
        Map<String, List<Timeline<OriginRelation>>> masterTimeline = commonRelationsComponent.loadTimelines(masterCtx);

        masterTimeline.forEach((key, value) -> {

            RelationDef rel = state.toTypeByName(key);

            // Possibly skip an old rel, already removed from model
            if (Objects.isNull(rel)) {
                return;
            }

            value.forEach(t -> {

                RelationKeys relationKeys = t.getKeys();
                t.forEach(interval -> {

                    state.collectedIndexIds().add(
                            RelationFromIndexId.of(
                                    rel.getFromEntity(),
                                    relationKeys.getRelationName(),
                                    relationKeys.getEtalonKey().getFrom().getId(),
                                    relationKeys.getEtalonKey().getTo().getId(),
                                    interval.getPeriodId()));

                    state.collectedIndexIds().add(
                            RelationToIndexId.of(
                                    rel.getToEntity(),
                                    relationKeys.getRelationName(),
                                    relationKeys.getEtalonKey().getFrom().getId(),
                                    relationKeys.getEtalonKey().getTo().getId(),
                                    interval.getPeriodId()));
                });

                result.computeIfAbsent(rel, k -> new HashMap<String, RelationKeys>())
                    .put(relationKeys.getEtalonKey().getFrom().getId(), relationKeys);
            });
        });

        state.toIdsMap = result;
    }

    private void mergeInitMasterFromRelations(RecordKeys master, RelationMergeState state) {

        if (state.fromSideHasNoRelations()) {
            return;
        }

        GetRelationsTimelineRequestContext masterCtx = GetRelationsTimelineRequestContext.builder()
                .fetchByToSide(false)
                .etalonKey(master.getEtalonKey())
                .build();

        Map<RelationDef, Map<String, RelationKeys>> result = new IdentityHashMap<>();
        Map<String, List<Timeline<OriginRelation>>> masterTimeline = commonRelationsComponent.loadTimelines(masterCtx);
        masterTimeline.forEach((key, value) -> {

            RelationDef rel = state.fromTypeByName(key);

            // Possibly skip an old rel, already removed from model
            if (Objects.isNull(rel)) {
                return;
            }

            value.forEach(t -> {

                RelationKeys relationKeys = t.getKeys();
                t.forEach(interval -> {

                    state.collectedIndexIds().add(
                            RelationFromIndexId.of(
                                    rel.getFromEntity(),
                                    relationKeys.getRelationName(),
                                    relationKeys.getEtalonKey().getFrom().getId(),
                                    relationKeys.getEtalonKey().getTo().getId(),
                                    interval.getPeriodId()));
                    state.collectedIndexIds().add(
                            RelationToIndexId.of(
                                    rel.getToEntity(),
                                    relationKeys.getRelationName(),
                                    relationKeys.getEtalonKey().getFrom().getId(),
                                    relationKeys.getEtalonKey().getTo().getId(),
                                    interval.getPeriodId()));
                });

                result.computeIfAbsent(rel, k -> new HashMap<String, RelationKeys>())
                      .put(relationKeys.getEtalonKey().getTo().getId(), relationKeys);
            });
        });

        state.fromIdsMap = result;
    }
    /**
     * Set record status to inactive for all rels for it rel name!
     *
     * @param relationName relation name
     */
    public void deactiveteRelationsByName(String relationName) {
        commonRelationsComponent.deactivateRelationsByName(relationName);
    }

    private AbstractBatchSetAccumulator<UpsertRelationsRequestContext, UpsertRelationsDTO> getDefaultRelationUpsertAccumulator() {
        RelationUpsertBatchSetAccumulator accumulator
                = new RelationUpsertBatchSetAccumulator(500, false, false);
        accumulator.setBatchSetSize(BatchSetSize.SMALL);
        return accumulator;
    }

    private AbstractBatchSetAccumulator<DeleteRelationsRequestContext, DeleteRelationsDTO> getDefaultRelationDeleteAccumulator() {
        RelationDeleteBatchSetAccumulator accumulator
                = new RelationDeleteBatchSetAccumulator(500, false);
        accumulator.setBatchSetSize(BatchSetSize.SMALL);
        return accumulator;
    }

    private class RelationMergeState {
        /**
         * Index drop updates.
         */
        private final List<ManagedIndexId> collectedIndexIds = new ArrayList<>();
        /**
         * The change set.
         */
        private final RelationMergeChangeSet changeSet;
        /**
         * The timestamp.
         */
        private final Date timestamp;
        /**
         * The user name.
         */
        private final String userName;
        /**
         * The op. id.
         */
        private final String operationId;
        /**
         * From reltypes.
         */
        private Map<String, RelationDef> fromMap = Collections.emptyMap();
        /**
         * To reltypes.
         */
        private Map<String, RelationDef> toMap = Collections.emptyMap();
        /**
         * From master ids.
         */
        private Map<RelationDef, Map<String, RelationKeys>> fromIdsMap = Collections.emptyMap();
        /**
         * To master ids.
         */
        private Map<RelationDef, Map<String, RelationKeys>> toIdsMap = Collections.emptyMap();
        /**
         * Constructor.
         * @param ctx the context
         */
        public RelationMergeState(MergeRequestContext ctx) {
            super();
            changeSet = ctx.relationChangeSet();
            timestamp = ctx.timestamp();
            operationId = ctx.getOperationId();
            userName = SecurityUtils.getCurrentUserName();
        }
        /**
         * @return the collectedIndexIds
         */
        public List<ManagedIndexId> collectedIndexIds() {
            return collectedIndexIds;
        }

        public RelationDef fromTypeByName(String name) {
            return fromMap.get(name);
        }

        public boolean fromSideHasNoRelations() {
            return MapUtils.isEmpty(fromMap);
        }

        public RelationKeys masterKeysByTypeAndToId(RelationDef relation, String toId) {
            Map<String, RelationKeys> masterKeysTable = fromIdsMap.get(relation);
            return Objects.isNull(masterKeysTable) ? null : masterKeysTable.get(toId);
        }

        public RelationKeys masterKeysByTypeAndFromId(RelationDef relation, String fromId) {
            Map<String, RelationKeys> masterKeysTable = toIdsMap.get(relation);
            return Objects.isNull(masterKeysTable) ? null : masterKeysTable.get(fromId);
        }

        public RelationDef toTypeByName(String name) {
            return toMap.get(name);
        }

        public boolean toSideHasNoRelations() {
            return MapUtils.isEmpty(toMap);
        }
        /**
         * @return the changeSet
         */
        public RelationMergeChangeSet changeSet() {
            return changeSet;
        }
        /**
         * @return the timestamp
         */
        public Date timestamp() {
            return timestamp;
        }
        /**
         * @return the userName
         */
        public String userName() {
            return userName;
        }
        /**
         * @return the operationId
         */
        public String operationId() {
            return operationId;
        }
    }
}
