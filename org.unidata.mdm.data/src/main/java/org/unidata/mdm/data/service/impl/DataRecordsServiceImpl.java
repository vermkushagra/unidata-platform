package org.unidata.mdm.data.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.core.context.DeleteLargeObjectRequestContext;
import org.unidata.mdm.core.context.FetchLargeObjectRequestContext;
import org.unidata.mdm.core.context.SaveLargeObjectRequestContext;
import org.unidata.mdm.core.dto.LargeObjectDTO;
import org.unidata.mdm.core.service.LargeObjectsServiceComponent;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.audit.AuditDataFallback;
import org.unidata.mdm.data.audit.AuditDataSegment;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.GetMultipleRequestContext;
import org.unidata.mdm.data.context.GetRecordTimelineRequestContext;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.context.JoinRequestContext;
import org.unidata.mdm.data.context.MergeRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContext;
import org.unidata.mdm.data.context.SplitRecordRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.dto.BulkUpsertResultDTO;
import org.unidata.mdm.data.dto.DeleteRecordDTO;
import org.unidata.mdm.data.dto.EtalonRecordDTO;
import org.unidata.mdm.data.dto.GetRecordDTO;
import org.unidata.mdm.data.dto.GetRecordsDTO;
import org.unidata.mdm.data.dto.KeysJoinDTO;
import org.unidata.mdm.data.dto.MergeRecordsDTO;
import org.unidata.mdm.data.dto.SplitRecordsDTO;
import org.unidata.mdm.data.dto.UpsertRecordDTO;
import org.unidata.mdm.data.service.DataRecordsService;
import org.unidata.mdm.data.service.segments.RecordGetAttributesPostProcessingExecutor;
import org.unidata.mdm.data.service.segments.RecordGetDiffExecutor;
import org.unidata.mdm.data.service.segments.RecordGetFinishExecutor;
import org.unidata.mdm.data.service.segments.RecordGetSecurityExecutor;
import org.unidata.mdm.data.service.segments.RecordGetStartExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertFinishExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertIndexingExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertLobSubmitExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertMeasuredAttributesExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertMergeTimelineExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertModboxExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertPeriodCheckExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertPersistenceExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertResolveCodePointersExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertSecurityExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertStartExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertValidateExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertFinishExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertStartExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationsUpsertConnectorExecutor;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.service.ExecutionService;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.pipeline.Pipeline;

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

    // TODO: @Modules
//    /**
//     * Records vistory component.
//     */
//    @Autowired
//    private RecordsServiceComponent recordsComponent;
//    /**
//     * Relations component.
//     */
//    @Autowired
//    private RelationsServiceComponent relationsComponent;
    /**
     * LOB component.
     */
    @Autowired
    private LargeObjectsServiceComponent lobComponent;
    /**
     * Records component.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Pipeline service.
     */
    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private ExecutionService executionService;

    // TODO: @Modules
//    /**
//     * Audit writer
//     */
//    @Autowired
//    private AuditEventsWriter auditEventsWriter;

    // TODO: @Modules
//    @Autowired
//    private ConfigurationServiceExt configurationService;

    @Autowired
    private AuditDataFallback auditDataFallback;

    @Value("${unidata.search.index.refresh_interval:1000}")
    long rollBackDelay;

    @Value("${unidata.data.rollback.thread.count:1}")
    int rollBackPoolSize;

    private final ScheduledExecutorService rollBackExecutor = Executors.newScheduledThreadPool(
            rollBackPoolSize,
            new CustomizableThreadFactory("rollbackRecord-worker-")
    );

    @PreDestroy
    public void preDestroy() {
        rollBackExecutor.shutdown();
    }

    // TODO: @Modules
