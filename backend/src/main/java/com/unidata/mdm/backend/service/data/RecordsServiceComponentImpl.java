package com.unidata.mdm.backend.service.data;

import static com.unidata.mdm.backend.common.context.StorageId.DATA_UPSERT_KEYS;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_ETALON_ID;
import static java.lang.Boolean.FALSE;
import static java.util.Collections.singletonList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext.DeleteRequestContextBuilder;
import com.unidata.mdm.backend.common.context.GetClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.GetTasksRequestContext;
import com.unidata.mdm.backend.common.context.GetTasksRequestContext.GetTasksRequestContextBuilder;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext.UpsertRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.DeleteRecordDTO;
import com.unidata.mdm.backend.common.dto.ErrorInfoDTO;
import com.unidata.mdm.backend.common.dto.EtalonRecordDTO;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.GetClassifiersDTO;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.dto.GetRecordsDTO;
import com.unidata.mdm.backend.common.dto.GetRelationDTO;
import com.unidata.mdm.backend.common.dto.GetRelationsDTO;
import com.unidata.mdm.backend.common.dto.MergeRecordsDTO;
import com.unidata.mdm.backend.common.dto.RelationStateDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.UpsertRecordDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.exits.ExitException;
import com.unidata.mdm.backend.common.integration.exits.ExitState;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.TypeOfChange;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.data.impl.ExtendedRecord;
import com.unidata.mdm.backend.service.data.batch.BatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.BatchSetIterationType;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.data.batch.RecordUpsertBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersServiceComponent;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.driver.CalculableHolder;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.etalon.EtalonsUpsertRunnable;
import com.unidata.mdm.backend.service.data.listener.DataRecordLifecycleListener;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.data.util.DataUtils;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.wf.WorkflowService;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.conf.WorkflowProcessType;
import com.unidata.mdm.meta.RelationDef;

import reactor.core.publisher.Flux;


/**
 * @author Mikhail Mikhailov
 *         The component serves the 'record' part of the data.
 */
