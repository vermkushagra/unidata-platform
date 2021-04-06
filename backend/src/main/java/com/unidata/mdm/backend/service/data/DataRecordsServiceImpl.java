package com.unidata.mdm.backend.service.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.dto.SplitRecordsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.audit.AuditLevel;
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
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.SplitContext;
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
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifierDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifiersDTO;
import com.unidata.mdm.backend.common.dto.UpsertRecordDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationsDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.conf.impl.JoinImpl;
import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.binary.LargeObjectsServiceComponent;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersServiceComponent;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordLifecycleListener;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.data.util.DataUtils;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;

/**
 * The Class DataRecordsServiceImpl.
 *
 * @author Mikhail Mikhailov Data service.
 */
@Service
public class DataRecordsServiceImpl implements DataRecordsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordsServiceImpl.class);

    public static final String SPLIT_RECORD_ACTION_LISTENER_QUALIFIER = "splitRecordActionListener";

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
     * Search service.
     */
    @Autowired
    private SearchService searchService;
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

    @Autowired
    private OriginRecordsComponent originRecordsComponent;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Data record DAO.
     */
    @Autowired
    private DataRecordsDao dataRecordsDao;

    /**
     * 'Merge' action listener.
     */
    @Autowired
    @Qualifier(value = SPLIT_RECORD_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<SplitContext> splitContextDataRecordLifecycleListener;

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
                Optional.ofNullable(j.getBeforeJoinInstances().get(ctx.getEntityName()))
                        .flatMap(beforeJoinListener -> Optional.ofNullable(beforeJoinListener.beforeJoin(ctx)))
                        .ifPresent(exitResult -> {
                            if (ExitResult.Status.ERROR == exitResult.getStatus()) {
                                throw new BusinessException(
                                        "Error occurred during run before join user exit: " + exitResult.getWarningMessage(),
                                        ExceptionId.EX_JOIN_USER_EXIT_BEFORE_ERROR,
                                        exitResult.getWarningMessage()
                                );
                            }
                        })
        );

        final KeysJoinDTO joinDTO = commonComponent.join(ctx);

        join.ifPresent(j ->
                Optional.ofNullable(j.getAfterJoinInstances().get(ctx.getEntityName()))
                        .flatMap(afterJoinListener -> Optional.ofNullable(afterJoinListener.afterJoin(joinDTO)))
                        .ifPresent(exitResult -> {
                            if (ExitResult.Status.ERROR == exitResult.getStatus()) {
                                throw new BusinessException(
                                        "Error occurred during run after join user exit: " + exitResult.getWarningMessage(),
                                        ExceptionId.EX_JOIN_USER_EXIT_AFTER_ERROR,
                                        exitResult.getWarningMessage()
                                );
                            }
                        })
        );

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

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRecordDTO upsertRecord(UpsertRequestContext ctx) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRecordDTO recalculateEtalon(UpsertRequestContext ctx) {

        try {

            ctx.putToStorage(StorageId.DATA_UPSERT_EXACT_ACTION, UpsertAction.UPDATE);

            recordsComponent.calculateEtalons(ctx);
            EtalonRecord etalon = null;
            if (ctx.isReturnEtalon()) {
                etalon = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
            }

            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_UPSERT, ctx);
            }

            UpsertRecordDTO result = new UpsertRecordDTO(etalon, UpsertAction.UPDATE, ctx.getDqErrors(), Collections.emptyList());
            result.setRecordKeys(ctx.keys());
            return result;
        } catch (Exception e) {

            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT, e, ctx);
            }
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
            if ( AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_MERGE, ctx);
            }

            return result;
        } catch (Exception e) {
            if ( AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_MERGE, e, ctx);
            }
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
    public LargeObjectDTO fetchLargeObject(FetchLargeObjectRequestContext ctx){
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
    public List<TimelineDTO> getRelationsTimeline(String recordEtalonId, String name, boolean includeDrafts, boolean checkPendingState) {
        return relationsComponent.loadRelationsTimeline(recordEtalonId, name, includeDrafts, checkPendingState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimelineDTO> getRelationsTimeline(String recordEtalonId, String name, Date asOf, boolean includeDrafts, boolean checkPendingState) {
        return relationsComponent.loadRelationsTimeline(recordEtalonId, name, asOf, includeDrafts, checkPendingState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimelineDTO> getRelationsTimeline(String recordEtalonId, String name, Date from, Date to, boolean includeDrafts, boolean checkPendingState) {
        return relationsComponent.loadRelationsTimeline(recordEtalonId, name, from, to, includeDrafts, checkPendingState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimelineDTO getRelationTimeline(String recordEtalonId) {
        return relationsComponent.loadRelationTimeline(recordEtalonId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimelineDTO getRelationTimeline(String recordEtalonId, Date asOf, boolean includeDrafts, boolean checkPendingState) {
        return relationsComponent.loadRelationTimeline(recordEtalonId, asOf, includeDrafts, checkPendingState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimelineDTO getRelationTimeline(String recordEtalonId, Date from, Date to, boolean includeDrafts, boolean checkPendingState) {
        return relationsComponent.loadRelationTimeline(recordEtalonId, from, to, includeDrafts, checkPendingState);
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
    @Transactional
    public UpsertRecordDTO atomicUpsert(@Nonnull UpsertRequestContext ctx) {

        // Audit is written in upsertrecord
        // Relations and classifiers still lack audit events.
        // TODO: Implement separate audit logging for classifiers and relations.
        UpsertRecordDTO result = upsertRecord(ctx);

        //we separate this actions because rCtx required filled etalonId or originId
        if (Objects.nonNull(ctx.getRelations())) {
            UpsertRelationsRequestContext uRctx = ctx.getRelations();
            uRctx.putToStorage(uRctx.keysId(), ctx.keys());
            UpsertRelationsDTO relationsResult = upsertRelations(uRctx);
            result.setRelations(relationsResult == null ? null : relationsResult.getRelations());
        }

        if (Objects.nonNull(ctx.getClassifierUpserts())) {
            UpsertClassifiersDataRequestContext uCctx = ctx.getClassifierUpserts();
            uCctx.putToStorage(uCctx.keysId(), ctx.keys());
            UpsertClassifiersDTO classifiersResult = upsertClassifiers(uCctx);
            result.setClassifiers(classifiersResult == null ? null : classifiersResult.getClassifiers());
        }

        if (Objects.nonNull(ctx.getClassifierDeletes())) {
            DeleteClassifiersDataRequestContext dCtx = ctx.getClassifierDeletes();
            dCtx.putToStorage(dCtx.keysId(), ctx.keys());
            deleteClassifiers(dCtx);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Collection<UpsertRecordDTO> atomicBulkUpsert(@Nonnull Collection<UpsertRequestContext> recordUpsertCtxs) {
        try {
            return recordUpsertCtxs.stream()
                    .map(this::atomicUpsert)
                    .collect(Collectors.toList());
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
    @SuppressWarnings("unchecked")
    public List<DataQualityError> getDQErrors(String id, String entity, Date date) {
        List<DataQualityError> errors = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        fields.add(RecordHeaderField.FIELD_DQ_ERRORS_AS_BINARY.getField());
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(entity)
                .search(SearchRequestType.TERM)
                .searchFields(Collections.singletonList(
                        RecordHeaderField.FIELD_ETALON_ID.getField()))
                .text(id)
                .count(10)
                .asOf(date)
                .operator(SearchRequestOperator.OP_AND)
                .returnFields(fields)
                .build();
        SearchResultDTO searchResultDTO = searchService.search(ctx);
        if (searchResultDTO.getHits() == null || searchResultDTO.getHits().size() == 0) {
            return errors;
        }
        SearchResultHitDTO results = searchResultDTO.getHits().get(0);
        SearchResultHitFieldDTO error = results.getFieldValue(RecordHeaderField.FIELD_DQ_ERRORS_AS_BINARY.getField());
        if (error != null && error.isNonNullField()) {
            errors.addAll((Collection<? extends DataQualityError>) DataUtils.fromString((String) error.getFirstValue()));
        }
        return errors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SplitRecordsDTO detachOrigin(final String originId) {
        final OriginRecordPO originRecord = dataRecordsDao.findOriginRecordById(originId);
        if (originRecord == null) {
            throw new BusinessException(
                    "Origin record not found. Id: " + originId, ExceptionId.EX_ORIGIN_NOT_FOUND, originId
            );
        }

        final SplitContext splitContext = new SplitContext(
                OriginKey.builder().id(originId).entityName(originRecord.getName()).build(),
                EtalonKey.builder().id(originRecord.getEtalonId()).build());

        try {
            if (!splitContextDataRecordLifecycleListener.before(splitContext)) {
                LOGGER.warn("SplitContextDataRecordLifecycleListener.before() return false, check errors in log");
                return null;
            }
        }
        catch (Exception e) {
            LOGGER.error("SplitContextDataRecordLifecycleListener.before() throw exception", e);
            throw e;
        }

        final EtalonKey newEtalonKey = originRecordsComponent.detachOrigin(splitContext);
        splitContext.setNewEtalonKey(newEtalonKey);

        splitContextDataRecordLifecycleListener.after(splitContext);

        SplitRecordsDTO result = new SplitRecordsDTO();
        result.setErrors(splitContext.getFromStorage(StorageId.PROCESS_ERRORS));
        result.setEtalonId(Collections.singletonMap(DataRecordsService.ETALON_ID, newEtalonKey.getId()));
        return result;
    }
}
