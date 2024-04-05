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

package com.unidata.mdm.backend.service.data.etalon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.dao.impl.DaoHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext.GetRequestContextBuilder;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.SplitContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.data.DataRecordHolder;
import com.unidata.mdm.backend.common.dto.ContributorDTO;
import com.unidata.mdm.backend.common.dto.ErrorInfoDTO;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.GetClassifiersDTO;
import com.unidata.mdm.backend.common.dto.MergeRecordsDTO;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterSet;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.search.id.RecordIndexId;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.TimeIntervalContributorInfo;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.VistoryOperationType;
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl;
import com.unidata.mdm.backend.conf.impl.UpsertImpl;
import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.dao.OriginsVistoryDao;
import com.unidata.mdm.backend.data.impl.ExtendedRecord;
import com.unidata.mdm.backend.po.ContributorPO;
import com.unidata.mdm.backend.po.TimeIntervalPO;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.configuration.ConfigurationServiceExt;
import com.unidata.mdm.backend.service.data.batch.BatchIterator;
import com.unidata.mdm.backend.service.data.batch.BatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.BatchSetIterationType;
import com.unidata.mdm.backend.service.data.batch.RecordBatchSet;
import com.unidata.mdm.backend.service.data.batch.RecordBatchSetProcessor;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersServiceComponent;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.driver.EtalonComposer;
import com.unidata.mdm.backend.service.data.driver.EtalonCompositionDriverType;
import com.unidata.mdm.backend.service.data.listener.DataRecordLifecycleListener;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;

/**
 * @author Mikhail Mikhailov
 *         Etalon data related stuff.
 */
@Component
public class EtalonRecordsComponent {

    /**
     * The class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EtalonRecordsComponent.class);
    /**
     * 'Delete' action listener qualifier name.
     */
    public static final String DELETE_RECORD_ACTION_LISTENER_QUALIFIER = "deleteRecordActionListener";
    /**
     * Etalon calculation after 'Upsert' action listener qualifier name.
     */
    public static final String ETALON_CALCULATION_ACTION_LISTENER_QUALIFIER = "etalonCalculationActionListener";
    /**
     * 'Upsert' record pooling executor.
     */
    public static final String CALCULATE_INTERVAL_EXECUTOR_QUALIFIER = "etalonIntervalCalculationExecutor";
    /**
     * 'Merge' action listener qualifier name.
     */
    public static final String MERGE_RECORD_ACTION_LISTENER_QUALIFIER = "mergeRecordActionListener";

    /**
     * Delay for async audit operations.
     */
    @Value("${unidata.data.refresh.immediate:true}")
    private Boolean refreshImmediate;


