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


import static com.unidata.mdm.backend.common.context.StorageId.DATA_UPSERT_KEYS;
import static java.lang.Boolean.FALSE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext.DeleteRequestContextBuilder;
import com.unidata.mdm.backend.common.context.GetClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.GetTasksRequestContext;
import com.unidata.mdm.backend.common.context.GetTasksRequestContext.GetTasksRequestContextBuilder;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContextConfig;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.NestedSearchRequestContext;
import com.unidata.mdm.backend.common.context.NestedSearchRequestContext.NestedSearchType;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.SplitContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext.UpsertRequestContextBuilder;
import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.dq.DataQualityExecutionMode;
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
import com.unidata.mdm.backend.common.dto.SplitRecordsDTO;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.UpsertRecordDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.integration.exits.ExitException;
import com.unidata.mdm.backend.common.integration.exits.ExitState;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterSet;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.fields.ClassifierDataHeaderField;
import com.unidata.mdm.backend.common.search.fields.DQHeaderField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.search.fields.RelationHeaderField;
import com.unidata.mdm.backend.common.search.id.ClassifierIndexId;
import com.unidata.mdm.backend.common.search.id.RecordIndexId;
import com.unidata.mdm.backend.common.search.id.RelationFromIndexId;
import com.unidata.mdm.backend.common.search.id.RelationToIndexId;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataQualityStatus;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.SeverityType;
import com.unidata.mdm.backend.common.types.TypeOfChange;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.data.impl.ExtendedRecord;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.data.batch.AbstractBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.BatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.BatchSetIterationType;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.data.batch.RecordDeleteBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.RecordUpsertBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersServiceComponent;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.convert.TimelineConverter;
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
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.wf.WorkflowServiceExt;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.conf.WorkflowProcessType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleDataType;

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

    public static final String SPLIT_RECORD_ACTION_LISTENER_QUALIFIER = "splitRecordActionListener";

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
     * 'Split' action listener.
     */
    @Autowired
    @Qualifier(value = SPLIT_RECORD_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<SplitContext> splitContextDataRecordLifecycleListener;
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
    private WorkflowServiceExt workflowService;
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
        return TimelineConverter.to(commonComponent.loadTimeline(ctx));
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
                            .tasks(ctx.isTasks())
                            .includeDrafts(ctx.isIncludeDrafts())
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

                ctx.putToStorage(StorageId.DATA_GET_ETALON_RECORD, etalonRecord);
                result.setEtalon(etalonRecord);
                result.setAttributeWinnerMap(extended.getAttributeWinnersMap());

                List<String> previewKeysReverse = ctx.getPreviewEtalonKeys();
                Collections.reverse(previewKeysReverse);

                Map<String, List<String>> classifierUsage = classifiersComponent.checkUsageByRecordEtalonIdsSQL(ctx.getPreviewEtalonKeys());
                Map<String, List<GetClassifierDTO>> winnerClassifiers = new HashMap<>();
                for (Map.Entry<String, List<String>> entry : classifierUsage.entrySet()) {
                    for (String etalonKey : previewKeysReverse) {
                        if (entry.getValue().contains(etalonKey)) {
                            GetClassifiersDataRequestContext clsCtx = GetClassifiersDataRequestContext.builder()
                                    .classifierNames(entry.getKey())
                                    .etalonKey(etalonKey)
                                    .forDate(ctx.getForDate())
                                    .build();
                            if (!winnerClassifiers.containsKey(entry.getKey())) {
                                winnerClassifiers.put(entry.getKey(), new ArrayList<>());
                            }

                            winnerClassifiers.get(entry.getKey()).addAll(classifiersComponent.getClassifiers(clsCtx)
                                    .getClassifiers()
                                    .get(entry.getKey()));
                        }
                    }
                }

                result.setClassifiers(winnerClassifiers);
                if (MapUtils.isNotEmpty(winnerClassifiers)) {

                    winnerClassifiers.forEach((s, classifierDTOS) -> classifierDTOS.forEach(classifiersDTO -> {
                        if (classifiersDTO.getEtalon() != null && classifiersDTO.getEtalon().getInfoSection() != null) {
                            result.getAttributeWinnersMap().put(s + "." +
                                            classifiersDTO.getEtalon().getInfoSection().getClassifierEtalonKey(),
                                    classifiersDTO.getEtalon().getInfoSection().getRecordEtalonKey().getId());
                        }
                    }));
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
            DeleteRecordDTO result = new DeleteRecordDTO(ctx.keys());
            result.setErrors(ctx.getFromStorage(StorageId.PROCESS_ERRORS));
            return result;
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

    @Override
    public List<DeleteRecordDTO> batchDeleteRecords(List<DeleteRequestContext> ctxs) {
        AbstractBatchSetAccumulator<DeleteRequestContext> accumulator = getDefaultRecordsDeleteAccumulator();
        accumulator.charge(ctxs);
        List<DeleteRecordDTO> result;
        try {
            result = batchDeleteRecords(accumulator);
        } finally {
            accumulator.discharge();
        }
        return result;
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
            preprocessRecordsDeleteAccumulator(accumulator);

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
    public List<UpsertRecordDTO> batchUpsertRecords(List<UpsertRequestContext> ctxs, boolean abortOnFailure) {
        AbstractBatchSetAccumulator<UpsertRequestContext> accumulator = getDefaultRecordsUpsertAccumulator();
        accumulator.setAbortOnFailure(abortOnFailure);
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
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public EtalonRecordDTO restorePeriod(UpsertRequestContext ctx) {

        UpsertRecordDTO result = null;
        MeasurementPoint.start();
        try {
            // 1. Resolve keys, validate.
            restoreRecordsActionListner.before(ctx);

            RecordKeys keys = ctx.keys();

            Date ts = new Date();
            ctx.putToStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP, ts);

            // 2. Re-enable inactive origins
            originComponent.restorePeriod(ctx);

            // 3. Re-calculate state and mark index
            Map<RecordHeaderField, Object> fields = new EnumMap<>(RecordHeaderField.class);
            fields.put(RecordHeaderField.FIELD_INACTIVE, FALSE);
            fields.put(RecordHeaderField.FIELD_UPDATED_AT, ts);

            FormFieldsGroup group = FormFieldsGroup.createAndGroup(
                    FormField.strictString(RecordHeaderField.FIELD_ETALON_ID.getField(), keys.getEtalonKey().getId()),
                    FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getField(), null, ctx.getValidTo()),
                    FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_TO.getField(), ctx.getValidFrom(), null));

            SearchRequestContext searchContext = SearchRequestContext.forEtalonData(keys.getEntityName())
                    .form(group)
                    .build();

            searchService.mark(searchContext, fields);

            // 4. Return result.
            result = new UpsertRecordDTO(UpsertAction.NO_ACTION);

            Map<TimeIntervalDTO, Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>>> data
                = ctx.getFromStorage(StorageId.DATA_INTERVALS_AFTER);

            if (MapUtils.isNotEmpty(data)) {

                Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> record
                    = data.entrySet().iterator().next().getValue();
                result.setEtalon(record != null ? record.getKey() : null);
            }

            ctx.putToStorage(StorageId.DATA_UPSERT_EXACT_ACTION, UpsertAction.UPDATE);
            ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_RECORD, result.getEtalon());
            final TimeIntervalDTO timeInterval = data.keySet().iterator().next();
            if (timeInterval instanceof WorkflowTimeIntervalDTO) {
                WorkflowTimelineDTO workflowTimeline = new WorkflowTimelineDTO(
                        result.getEtalon().getInfoSection().getEtalonKey().getId(),
                        true,
                        true
                );
                workflowTimeline.getIntervals().add(timeInterval);
                ctx.putToStorage(StorageId.DATA_RECORD_TIMELINE, workflowTimeline);
            }

            result.setRecordKeys(keys);

            if (ctx.getFromStorage(StorageId.DATA_UPSERT_WF_ASSIGNMENTS) != null) {
                commonComponent.changeApproval(keys.getEtalonKey().getId(), ApprovalState.PENDING);
            }

            restoreRecordsActionListner.after(ctx);

        } finally {
            MeasurementPoint.stop();
        }

        return result;
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
            RecordKeys keys = ctx.keys();

            UpsertRecordDTO result = new UpsertRecordDTO(action);
            result.setRecordKeys(keys);

            calculateEtalons(ctx);

            EtalonRecord etalon = null;
            if (ctx.isReturnEtalon()) {
                etalon = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
                etalon = appyAutoMergeIfNeed(result, etalon);
            }

            result.setEtalon(etalon);
            List<ErrorInfoDTO> erros = ctx.getFromStorage(StorageId.PROCESS_ERRORS);
            if (CollectionUtils.isNotEmpty(erros)) {
                result.setErrors(erros);
            }
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
        if (!enableAutoMerge.get() || Objects.isNull(etalon)) {
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
     * @param ctx request context
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
                    boolean restoreCapable = ctx.getDqErrors()
                            .stream()
                            .noneMatch(er -> er.getExecutionMode() == DataQualityExecutionMode.MODE_ORIGIN);

                    if (!restoreCapable) {

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

    @Override
    public SplitRecordsDTO splitRecord(final String originId) {
        SplitContext splitContext = null;
        try {
            final OriginRecordPO originRecord = dataRecordsDao.findOriginRecordById(originId);
            if (originRecord == null) {
                throw new BusinessException(
                        "Origin record not found. Id: " + originId, ExceptionId.EX_ORIGIN_NOT_FOUND, originId
                );
            }

            splitContext = new SplitContext(
                    OriginKey.builder()
                            .id(originId)
                            .entityName(originRecord.getName())
                            .externalId(originRecord.getExternalId())
                            .build(),
                    EtalonKey.builder().id(originRecord.getEtalonId()).build());
            try {
                if (!splitContextDataRecordLifecycleListener.before(splitContext)) {
                    LOGGER.warn("SplitContextDataRecordLifecycleListener.before() return false, check errors in log");
                    return null;
                }
            } catch (Exception e) {
                LOGGER.error("SplitContextDataRecordLifecycleListener.before() throw exception", e);
                throw e;
            }

            final EtalonKey newEtalonKey = originComponent.splitOrigin(splitContext);
            splitContext.setNewEtalonKey(newEtalonKey);

            etalonComponent.splitEtalon(splitContext);

            splitContextDataRecordLifecycleListener.after(splitContext);

            SplitRecordsDTO result = new SplitRecordsDTO();
            result.setErrors(splitContext.getFromStorage(StorageId.PROCESS_ERRORS));
            result.setEtalonId(Collections.singletonMap(DataRecordsService.ETALON_ID, newEtalonKey.getId()));
            auditEventsWriter.writeSuccessEvent(AuditActions.DATA_SPLIT, splitContext);
            return result;
        } catch (Exception e) {
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_SPLIT, e, splitContext);
            throw e;
        }
    }

    /**
     * Merges several etalon records (and their origins) to one master record.
     *
     * @param ctx current merge context
     * @return true if successful, false otherwise
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MergeRecordsDTO merge(MergeRequestContext ctx) {

        MergeRecordsDTO result = etalonComponent.mergeEtalons(ctx);

        // 7. Recalculate timeline
        UpsertRequestContext uCtx = new UpsertRequestContextBuilder()
                .recalculateWholeTimeline(true)
                .returnEtalon(true)
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
        MeasurementPoint.start();
        try {
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
                            .restore(true)
                            .batchUpsert(ctx.isBatchUpsert())
                            .auditLevel(ctx.getAuditLevel())
                            .skipConsistencyChecks(true)
                            .build();

                    if (!ctx.sendNotification()) {
                        uCtx.skipNotification();
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
            if (!ctxs.get(0).isDirtyMode()) {
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
             }

            return result;
        } finally {
            MeasurementPoint.stop();
        }
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
     * Get data quality error from the elastic search.
     *
     * @param id
     *            etalon id.
     * @param entity
     *            entity name.
     * @param date date
     * @return list with data quality errors.
     */
    /*
    @Override
    public List<DataQualityError> extractDQErrors(String id, String entity, Date date) {

        List<DataQualityError> errors = new ArrayList<>();
        SearchRequestContext ctx = SearchRequestContext.forEtalon(EntitySearchType.DQ_ERRORS, entity)
                .form(FormFieldsGroup
                        .createAndGroup()
                        .addFormField(FormField.strictString(RecordHeaderField.FIELD_ETALON_ID.getField(), id)))
                .asOf(date)
                .routings(Collections.singletonList(id))
                .operator(SearchRequestOperator.OP_AND)
                .returnFields(
                        DQHeaderField.ERROR_ID.getField(),
                        DQHeaderField.CREATE_DATE.getField(),
                        DQHeaderField.UPDATE_DATE.getField(),
                        DQHeaderField.STATUS.getField(),
                        DQHeaderField.RULE_NAME.getField(),
                        DQHeaderField.MESSAGE.getField(),
                        DQHeaderField.SEVERITY.getField(),
                        DQHeaderField.CATEGORY.getField(),
                        // DqHeaderField.FIELD.getField(),
                        DQHeaderField.PATHS.getField())
                .build();

        SearchResultDTO searchResultDTO = searchService.search(ctx);
        if (CollectionUtils.isEmpty(searchResultDTO.getHits())) {
            return errors;
        }
        for (SearchResultHitDTO searchResultHitDTO : searchResultDTO.getHits()) {
            SearchResultHitFieldDTO errorId = searchResultHitDTO.getFieldValue(DQHeaderField.ERROR_ID.getField());
            SearchResultHitFieldDTO createDate = searchResultHitDTO.getFieldValue(DQHeaderField.CREATE_DATE.getField());
            SearchResultHitFieldDTO updateDate = searchResultHitDTO.getFieldValue(DQHeaderField.UPDATE_DATE.getField());
            SearchResultHitFieldDTO status = searchResultHitDTO.getFieldValue(DQHeaderField.STATUS.getField());
            SearchResultHitFieldDTO ruleName = searchResultHitDTO.getFieldValue(DQHeaderField.RULE_NAME.getField());
            SearchResultHitFieldDTO message = searchResultHitDTO.getFieldValue(DQHeaderField.MESSAGE.getField());
            SearchResultHitFieldDTO severity = searchResultHitDTO.getFieldValue(DQHeaderField.SEVERITY.getField());
            SearchResultHitFieldDTO category = searchResultHitDTO.getFieldValue(DQHeaderField.CATEGORY.getField());
            SearchResultHitFieldDTO paths = searchResultHitDTO.getFieldValue(DQHeaderField.PATHS.getField());
            //SearchResultHitFieldDTO field = searchResultHitDTO.getFieldValue(DqHeaderField.FIELD.getField());

            if (errorId != null && errorId.isNonNullField()) {
                final Object upDate = updateDate != null && updateDate.isNonNullField() ? updateDate.getFirstValue() : null;
                errors.add(
                        DataQualityError.builder()
                                .etalonId(id)
                                .errorId((String) errorId.getFirstValue())
                                .createDate((Date) createDate.getFirstValue())
                                .updateDate((Date) upDate)
                                .status(DataQualityStatus.fromValue((String) status.getFirstValue()))
                                .ruleName((String) ruleName.getFirstValue())
                                .message((String) message.getFirstValue())
                                .severity((String) severity.getFirstValue())
                                .category((String) category.getFirstValue())
                                .paths(paths.getValues().stream().map(String::valueOf).collect(Collectors.toList()))
                                .build()
                );
            }
        }

        return errors;
    }
    */

    /**
     * Get data quality error from the elastic search.
     *
     * @param id etalon id.
     * @param entity entity name.
     * @param date date
     * @return list with data quality errors.
     */
    @Override
    public List<DataQualityError> extractDQErrors(String id, String entity, Date date) {

        List<DataQualityError> errors = new ArrayList<>();
        SearchRequestContext ctx = SearchRequestContext.builder(EntitySearchType.ETALON_DATA, entity)
                .count(1)
                .form(FormFieldsGroup.createAndGroup(FormField.strictString(RecordHeaderField.FIELD_ETALON_ID.getField(), id)))
                .source(false)
                .asOf(date)
                .operator(SearchRequestOperator.OP_AND)
                .nestedSearch(
                        NestedSearchRequestContext.builder(SearchRequestContext.builder()
                                .nestedPath(RecordHeaderField.FIELD_DQ_ERRORS.getField())
                                .fetchAll(true)
                                .count(1000)
                                .returnFields(
                                        DQHeaderField.ERROR_ID.getField(),
                                        DQHeaderField.CREATE_DATE.getField(),
                                        DQHeaderField.UPDATE_DATE.getField(),
                                        DQHeaderField.STATUS.getField(),
                                        DQHeaderField.RULE_NAME.getField(),
                                        DQHeaderField.MESSAGE.getField(),
                                        DQHeaderField.SEVERITY.getField(),
                                        DQHeaderField.CATEGORY.getField(),
                                        DQHeaderField.EXECUTION_MODE.getField(),
                                        DQHeaderField.PATHS.getField()
                                )
                                .build())
                                .nestedQueryName(RecordHeaderField.FIELD_DQ_ERRORS.getField())
                                .nestedSearchType(NestedSearchType.NESTED_OBJECTS)
                                .build())
                .routings(Collections.singletonList(id))
                .build();

        SearchResultDTO searchResultDTO = searchService.search(ctx);
        if (CollectionUtils.isEmpty(searchResultDTO.getHits())) {
            return errors;
        }

        SearchResultHitDTO results = searchResultDTO.getHits().get(0);
        List<SearchResultHitDTO> innerHits = results.getInnerHits().get(RecordHeaderField.FIELD_DQ_ERRORS.getField());
        if (CollectionUtils.isNotEmpty(innerHits)) {
            for (SearchResultHitDTO innerHit : innerHits) {

                List<Object> rawPaths = innerHit.getFieldValues(DQHeaderField.PATHS.getField());
                String executionModeAsString = innerHit.getFieldFirstValue(DQHeaderField.EXECUTION_MODE.getField());
                errors.add(DataQualityError.builder()
                        .errorId(innerHit.getFieldFirstValue(DQHeaderField.ERROR_ID.getField()))
                        .createDate(SearchUtils.parseFromIndex(innerHit.getFieldFirstValue(DQHeaderField.CREATE_DATE.getField())))
                        .updateDate(SearchUtils.parseFromIndex(innerHit.getFieldFirstValue(DQHeaderField.UPDATE_DATE.getField())))
                        .status(DataQualityStatus.fromValue(innerHit.getFieldFirstValue(DQHeaderField.STATUS.getField())))
                        .ruleName(innerHit.getFieldFirstValue(DQHeaderField.RULE_NAME.getField()))
                        .message(innerHit.getFieldFirstValue(DQHeaderField.MESSAGE.getField()))
                        .severity(SeverityType.fromValue(innerHit.getFieldFirstValue(DQHeaderField.SEVERITY.getField())))
                        .category(innerHit.getFieldFirstValue(DQHeaderField.CATEGORY.getField()))
                        .executionMode(executionModeAsString == null ? null : DataQualityExecutionMode.valueOf(executionModeAsString))
                        .values(CollectionUtils.isEmpty(rawPaths) ? Collections.emptyList() : rawPaths.stream()
                                .filter(Objects::nonNull)
                                .map(obj -> new ImmutablePair<String, Attribute>(obj.toString(), null))
                                .collect(Collectors.toList()))
                        .build());
            }
        }

        return errors;
    }

    @Override
    public boolean reindexEtalon(GetRequestContext ctx) {
        WorkflowTimelineDTO timeline = null;
        try {
            timeline = originComponent.loadWorkflowTimeline(ctx, true);
        } catch (DataProcessingException e) {
            // sometimes we can't identify record and entity name
            if (ctx.getEntityName() == null) {
                throw e;
            }
        } catch (SystemRuntimeException e) { /* NOP */ }


        final IndexRequestContext idx = buildIndexRequestContext(
                IndexRequestContextConfig.builder()
                        .skipNotification(true)
                        .build(),
                ctx.keys(),
                timeline);

        if (idx == null) {
            if (ctx.getEtalonKey() == null) {
                return true;
            }
            List<SearchRequestContext> forRemove = new ArrayList<>();
            forRemove.add(SearchRequestContext.forIndex(ctx.getEntityName())
                    .form(FormFieldsGroup.createOrGroup(Arrays.asList(
                            FormField.strictString(RelationHeaderField.FIELD_FROM_ETALON_ID.getField(), ctx.getEtalonKey()),
                            FormField.strictString(RecordHeaderField.FIELD_ETALON_ID.getField(), ctx.getEtalonKey()))))
                    .onlyQuery(true)
                    .build());
            List<String> toSideNames = getRelsToSideForEntity(ctx.getEntityName());
            if (CollectionUtils.isNotEmpty(toSideNames)) {
                for (String toSideName : toSideNames) {
                    forRemove.add(SearchRequestContext.forIndex(toSideName)
                            .form(FormFieldsGroup.createAndGroup(
                                    FormField.strictString(RelationHeaderField.FIELD_TO_ETALON_ID.getField(), ctx.getEtalonKey())))
                            .onlyQuery(true)
                            .build());
                }
            }
            return searchService.deleteFoundResult(ComplexSearchRequestContext.multi(forRemove));
        } else {
            final IndexRequestContext indexRequestContext = IndexRequestContext.builder(idx)
                    .drop(true)
                    .build();
            return searchService.index(indexRequestContext);
        }
    }

    private AbstractBatchSetAccumulator<UpsertRequestContext> getDefaultRecordsUpsertAccumulator() {
        RecordUpsertBatchSetAccumulator recordsAccumulator
                = new RecordUpsertBatchSetAccumulator(500, null, null);
        recordsAccumulator.setBatchSetSize(BatchSetSize.SMALL);
        recordsAccumulator.setSupportedIterationTypes(Arrays.asList(BatchSetIterationType.UPSERT_ORIGINS,
                BatchSetIterationType.UPSERT_ETALONS));
        return recordsAccumulator;
    }

    private AbstractBatchSetAccumulator<DeleteRequestContext> getDefaultRecordsDeleteAccumulator() {
        RecordDeleteBatchSetAccumulator recordsAccumulator
                = new RecordDeleteBatchSetAccumulator(500, null);
        recordsAccumulator.setBatchSetSize(BatchSetSize.SMALL);
        recordsAccumulator.setSupportedIterationTypes(Arrays.asList(BatchSetIterationType.DELETE_ORIGINS,
                BatchSetIterationType.DELETE_ETALONS));
        return recordsAccumulator;
    }

    private void preprocessRecordsDeleteAccumulator(BatchSetAccumulator<DeleteRequestContext> accumulator) {
        commonComponent.identify(accumulator.workingCopy());
    }

    @Override
    public IndexRequestContext buildIndexRequestContext(final IndexRequestContextConfig config,
                                                        final RecordKeys keys,
                                                        final WorkflowTimelineDTO timeline) {
        if (timeline == null || timeline.getIntervals().isEmpty()) {
            return null;
        }

        final boolean isSoftDeleteReindex = keys.getEtalonStatus() == RecordStatus.INACTIVE;
        final boolean isActive = keys.getEtalonStatus() == RecordStatus.ACTIVE;
        if (!isSoftDeleteReindex && !isActive) {
            return null;
        }
        IndexRequestContext.IndexRequestContextBuilder ircB = IndexRequestContext.builder();
        ircB.drop(!config.isIndexesAreEmpty())
                .entity(keys.getEntityName())
                .routing(keys.getEtalonKey().getId());

        addRecords(keys, timeline, ircB, config);
        addRelations(keys, ircB, config);
        addClassifiers(keys, ircB, config);

        return ircB.hasUpdates()
                ? ircB.build()
                : null;
    }

    private List<String> getRelsForEntity(String entityName) {
        if (StringUtils.isBlank(entityName)) {
            return Collections.emptyList();
        }
        return metaModelService.getRelationsByFromEntityName(entityName)
                .stream()
                .map(RelationDef::getName)
                .collect(Collectors.toList());
    }

    private List<String> getRelsToSideForEntity(String entityName) {
        if (StringUtils.isBlank(entityName)) {
            return Collections.emptyList();
        }
        return metaModelService.getRelationsByFromEntityName(entityName)
                .stream()
                .map(RelationDef::getToEntity)
                .collect(Collectors.toList());
    }

    private List<String> getClassifiersForEntity(String entityName) {
        if (StringUtils.isBlank(entityName)) {
            return Collections.emptyList();
        }
        return metaModelService.getClassifiersForEntity(entityName);
    }

    /**
     * Adds records and clusters to the builder.
     *
     * @param timeline the timeline to process
     * @param builder the builder
     */
    private void addRecords(final RecordKeys keys,
                            final WorkflowTimelineDTO timeline,
                            final IndexRequestContext.IndexRequestContextBuilder builder,
                            final IndexRequestContextConfig config) {

        if (!config.isReindexRecords() && !config.isReindexMatching()) {
            return;
        }

        UpsertRequestContext ctx = UpsertRequestContext.builder()
                .entityName(keys.getEntityName())
                .recalculateWholeTimeline(true)
                .restore(true)
                .skipCleanse(config.isSkipDQ())
                .skipConsistencyChecks(config.isSuppressConsistencyChecks())
                .skipMatchingPreprocessing(!config.isReindexMatching())
                .bypassExtensionPoints(true)
                .returnEtalon(true)
                .build();

        ctx.setOperationId(config.getOperationId());
        if (config.isSkipNotification()) {
            ctx.skipNotification();
        } else {
            ctx.setFlag(ContextUtils.CTX_FLAG_SEND_NOTIFICATION);
        }
        ctx.putToStorage(ctx.keysId(), keys);
        ctx.putToStorage(StorageId.DATA_UPSERT_IS_PUBLISHED, timeline.isPublished());
        ctx.putToStorage(StorageId.DATA_RECORD_TIMELINE, timeline);

        List<UpsertRequestContext> periodEtalons = etalonComponent.calculatePeriods(ctx, timeline.getIntervals());
        Triple<Map<EtalonRecord, Map<? extends SearchField, Object>>,
                Map<EtalonRecord, ClusterSet>,
                Map<EtalonRecord, List<DataQualityError>>>
                recordUpdates = collectUpdates(periodEtalons);

        builder.records(recordUpdates.getLeft());
        builder.clusters(recordUpdates.getMiddle());
        builder.dqErrors(recordUpdates.getRight());
        if (!config.isIndexesAreEmpty()) {

            SearchRequestContext sCtx = SearchRequestContext.builder(EntitySearchType.ETALON_DATA, keys.getEntityName())
                    .form(Collections.singletonList(FormFieldsGroup.createAndGroup(
                            FormField.strictString(RecordHeaderField.FIELD_ETALON_ID.getField(), keys.getEtalonKey().getId()))))
                    .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                    .onlyQuery(true)
                    .source(false)
                    .count(SearchRequestContext.MAX_PAGE_SIZE)
                    .returnFields(Collections.singletonList(RecordHeaderField.FIELD_PERIOD_ID.getField()))
                    .build();

            SearchResultDTO result = searchService.search(sCtx);
            builder.recordsToDelete(result.getHits().stream()
                    .map(hit -> {
                        String periodId = hit.getFieldFirstValue(RecordHeaderField.FIELD_PERIOD_ID.getField());
                        return RecordIndexId.of(keys.getEntityName(), keys.getEtalonKey().getId(), periodId);
                    })
                    .collect(Collectors.toList()));
        }
    }

    //it is a duplicate code, will be deleted after remove map with search field
    private Triple<
            Map<EtalonRecord, Map<? extends SearchField, Object>>,
            Map<EtalonRecord, ClusterSet>,
            Map<EtalonRecord, List<DataQualityError>>>
    collectUpdates(List<UpsertRequestContext> periods) {

        if (periods.isEmpty()) {
            return new ImmutableTriple<>(null, null, null);
        }

        Map<EtalonRecord, Map<? extends SearchField, Object>> indexUpdate = new HashMap<>(periods.size(), 1);
        Map<EtalonRecord, ClusterSet> matchingUpdates = new HashMap<>(periods.size(), 1);
        Map<EtalonRecord, List<DataQualityError>> dqErrorsUpdates = new HashMap<>(periods.size(), 1);
        for (UpsertRequestContext pCtx : periods) {

            EtalonRecord etalon = pCtx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
            if (etalon == null) {
                continue;
            }

            Map<? extends SearchField, Object> fields = pCtx.getFromStorage(StorageId.DATA_UPSERT_ETALON_INDEX_UPDATE);
            if (fields != null) {
                indexUpdate.put(etalon, fields);
            }

            Collection<Cluster> clusters = pCtx.getFromStorage(StorageId.DATA_UPSERT_ETALON_MATCHING_UPDATE);
            if (CollectionUtils.isNotEmpty(clusters)) {
                matchingUpdates.put(etalon, new ClusterSet(clusters,
                        etalon.getInfoSection().getValidFrom(), etalon.getInfoSection().getValidTo()));
            }

            if (CollectionUtils.isNotEmpty(pCtx.getDqErrors())) {
                dqErrorsUpdates.put(etalon, pCtx.getDqErrors());
            }
        }

        return new ImmutableTriple<>(indexUpdate, matchingUpdates, dqErrorsUpdates);
    }

    /**
     * Gets and adds classifiers updates.
     *
     * @param builder the builder
     */

    private void addClassifiers(final RecordKeys keys,
                                final IndexRequestContext.IndexRequestContextBuilder builder,
                                final IndexRequestContextConfig config) {

        if (!config.isReindexClassifiers()) {
            return;
        }

        List<String> classifiers = config.getClassifierNames() == null
                ? getClassifiersForEntity(keys.getEntityName())
                : config.getClassifierNames();

        if (classifiers.isEmpty()) {
            return;
        }

        String etalonId = keys.getEtalonKey().getId();
        GetClassifiersDataRequestContext clsfCtx = GetClassifiersDataRequestContext.builder()
                .etalonKey(etalonId)
                .classifierNames(classifiers)
                .build();

        clsfCtx.setOperationId(config.getOperationId());
        clsfCtx.putToStorage(clsfCtx.keysId(), keys);

        GetClassifiersDTO result = classifiersComponent.getClassifiers(clsfCtx);
        List<EtalonClassifier> classifiersData = result.getClassifiers()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(GetClassifierDTO::getEtalon)
                .filter(Objects::nonNull)
                .map(cl -> {
                    cl.getInfoSection().withStatus(keys.getEtalonStatus());
                    return cl;
                })
                .collect(Collectors.toList());

        builder.classifiers(classifiersData);
        if (!config.isIndexesAreEmpty()) {
            SearchRequestContext sCtx = SearchRequestContext.builder(EntitySearchType.CLASSIFIER, keys.getEntityName())
                    .form(FormFieldsGroup.createAndGroup(
                            FormField.strictString(ClassifierDataHeaderField.FIELD_ETALON_ID_RECORD.getField(), keys.getEtalonKey().getId())))
                    .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                    .onlyQuery(true)
                    .source(false)
                    .count(SearchRequestContext.MAX_PAGE_SIZE)
                    .returnFields(
                            ClassifierDataHeaderField.FIELD_ROOT_NODE_ID.getField(),
                            ClassifierDataHeaderField.FIELD_NAME.getField())
                    .build();

            SearchResultDTO searchResult = searchService.search(sCtx);
            if (CollectionUtils.isNotEmpty(searchResult.getHits())) {
                searchResult.getHits().forEach(h -> {
                    builder.classifierToDelete(ClassifierIndexId.of(
                            keys.getEntityName(),
                            h.getFieldFirstValue(ClassifierDataHeaderField.FIELD_NAME.getField()),
                            keys.getEtalonKey().getId(),
                            h.getFieldFirstValue(ClassifierDataHeaderField.FIELD_ROOT_NODE_ID.getField())));
                });
            }
        }
    }

    /**
     * Gets and adds to context relation data suitable for reindexing.
     *
     * @param builder the builder to fill
     * @return relations data
     */
    private void addRelations(final RecordKeys keys,
                              final IndexRequestContext.IndexRequestContextBuilder builder,
                              final IndexRequestContextConfig config) {

        // 1. Skip action, if disabled by the user
        if (!config.isReindexRelations()) {
            return;
        }

        // 2. Find out the names
        List<String> names = CollectionUtils.isEmpty(config.getRelationNames())
                ? getRelsForEntity(keys.getEntityName())
                : config.getRelationNames();

        if (CollectionUtils.isEmpty(names)) {
            return;
        }

        // 3. Load current elastic data to collect ids for bulk deletion.
        // Skip this, if the whole index was already cleansed.
        if (!config.isIndexesAreEmpty()) {

            SearchRequestContext sCtx = SearchRequestContext.builder(EntitySearchType.ETALON_RELATION, keys.getEntityName())
                    .form(FormFieldsGroup.createAndGroup(
                            FormField.strictString(RelationHeaderField.FIELD_FROM_ETALON_ID.getField(), keys.getEtalonKey().getId())))
                    .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                    .onlyQuery(true)
                    .source(false)
                    .count(SearchRequestContext.MAX_PAGE_SIZE)
                    .returnFields(Arrays.asList(
                            RelationHeaderField.FIELD_PERIOD_ID.getField(),
                            RelationHeaderField.FIELD_TO_ETALON_ID.getField(),
                            RelationHeaderField.REL_NAME.getField()))
                    .build();

            SearchResultDTO result = searchService.search(sCtx);
            result.getHits().forEach(h -> {

                String periodId = h.getFieldFirstValue(RelationHeaderField.FIELD_PERIOD_ID.getField());
                String relationName = h.getFieldFirstValue(RelationHeaderField.REL_NAME.getField());
                String toEtalonId = h.getFieldFirstValue(RelationHeaderField.FIELD_TO_ETALON_ID.getField());
                if (Objects.nonNull(periodId) && Objects.nonNull(relationName) && Objects.nonNull(toEtalonId)) {

                    builder.relationToDelete(
                        RelationFromIndexId.of(
                            keys.getEntityName(),
                            relationName,
                            keys.getEtalonKey().getId(),
                            toEtalonId,
                            periodId));

                    RelationDef rel = metaModelService.getRelationById(relationName);
                    if (Objects.nonNull(rel)) {
                        builder.relationToDelete(
                            RelationToIndexId.of(
                                rel.getToEntity(),
                                relationName,
                                keys.getEtalonKey().getId(),
                                toEtalonId,
                                periodId));
                    }
                }
            });
        }

        List<EtalonRelation> relations = relationsComponent.loadActiveEtalonsRelationsByFromSideAsList(keys, config.getOperationId());
        if (CollectionUtils.isNotEmpty(relations)) {
            builder.relations(relations.stream()
                    .map(rel -> {
                        rel.getInfoSection().withStatus(keys.getEtalonStatus());
                        return rel;
                    })
                    .collect(Collectors.toList()));
        }
    }
}