@Component
public class RecordsServiceComponentImpl implements RecordsServiceComponent, ConfigurationUpdatesConsumer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordsServiceComponentImpl.class);

    private static final String AUTO_MERGE_RESULT_MESSAGE = "app.data.automerge.result";
    /**
     * 'Upsert' record pooling executor.
     */
    public static final String UPSERT_RECORD_POOLING_EXECUTOR_QUALIFIER = "etalonsCalculationPoolingExecutor";
    /**
     * 'Get' action listener qualifier name.
     */
    public static final String GET_RECORD_ACTION_LISTENER_QUALIFIER = "getRecordActionListener";
    /**
     * 'Restore' action listener qualifier name.
     */
    public static final String RESTORE_RECORD_ACTION_LISTENER_QUALIFIER = "restoreRecordActionListener";

    private AtomicBoolean enableAutoMerge = new AtomicBoolean(
            (Boolean) UnidataConfigurationProperty.UNIDATA_DATA_AUTOMERGE_ENABLED.getDefaultValue().get()
    );

    /**
     * Data record DAO.
     */
    @Autowired
    private DataRecordsDao dataRecordsDao;

    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchService;

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

    @Autowired
    private ClusterService clusterService;
    /**
     * Audit writer
     */
    @Autowired
    private AuditEventsWriter auditEventsWriter;

    /**
     * 'Upsert' pooling executor.
     */
    @Autowired
    @Qualifier(value = UPSERT_RECORD_POOLING_EXECUTOR_QUALIFIER)
    private ThreadPoolTaskExecutor etalonCalculationExecuter;
    /**
     * 'Get' action listener.
     */
    @Autowired
    @Qualifier(value = GET_RECORD_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<GetRequestContext> getRecordActionListener;
    /**
     * 'Restore' action listener.
     */
    @Autowired
    @Qualifier(value = RESTORE_RECORD_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<UpsertRequestContext> restoreRecordsActionListner;
    /**
     * Common functionality.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;
    /**
     * Etalon data component.
     */
    @Autowired
    private EtalonRecordsComponent etalonComponent;
    /**
     * Origin/Vistory data component.
     */
    @Autowired
    private OriginRecordsComponent originComponent;
    /**
     * Workflow component.
     */
    @Autowired(required = false)
    private WorkflowService workflowService;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /**
     * Constructor.
     */
    public RecordsServiceComponentImpl() {
        super();
    }

    /**
     * Loads (calculates) contributing records time line for an etalon ID.
     *
     * @param ctx the identifying context
     * @return time line
     */
    @Override
    public TimelineDTO loadRecordsTimeline(GetRequestContext ctx) {
        return etalonComponent.loadEtalonTimeline(ctx);
    }

    /**
     * Gets a record using parameters set by the context.
     *
     * @param ctx the request context
     * @return {@link GetRecordsDTO}
     */
    @Override
    public GetRecordDTO loadRecord(GetRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Before actions + fetch keys.
            getRecordActionListener.before(ctx);

            RecordKeys keys = ctx.keys();

            // UN-202, select current etalon if no date supplied. Compose etalon record, if a date is supplied
            GetRecordDTO result = new GetRecordDTO(keys);

            // 2. Load work flow state first
            if (ctx.isTasks()) {

                GetTasksRequestContext tCtx = new GetTasksRequestContextBuilder()
                        .assignedUser(SecurityUtils.getCurrentUserName())
                        .processKey(keys.getEtalonKey().getId())
                        .build();

                result.setTasks(workflowService != null ? workflowService.tasks(tCtx) : Collections.emptyList());
            }

            boolean hasEditTasks = CollectionUtils.isNotEmpty(result.getTasks())
                    && result.getTasks().stream().anyMatch(r -> r.getProcessType() == WorkflowProcessType.RECORD_EDIT);


            String user = ctx.isStrictDraft() ? null : SecurityUtils.getCurrentUserName();
            boolean viewDraft = ctx.isStrictDraft() ? ctx.isIncludeDrafts() :
                    ctx.isTasks() && (SecurityUtils.isAdminUser() || hasEditTasks) || ctx.isIncludeDrafts();

            // 3. Load etalon record.
            Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> etalonDataFull = etalonComponent.loadEtalonDataFull(
                    keys.getEtalonKey().getId(),
                    ctx.getForDate(),
                    ctx.getForLastUpdate(),
                    ctx.getUpdatesAfter(),
                    ctx.getForOperationId(),
                    ctx.isIncludeInactive(),
                    viewDraft,
                    ctx.isIncludeWinners(),
                    user);

            if (etalonDataFull != null) {
                EtalonRecord etalon = etalonDataFull.getKey();
                if (etalon != null) {
                    result.setEtalon(etalon);
                    result.setDqErrors(extractDQErrors(
                            etalon.getInfoSection().getEtalonKey().getId(),
                            etalon.getInfoSection().getEntityName(),
                            ctx.getForDate()));

                    // 3.1 Set record for post processing
                    ctx.putToStorage(StorageId.DATA_GET_ETALON_RECORD, etalon);
                }
            }

            // 4. Load origins if requested
            if (ctx.isFetchOrigins()) {
                List<OriginRecord> origins = CollectionUtils.isEmpty(etalonDataFull.getRight())
                        ? Collections.emptyList()
                        : etalonDataFull.getRight().stream()
                        .map(CalculableHolder::getValue)
                        .collect(Collectors.toList());

                result.setOrigins(origins);
                ctx.putToStorage(StorageId.DATA_GET_ORIGINS_RECORDS, origins);
            }

            // 5. Load diff to draft state, if requested
            if (ctx.isDiffToDraft() && keys.isPending() && viewDraft && Objects.nonNull(etalonDataFull.getKey())) {

                Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> oldEtalonDataFull = etalonComponent.loadEtalonDataFull(
                        keys.getEtalonKey().getId(),
                        ctx.getForDate(),
                        ctx.getForLastUpdate(),
                        ctx.getUpdatesAfter(),
                        ctx.getForOperationId(),
                        ctx.isIncludeInactive(),
                        false,
                        ctx.isIncludeWinners(),
                        user);

                if (oldEtalonDataFull != null && oldEtalonDataFull.getKey() != null) {
                    Map<String, Map<TypeOfChange, Attribute>> diffToDraft
                            = DataUtils.simpleDataDiffAsAttributesTable(
                            keys.getEntityName(), etalonDataFull.getKey(), oldEtalonDataFull.getKey(), true);
                    result.setDiffToDraft(diffToDraft);
                    ctx.putToStorage(StorageId.DATA_GET_DIFF_TO_DRAFT, diffToDraft);
                }
            }

            // 6. Load relations if requested
            if (ctx.isFetchRelations()) {

                List<String> relationNames
                        = metaModelService.getRelationsByFromEntityName(keys.getEntityName()).stream()
                        .map(RelationDef::getName)
                        .collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(relationNames)) {

                    GetRelationsRequestContext relCtx = GetRelationsRequestContext.builder()
                            .etalonKey(keys.getEtalonKey().getId())
                            .forDate(ctx.getForDate())
                            .forOperationId(ctx.getForOperationId())
                            .relationNames(relationNames)
                            .build();

                    relCtx.setOperationId(ctx.getOperationId());
                    relCtx.putToStorage(StorageId.RELATIONS_FROM_KEY, keys);

                    GetRelationsDTO relations = relationsComponent.getRelations(relCtx);
                    result.setRelations(relations.getRelations());
                }
            }

            // 7. Load classifiers, if requested
            if (ctx.isFetchClassifiers()) {

                List<String> availableClassifiers = metaModelService.isLookupEntity(keys.getEntityName())
                        ? metaModelService.getValueById(keys.getEntityName(), LookupEntityWrapper.class).getEntity().getClassifiers()
                        : metaModelService.getValueById(keys.getEntityName(), EntityWrapper.class).getEntity().getClassifiers();

                if (CollectionUtils.isNotEmpty(availableClassifiers)) {

                    GetClassifiersDataRequestContext clsfCtx = GetClassifiersDataRequestContext.builder()
                            .etalonKey(keys.getEtalonKey().getId())
                            .forDate(ctx.getForDate())
                            .forOperationId(ctx.getForOperationId())
                            .classifierNames(availableClassifiers)
                            .fetchOrigins(ctx.isFetchOrigins())
                            .build();

                    clsfCtx.setOperationId(ctx.getOperationId());
                    clsfCtx.putToStorage(clsfCtx.keysId(), keys);

                    GetClassifiersDTO classifiers = classifiersComponent.getClassifiers(clsfCtx);
                    result.setClassifiers(classifiers.getClassifiers());
                }
            }

            // 8. Calc matching clusters, if requested
            if (ctx.isFetchClusters() && etalonDataFull != null) {
                //calculateClusterCount(ctx, etalonDataFull);
            }

            // 9. Rights post-processing
            result.setRights(SecurityUtils.calculateRightsForTopLevelResource(
                    keys.getEntityName(),
                    result.getEtalon() != null ? result.getEtalon().getInfoSection().getStatus() : null,
                    result.getEtalon() != null ? result.getEtalon().getInfoSection().getApproval() : null,
                    hasEditTasks, false));

            // 9. After actions.
            getRecordActionListener.after(ctx);

            return result;

        } finally {
            MeasurementPoint.stop();
        }
    }


    /**
     * Gets a record using parameters set by the context.
     *
     * @param ctx the request context
     * @return {@link GetRecordsDTO}
     */
    @Override
    public GetRecordDTO loadEtalonRecordView(GetRequestContext ctx) {

        MeasurementPoint.start();
        try {
            // 1. Before actions + fetch keys.
            getRecordActionListener.before(ctx);

            GetRecordDTO result = new GetRecordDTO();

            ExtendedRecord extended = etalonComponent.loadMergedEtalonDataView(ctx.getPreviewEtalonKeys(), ctx.getForDate(), ctx.getForLastUpdate());

            if (extended != null && extended.getRecord() != null) {
                EtalonRecordImpl etalonRecord = new EtalonRecordImpl()
                        .withDataRecord(extended.getRecord())
                        .withInfoSection(new EtalonRecordInfoSection()
                                .withEntityName(extended.getEntityName()));

                EtalonRecord etalonForClassifiers = new EtalonRecordImpl()
                        .withInfoSection(new EtalonRecordInfoSection()
                                .withEntityName(extended.getEntityName())
                                .withEtalonKey(EtalonKey.builder().id(ctx.getEtalonKey()).build()));

                Map<String, List<GetClassifierDTO>> winnerClassifiers = classifiersComponent.loadActiveEtalonsClassifiers(etalonForClassifiers, ctx.getForDate());
                result.setClassifiers(winnerClassifiers);

                ctx.putToStorage(StorageId.DATA_GET_ETALON_RECORD, etalonRecord);

                result.setEtalon(etalonRecord);
                result.setAttributeWinnerMap(extended.getAttributeWinnersMap());

                if (MapUtils.isNotEmpty(winnerClassifiers)) {
                    winnerClassifiers.forEach((s, classifierDTOS) -> {
                        if (classifierDTOS != null && classifierDTOS.get(0) != null) {
                            GetClassifierDTO classifiersDTO = classifierDTOS.get(0);
                            if (classifiersDTO.getEtalon() != null && classifiersDTO.getEtalon().getInfoSection() != null) {
                                result.getAttributeWinnersMap().put(s, classifiersDTO.getEtalon().getInfoSection().getRecordEtalonKey().getId());
                            }
                        }
                    });
                }
            }
            // 3. After actions.
            getRecordActionListener.after(ctx);
            return result;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets a records list using parameters set by the context.
     *
     * @param ctx the request context
     * @return {@link GetRecordsDTO}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public GetRecordsDTO loadRecords(GetMultipleRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Check input
            if (!ctx.isValid()) {
                final String message = "Ivalid input [{}]";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_INVALID_GET_LIST_INPUT, ctx);
            }

            // 2. Get stuff
            GetRecordsDTO result = new GetRecordsDTO();
            List<EtalonRecord> etalons = null;
            Map<EtalonRecord, Map<RelationStateDTO, List<GetRelationDTO>>> relations = null;
            Map<EtalonRecord, Map<String, List<GetClassifierDTO>>> classifiers = null;
            for (GetRequestContext context : ctx.getInnerGetContexts()) {
                GetRecordDTO record = loadRecord(context);
                EtalonRecord etalonRecord = record.getEtalon();

                if (etalonRecord == null) {
                    continue;
                }

                if (etalons == null) {
                    etalons = new ArrayList<>(ctx.getInnerGetContexts().size());
                }
                etalons.add(etalonRecord);

                boolean isRelsPresent = record.getRelations() != null && !record.getRelations().isEmpty();
                if (isRelsPresent) {
                    if (relations == null) {
                        relations = new HashMap<>(ctx.getInnerGetContexts().size());
                    }
                    relations.put(etalonRecord, record.getRelations());
                }

                boolean isClassifiersPresent = record.getClassifiers() != null && !record.getClassifiers().isEmpty();
                if (isClassifiersPresent) {
                    if (classifiers == null) {
                        classifiers = new HashMap<>(ctx.getInnerGetContexts().size());
                    }
                    classifiers.put(etalonRecord, record.getClassifiers());
                }
            }

            result.setEtalons(etalons);
            result.setRelations(relations);
            result.setClassifiers(classifiers);

            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Deletes a record.
     *
     * @param ctx the context
     * @return key of the record deleted
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteRecordDTO deleteRecord(DeleteRequestContext ctx) {

        MeasurementPoint.start();
        try {
            originComponent.deleteOrigin(ctx);
            etalonComponent.deleteEtalon(ctx);
            return new DeleteRecordDTO(ctx.keys());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Deletes a number of records at once.
     *
     * @param ctxts the context
     * @return key of the record deleted
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<DeleteRecordDTO> deleteRecords(List<DeleteRequestContext> ctxts) {

        List<DeleteRecordDTO> result = new ArrayList<>(ctxts.size());
        for (DeleteRequestContext ctx : ctxts) {
            result.add(deleteRecord(ctx));
        }

        return result;
    }

    /**
     * Does processing of an etalon.
     * Basically, does the following things for all affected periods:<ul>
     * <li>executes DQ rules,</li>
     * <li>does possible origin upsert,</li>
     * <li>updates Elastiocsearch state</li>
     * </ul>
     *
     * @param ctx the context
     */
    @Override
    public void calculateEtalons(final UpsertRequestContext ctx) {
        if (ctx.isReturnEtalon()) {
            etalonComponent.upsertEtalon(ctx);
        } else {
            etalonCalculationExecuter.execute(new EtalonsUpsertRunnable(ctx, etalonComponent));
        }
    }

    /**
     * Butch delete runner.
     *
     * @param accumulator the accumulator
     * @return result
     */
    @Override
    public List<DeleteRecordDTO> batchDeleteRecords(BatchSetAccumulator<DeleteRequestContext> accumulator) {

        MeasurementPoint.start();
        try {

            if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.DELETE_ORIGINS)) {
                originComponent.batchDeleteOrigins(accumulator);
            }

            if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.DELETE_ETALONS)) {
                etalonComponent.batchDeleteEtalons(accumulator);
            }

            return accumulator.workingCopy().stream()
                    .map(DeleteRequestContext::keys)
                    .map(DeleteRecordDTO::new)
                    .collect(Collectors.toList());

        } catch (Exception exc) {
            LOGGER.warn("Batch DELETE caught an exception", exc);
            throw exc;
        } finally {
            MeasurementPoint.stop();
        }
    }

    @Override
    public List<UpsertRecordDTO> batchUpsertRecords(List<UpsertRequestContext> ctxs) {
        BatchSetAccumulator<UpsertRequestContext> accumulator = getDefaultRecordsUpsertAccumulator();
        accumulator.charge(ctxs);
        List<UpsertRecordDTO> result;
        try {
            result = batchUpsertRecords(accumulator);
        } finally {
            accumulator.discharge();
        }
        return result;
    }

    /**
     * Upsert a record.
     *
     * @param accumulator accumulator for {@link UpsertRequestContext}
     * @return {@link UpsertRecordDTO}
     */
    @Override
    public List<UpsertRecordDTO> batchUpsertRecords(BatchSetAccumulator<UpsertRequestContext> accumulator) {

        MeasurementPoint.start();
        try {

            if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ORIGINS)) {
                originComponent.batchUpsertOrigins(accumulator);
            }

            if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ETALONS)) {
                etalonComponent.batchUpsertEtalons(accumulator);
            }

            return accumulator.workingCopy().stream()
                    .map(ctx -> {
                        UpsertRecordDTO dto = new UpsertRecordDTO(ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION));
                        dto.setRecordKeys(ctx.keys());
                        dto.setOrigin(ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD));
                        dto.setEtalon(ctx.isReturnEtalon() ? ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD) : null);

                        return dto;
                    })
                    .collect(Collectors.toList());

        } catch (Exception exc) {
            LOGGER.warn("Batch UPSERT caught an exception", exc);
            throw exc;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Upsert a record.
     *
     * @param ctx the request context
     * @return {@link UpsertRecordDTO}
     */
    @Override
    public UpsertRecordDTO upsertRecord(UpsertRequestContext ctx) {
        MeasurementPoint.start();
        try {

            originComponent.upsertOrigin(ctx);

            UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
            OriginRecord origin = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
            RecordKeys keys = ctx.keys();

            UpsertRecordDTO result = new UpsertRecordDTO(action);
            result.setRecordKeys(keys);
            result.setOrigin(origin);

            calculateEtalons(ctx);

            EtalonRecord etalon = null;
            if (ctx.isReturnEtalon()) {
                etalon = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
                etalon = appyAutoMergeIfNeed(result, etalon);
            }

            result.setEtalon(etalon);
            return result;

        } catch (Exception exc) {
            if (ExitException.class.isInstance(exc) && (((ExitException) exc).getExitState() == ExitState.ES_UPSERT_DENIED)) {
                throw exc;
            }

            LOGGER.warn("Upsert caught an exception", exc);
            throw exc;
        } finally {
            MeasurementPoint.stop();
        }
    }

    private EtalonRecord appyAutoMergeIfNeed(UpsertRecordDTO result, EtalonRecord etalon) {
        if (!enableAutoMerge.get()) {
            return etalon;
        }
        String masterEtalonKey = etalon.getInfoSection().getEtalonKey().getId();

        Map<String, Date> etalonIdsForAutoMerge = clusterService.getEtalonIdsForAutoMerge(etalon);
        if (etalonIdsForAutoMerge.size() > 1) {
            List<RecordIdentityContext> duplicates = etalonIdsForAutoMerge.keySet().stream()
                    .filter(id -> !id.equals(masterEtalonKey))
                    .map(id -> new GetRequestContext.GetRequestContextBuilder().etalonKey(id).build())
                    .collect(Collectors.toList());

            MergeRequestContext autoMergeRequest = MergeRequestContext
                    .builder()
                    .etalonKey(masterEtalonKey)
                    .duplicates(duplicates)
                    .manual(false)
                    .build();

            autoMergeRequest.putToStorage(StorageId.DATA_MERGE_KEYS_FOR_DATES, etalonIdsForAutoMerge);

            MergeRecordsDTO mergeResult = merge(autoMergeRequest);
            if (CollectionUtils.isNotEmpty(mergeResult.getMergedIds())) {
                ErrorInfoDTO errorInfo = new ErrorInfoDTO();
                errorInfo.setSeverity(ErrorInfoDTO.Severity.LOW);
                errorInfo.setUserMessage(MessageUtils.getMessage(AUTO_MERGE_RESULT_MESSAGE,
                        mergeResult.getWinnerId(),
                        mergeResult.getMergedIds()));
                result.setErrors(Collections.singletonList(errorInfo));
            }

            if (autoMergeRequest.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD) != null) {
                etalon = autoMergeRequest.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
            }
        }
        return etalon;
    }

    /**
     * Try to restore given record.
     * If record was modified save it.
     * If it wasn't modified restore and recalculate etalon.
     *
     * @param isModified
     * @param ctx        request context
     * @return <code>true</code> if restored, otherwise<code>false</code>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EtalonRecordDTO restoreRecord(UpsertRequestContext ctx, boolean isModified) {

        MeasurementPoint.start();
        try {
            restoreRecordsActionListner.before(ctx);

            RecordKeys keys = ctx.keys();

            Date ts = new Date();
            ctx.putToStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP, ts);

            dataRecordsDao.restoreEtalonRecord(keys.getEtalonKey().getId(), ctx.getOperationId(), ts);
            //change ES status
            Map<RecordHeaderField, Object> fields = new HashMap<>();
            fields.put(RecordHeaderField.FIELD_DELETED, FALSE);
            fields.put(RecordHeaderField.FIELD_UPDATED_AT, ts);

            String id = keys.getEtalonKey().getId();
            searchService.mark(keys.getEntityName(), id, fields);

            // toMikhail: review
            // start
            RecordKeys newKeys = commonComponent.identify(keys.getEtalonKey());
            ctx.putToStorage(DATA_UPSERT_KEYS, newKeys);
            // end
            calculateEtalons(ctx);

            EtalonRecordDTO result;
            if (isModified) {
                try {
                    result = upsertRecord(ctx);
                    if (!ctx.getDqErrors().isEmpty()) {

                        DeleteRequestContext dCtx = new DeleteRequestContextBuilder()
                                .entityName(ctx.getEntityName())
                                .etalonKey(ctx.getEtalonKey())
                                .inactivateEtalon(true)
                                .cascade(true)
                                .validFrom(ctx.getValidFrom())
                                .validTo(ctx.getValidTo())
                                .build();

                        dCtx.setOperationId(ctx.getOperationId());
                        deleteRecord(dCtx);
                        return null;
                    }
                } catch (Exception exception) {
                    LOGGER.warn("Exception caught while restoring record with upsert!", exception);

                    DeleteRequestContext dCtx = new DeleteRequestContextBuilder()
                            .entityName(ctx.getEntityName())
                            .etalonKey(ctx.getEtalonKey())
                            .inactivateEtalon(true)
                            .cascade(true)
                            .validFrom(ctx.getValidFrom())
                            .validTo(ctx.getValidTo())
                            .build();

                    dCtx.setOperationId(ctx.getOperationId());
                    deleteRecord(dCtx);
                    return null;
                }
            } else {

                GetRequestContext gCtx = GetRequestContext.builder()
                        .forDate(ctx.getValidFrom() != null ? ctx.getValidFrom() : ctx.getValidTo())
                        .build();

                gCtx.putToStorage(StorageId.DATA_GET_KEYS, keys);
                result = loadRecord(gCtx);
            }

            restoreRecordsActionListner.after(ctx);

            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Merges several etalon records (and their origins) to one master record.
     *
     * @param ctx current merge context
     * @return true if successful, false otherwise
     */
    @Override
    public MergeRecordsDTO merge(MergeRequestContext ctx) {

        MergeRecordsDTO result = etalonComponent.mergeEtalons(ctx);

        // 7. Recalculate timeline
        UpsertRequestContext uCtx = new UpsertRequestContextBuilder()
                .recalculateWholeTimeline(true)
                .returnEtalon(true)
                .batchUpsert(ctx.isBatchUpsert())
                .build();

        final EtalonKey key = EtalonKey.builder().id(result.getWinnerId()).build();
        RecordKeys keys = commonComponent.identify(key);
        uCtx.putToStorage(DATA_UPSERT_KEYS, keys);
        uCtx.putToStorage(StorageId.DATA_UPSERT_PREVIOUS_TIMELINE, ctx.getFromStorage(StorageId.DATA_UPSERT_PREVIOUS_TIMELINE));

        calculateEtalons(uCtx);

        ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_RECORD, uCtx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD));
        return result;
    }

    @Override
    public List<MergeRecordsDTO> batchMerge(List<MergeRequestContext> ctxs) {
        List<MergeRecordsDTO> result = new ArrayList<>();
        List<UpsertRequestContext> batchUctxs = new ArrayList<>();
        for (MergeRequestContext ctx : ctxs) {
            try {
                MergeRecordsDTO mergeResult = etalonComponent.mergeEtalons(ctx);
                if (AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
                    auditEventsWriter.writeSuccessEvent(AuditActions.DATA_MERGE, ctx);
                }
                result.add(mergeResult);
                // 7. Recalculate timeline
                UpsertRequestContext uCtx = new UpsertRequestContextBuilder()
                        .recalculateWholeTimeline(true)
                        .returnEtalon(true)
                        .batchUpsert(ctx.isBatchUpsert())
                        .auditLevel(ctx.getAuditLevel())
                        .build();
                if(!ctx.sendNotification()){
                    uCtx.skipNotification();
                } else {
                    uCtx.clearFlag(ContextUtils.CTX_FLAG_SEND_NOTIFICATION);
                }


                final EtalonKey key = EtalonKey.builder().id(mergeResult.getWinnerId()).build();
                RecordKeys keys = commonComponent.identify(key);
                uCtx.putToStorage(DATA_UPSERT_KEYS, keys);
                uCtx.putToStorage(StorageId.DATA_UPSERT_PREVIOUS_TIMELINE, ctx.getFromStorage(StorageId.DATA_UPSERT_PREVIOUS_TIMELINE));
                batchUctxs.add(uCtx);
            } catch (Exception e) {
                if (AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
                    auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_MERGE, e, ctx);
                    LOGGER.error("Can't merge records", e);
                }
            }
        }

        RecordUpsertBatchSetAccumulator recordsAccumulator
                = new RecordUpsertBatchSetAccumulator(500, null, null);
        recordsAccumulator.setBatchSetSize(BatchSetSize.SMALL);
        recordsAccumulator.setSupportedIterationTypes(Collections.singletonList(BatchSetIterationType.UPSERT_ETALONS));
        try {
            recordsAccumulator.charge(batchUctxs);
            batchUpsertRecords(recordsAccumulator);
        } finally {
            recordsAccumulator.discharge();
        }
        return result;
    }


    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final String autoMergeEnabledKey = UnidataConfigurationProperty.UNIDATA_DATA_AUTOMERGE_ENABLED.getKey();
        updates
                .filter(values ->
                        values.containsKey(autoMergeEnabledKey) && values.get(autoMergeEnabledKey).isPresent()
                )
                .map(values -> (Boolean) values.get(autoMergeEnabledKey).get())
                .subscribe(enableAutoMerge::set);
    }

    /**
     * TODO Kick this crap out!
     * Get data quality error from the elastic search.
     *
     * @param id     etalon id.
     * @param entity entity name.
     * @param date   date
     * @return list with data quality errors.
     */
    @SuppressWarnings("unchecked")
    private List<DataQualityError> extractDQErrors(String id, String entity, Date date) {
        List<DataQualityError> errors = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        fields.add(RecordHeaderField.FIELD_DQ_ERRORS_AS_BINARY.getField());
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(entity)
                .search(SearchRequestType.TERM)
                .searchFields(singletonList(FIELD_ETALON_ID.getField()))
                .text(id)
                .count(10)
                .asOf(date)
                .operator(SearchRequestOperator.OP_AND)
                .returnFields(fields)
                .build();
        SearchResultDTO searchResultDTO = searchService.search(ctx);
        if (searchResultDTO.getHits() == null || searchResultDTO.getHits().isEmpty()) {
            return errors;
        }
        SearchResultHitDTO results = searchResultDTO.getHits().get(0);
        SearchResultHitFieldDTO error = results.getFieldValue(RecordHeaderField.FIELD_DQ_ERRORS_AS_BINARY.getField());
        if (error != null && error.isNonNullField()) {
            errors.addAll((Collection<? extends DataQualityError>) DataUtils.fromString((String) error.getFirstValue()));
        }
        return errors;
    }

    private BatchSetAccumulator<UpsertRequestContext> getDefaultRecordsUpsertAccumulator() {
        RecordUpsertBatchSetAccumulator recordsAccumulator
                = new RecordUpsertBatchSetAccumulator(500, null, null);
        recordsAccumulator.setBatchSetSize(BatchSetSize.SMALL);
        recordsAccumulator.setSupportedIterationTypes(Arrays.asList(BatchSetIterationType.UPSERT_ORIGINS,
                BatchSetIterationType.UPSERT_ETALONS));
        return recordsAccumulator;
    }
}