    /**
     * Etalon calculation after 'Upsert' action listener.
     */
    @Autowired
    @Qualifier(value = ETALON_CALCULATION_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<UpsertRequestContext> etalonCalculationActionListener;
    /**
     * 'Delete' action listener.
     */
    @Autowired
    @Qualifier(value = DELETE_RECORD_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<DeleteRequestContext> deleteRecordActionListener;
    /**
     * 'Merge' action listener.
     */
    @Autowired
    @Qualifier(value = MERGE_RECORD_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<MergeRequestContext> mergeRecordActionListener;
    /**
     * Etalon composer.
     */
    @Autowired
    private EtalonComposer etalonComposer;
    /**
     * Common functionality.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;
    /**
     * Origin/Vistory data component.
     */
    @Autowired
    private OriginRecordsComponent originRecordsComponent;
    /**
     * Relations component.
     */
    @Autowired
    private RelationsServiceComponent relationsServiceComponent;
    /**
     * Classifiers component.
     */
    @Autowired
    private ClassifiersServiceComponent classifiersComponent;

    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchService;
    /**
     * Origins vistory DAO.
     */
    @Autowired
    private OriginsVistoryDao originsVistoryDao;
    /**
     * Data record DAO.
     */
    @Autowired
    private DataRecordsDao dataRecordsDao;
    /**
     * Meta model service
     */
    @Autowired
    private MetaModelServiceExt modelService;

    @Autowired
    private OriginRecordsComponent originComponent;

    /**
     * 'Upsert' pooling executor.
     */
    @Autowired
    @Qualifier(value = CALCULATE_INTERVAL_EXECUTOR_QUALIFIER)
    private TaskExecutor etalonIntervalCalculationExecutor;
    /**
     * Audit writer
     */
    @Autowired
    private AuditEventsWriter auditEventsWriter;
    /**
     * Record batch processor.
     */
    @Autowired
    private RecordBatchSetProcessor recordBatchSetProcessor;

    @Autowired
    private ClusterService clusterService;

    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationServiceExt configurationService;
    /**
     * Constructor.
     */
    public EtalonRecordsComponent() {
        super();
    }

    /**
     * Loads etalon state for a time stamp and uses base origin object as data source.
     *
     * @param etalonId the id
     * @param asOf time stamp
     * @param data origin data
     * @return etalon record
     */
    private EtalonRecord loadEtalonState(String etalonId, Date asOf, DataRecord data, boolean includeInactive, Boolean isApproverView, boolean includeWinners) {
        MeasurementPoint.start();
        try {

            TimeIntervalPO info = originsVistoryDao.loadEtalonBoundary(etalonId, asOf, isApproverView);

            TimeIntervalContributorInfo selected = composeContributor(info, false, false);
            VistoryOperationType operationType = selected == null ? null : selected.getOperationType();

            EtalonRecordInfoSection is = new EtalonRecordInfoSection()
                    .withPeriodId(info.getPeriodId())
                    .withValidFrom(info.getFrom())
                    .withValidTo(info.getTo())
                    .withCreateDate(info.getCreateDate())
                    .withUpdateDate(info.getUpdateDate())
                    .withEntityName(info.getName())
                    .withStatus(info.getStatus())
                    .withApproval(info.getState())
                    .withCreatedBy(info.getCreatedBy())
                    .withUpdatedBy(info.getUpdatedBy())
                    .withOperationType(operationType)
                    .withEtalonKey(EtalonKey.builder()
                            .id(etalonId)
                            .gsn(info.getEtalonGsn())
                            .build());

            return new EtalonRecordImpl()
                    .withInfoSection(is)
                    .withDataRecord(data);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * compose contributor
     *
     * @param info
     * @param includeInactive
     * @param includeWinners
     * @return
     */
    private TimeIntervalContributorInfo composeContributor(TimeIntervalPO info, boolean includeInactive, boolean includeWinners) {

        List<CalculableHolder<TimeIntervalContributorInfo>> calculables
                = new ArrayList<>(info.getContributors() != null ? info.getContributors().length : 0);

        for (int j = 0; info.getContributors() != null && j < info.getContributors().length; j++) {

            ContributorPO copo = info.getContributors()[j];
            TimeIntervalContributorInfo tici = new TimeIntervalContributorInfo()
                    .withApprovalState(copo.getApproval())
                    .withCreateDate(copo.getLastUpdate())
                    .withCreatedBy(copo.getOwner())
                    .withOperationType(copo.getOperationType())
                    .withOriginId(copo.getOriginId())
                    .withRevision(copo.getRevision())
                    .withSourceSystem(copo.getSourceSystem())
                    .withStatus(copo.getStatus());

            calculables.add(CalculableHolder.of(tici));
        }

        return etalonComposer.compose(EtalonCompositionDriverType.BVR, calculables, includeInactive, includeWinners);
    }


    /**
     * Loads (calculates) etalon for a date and for last update date.
     *
     * @param etalonIds the etalon ids
     * @param asOf date on the time line
     * @param lud last update date
     * @return record or null
     */
    public ExtendedRecord loadMergedEtalonDataView(Collection<String> etalonIds, Date asOf, Date lud) {

        if (Objects.isNull(etalonIds) || etalonIds.isEmpty()) {
            return null;
        }
        MeasurementPoint.start();
        try {

            Map<String, List<CalculableHolder<OriginRecord>>> map = new HashMap<>();
            // todo Thing about correct userName for this action
            String user = SecurityUtils.getCurrentUserName();
            for (String etalonId : etalonIds) {
                map.put(etalonId, originRecordsComponent.loadOriginsCalculables(etalonId, asOf, lud, null, null, false, user));
            }

            if (map.isEmpty()) {
                return null;
            }

            String entityName = originsVistoryDao.getEntityNameByEtalonId(etalonIds.stream().findFirst().orElse(null));

            Map<String, String> attributeWinnersMap = etalonComposer.getAttributeWinnersMap(map, entityName);

            List<CalculableHolder<OriginRecord>> input = map.values().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            DataRecord result = etalonComposer.compose(EtalonCompositionDriverType.BVT, input, false, true);

            if (result != null) {
                ExtendedRecord extendedRecord = new ExtendedRecord(result, entityName);
                extendedRecord.addAllWinnersAttributes(attributeWinnersMap);
                return extendedRecord;
            } else {
                return null;
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Internal etalon loading method.
     *
     * @param etalonId the id
     * @param asOf the date
     * @param lud the last update date
     * @param updatedAfter has versions after
     * @param operationId the operation id
     * @param includeInactive include inactive or not
     * @param viewDraft include draft versions or not
     * @return Etalon record and its origins as calculable objects
     */
    public Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> loadEtalonDataFull(
            String etalonId, Date asOf, Date lud, Date updatedAfter, String operationId, boolean includeInactive, boolean viewDraft) {
        String user = SecurityUtils.getCurrentUserName();
        return loadEtalonDataFull(etalonId, asOf, lud, updatedAfter, operationId, includeInactive, viewDraft, false, user);
    }

    /**
     * Internal etalon loading method.
     *
     * @param etalonId the id
     * @param asOf the date
     * @param lud the last update date
     * @param updatedAfter has versions after
     * @param operationId the operation id
     * @param includeInactive include inactive or not
     * @param viewDraft include draft versions or not
     * @param userName
     * @return Etalon record and its origins as calculable objects
     */
    public Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> loadEtalonDataFull(
            String etalonId, Date asOf, Date lud, Date updatedAfter, String operationId, boolean includeInactive, boolean viewDraft, boolean includeWinners, String userName) {

        MeasurementPoint.start();
        try {

            List<CalculableHolder<OriginRecord>> input
                    = originRecordsComponent.loadOriginsCalculables(etalonId, asOf, lud, updatedAfter, operationId, viewDraft, userName);
            if (CollectionUtils.isNotEmpty(input)) {
                // UN-2327
                DataRecord result = etalonComposer.compose(EtalonCompositionDriverType.BVT, input, includeInactive, includeWinners);
                if (result != null) {
                    //UN-1396
                    EtalonRecord etalon = loadEtalonState(etalonId, asOf, result, includeInactive, viewDraft, includeWinners);
                    return new ImmutablePair<>(etalon, input);
                }

                return new ImmutablePair<>(null, input);
            }

        } finally {
            MeasurementPoint.stop();
        }

        return null;
    }

    /**
     * Runs periods calculations.
     *
     * @param parentCtx parent context
     * @param affected affected intevals
     * @return result
     */
    public List<UpsertRequestContext> calculatePeriods(UpsertRequestContext parentCtx, List<TimeIntervalDTO> affected) {

        final CountDownLatch latch = affected.size() > 1 ? new CountDownLatch(affected.size()) : null;
        final List<UpsertRequestContext> periods = new ArrayList<>(affected.size());
        final Boolean isPublished = parentCtx.getFromStorage(StorageId.DATA_UPSERT_IS_PUBLISHED);

        try {
            for (TimeIntervalDTO interval : affected) {

                UpsertRequestContext periodCtx = UpsertRequestContext.builder()
                        .validFrom(interval.getValidFrom())
                        .validTo(interval.getValidTo())
                        .recalculateWholeTimeline(parentCtx.isRecalculateWholeTimeline())
                        .restore(parentCtx.isRestore())
                        .bypassExtensionPoints(parentCtx.isBypassExtensionPoints())
                        .skipCleanse(parentCtx.isSkipCleanse())
                        .skipConsistencyChecks(parentCtx.isSkipConsistencyChecks())
                        .includeDraftVersions(parentCtx.isIncludeDraftVersions() || (isPublished != null && !isPublished))
                        .skipMatchingPreprocessing(parentCtx.isSkipMatchingPreprocessing())
                        .build();

                periodCtx.repeatNotificationBehavior(parentCtx);
                periodCtx.setOperationId(parentCtx.getOperationId());

                ContextUtils.userCopy(parentCtx, periodCtx);
                ContextUtils.storageCopy(parentCtx, periodCtx,
                        StorageId.DATA_UPSERT_KEYS,
                        StorageId.DATA_UPSERT_EXACT_ACTION,
                        StorageId.DATA_UPSERT_ORIGIN_RECORD,
                        StorageId.DATA_UPSERT_WF_ASSIGNMENTS,
                        StorageId.DATA_UPSERT_IS_PUBLISHED,
                        StorageId.DATA_UPSERT_RECORD_TIMESTAMP,
                        StorageId.DATA_UPSERT_VISTORY_OPERATION_TYPE,
                        StorageId.DATA_RECORD_TIMELINE);

                periodCtx.putToStorage(StorageId.DATA_UPSERT_WORKFLOW_INTERVAL, interval);
                periods.add(periodCtx);

                if (affected.size() > 1) {
                    etalonIntervalCalculationExecutor.execute(new EtalonUpsertRunnable(periodCtx, this, latch));
                } else {
                    upsertEtalonPeriod(periodCtx);
                }
            }

            if (latch != null) {
                latch.await();
            }

        } catch (InterruptedException ie) {
            LOGGER.warn("Calculate etalon. Interrupted exception caught!", ie);
            Thread.currentThread().interrupt();
        }

        return periods;
    }

    /**
     * Basically envelopes origin data into etalon info section for new records.
     *
     * @param ctx the context to process
     * @return prepared context
     */
    private void calculateInplacePeriodForInsertAction(UpsertRequestContext ctx) {

        OriginRecord record = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
        Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);
        RecordKeys keys = ctx.keys();

        EtalonRecordInfoSection is = new EtalonRecordInfoSection()
                .withPeriodId(Objects.isNull(ctx.getValidTo()) ? ValidityPeriodUtils.TIMELINE_MAX_PERIOD_ID : ctx.getValidTo().getTime())
                .withValidFrom(ctx.getValidFrom())
                .withValidTo(ctx.getValidTo())
                .withCreateDate(ts)
                .withUpdateDate(ts)
                .withEntityName(keys.getEntityName())
                .withStatus(keys.getEtalonStatus())
                .withApproval(keys.getEtalonState())
                .withCreatedBy(SecurityUtils.getCurrentUserName())
                .withUpdatedBy(SecurityUtils.getCurrentUserName())
                .withEtalonKey(keys.getEtalonKey());

        EtalonRecord inplaceResult = new EtalonRecordImpl()
                .withInfoSection(is)
                .withDataRecord(record);

        List<CalculableHolder<OriginRecord>> records = new ArrayList<>(1);
        records.add(new DataRecordHolder(record));

        boolean isPenging = ApprovalState.PENDING == keys.getEtalonState();

        WorkflowTimeIntervalDTO interval = new WorkflowTimeIntervalDTO(ctx.getValidFrom(), ctx.getValidTo(), 1, !isPenging, isPenging);
        if (isPenging) {
            OriginRecordInfoSection infoSection = record.getInfoSection();
            ContributorDTO cdto = new ContributorDTO(infoSection.getOriginKey().getId(),
                    infoSection.getRevision(),
                    infoSection.getOriginKey().getSourceSystem(),
                    keys.getEtalonStatus().toString(),
                    keys.getEtalonState().toString(),
                    infoSection.getCreatedBy(),
                    infoSection.getCreateDate(),
                    keys.getEntityName(),
                    ctx.getFromStorage(StorageId.DATA_UPSERT_VISTORY_OPERATION_TYPE));
            interval.getPendings().add(cdto);
        }

        WorkflowTimelineDTO timeline = new WorkflowTimelineDTO(keys.getEtalonKey().getId(), isPenging, !isPenging);
        timeline.getIntervals().add(interval);

        ctx.putToStorage(StorageId.DATA_UPSERT_WORKFLOW_INTERVAL, interval);
        ctx.putToStorage(StorageId.DATA_RECORD_TIMELINE, timeline);
        ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_BASE, records);
        ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_RECORD, inplaceResult);
    }

    /**
     * Index collected periods.
     *
     * @param ctx the context
     * @param iCtx collected updates
     */
    private void indexPeriods(UpsertRequestContext ctx, IndexRequestContext iCtx) {

        if (ctx.isBatchUpsert()) {
            RecordBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RECORDS);
            batchSet.setIndexRequestContext(iCtx);
            return;
        }

        searchService.index(iCtx);
    }

    /**
     * @param keys - keys.
     * @param operationId - operation id;
     * @return map where key is etalon id, and value is a collection of classifier etalons
     */
    @Nonnull
    private Collection<EtalonClassifier> getClassifierEtalonsForEtalon(RecordKeys keys, String operationId) {

        String entityName = keys.getEntityName();
        List<String> classifierNames = modelService.getClassifiersForEntity(entityName);

        if (CollectionUtils.isEmpty(classifierNames)) {
            return Collections.emptyList();
        }

        String etalonId = keys.getEtalonKey().getId();

        GetClassifiersDataRequestContext clsfCtx = GetClassifiersDataRequestContext.builder()
                .etalonKey(etalonId)
                .classifierNames(classifierNames)
                .build();

        clsfCtx.setOperationId(operationId);
        clsfCtx.putToStorage(clsfCtx.keysId(), keys);

        GetClassifiersDTO result = classifiersComponent.getClassifiers(clsfCtx);
        return result.getClassifiers()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(GetClassifierDTO::getEtalon)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Loads (calculates) etalon for a date and for last update date.
     *
     * @param etalonId the etalon id
     * @param asOf date on the time line
     * @param lud last update date
     * @param updatesAfter has updates after
     * @param operationId the operation id
     * @param includeInactive include inactive or not
     * @param viewDraft show draft versions, if exist (or not)
     * @return record or null
     */
    public EtalonRecord loadEtalonData(String etalonId, Date asOf, Date lud, Date updatesAfter, String operationId,
                                       boolean includeInactive, boolean viewDraft) {

        Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> result
                = loadEtalonDataFull(etalonId, asOf, lud, updatesAfter, operationId, includeInactive, viewDraft);
        return result != null ? result.getKey() : null;
    }

    /**
     * Does processing of bulk of etalons.
     * Basically, does the following things for all affected periods of all etalons:<ul>
     * <li>executes DQ rules,</li>
     * <li>does possible origin upsert,</li>
     * <li>updates Elasticsearch state</li>
     * </ul>
     * <p>
     * The contexts must return {@linkplain UpsertRequestContext#isBatchUpsert()} == true for this method to succeed.
     *
     * @param accumulator the accumulator
     */
    public void batchUpsertEtalons(BatchSetAccumulator<UpsertRequestContext> accumulator) {

        MeasurementPoint.start();
        try {
            for (BatchIterator<UpsertRequestContext> li = accumulator.iterator(BatchSetIterationType.UPSERT_ETALONS); li.hasNext(); ) {

                UpsertRequestContext ctx = li.next();
                try {

                    upsertEtalon(ctx);

                    if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
                        auditEventsWriter.writeSuccessEvent(AuditActions.DATA_UPSERT, ctx);
                    }

                    ctx.putToStorage(StorageId.DATA_BATCH_ACCEPT, Boolean.TRUE);
                } catch (Exception e) {
                    if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
                        auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT, e, ctx);
                    }

                    if (accumulator.isAbortOnFailure()) {
                        throw e;
                    }

                    li.remove();
                }
            }

            recordBatchSetProcessor.applyEtalons(accumulator);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Performs a batch delete on a bunch of contexts.
     * The contexts must return {@linkplain DeleteRequestContext#isBatchUpsert()} == true for this method to succeed.
     * The method must be called in transactional context.
     *
     * @param accumulator the accumulator
     */
    public void batchDeleteEtalons(BatchSetAccumulator<DeleteRequestContext> accumulator) {
        MeasurementPoint.start();
        try {

            // 1. Process contexts
            for (BatchIterator<DeleteRequestContext> it = accumulator.iterator(BatchSetIterationType.DELETE_ETALONS); it.hasNext(); ) {

                DeleteRequestContext ctx = it.next();
                try {

                    deleteEtalon(ctx);

                    if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
                        auditEventsWriter.writeSuccessEvent(AuditActions.DATA_DELETE, ctx);
                    }
                } catch (Exception e) {
                    if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
                        auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_DELETE, e, ctx);
                    }

                    it.remove();
                }
            }

            // 2. Apply the result
            recordBatchSetProcessor.applyEtalons(accumulator);

        } catch (Exception exc) {
            LOGGER.warn("Batch DELETE caught an exception", exc);
            throw exc;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Does processing of an etalon.
     * Basically, does the following things for all affected periods:<ul>
     * <li>executes DQ rules,</li>
     * <li>does possible origin upsert,</li>
     * <li>updates Elasticsearch state</li>
     * </ul>
     *
     * @param ctx the context
     */
    public void upsertEtalon(final UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            List<UpsertRequestContext> periods = null;
            List<RecordIndexId> deletes = null;

            // 1. Return etalon from last origin immediately
            // if this is the only new period and no other data is present yet
            UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
            if (action == UpsertAction.INSERT) {

                calculateInplacePeriodForInsertAction(ctx);
                upsertEtalonPeriod(ctx);
                periods = Collections.singletonList(ctx);
                deletes = Collections.emptyList();

                // 2. Run calculations otherwise
            } else {

                Pair<List<TimeIntervalDTO>, List<TimeIntervalDTO>> affected = processTimeline(ctx);
                if (Objects.nonNull(affected) && CollectionUtils.isNotEmpty(affected.getLeft())) {
                    periods = calculatePeriods(ctx, affected.getLeft());
                }

                if (Objects.nonNull(affected) && CollectionUtils.isNotEmpty(affected.getRight())) {
                    deletes = collectDeletes(ctx, affected.getRight());
                }
            }

            // 3. Collect updates, prepare notifications
            IndexRequestContext iCtx = collectUpdates(ctx, periods, deletes);

            // 4. Index etalons
            if (Objects.nonNull(iCtx) && !ctx.isRestore()) {
                indexPeriods(ctx, iCtx);
            }

            // 5. todo move to after executor
            upsertMatching(ctx);

            // 6. Run "after all" actions
            if (!ctx.isBypassExtensionPoints() && action != UpsertAction.NO_ACTION) {

                RecordKeys keys = ctx.keys();
                EtalonRecord etalon = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
                UpsertImpl ui = configurationService.getUpsert();
                if (Objects.nonNull(ui)) {
                    Collection<UpsertListener> listeners = configurationService.getListeners(
                            keys.getEntityName(),
                            ui.getAfterCompleteInstances());
                    if (CollectionUtils.isNotEmpty(listeners)) {
                        for (UpsertListener l : listeners) {
                            l.afterComplete(etalon, ctx);
                        }
                    }
                }
                DaoHelper.executeAfterCommitAction(ctx.getFinalizeExecutors(), ctx);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    private void upsertMatching(final UpsertRequestContext ctx) {
        if (ctx.isSkipMatching()) {
            return;
        }
        EtalonRecord etalon = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);

        if (etalon == null
                || etalon.getInfoSection().getStatus() != RecordStatus.ACTIVE
                || etalon.getInfoSection().getApproval() != ApprovalState.APPROVED) {
            return;
        }

        // todo optimize this
        clusterService.excludeFromClusters(etalon.getInfoSection().getEntityName(),
                Collections.singletonList(etalon.getInfoSection().getEtalonKey().getId()));

        Collection<Cluster> clusters = clusterService.searchNewClusters(etalon, new Date(), null, false);
        if (CollectionUtils.isNotEmpty(clusters)) {
            clusters.forEach(cluster -> clusterService.upsertCluster(cluster, true, true));
        }

    }

    public Pair<List<TimeIntervalDTO>, List<TimeIntervalDTO>> processTimeline(final UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Check keys
            RecordKeys keys = ctx.keys();
            if (keys == null) {
                keys = commonComponent.identify(ctx);
                ctx.putToStorage(ctx.keysId(), keys);
            }

            // 2. Load timeline otherwise
            final GetRequestContext gCtx = new GetRequestContextBuilder().build();
            gCtx.putToStorage(gCtx.keysId(), keys);

            WorkflowTimelineDTO intervals = originRecordsComponent.loadWorkflowTimeline(gCtx, true);
            if (intervals == null || intervals.getIntervals().isEmpty()) {
                return null;
            }

            // fetch whole approved time line in case when 'include draft versions' is true
            // and we want to re-index data
            // but this line has pending versions which we should skip in final time line.
            if (ctx.isIncludeDraftVersions() && ctx.isRestore() && intervals.isPending() && intervals.isPublished()) {
                WorkflowTimelineDTO approvedIntervals = originRecordsComponent.loadWorkflowTimeline(gCtx, false);
                intervals.getIntervals().clear();
                intervals.getIntervals().addAll(approvedIntervals.getIntervals());
            }

            // 3. Save timeline for cheldren executions.
            ctx.putToStorage(StorageId.DATA_RECORD_TIMELINE, intervals);

            // 4. Identify affected intervals (new, old)
            return identifyAffectedIntervals(ctx, intervals);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * identify affected intervals
     *
     * @param ctx - upsert context
     * @param intervals - intervals
     * @return affected intervals (new + old, scheduled for deletion)
     */
    private Pair<List<TimeIntervalDTO>, List<TimeIntervalDTO>>
    identifyAffectedIntervals(UpsertRequestContext ctx, WorkflowTimelineDTO intervals) {

        List<TimeIntervalDTO> affectedNew = new ArrayList<>(intervals.getIntervals().size());
        List<TimeIntervalDTO> affectedOld = new ArrayList<>();

        WorkflowTimelineDTO previousTimeline = ctx.getFromStorage(StorageId.DATA_UPSERT_PREVIOUS_TIMELINE);
        if (ctx.isRecalculateWholeTimeline()) {

            affectedNew.addAll(intervals.getIntervals());
            if (Objects.nonNull(previousTimeline) && CollectionUtils.isNotEmpty(previousTimeline.getIntervals())) {
                affectedOld.addAll(previousTimeline.getIntervals());
            }

        } else {

            if (Objects.nonNull(previousTimeline) && CollectionUtils.isNotEmpty(previousTimeline.getIntervals())) {

                for (int i = 0; i < previousTimeline.getIntervals().size(); i++) {

                    TimeIntervalDTO prevInterval = previousTimeline.getIntervals().get(i);
                    if (prevInterval.getValidTo() == null || ctx.getValidFrom() == null || !prevInterval.getValidTo().before(ctx.getValidFrom())
                            && (prevInterval.getValidFrom() == null || ctx.getValidTo() == null || !prevInterval.getValidFrom().after(ctx.getValidTo()))) {
                        affectedOld.add(prevInterval);
                    } else {
                        if (!affectedOld.isEmpty()) {
                            break;
                        }
                    }
                }
            }

            boolean fromMatches = false;
            boolean toMatches = false;

            Date affectedFrom = affectedOld.isEmpty()
                    ? ctx.getValidFrom()
                    : ValidityPeriodUtils.leastFrom(affectedOld.get(0).getValidFrom(), ctx.getValidFrom());
            Date affectedTo = affectedOld.isEmpty()
                    ? ctx.getValidTo()
                    : ValidityPeriodUtils.mostTo(affectedOld.get(affectedOld.size() - 1).getValidTo(), ctx.getValidTo());
            for (int i = 0; i < intervals.getIntervals().size(); i++) {

                TimeIntervalDTO interval = intervals.getIntervals().get(i);

                // Order of arguments to Objects.equals(...) matters,
                // since interval.getValid*() is a java.sql.Timestamp
                // and ctx.getValid*() is a java.util.Date!
                if (!fromMatches) {
                    fromMatches = Objects.equals(affectedFrom, interval.getValidFrom());
                }

                if (fromMatches) {
                    // Add current
                    affectedNew.add(interval);
                }

                toMatches = Objects.equals(affectedTo, interval.getValidTo());
                if (toMatches) {
                    break;
                }
            }
        }

        return new ImmutablePair<>(affectedNew, affectedOld);
    }

    private List<RecordIndexId> collectDeletes(UpsertRequestContext ctx, List<TimeIntervalDTO> deletes) {

        if (CollectionUtils.isEmpty(deletes)) {
            return Collections.emptyList();
        }

        RecordKeys keys = ctx.keys();
        return deletes.stream()
                .map(interval -> RecordIndexId.of(keys.getEntityName(), keys.getEtalonKey().getId(), interval.getPeriodId()))
                .collect(Collectors.toList());
    }

    /**
     * @param ctx - upsert context
     * @param periods - affected periods
     * @param deleteIds - delete ids
     * @return map where key is etalon and value is map for indexing!
     */
    public IndexRequestContext collectUpdates(UpsertRequestContext ctx, List<UpsertRequestContext> periods, List<RecordIndexId> deleteIds) {

        if (periods.isEmpty()) {
            return null;
        }

        RecordKeys keys = ctx.keys();
        Map<EtalonRecord, Map<? extends SearchField, Object>> records = new IdentityHashMap<>(periods.size());
        Map<EtalonRecord, ClusterSet> clusters = new IdentityHashMap<>(periods.size());
        Map<EtalonRecord, List<DataQualityError>> errors = new IdentityHashMap<>(periods.size());
        for (UpsertRequestContext pCtx : periods) {

            EtalonRecord etalon = pCtx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);

            // Identify trigger etalon period
            boolean fromMatches = Objects.equals(ctx.getValidFrom(), pCtx.getValidFrom());
            boolean toMatches = Objects.equals(ctx.getValidTo(), pCtx.getValidTo());
            boolean isTriggerPeriod = fromMatches && toMatches;

            // Collect delete / update ES data
            if (etalon != null) {

                // Record data
                Map<? extends SearchField, Object> fields = pCtx.getFromStorage(StorageId.DATA_UPSERT_ETALON_INDEX_UPDATE);
                if (fields != null) {
                    records.put(etalon, fields);
                }

                // Match clusters
                Collection<Cluster> periodClusters = pCtx.getFromStorage(StorageId.DATA_UPSERT_ETALON_MATCHING_UPDATE);
                if (!CollectionUtils.isEmpty(periodClusters)) {
                    clusters.put(etalon, new ClusterSet(periodClusters, pCtx.getValidFrom(), pCtx.getValidTo()));
                }

                // Copy errors
                // For INSERT the original context and period context are the same
                // Skip upon pointer equality
                if (isTriggerPeriod && pCtx != ctx) {

                    // UN-7494
                    // Add errors to parent ctx to be shown on UI
                    ctx.getDqErrors().addAll(pCtx.getDqErrors());

                    // Copy data
                    ContextUtils.storageCopy(pCtx, ctx, StorageId.DATA_UPSERT_ETALON_RECORD);

                    //UN-8218 Copy process errors
                    List<ErrorInfoDTO> pCtxErrors = pCtx.getFromStorage(StorageId.PROCESS_ERRORS);
                    if (CollectionUtils.isNotEmpty(pCtxErrors)) {
                        List<ErrorInfoDTO> ctxErrors = ctx.getFromStorage(StorageId.PROCESS_ERRORS);
                        if (ctxErrors == null) {
                            ctx.putToStorage(StorageId.PROCESS_ERRORS, pCtxErrors);
                        } else {
                            ctxErrors.addAll(pCtxErrors);
                        }
                    }

                    pCtx.getFinalizeExecutors().forEach(ctx::addFinalizeExecutor);
                }

                errors.computeIfAbsent(etalon, key -> new ArrayList<>(isTriggerPeriod ? ctx.getDqErrors().size() : pCtx.getDqErrors().size()))
                        .addAll(isTriggerPeriod ? ctx.getDqErrors() : pCtx.getDqErrors());
            }
        }

        if (records.isEmpty()) {
            return null;
        }

        return IndexRequestContext.builder()
                .drop(UpsertAction.INSERT != ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION))
                .entity(keys.getEntityName())
                .records(records)
                .clusters(clusters)
                .recordsToDelete(deleteIds)
                .routing(keys.getEtalonKey().getId())
                .dqErrors(errors)
                .refresh(!ctx.isBatchUpsert() && refreshImmediate)
                .build();
    }

    /**
     * Recalculate etalon.
     *
     * @param ctx the context
     */
    public void upsertEtalonPeriod(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Fetch keys
            RecordKeys keys = ctx.keys();
            if (keys == null) {
                keys = commonComponent.identify(ctx);
            }

            // 2. Calculate etalon
            // For INSERT action the fields DATA_UPSERT_ETALON_RECORD and DATA_UPSERT_ETALON_BASE
            // have to be set already.
            UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
            if (action != UpsertAction.INSERT) {

                boolean isApproverView = ctx.isIncludeDraftVersions();
                if (ctx.isRestore()) {
                    WorkflowTimelineDTO timeline = ctx.getFromStorage(StorageId.DATA_RECORD_TIMELINE);
                    isApproverView = !timeline.isPublished();
                }

                Date asOf = ctx.getValidFrom() != null ? ctx.getValidFrom() : ctx.getValidTo();
                Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> result
                        = loadEtalonDataFull(keys.getEtalonKey().getId(), asOf, null, null, null,
                        ctx.isRecalculateWholeTimeline(), isApproverView);

                EtalonRecord etalon = result != null ? result.getKey() : null;
                List<CalculableHolder<OriginRecord>> calculables = result != null ? result.getValue() : null;

                // 3. Set fields to context
                ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_RECORD, etalon);
                ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_BASE, calculables);
            }

            // 4. Etalon before executor. No returned value check
            etalonCalculationActionListener.before(ctx);

            // 5. After all executor for new etalon data. No return value check is done.
            // The action may save versions possibly created by DQ and recalculate data and state,
            // if there were enrichments.
            etalonCalculationActionListener.after(ctx);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Soft delete etalon record cascading.
     *
     * @param ctx the context
     */
    public void deleteEtalon(DeleteRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        if (ctx.isInactivatePeriod() && !keys.isPending() && !ctx.isBatchUpsert()) {
            // TODO REVIEW!!!
            originComponent.loadAndSaveWorkflowTimeline(ctx,
                    StorageId.DATA_TIMELINE_AFTER,
                    StorageId.DATA_INTERVALS_AFTER, false, false, true);
        }

        // This is the only content for now.
        // Indexing data calculation, UE, etc.
        deleteRecordActionListener.after(ctx);
    }

    /**
     * Merges several etalon records (and their origins) to one master record.
     *
     * @param ctx current merge context
     * @return true if successful, false otherwise
     */
    @Transactional(rollbackFor = Exception.class)
    public MergeRecordsDTO mergeEtalons(MergeRequestContext ctx) {

        // 1. Run before executors
        boolean isOk = mergeRecordActionListener.before(ctx);
        if (!isOk) {
            final String message = "Etalon merge BEFORE executor failed.";
            throw new DataProcessingException(message, ExceptionId.EX_DATA_ETALON_MERGE_BEFORE);
        }

        RecordKeys masterKey = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS);
        List<RecordKeys> duplicatesKeys = ctx.getFromStorage(StorageId.DATA_MERGE_DUPLICATES_KEYS);

        // 2. Remove from DB
        List<String> duplicateEtalonIds = duplicatesKeys.stream()
                .map(RecordKeys::getEtalonKey)
                .map(EtalonKey::getId)
                .collect(Collectors.toList());

        boolean success = dataRecordsDao.mergeRecords(masterKey.getEtalonKey().getId(),
                duplicateEtalonIds, ctx.getOperationId(), ctx.isValidRecordKey());

        if (!success) {
            final String message = "Merge failed, storage update unsuccessful, winner [{}] duplicates [{}].";
            LOGGER.warn(message, masterKey.getEtalonKey().getId(), duplicateEtalonIds);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_MERGE_FAILED_UPDATE,
                    masterKey.getEtalonKey().getId(),
                    duplicateEtalonIds);

        }

        if (!ctx.isSkipRelations()) {
            // 3. Deactivate possibly active links
            relationsServiceComponent.mergeRelations(masterKey, duplicatesKeys, ctx.getOperationId());
        }

        if (!ctx.isSkipClassifiers()) {
            // 4. Deactivate classifier records
            classifiersComponent.mergeClassifiers(masterKey, duplicatesKeys, ctx.getOperationId());
        }

        // 5. Run after actions.
        mergeRecordActionListener.after(ctx);

        MergeRecordsDTO result = new MergeRecordsDTO(masterKey.getEtalonKey().getId(), duplicateEtalonIds);
        result.setErrors(ctx.getFromStorage(StorageId.PROCESS_ERRORS));
        return result;
    }

    public boolean splitEtalon(SplitContext splitContext) {
        SearchRequestContext removeOldSearchCtx = SearchRequestContext.forEtalonData(splitContext.getEntityName())
                .form(FormFieldsGroup.createAndGroup()
                        .addFormField(FormField.strictString(RecordHeaderField.FIELD_ETALON_ID.getField(),
                                splitContext.getOldEtalonKey().getId())))
                .routings(Collections.singletonList(splitContext.getOldEtalonKey().getId()))
                .build();

        searchService.deleteFoundResult(removeOldSearchCtx);
        final EtalonKey oldEtalonKey = splitContext.getOldEtalonKey();
        final RecordKeys oldKeys = commonComponent.identify(oldEtalonKey);
        final UpsertRequestContext oldEtalonUpdateContext = UpsertRequestContext.builder()
                .recalculateWholeTimeline(true)
                .returnEtalon(true)
                .build();
        oldEtalonUpdateContext.putToStorage(StorageId.DATA_UPSERT_KEYS, oldKeys);
        upsertEtalon(oldEtalonUpdateContext);

        final EtalonKey newEtalonKey = splitContext.getNewEtalonKey();
        final RecordKeys newKeys = commonComponent.identify(newEtalonKey);
        final UpsertRequestContext newEtalonUpdateContext = UpsertRequestContext.builder()
                .recalculateWholeTimeline(true)
                .returnEtalon(true)
                .build();
        newEtalonUpdateContext.putToStorage(StorageId.DATA_UPSERT_KEYS, newKeys);
        upsertEtalon(newEtalonUpdateContext);
        return true;
    }
}