//    /**
//     * Get multiple classifiers executors.
//     */
//    @Autowired
//    @Qualifier(ATOMIC_UPSERT_ACTION_LISTENER)
//    private DataRecordLifecycleListener<UpsertRequestContext> atomicUpsertActionListener;

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
        Pipeline p = Pipeline.start(pipelineService.start(RecordGetStartExecutor.SEGMENT_ID))
                .with(pipelineService.point(RecordGetSecurityExecutor.SEGMENT_ID))
                .with(pipelineService.point(RecordGetDiffExecutor.SEGMENT_ID))
                .with(pipelineService.point(RecordGetAttributesPostProcessingExecutor.SEGMENT_ID))
                .with(pipelineService.point(AuditDataSegment.SEGMENT_ID))
                .fallback(pipelineService.fallback(AuditDataFallback.SEGMENT_ID))
                .end(pipelineService.finish(RecordGetFinishExecutor.SEGMENT_ID));

        return executionService.execute(p, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys identify(RecordIdentityContext ctx) {
        return null;//commonComponent.identify(ctx);// TODO: @Modules
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KeysJoinDTO join(JoinRequestContext ctx) {
        // TODO: @Modules
//        final Optional<JoinImpl> join = Optional.ofNullable(configurationService.getJoin());
//
//        join.ifPresent(j ->
//                Optional.ofNullable(configurationService.getListeners(ctx.getEntityName(), j.getBeforeJoinInstances()))
//                        .ifPresent(beforeJoinListeners ->
//                                beforeJoinListeners.forEach(beforeJoinListener -> Optional.ofNullable(beforeJoinListener.beforeJoin(ctx))
//                                        .ifPresent(exitResult -> {
//                                            if (ExitResult.Status.ERROR == exitResult.getStatus()) {
//                                                throw new BusinessException(
//                                                        "Error occurred during run before join user exit: " + exitResult.getWarningMessage(),
//                                                        ExceptionId.EX_JOIN_USER_EXIT_BEFORE_ERROR,
//                                                        exitResult.getWarningMessage()
//                                                );
//                                            }
//                                        }))));
//
//        final KeysJoinDTO joinDTO = commonComponent.join(ctx);
//
//        join.ifPresent(j ->
//                Optional.ofNullable(j.getAfterJoinInstances().get(ctx.getEntityName()))
//                        .ifPresent(afterJoinListeners ->
//                                afterJoinListeners.forEach(afterJoinListener -> Optional.ofNullable(afterJoinListener.afterJoin(joinDTO))
//                                        .ifPresent(exitResult -> {
//                                            if (ExitResult.Status.ERROR == exitResult.getStatus()) {
//                                                throw new BusinessException(
//                                                        "Error occurred during run after join user exit: " + exitResult.getWarningMessage(),
//                                                        ExceptionId.EX_JOIN_USER_EXIT_AFTER_ERROR,
//                                                        exitResult.getWarningMessage()
//                                                );
//                                            }
//                                        }))));
//
//        return joinDTO;
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetRecordDTO getEtalonRecordPreview(GetRequestContext ctx) {
        return null;//recordsComponent.loadEtalonRecordPreview(ctx);// TODO: @Modules
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GetRecordsDTO getRecords(GetMultipleRequestContext ctx) {
        // TODO: @Modules
//        try {
//            GetRecordsDTO result = recordsComponent.loadRecords(ctx);
//            ctx.getInnerGetContexts().forEach(tx -> auditEventsWriter.writeSuccessEvent(AuditActions.DATA_GET, tx));
//            return result;
//        } catch (Exception e) {
//            ctx.getInnerGetContexts()
//                    .forEach(tx -> auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_GET, e, tx));
//            throw e;
//        }
        return null;
    }

    // TODO: @Modules
//    private UpsertRecordDTO upsertRecordWithAudit(UpsertRequestContext ctx) {
//        try {
//            UpsertRecordDTO result = recordsComponent.upsertRecord(ctx);
//            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
//                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_UPSERT, ctx);
//            }
//            return result;
//        } catch (Exception e) {
//
//            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
//                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT, e, ctx);
//            }
//            throw e;
//        }
//    }
//
//    private UpsertRelationsDTO upsertRelationWithAudit(UpsertRelationsRequestContext ctx) {
//        try {
//            UpsertRelationsDTO result = upsertRelations(ctx);
//            ctx.getRelations().values().stream().flatMap(Collection::stream).forEach(rCtx -> {
//                if (AuditLevel.AUDIT_SUCCESS <= rCtx.getAuditLevel()) {
//                    auditEventsWriter.writeSuccessEvent(AuditActions.DATA_UPSERT_RELATION, rCtx);
//                }
//            });
//            return result;
//        } catch (Exception e) {
//            ctx.getRelations().values().stream().flatMap(Collection::stream).forEach(rCtx -> {
//                if (AuditLevel.AUDIT_SUCCESS <= rCtx.getAuditLevel()) {
//                    auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT_RELATION, e, rCtx);
//                }
//            });
//            throw e;
//        }
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteRecordDTO deleteRecord(DeleteRequestContext ctx) {
        // TODO: @Modules
//        try {
//            DeleteRecordDTO result = recordsComponent.deleteRecord(ctx);
//            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
//                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_DELETE, ctx);
//            }
//            return result;
//        } catch (Exception e) {
//            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
//                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_DELETE, e, ctx);
//            }
//
//            throw e;
//        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MergeRecordsDTO merge(MergeRequestContext ctx) {
        // TODO: @Modules
//        try {
//            MergeRecordsDTO result = recordsComponent.merge(ctx);
//            if (AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
//                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_MERGE, ctx);
//            }
//
//            return result;
//        } catch (Exception e) {
//            if (AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
//                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_MERGE, e, ctx);
//            }
//            RecordKeys masterKey = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS);
//            List<RecordKeys> duplicatesKeys = ctx.getFromStorage(StorageId.DATA_MERGE_DUPLICATES_KEYS);
//            if (masterKey != null) {
//                reindexEtalon(masterKey);
//            }
//            if (duplicatesKeys != null) {
//                duplicatesKeys.stream()
//                        .filter(Objects::nonNull)
//                        .forEach(this::reindexEtalon);
//            }
//            throw e;
//        }
        return null;
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
    public Timeline<OriginRecord> loadTimeline(GetRecordTimelineRequestContext ctx) {
        return commonRecordsComponent.loadTimeline(ctx);
    }

    // TODO: @Modules
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public GetClassifierDTO getClassifier(GetClassifierDataRequestContext ctx) {
//        return classifiersComponent.getClassifier(ctx);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public GetClassifiersDTO getClassifiers(GetClassifiersDataRequestContext ctx) {
//        return classifiersComponent.getClassifiers(ctx);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public UpsertClassifierDTO upsertClassifier(UpsertClassifierDataRequestContext ctx) {
//        return classifiersComponent.upsertClassifier(ctx);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public UpsertClassifiersDTO upsertClassifiers(List<UpsertClassifierDataRequestContext> ctxts) {
//        return classifiersComponent.upsertClassifiers(ctxts);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public UpsertClassifiersDTO upsertClassifiers(UpsertClassifiersDataRequestContext ctx) {
//        return classifiersComponent.upsertClassifiers(ctx);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public DeleteClassifierDTO deleteClassifier(DeleteClassifierDataRequestContext ctx) {
//        return classifiersComponent.deleteClassifier(ctx);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public DeleteClassifiersDTO deleteClassifiers(List<DeleteClassifierDataRequestContext> ctxts) {
//        return classifiersComponent.deleteClassifiers(ctxts);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public DeleteClassifiersDTO deleteClassifiers(DeleteClassifiersDataRequestContext ctxts) {
//        return classifiersComponent.deleteClassifiers(ctxts);
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRecordDTO upsertRecord(@Nonnull UpsertRequestContext ctx) {

        // Data
        // FIXME Temporary, for testing
//        Pipeline l = Pipeline.start(pipelineService.start(RelationUpsertStartExecutor.SEGMENT_ID))
//                .end(pipelineService.finish(RelationUpsertFinishExecutor.SEGMENT_ID));
//
//        Pipeline p = Pipeline.start(pipelineService.start(RecordUpsertStartExecutor.SEGMENT_ID))
//                .with(pipelineService.point(RecordUpsertValidateExecutor.SEGMENT_ID))
//                .with(pipelineService.point(RecordUpsertSecurityExecutor.SEGMENT_ID))
//                .with(pipelineService.point(RecordUpsertPeriodCheckExecutor.SEGMENT_ID))
//                .with(pipelineService.point(RecordUpsertResolveCodePointersExecutor.SEGMENT_ID))
//                .with(pipelineService.point(RecordUpsertMeasuredAttributesExecutor.SEGMENT_ID))
//                .with(pipelineService.point(RecordUpsertModboxExecutor.SEGMENT_ID)) // <- Modbox create
//                .with(pipelineService.point(RecordUpsertLobSubmitExecutor.SEGMENT_ID))
//                .with(pipelineService.point(RecordUpsertMergeTimelineExecutor.SEGMENT_ID))
//                .with(pipelineService.point(RecordUpsertIndexingExecutor.SEGMENT_ID))
//                .with(pipelineService.point(RecordUpsertPersistenceExecutor.SEGMENT_ID))
//                .with(pipelineService.connector(RelationsUpsertConnectorExecutor.SEGMENT_ID), l)
//                .with(pipelineService.point(AuditDataSegment.SEGMENT_ID))
//                .fallback(pipelineService.fallback(AuditDataFallback.SEGMENT_ID))
//                .end(pipelineService.finish(RecordUpsertFinishExecutor.SEGMENT_ID));
        final Pipeline pipeline = pipelineService.getById(RecordUpsertStartExecutor.SEGMENT_ID);

        UpsertRecordDTO result = executionService.execute(pipeline, ctx);
        return result;
    }

    /**
     * @param ctx - record get request context for copy
     * @return {@link UpsertRecordDTO}
     */
    @Override
    public List<UpsertRecordDTO> copyRecord(GetRequestContext ctx) {
        // TODO: @Modules
//        try {
//            return recordsComponent.copyRecord(ctx);
//        } catch (DataProcessingException exc) {
//            if (exc.getId() == ExceptionId.EX_DATA_ETALON_COPY_FAILED && exc.getArgs()[0] instanceof RecordKeys) {
//                reindexEtalon((RecordKeys) exc.getArgs()[0]);
//            }
//            throw exc;
//        }
        return null;
    }
    /**
     * @param ctx - apply draft version to record
     * @return {@link UpsertRecordDTO}
     */
    @Override
    public List<UpsertRecordDTO> applyDraftRecord(GetRequestContext ctx) {
        // TODO: @Modules
//        try {
//            return recordsComponent.applyDraft(ctx);
//        } catch (Exception exc) {
//            reindexEtalon(ctx);
//            throw exc;
//        }
        return null;
    }

    @Override
    @Transactional
    public UpsertRecordDTO upsertFullTransactional(@Nonnull UpsertRequestContext ctx) {
        // TODO: @Modules
//        UpsertRecordDTO result = upsertRecordWithAudit(ctx);
//
//        //we separate this actions because rCtx required filled etalonId or originId
//        if (Objects.nonNull(ctx.getRelationDeletes())) {
//            DeleteRelationsRequestContext dRctx = ctx.getRelationDeletes();
//            dRctx.putToStorage(dRctx.keysId(), ctx.keys());
//            dRctx.setOperationId(ctx.getOperationId());
//            DeleteRelationsDTO deleteRelationsResult = deleteRelations(dRctx);
//            result.setDeleteRelations(deleteRelationsResult == null ? null : deleteRelationsResult.getRelations());
//        }
//
//        if (Objects.nonNull(ctx.getRelations())) {
//            UpsertRelationsRequestContext uRctx = ctx.getRelations();
//            uRctx.putToStorage(uRctx.keysId(), ctx.keys());
//            uRctx.setOperationId(ctx.getOperationId());
//            UpsertRelationsDTO relationsResult = upsertRelationWithAudit(uRctx);
//            result.setRelations(relationsResult == null ? null : relationsResult.getRelations());
//        }
//
//        if (Objects.nonNull(ctx.getClassifierDeletes())) {
//            DeleteClassifiersDataRequestContext dCtx = ctx.getClassifierDeletes();
//            dCtx.putToStorage(dCtx.keysId(), ctx.keys());
//            dCtx.setOperationId(ctx.getOperationId());
//            deleteClassifiers(dCtx);
//        }
//
//        if (Objects.nonNull(ctx.getClassifierUpserts())) {
//
//            UpsertClassifiersDataRequestContext uCctx = ctx.getClassifierUpserts();
//            uCctx.putToStorage(uCctx.keysId(), ctx.keys());
//            uCctx.setOperationId(ctx.getOperationId());
//            try {
//                UpsertClassifiersDTO classifiersResult = upsertClassifiers(uCctx);
//                if (classifiersResult != null) {
//                    result.setClassifiers(classifiersResult.getClassifiers());
//                    ctx.getDqErrors().addAll(uCctx.getDqErrors());
//                }
//            } catch (DataProcessingException e) {
//                if (CollectionUtils.isNotEmpty(uCctx.getDqErrors())) {
//                    ctx.getDqErrors().addAll(uCctx.getDqErrors());
//                }
//                throw e;
//            }
//        }
//
//        atomicUpsertActionListener.after(ctx);
//        return result;
        return null;
    }

    // TODO: @Modules
//    @Override
//    public void reindexModifiedEtalons(final CommonDependentContext ctx) {
//        if (ctx == null) {
//            return;
//        }
//        flatCollectCtxs(ctx).forEach(this::reindexEtalon);
//    }
//
//    private void reindexEtalon(@Nonnull RecordKeys keys) {
//        GetRequestContext rollbackCtx = GetRequestContext.builder()
//                    .entityName(keys.getEntityName())
//                    .lsn(keys.getEtalonKey().getLsn())
//                    .shard(keys.getShard())
//                    .etalonKey(keys.getEtalonKey().getId())
//                    .originKey(keys.getOriginKey().getId())
//                    .sourceSystem(keys.getOriginKey().getSourceSystem())
//                    .externalId(keys.getOriginKey().getExternalId())
//                    .build();
//        reindexEtalon(rollbackCtx);
//    }
//
//    private List<RecordIdentityContext> flatCollectCtxs(final CommonRequestContext ctx) {
//
//        if (ctx == null) {
//            return Collections.emptyList();
//        }
//
//        final List<CommonRequestContext> ctxs = ctx.getDependencies();
//        if (CollectionUtils.isEmpty(ctxs)) {
//            return ctx instanceof RecordIdentityContext
//                    ? Collections.singletonList((RecordIdentityContext) ctx)
//                    : Collections.emptyList();
//        }
//
//        return Stream.concat((ctx instanceof RecordIdentityContext ? Stream.of((RecordIdentityContext) ctx) : Stream.empty()),
//                ctxs.stream().flatMap(c -> this.flatCollectCtxs(c).stream()))
//                .collect(Collectors.toList());
//    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public BulkUpsertResultDTO bulkUpsertRecords(@Nonnull List<UpsertRequestContext> recordUpsertCtxs, boolean abortOnFailure) {
// TODO: @Modules
//        Map<BatchOperationType, List<? extends CommonRequestContext>> collected = new EnumMap<>(BatchOperationType.class);
//        recordUpsertCtxs.forEach(ctx -> {
//
//            if (Objects.nonNull(ctx.getRelations())) {
//                ((List<UpsertRelationsRequestContext>) collected.computeIfAbsent(
//                        BatchOperationType.UPSERT_RELATIONS, k -> new ArrayList<UpsertRelationsRequestContext>()))
//                        .add(ctx.getRelations());
//            }
//
//            if (Objects.nonNull(ctx.getRelationDeletes())) {
//                ((List<DeleteRelationsRequestContext>) collected.computeIfAbsent(
//                        BatchOperationType.DELETE_RELATIONS, k -> new ArrayList<DeleteRelationsRequestContext>()))
//                        .add(ctx.getRelationDeletes());
//            }
//
//            if (Objects.nonNull(ctx.getClassifierUpserts())) {
//                ((List<UpsertClassifiersDataRequestContext>) collected.computeIfAbsent(
//                        BatchOperationType.UPSERT_CLASSIFIERS, k -> new ArrayList<UpsertClassifiersDataRequestContext>()))
//                        .add(ctx.getClassifierUpserts());
//            }
//
//            if (Objects.nonNull(ctx.getClassifierDeletes())) {
//                ((List<DeleteClassifiersDataRequestContext>) collected.computeIfAbsent(
//                        BatchOperationType.DELETE_CLASSIFIERS, k -> new ArrayList<DeleteClassifiersDataRequestContext>()))
//                        .add(ctx.getClassifierDeletes());
//            }
//
//            ((List<UpsertRequestContext>) collected.computeIfAbsent(
//                    BatchOperationType.UPSERT_RECORDS, k -> new ArrayList<UpsertRequestContext>()))
//                    .add(ctx);
//        });
//
//        BulkUpsertResultDTO result = new BulkUpsertResultDTO();
//        for (BatchOperationType bot : BatchOperationType.values()) {
//
//            List<? extends CommonRequestContext> payload = collected.get(bot);
//            if (CollectionUtils.isEmpty(payload)) {
//                continue;
//            }
//
//            switch (bot) {
//                case BatchOperationType.UPSERT_RECORDS:
//                    result.setRecords(recordsComponent.batchUpsertRecords((List<UpsertRequestContext>) payload, abortOnFailure));
//                    break;
//                case BatchOperationType.UPSERT_RELATIONS:
//                    result.setRelations(relationsComponent.batchUpsertRelations((List<UpsertRelationsRequestContext>) payload, abortOnFailure));
//                    break;
//                case BatchOperationType.DELETE_RELATIONS:
//                    result.setDeleteRelations(relationsComponent.batchDeleteRelations((List<DeleteRelationsRequestContext>) payload, abortOnFailure));
//                    break;
//                case BatchOperationType.UPSERT_CLASSIFIERS:
//                    result.setClassifiers(classifiersComponent.batchUpsertClassifiers((List<UpsertClassifiersDataRequestContext>) payload, abortOnFailure));
//                    break;
//                case BatchOperationType.DELETE_CLASSIFIERS:
//                    result.setDeleteClassifiers(classifiersComponent.batchDeleteClassifiers((List<DeleteClassifiersDataRequestContext>) payload, abortOnFailure));
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        return result;
        return null;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public BulkUpsertResultDTO bulkUpsertRecords(@Nonnull List<UpsertRequestContext> recordUpsertCtxs) {
        return bulkUpsertRecords(recordUpsertCtxs, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRecordDTO restore(UpsertRequestContext ctx, boolean isModified) {
        // TODO: @Modules
//        try {
//            EtalonRecordDTO etalonRecordDTO = recordsComponent.restoreRecord(ctx, isModified);
//            auditEventsWriter.writeSuccessEvent(AuditActions.DATA_RESTORE, ctx);
//            return etalonRecordDTO;
//        } catch (Exception e) {
//            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_RESTORE, e, ctx);
//            throw e;
//        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRecordDTO restorePeriod(UpsertRequestContext ctx) {
        // TODO: @Modules
//        try {
//            EtalonRecordDTO etalonRecordDTO = recordsComponent.restorePeriod(ctx);
//            auditEventsWriter.writeSuccessEvent(AuditActions.PERIOD_RESTORE, ctx);
//            return etalonRecordDTO;
//        } catch (Exception e) {
//            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.PERIOD_RESTORE, e, ctx);
//            throw e;
//        }
        return null;
    }

    // TODO: @Modules
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public List<DataQualityError> getDQErrors(String id, String entity, Date date) {
//        return recordsComponent.extractDQErrors(id, entity, date);
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SplitRecordsDTO detachOrigin(SplitRecordRequestContext ctx) {
       return null;//recordsComponent.splitRecord(ctx);// TODO: @Modules
    }

    @Override
    public boolean reindexEtalon(RecordIdentityContext ctx) {
        return false;//recordsComponent.reindexEtalon(ctx);// TODO: @Modules
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reapplyEtalon(UpsertRequestContext ctx) {
        // TODO: @Modules
//        ctx.putToStorage(StorageId.DATA_UPSERT_EXACT_ACTION, UpsertAction.UPDATE);
//        recordsComponent.calculateEtalon(ctx);
    }

    @Override
    public List<String> selectCovered(List<String> etalonIds, LocalDateTime from, LocalDateTime to, boolean full) {
        // TODO: @Modules
//        if (CollectionUtils.isEmpty(etalonIds)) {
//            return Collections.emptyList();
//        }
//        final Date fromDate = toDate(from, ValidityPeriodUtils.getGlobalValidityPeriodStart());
//        final Date toDate = toDate(to, ValidityPeriodUtils.getGlobalValidityPeriodEnd());
//
//
//        final GetRecordsTimelinesRequestContext gtrc = GetRecordsTimelinesRequestContext.builder()
//                .etalonKeys(etalonIds)
//                .fetchData(false)
//                .build();
//
//        List<Timeline<OriginRecord>> timelines = commonComponent.loadTimelines(gtrc);
//        return timelines.stream()
//                .filter(tl -> full ? tl.isFullCovered(fromDate, toDate, true) : tl.selectBy(fromDate, toDate).stream().anyMatch(TimeInterval::isActive))
//                .filter(Timeline::isPublished)
//                .map(tl -> tl.<RecordKeys>getKeys().getEtalonKey().getId())
//                .collect(Collectors.toList());
        return null;
    }

	@Override
	public String getRecordAsXMLString(GetRequestContext ctx) {
        // TODO: @Modules
//		GetRecordDTO getRecordDTO = getRecord(ctx);
//		EtalonRecordFull etalonRecordFull = new EtalonRecordFull();
//		EtalonRecord etalonRecord = getRecordDTO.getEtalon();
//		Map<RelationStateDTO, List<GetRelationDTO>> etalonRelations = getRecordDTO.getRelations();
//		Map<String, List<GetClassifierDTO>> etalonClassifiers = getRecordDTO.getClassifiers();
//		if (Objects.nonNull(etalonRecord)) {
//			etalonRecordFull.setEtalonRecord(JaxbDataRecordUtils.to(etalonRecord, etalonRecord.getInfoSection(),
//					com.unidata.mdm.data.EtalonRecord.class));
//		}
//		if (MapUtils.isNotEmpty(etalonRelations)) {
//			etalonRelations.values().forEach(er -> {
//				er.forEach(r -> {
//					if (r.getRelationType() == RelationType.REFERENCES
//							|| r.getRelationType() == RelationType.MANY_TO_MANY) {
//						etalonRecordFull.getRelationTo().add(
//								JaxbDataRecordUtils.to(r.getEtalon(), null, com.unidata.mdm.data.RelationTo.class));
//					} else {
//						etalonRecordFull.getIntegralRecord().add(JaxbDataRecordUtils.to(r.getEtalon(),
//								r.getEtalon().getInfoSection(), com.unidata.mdm.data.IntegralRecord.class));
//					}
//				});
//			});
//		}
//		if (MapUtils.isNotEmpty(etalonClassifiers)) {
//			etalonClassifiers.values().stream().forEach(ec -> {
//				ec.stream().forEach(c -> {
//					etalonRecordFull.getEtalonClassifierRecord().add(JaxbDataRecordUtils.to(c.getEtalon(),
//							c.getEtalon().getInfoSection(), com.unidata.mdm.data.EtalonClassifierRecord.class));
//				});
//			});
//		}
//		String result = JaxbUtils.marshalEtalonRecordFull(etalonRecordFull);
//		return result;
        return null;
	}

    private Date toDate(LocalDateTime from, Date defaultDate) {
        if (from == null) {
            return defaultDate;
        }
        return Date.from(from.atZone(ZoneId.systemDefault()).toInstant());
    }
}
