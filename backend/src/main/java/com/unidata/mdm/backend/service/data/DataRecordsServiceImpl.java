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

package com.unidata.mdm.backend.service.data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.CommonDependableContext;
import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.FetchLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsDigestRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.JoinRequestContext;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteClassifierDTO;
import com.unidata.mdm.backend.common.dto.DeleteClassifiersDTO;
import com.unidata.mdm.backend.common.dto.DeleteRecordDTO;
import com.unidata.mdm.backend.common.dto.DeleteRelationDTO;
import com.unidata.mdm.backend.common.dto.DeleteRelationsDTO;
import com.unidata.mdm.backend.common.dto.EtalonRecordDTO;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.GetClassifiersDTO;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.dto.GetRecordsDTO;
import com.unidata.mdm.backend.common.dto.GetRelationDTO;
import com.unidata.mdm.backend.common.dto.GetRelationsDTO;
import com.unidata.mdm.backend.common.dto.KeysJoinDTO;
import com.unidata.mdm.backend.common.dto.LargeObjectDTO;
import com.unidata.mdm.backend.common.dto.MergeRecordsDTO;
import com.unidata.mdm.backend.common.dto.RelationDigestDTO;
import com.unidata.mdm.backend.common.dto.SplitRecordsDTO;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifierDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifiersDTO;
import com.unidata.mdm.backend.common.dto.UpsertRecordDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationsDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.ServiceUtils;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.conf.impl.JoinImpl;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.configuration.ConfigurationServiceExt;
import com.unidata.mdm.backend.service.data.batch.BatchOperationType;
import com.unidata.mdm.backend.service.data.binary.LargeObjectsServiceComponent;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersServiceComponent;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordLifecycleListener;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;

/**
 * The Class DataRecordsServiceImpl.
 *
 * @author Mikhail Mikhailov Data service.
 */
@Service
public class DataRecordsServiceImpl implements DataRecordsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordsServiceImpl.class);

    /**
     * Atomic upsert action listener.
     */
    private static final String ATOMIC_UPSERT_ACTION_LISTENER = "atomicUpsertActionListener";
    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;
    /**
     * Records vistory component.
     */
    @Autowired
    private RecordsServiceComponent recordsComponent;
    /**
     * Relations component.
     */
    @Autowired
    private RelationsServiceComponent relationsComponent;
    /**
     * Classifiers component.
     */
    @Autowired
    private ClassifiersServiceComponent classifiersComponent;
    /**
     * LOB component.
     */
    @Autowired
    private LargeObjectsServiceComponent lobComponent;
    /**
     * Audit writer
     */
    @Autowired
    private AuditEventsWriter auditEventsWriter;

    private OriginRecordsComponent originRecordsComponent;

    @Autowired
    private ConfigurationServiceExt configurationService;

    @Value("${unidata.search.index.refresh_interval:1000}")
    long rollBackDelay;

    @Value("${unidata.data.rollback.thread.count:1}")
    int rollBackPoolSize;

    private final ScheduledExecutorService rollBackExecutor = Executors.newScheduledThreadPool(rollBackPoolSize, new CustomizableThreadFactory("rollbackRecord-worker-"));
    @PreDestroy
    public void preDestroy() {
        rollBackExecutor.shutdown();
    }

    /**
     * Get multiple classifiers executors.
     */
    @Autowired
    @Qualifier(ATOMIC_UPSERT_ACTION_LISTENER)
    private DataRecordLifecycleListener<UpsertRequestContext> atomicUpsertActionListener;

    @Autowired
    public void setOriginRecordsComponent(final OriginRecordsComponent originRecordsComponent) {
        this.originRecordsComponent = originRecordsComponent;
    }

    /**
     * Default Spring ctor.
     */
    public DataRecordsServiceImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetRecordDTO getRecord(GetRequestContext ctx) {
        try {
            GetRecordDTO result = recordsComponent.loadRecord(ctx);
            auditEventsWriter.writeSuccessEvent(AuditActions.DATA_GET, ctx);
            return result;
        } catch (Exception e) {
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_GET, e, ctx);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys identify(RecordIdentityContext ctx) {
        return commonComponent.identify(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KeysJoinDTO join(JoinRequestContext ctx) {
        final Optional<JoinImpl> join = Optional.ofNullable(configurationService.getJoin());

        join.ifPresent(j ->
                Optional.ofNullable(configurationService.getListeners(ctx.getEntityName(), j.getBeforeJoinInstances()))
                        .ifPresent(beforeJoinListeners ->
                                beforeJoinListeners.forEach(beforeJoinListener -> Optional.ofNullable(beforeJoinListener.beforeJoin(ctx))
                                        .ifPresent(exitResult -> {
                                            if (ExitResult.Status.ERROR == exitResult.getStatus()) {
                                                throw new BusinessException(
                                                        "Error occurred during run before join user exit: " + exitResult.getWarningMessage(),
                                                        ExceptionId.EX_JOIN_USER_EXIT_BEFORE_ERROR,
                                                        exitResult.getWarningMessage()
                                                );
                                            }
                                        }))));

        final KeysJoinDTO joinDTO = commonComponent.join(ctx);

        join.ifPresent(j ->
                Optional.ofNullable(j.getAfterJoinInstances().get(ctx.getEntityName()))
                        .ifPresent(afterJoinListeners ->
                                afterJoinListeners.forEach(afterJoinListener -> Optional.ofNullable(afterJoinListener.afterJoin(joinDTO))
                                        .ifPresent(exitResult -> {
                                            if (ExitResult.Status.ERROR == exitResult.getStatus()) {
                                                throw new BusinessException(
                                                        "Error occurred during run after join user exit: " + exitResult.getWarningMessage(),
                                                        ExceptionId.EX_JOIN_USER_EXIT_AFTER_ERROR,
                                                        exitResult.getWarningMessage()
                                                );
                                            }
                                        }))));

        return joinDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetRecordDTO getEtalonRecordPreview(GetRequestContext ctx) {
        return recordsComponent.loadEtalonRecordView(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetRecordsDTO getRecords(GetMultipleRequestContext ctx) {
        try {
            GetRecordsDTO result = recordsComponent.loadRecords(ctx);
            ctx.getInnerGetContexts().forEach(tx -> auditEventsWriter.writeSuccessEvent(AuditActions.DATA_GET, tx));
            return result;
        } catch (Exception e) {
            ctx.getInnerGetContexts()
                    .forEach(tx -> auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_GET, e, tx));
            throw e;
        }
    }

    private UpsertRecordDTO upsertRecordWithAudit(UpsertRequestContext ctx) {
        try {
            UpsertRecordDTO result = recordsComponent.upsertRecord(ctx);
            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_UPSERT, ctx);
            }
            return result;
        } catch (Exception e) {

            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT, e, ctx);
            }
            throw e;
        }
    }

    private UpsertRelationsDTO upsertRelationWithAudit(UpsertRelationsRequestContext ctx) {
        try {
            UpsertRelationsDTO result = upsertRelations(ctx);
            ctx.getRelations().values().stream().flatMap(Collection::stream).forEach(rCtx -> {
                if (AuditLevel.AUDIT_SUCCESS <= rCtx.getAuditLevel()) {
                    auditEventsWriter.writeSuccessEvent(AuditActions.DATA_UPSERT_RELATION, rCtx);
                }
            });
            return result;
        } catch (Exception e) {
            ctx.getRelations().values().stream().flatMap(Collection::stream).forEach(rCtx -> {
                if (AuditLevel.AUDIT_SUCCESS <= rCtx.getAuditLevel()) {
                    auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT_RELATION, e, rCtx);
                }
            });
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteRecordDTO deleteRecord(DeleteRequestContext ctx) {
        try {
            DeleteRecordDTO result = recordsComponent.deleteRecord(ctx);
            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_DELETE, ctx);
            }
            return result;
        } catch (Exception e) {
            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_DELETE, e, ctx);
            }

            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MergeRecordsDTO merge(MergeRequestContext ctx) {
        try {
            MergeRecordsDTO result = recordsComponent.merge(ctx);
            if (AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_MERGE, ctx);
            }

            return result;
        } catch (Exception e) {
            if (AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_MERGE, e, ctx);
            }
            ctx.getDuplicates().forEach(this::reindexEtalon);
            reindexEtalon(ctx);
            throw e;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<MergeRecordsDTO> batchMerge(List<MergeRequestContext> ctxs) {
        return recordsComponent.batchMerge(ctxs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LargeObjectDTO fetchLargeObject(FetchLargeObjectRequestContext ctx) {
        return lobComponent.fetchLargeObject(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LargeObjectDTO saveLargeObject(SaveLargeObjectRequestContext ctx) {
        return lobComponent.saveLargeObject(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteLargeObject(DeleteLargeObjectRequestContext ctx) {
        return lobComponent.deleteLargeObject(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimelineDTO getRecordsTimeline(GetRequestContext ctx) {
        return recordsComponent.loadRecordsTimeline(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimelineDTO> getRelationsTimeline(GetRelationsRequestContext ctx) {
        return relationsComponent.loadRelationsTimeline(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimelineDTO getRelationTimeline(GetRelationRequestContext ctx) {
        return relationsComponent.loadRelationTimeline(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationDigestDTO loadRelatedEtalonIdsForDigest(GetRelationsDigestRequestContext ctx) {
        return relationsComponent.loadRelatedEtalonIdsForDigest(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetRelationDTO getRelation(GetRelationRequestContext ctx) {
        return relationsComponent.getRelation(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetRelationsDTO getRelations(GetRelationsRequestContext ctx) {
        return relationsComponent.getRelations(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRelationDTO upsertRelation(UpsertRelationRequestContext ctx) {
        return relationsComponent.upsertRelation(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRelationsDTO upsertRelations(UpsertRelationsRequestContext ctx) {
        return relationsComponent.upsertRelations(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetClassifierDTO getClassifier(GetClassifierDataRequestContext ctx) {
        return classifiersComponent.getClassifier(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetClassifiersDTO getClassifiers(GetClassifiersDataRequestContext ctx) {
        return classifiersComponent.getClassifiers(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertClassifierDTO upsertClassifier(UpsertClassifierDataRequestContext ctx) {
        return classifiersComponent.upsertClassifier(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertClassifiersDTO upsertClassifiers(List<UpsertClassifierDataRequestContext> ctxts) {
        return classifiersComponent.upsertClassifiers(ctxts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertClassifiersDTO upsertClassifiers(UpsertClassifiersDataRequestContext ctx) {
        return classifiersComponent.upsertClassifiers(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteClassifierDTO deleteClassifier(DeleteClassifierDataRequestContext ctx) {
        return classifiersComponent.deleteClassifier(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteClassifiersDTO deleteClassifiers(List<DeleteClassifierDataRequestContext> ctxts) {
        return classifiersComponent.deleteClassifiers(ctxts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteClassifiersDTO deleteClassifiers(DeleteClassifiersDataRequestContext ctxts) {
        return classifiersComponent.deleteClassifiers(ctxts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRecordDTO upsertRecord(@Nonnull UpsertRequestContext ctx) {

        try {
            return ServiceUtils.getDataRecordsService().upsertFullTransactional(ctx);
        } catch (Exception ex) {
            LOGGER.error("Error happened while atomic upsert record", ex);
            rollBackExecutor.schedule(() -> reindexModifiedEtalons(ctx), rollBackDelay, TimeUnit.MILLISECONDS);
            throw ex;
        }
    }

    @Override
    @Transactional
    public UpsertRecordDTO upsertFullTransactional(@Nonnull UpsertRequestContext ctx) {

        UpsertRecordDTO result = upsertRecordWithAudit(ctx);

        //we separate this actions because rCtx required filled etalonId or originId
        if (Objects.nonNull(ctx.getRelationDeletes())) {
            DeleteRelationsRequestContext dRctx = ctx.getRelationDeletes();
            dRctx.putToStorage(dRctx.keysId(), ctx.keys());
            dRctx.setOperationId(ctx.getOperationId());
            DeleteRelationsDTO deleteRelationsResult = deleteRelations(dRctx);
            result.setDeleteRelations(deleteRelationsResult == null ? null : deleteRelationsResult.getRelations());
        }

        if (Objects.nonNull(ctx.getRelations())) {
            UpsertRelationsRequestContext uRctx = ctx.getRelations();
            uRctx.putToStorage(uRctx.keysId(), ctx.keys());
            uRctx.setOperationId(ctx.getOperationId());
            UpsertRelationsDTO relationsResult = upsertRelationWithAudit(uRctx);
            result.setRelations(relationsResult == null ? null : relationsResult.getRelations());
        }

        if (Objects.nonNull(ctx.getClassifierDeletes())) {
            DeleteClassifiersDataRequestContext dCtx = ctx.getClassifierDeletes();
            dCtx.putToStorage(dCtx.keysId(), ctx.keys());
            dCtx.setOperationId(ctx.getOperationId());
            deleteClassifiers(dCtx);
        }

        if (Objects.nonNull(ctx.getClassifierUpserts())) {
            UpsertClassifiersDataRequestContext uCctx = ctx.getClassifierUpserts();
            uCctx.putToStorage(uCctx.keysId(), ctx.keys());
            uCctx.setOperationId(ctx.getOperationId());
            UpsertClassifiersDTO classifiersResult = upsertClassifiers(uCctx);
            result.setClassifiers(classifiersResult == null ? null : classifiersResult.getClassifiers());
        }

        atomicUpsertActionListener.after(ctx);
        return result;
    }

    private void reindexModifiedEtalons(CommonDependableContext ctx) {
        if (ctx == null) {
            return;
        }
        flatCollectCtxs(ctx).forEach(this::reindexEtalon);
    }

    private void reindexEtalon(RecordIdentityContext ctx) {
        RecordKeys keys = ctx.keys();
        GetRequestContext rollbackCtx;
        if (keys != null) {
            rollbackCtx = GetRequestContext.builder()
                    .entityName(keys.getEntityName())
                    .gsn(keys.getGsn())
                    .etalonKey(keys.getEtalonKey().getId())
                    .originKey(keys.getOriginKey().getId())
                    .sourceSystem(keys.getOriginKey().getSourceSystem())
                    .externalId(keys.getOriginKey().getExternalId())
                    .build();
        } else {
            rollbackCtx = GetRequestContext.builder()
                    .entityName(ctx.getEntityName())
                    .gsn(ctx.getGsn())
                    .etalonKey(ctx.getEtalonKey())
                    .originKey(ctx.getOriginKey())
                    .sourceSystem(ctx.getSourceSystem())
                    .externalId(ctx.getExternalId())
                    .build();
        }

        reindexEtalon(rollbackCtx);
    }

    private List<CommonDependableContext> flatCollectCtxs(final CommonDependableContext ctx) {
        if (ctx == null) {
            return Collections.emptyList();
        }
        final List<CommonDependableContext> ctxs = ctx.getFromStorage(StorageId.DEPENDED_CONTEXTS);
        if (CollectionUtils.isEmpty(ctxs)) {
            return Collections.singletonList(ctx);
        }
        return Stream.concat(
                Stream.of(ctx),
                ctxs.stream().flatMap(c -> this.flatCollectCtxs(c).stream())
        ).collect(Collectors.toList());
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public Collection<UpsertRecordDTO> bulkUpsertRecords(@Nonnull List<UpsertRequestContext> recordUpsertCtxs) {
        try {

            Map<BatchOperationType, List<? extends CommonRequestContext>> collected = new EnumMap<>(BatchOperationType.class);
            recordUpsertCtxs.forEach(ctx -> {

                if (Objects.nonNull(ctx.getRelations())) {
                    ((List<UpsertRelationsRequestContext>) collected.computeIfAbsent(
                            BatchOperationType.UPSERT_RELATIONS, k -> new ArrayList<UpsertRelationsRequestContext>()))
                        .add(ctx.getRelations());
                }

                if (Objects.nonNull(ctx.getRelationDeletes())) {
                    ((List<DeleteRelationsRequestContext>) collected.computeIfAbsent(
                            BatchOperationType.DELETE_RELATIONS, k -> new ArrayList<DeleteRelationsRequestContext>()))
                        .add(ctx.getRelationDeletes());
                }

                if (Objects.nonNull(ctx.getClassifierUpserts())) {
                    ((List<UpsertClassifiersDataRequestContext>) collected.computeIfAbsent(
                            BatchOperationType.UPSERT_CLASSIFIERS, k -> new ArrayList<UpsertClassifiersDataRequestContext>()))
                        .add(ctx.getClassifierUpserts());
                }

                if (Objects.nonNull(ctx.getClassifierDeletes())) {
                    ((List<DeleteClassifiersDataRequestContext>) collected.computeIfAbsent(
                            BatchOperationType.DELETE_CLASSIFIERS, k -> new ArrayList<DeleteClassifiersDataRequestContext>()))
                        .add(ctx.getClassifierDeletes());
                }

                ((List<UpsertRequestContext>) collected.computeIfAbsent(
                        BatchOperationType.UPSERT_RECORDS, k -> new ArrayList<UpsertRequestContext>()))
                    .add(ctx);
            });

            List<UpsertRecordDTO> result = null;
            for (BatchOperationType bot : BatchOperationType.values()) {

                List<? extends CommonRequestContext> payload = collected.get(bot);
                if (CollectionUtils.isEmpty(payload)) {
                    continue;
                }

                switch (bot) {
                case UPSERT_RECORDS:
                    result = recordsComponent.batchUpsertRecords((List<UpsertRequestContext>) payload, true);
                    break;
                case UPSERT_RELATIONS:
                    relationsComponent.batchUpsertRelations((List<UpsertRelationsRequestContext>) payload, true);
                    break;
                case DELETE_RELATIONS:
                    relationsComponent.batchDeleteRelations((List<DeleteRelationsRequestContext>) payload, true);
                    break;
                case UPSERT_CLASSIFIERS:
                    classifiersComponent.batchUpsertClassifiers((List<UpsertClassifiersDataRequestContext>) payload, true);
                    break;
                case DELETE_CLASSIFIERS:
                    classifiersComponent.batchDeleteClassifiers((List<DeleteClassifiersDataRequestContext>) payload, true);
                    break;
                default:
                    break;
                }
            }

            return result;
        } catch (Exception e) {
            recordUpsertCtxs.forEach(ctx -> auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT, e, ctx));
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteRelationDTO deleteRelation(DeleteRelationRequestContext ctx) {
        return relationsComponent.deleteRelation(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteRelationsDTO deleteRelations(DeleteRelationsRequestContext ctx) {
        return relationsComponent.deleteRelations(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRecordDTO restore(UpsertRequestContext ctx, boolean isModified) {
        try {
            EtalonRecordDTO etalonRecordDTO = recordsComponent.restoreRecord(ctx, isModified);
            auditEventsWriter.writeSuccessEvent(AuditActions.DATA_RESTORE, ctx);
            return etalonRecordDTO;
        } catch (Exception e) {
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_RESTORE, e, ctx);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRecordDTO restorePeriod(UpsertRequestContext ctx) {
        try {
            EtalonRecordDTO etalonRecordDTO = recordsComponent.restorePeriod(ctx);
            auditEventsWriter.writeSuccessEvent(AuditActions.PERIOD_RESTORE, ctx);
            return etalonRecordDTO;
        } catch (Exception e) {
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.PERIOD_RESTORE, e, ctx);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataQualityError> getDQErrors(String id, String entity, Date date) {
        return recordsComponent.extractDQErrors(id, entity, date);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SplitRecordsDTO detachOrigin(final String originId) {
       return recordsComponent.splitRecord(originId);
    }

    @Override
    public boolean reindexEtalon(GetRequestContext ctx) {
        return recordsComponent.reindexEtalon(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reapplyEtalon(UpsertRequestContext ctx) {
        ctx.putToStorage(StorageId.DATA_UPSERT_EXACT_ACTION, UpsertAction.UPDATE);
        recordsComponent.calculateEtalons(ctx);
    }

    @Override
    public List<String> selectCovered(List<String> etalonIds, LocalDateTime from, LocalDateTime to, boolean full) {
        if (CollectionUtils.isEmpty(etalonIds)) {
            return Collections.emptyList();
        }
        final Date fromDate = toDate(from, ValidityPeriodUtils.getGlobalValidityPeriodStart());
        final Date toDate = toDate(to, ValidityPeriodUtils.getGlobalValidityPeriodEnd());
        return etalonIds.stream()
                .map(id -> {
                    final GetRequestContext getRequestContext = GetRequestContext.builder()
                            .etalonKey(id)
                            .includeInactive(false)
                            .build();
                    return originRecordsComponent
                            .loadAndReduceWorkflowTimeline(getRequestContext, fromDate, toDate, false);
                })
                .filter(tl -> full ? fullCoverPeriod(tl, fromDate, toDate) : partialCoverPeriod(tl, fromDate, toDate))
                .map(TimelineDTO::getEtalonId)
                .collect(Collectors.toList());
    }

    private Date toDate(LocalDateTime from, Date defaultDate) {
        if (from == null) {
            return defaultDate;
        }
        return Date.from(from.atZone(ZoneId.systemDefault()).toInstant());
    }

    private boolean fullCoverPeriod(
            final WorkflowTimelineDTO timeline,
            final Date from,
            final Date to
    ) {
        final List<TimeIntervalDTO> intervals = timeline.getIntervals();
        if (CollectionUtils.isEmpty(intervals)) {
            return false;
        }
        if (leftBorderInvalid(from, intervals)) {
            return false;
        }
        if (rightBorderInvalid(to, intervals)) {
            return false;
        }
        final Iterator<TimeIntervalDTO> iterator = intervals.iterator();
        for (TimeIntervalDTO current = iterator.next(); iterator.hasNext();) {
            TimeIntervalDTO next = iterator.next();
            if (!current.isActive() || !next.isActive()) {
                return false;
            }
            if (next.getValidFrom().getTime() - current.getValidTo().getTime() > 1) {
                return false;
            }
            current = next;
        }
        return true;
    }

    private boolean leftBorderInvalid(Date from, List<TimeIntervalDTO> intervals) {
        final TimeIntervalDTO firstInterval = intervals.get(0);
        if (!firstInterval.isActive()) {
            return true;
        }
        if (from == null && firstInterval.getValidFrom() != null) {
            return true;
        }
        else {
            return firstInterval.getValidFrom() != null && from.getTime() < firstInterval.getValidFrom().getTime();
        }
    }

    private boolean rightBorderInvalid(Date to, List<TimeIntervalDTO> intervals) {
        final TimeIntervalDTO lastInterval = intervals.get(intervals.size() - 1);
        if (!lastInterval.isActive()) {
            return true;
        }
        if (to == null && lastInterval.getValidTo() != null) {
            return true;
        }
        else {
            return lastInterval.getValidTo() != null && to.getTime() > lastInterval.getValidTo().getTime();
        }
    }

    private boolean partialCoverPeriod(
            final WorkflowTimelineDTO timeline,
            final Date from,
            final Date to
    ) {
        if (from == null && to == null) {
            return true;
        }
        TimeIntervalDTO period = new TimeIntervalDTO(from, to, 1, true);
        return timeline.getIntervals().stream()
                .filter(TimeIntervalDTO::isActive)
                .anyMatch(i ->
                        (from != null && i.isInRange(from))
                                || (to != null && i.isInRange(to))
                                || (i.getValidFrom()!= null && period.isInRange(i.getValidFrom()))
                                || (i.getValidTo() != null && period.isInRange(i.getValidTo()))
                );
    }
}
