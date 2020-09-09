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

package com.unidata.mdm.backend.service.data.origin;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.ApprovalStateSettingContext;
import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.SplitContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.ValidityRangeContext;
import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.data.DataRecordHolder;
import com.unidata.mdm.backend.common.data.ModificationBox;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.exits.ExitException;
import com.unidata.mdm.backend.common.integration.exits.ExitState;
import com.unidata.mdm.backend.common.integration.wf.EditWorkflowProcessTriggerType;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.TimeIntervalContributorInfo;
import com.unidata.mdm.backend.common.types.Timeline;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.impl.OriginRecordImpl;
import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.dao.OriginsVistoryDao;
import com.unidata.mdm.backend.po.ContributorPO;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.po.OriginsVistoryRecordPO;
import com.unidata.mdm.backend.po.TimeIntervalPO;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.data.batch.BatchIterator;
import com.unidata.mdm.backend.service.data.batch.BatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.BatchSetIterationType;
import com.unidata.mdm.backend.service.data.batch.RecordBatchSet;
import com.unidata.mdm.backend.service.data.batch.RecordBatchSetProcessor;
import com.unidata.mdm.backend.service.data.batch.RecordDeleteBatchSet;
import com.unidata.mdm.backend.service.data.batch.RecordUpsertBatchSet;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.convert.TimelineConverter;
import com.unidata.mdm.backend.service.data.driver.EtalonComposer;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordLifecycleListener;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.service.data.util.DataUtils;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Stuff, related to origins/vistory records management.
 */
@Component
public class OriginRecordsComponent {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OriginRecordsComponent.class);
    /**
     * 'Upsert' action listener qualifier name.
     */
    public static final String UPSERT_RECORD_ACTION_LISTENER_QUALIFIER = "upsertRecordActionListener";
    /**
     * 'Delete' action listener qualifier name.
     */
    public static final String DELETE_RECORD_ACTION_LISTENER_QUALIFIER = "deleteRecordActionListener";
    /**
     * 'Upsert' action listener.
     */
    @Autowired
    @Qualifier(value = UPSERT_RECORD_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<UpsertRequestContext> upsertRecordActionListener;
    /**
     * 'Delete' action listener.
     */
    @Autowired
    @Qualifier(value = DELETE_RECORD_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<DeleteRequestContext> deleteRecordActionListener;
    /**
     * Common functionality.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
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
     * Etalon composer.
     */
    @Autowired
    private EtalonComposer etalonComposer;
    /**
     * Etalon records component.
     */
    @Autowired
    private EtalonRecordsComponent etalonComponent;
    /**
     * Stalled data transformer chain.
     */
    @Autowired
    private TransformerChain transformerChain;
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
    /**
     * Constructor.
     */
    public OriginRecordsComponent() {
        super();
    }

    /**
     * Fills calculated fields for a vistory record.
     * @param ovr the vistory record
     */
    private OriginRecordInfoSection fillOriginRecordInfoSection(OriginsVistoryRecordPO ovr) {

        MeasurementPoint.start();
        try {

            return new OriginRecordInfoSection()
                .withValidFrom(ovr.getValidFrom())
                .withValidTo(ovr.getValidTo())
                .withCreateDate(ovr.getCreateDate())
                .withUpdateDate(ovr.getUpdateDate())
                .withCreatedBy(ovr.getCreatedBy())
                .withUpdatedBy(ovr.getUpdatedBy())
                .withRevision(ovr.getRevision())
                .withStatus(ovr.getStatus())
                .withApproval(ovr.getApproval())
                .withShift(ovr.getShift())
                .withMajor(ovr.getMajor())
                .withMinor(ovr.getMinor())
                .withOriginKey(
                    OriginKey.builder()
                        .id(ovr.getOriginId())
                        .entityName(ovr.getName())
                        .externalId(ovr.getExternalId())
                        .sourceSystem(ovr.getSourceSystem())
                        .revision(ovr.getRevision())
                        .gsn(ovr.getGsn())
                        .build());

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets origin vistory data for a date (now, if null).
     * @param originId the origin id
     * @param asOf the date
     * @param unpublishedView include draft versions into view
     * @return data or null
     */
    public OriginRecord loadOriginData(String originId, Date asOf, boolean unpublishedView) {
        MeasurementPoint.start();
        try {
            OriginsVistoryRecordPO ovr = originsVistoryDao.loadVersion(originId, asOf, unpublishedView);
            if (ovr != null) {

                OriginRecordImpl origin = new OriginRecordImpl()
                        .withDataRecord(ovr.getData())
                        .withInfoSection(fillOriginRecordInfoSection(ovr));

                // Possibly fix stalled records
                transformerChain.getTransformerChain().transform(origin);

                return origin;
            }

            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Loads origin versions for a date (now, if null) and last update date earlier then lud.
     * @param etalonId the etalon id
     * @param asOf date on the time line
     * @param lud last update date
     * @param updatedAfter has versions updated after
     * @param operationId the operation id
     * @param isApproverView - is approver
     * @param userName userName
     * @return origins list
     */
    public List<OriginRecord> loadOrigins(
            String etalonId, Date asOf, Date lud, Date updatedAfter, String operationId, boolean isApproverView, String userName) {

        MeasurementPoint.start();
        try {

            List<OriginsVistoryRecordPO> versions;
            if (Objects.nonNull(operationId)) {
                versions = originsVistoryDao.loadVersionsByOperationId(etalonId, asOf, operationId, isApproverView, userName);
            } else if (Objects.nonNull(lud)) {
                versions = originsVistoryDao.loadVersionsByLastUpdateDate(etalonId, asOf, lud, isApproverView, userName);
            } else if (Objects.nonNull(updatedAfter)) {
                versions = originsVistoryDao.loadVersionsByUpdatesAfter(etalonId, asOf, updatedAfter, isApproverView, userName);
            } else {
                versions = originsVistoryDao.loadVersions(etalonId, asOf, isApproverView, userName);
            }

            if (CollectionUtils.isEmpty(versions)) {
                return Collections.emptyList();
            }

            List<OriginRecord> records = new ArrayList<>();
            for (OriginsVistoryRecordPO po : versions) {

                OriginRecordImpl origin = new OriginRecordImpl()
                        .withDataRecord(po.getData())
                        .withInfoSection(fillOriginRecordInfoSection(po));

                // Possibly fix stalled records
                transformerChain.getTransformerChain().transform(origin);

                records.add(origin);
            }

            return records;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Loads origin versions for a date (now, if null) and last update date earlier then lud.
     * @param etalonId the etalon id
     * @param asOf date on the time line
     * @param lud last update date
     * @param updatedAfter has versions updated after
     * @param operationId the operation id
     * @param isApproverView - is approver
     * @param userName userName
     * @return origins list
     */
    public List<CalculableHolder<OriginRecord>> loadOriginsCalculables(
            String etalonId, Date asOf, Date lud, Date updatedAfter, String operationId, boolean isApproverView, String userName) {

        List<OriginRecord> originRecords = loadOrigins(etalonId, asOf, lud, updatedAfter, operationId, isApproverView, userName);
        if(CollectionUtils.isNotEmpty(originRecords)){
            return originRecords.stream()
                    .map(DataRecordHolder::new)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     * Checks the need for calculating origin diff.
     * @param ctx the context
     * @return true, if diff has to be calculated, false otherwise
     */
    private boolean isOriginDiffCalculation(UpsertRequestContext ctx) {

        UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
        RecordKeys keys = ctx.keys();
        return action == UpsertAction.UPDATE && metaModelService.isAdminSourceSystem(keys.getOriginKey().getSourceSystem());
    }
    /**
     * Returns diff origin if the data has been submitted for the admin source system
     * and partial update is forced.
     * @param ctx the context
     * @param origin TODO
     * @return diff origin or null
     */
    private OriginRecord calculateOriginDiff(UpsertRequestContext ctx, OriginRecord origin) {

        RecordKeys keys = ctx.keys();
        Date recordFrom = ctx.getValidFrom();
        Date recordTo = ctx.getValidTo();
        Date asOf = nonNull(recordFrom) ? recordFrom : recordTo;
        Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> prev
            = etalonComponent.loadEtalonDataFull(keys.getEtalonKey().getId(), asOf, null, null, null, false, true);

        //case when we create new period!
        if (isNull(prev) || isNull(prev.getKey())) {
            return origin;
        }

        EtalonRecord etalonRecord = prev.getKey();
        Date from = etalonRecord.getInfoSection().getValidFrom();
        Date to = etalonRecord.getInfoSection().getValidTo();

        //check overlapping. If record range cover more than one period(or period and a void) it is a overlapping.
        boolean fromMatches = Objects.equals(recordFrom, from);
        boolean toMatches = Objects.equals(recordTo, to);
        if (!fromMatches || !toMatches) {
            return origin;
        }

        OriginRecord prevBase = null;
        for (CalculableHolder<OriginRecord> ch : prev.getValue()) {
            if (keys.getOriginKey().getSourceSystem().equals(ch.getSourceSystem())
            &&  keys.getOriginKey().getExternalId().equals(ch.getExternalId())) {
                prevBase = ch.getValue();
                break;
            }
        }

        DataRecord diff = DataUtils.simpleDataDiff(keys.getEntityName(), origin, prev.getKey(), prevBase);
        if (diff != null) {
            return new OriginRecordImpl()
                    .withDataRecord(diff)
                    .withInfoSection(origin.getInfoSection());
        }

        return null;
    }

    /**
     * Performs a batch upsert on a bunch of contexts.
     * The contexts must return {@linkplain UpsertRequestContext#isBatchUpsert()} == true for this method to succeed.
     * The method must be called in transactional context.
     * @param accumulator the accumulator
     */
    public void batchUpsertOrigins(BatchSetAccumulator<UpsertRequestContext> accumulator) {

        MeasurementPoint.start();
        try {

            boolean hasEtalonPhase = accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ETALONS);
            for (BatchIterator<UpsertRequestContext> li = accumulator.iterator(BatchSetIterationType.UPSERT_ORIGINS); li.hasNext(); ) {

                UpsertRequestContext ctx = li.next();
                try {
                    upsertOriginNoTransaction(ctx);
                    if (!hasEtalonPhase && !ctx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
                        auditEventsWriter.writeSuccessEvent(AuditActions.DATA_UPSERT, ctx);
                    }

                    if (!hasEtalonPhase) {
                        ctx.putToStorage(StorageId.DATA_BATCH_ACCEPT, Boolean.TRUE);
                    }
                } catch (Exception e) {
                    if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
                        auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT, e, ctx);
                    }

                    if(accumulator.isAbortOnFailure()){
                        throw e;
                    }

                    li.remove();
                }
            }

            recordBatchSetProcessor.applyOrigins(accumulator);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Performs a batch delete on a bunch of contexts.
     * The contexts must return {@linkplain DeleteRequestContext#isBatchUpsert()} == true for this method to succeed.
     * The method must be called in transactional context.
     * @param accumulator the accumulator
     */
    public void batchDeleteOrigins(BatchSetAccumulator<DeleteRequestContext> accumulator) {
        MeasurementPoint.start();
        try {

            boolean hasOriginPhase = accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.DELETE_ORIGINS);
            for (BatchIterator<DeleteRequestContext> it = accumulator.iterator(BatchSetIterationType.DELETE_ORIGINS); it.hasNext(); ) {

                DeleteRequestContext ctx = it.next();
                try {
                    deleteOrigin(ctx);
                    if (!hasOriginPhase && !ctx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
                        auditEventsWriter.writeSuccessEvent(AuditActions.DATA_DELETE, ctx);
                    }
                } catch (Exception e) {
                    if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
                        auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_DELETE, e, ctx);
                    }

                    it.remove();
                }
            }

            recordBatchSetProcessor.applyOrigins(accumulator);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Does real origins upsert.
     * @param ctx the context
     */
    private void upsertOriginImpl(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Run pre-upsert.
            boolean isOk = upsertRecordActionListener.before(ctx);
            if (!isOk) {
                final String message = "Origin upsert BEFORE executor failed.";
                throw new DataProcessingException(message, ExceptionId.EX_DATA_ORIGIN_UPSERT_NEW_DQ_FAILED_BEFORE);
            }

            // 2. Check and create etalon/origin records if necessary
            ensureSystemRecords(ctx);

            // 3. Upsert 'PRISTINE' origin. Skip this for enrichments
            // TODO Temporary, until full switch to ModBox
            OriginRecord origin = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);

            // Upsert origin enrichments
            ModificationBox box = ctx.getFromStorage(StorageId.DATA_UPSERT_MODIFICATION_BOX);
            boolean hasDQModified = box.count(ModificationBox.toBoxKey(ctx)) > 1;
            if (hasDQModified) {

                // The cause record shouldn't be returned with this
                List<CalculableHolder<DataRecord>> versions = box.reset(ModificationBox.toBoxKey(ctx));
                if (CollectionUtils.isNotEmpty(versions)) {
                    OriginRecordInfoSection is = origin.getInfoSection();
                    origin = (OriginRecord) versions.get(versions.size() - 1).getValue();
                    ((OriginRecordImpl) origin).withInfoSection(is);
                }
                // UN-8846
                /*
                for (CalculableHolder<DataRecord> version : versions) {
                    origin = (OriginRecord) version.getValue();
                    putVersion(ctx, origin, origin.getInfoSection().getShift());

                    // TODO Temporary, until full switch to ModBox
                    origin.getInfoSection().withRevision(origin.getInfoSection().getRevision() + 1);
                }
                */
            }

            // 3.1. Generate diff for admin source system upserts only if mergeWithPreviousVersion flag is not set
            boolean isOriginDiffCalculation = isOriginDiffCalculation(ctx);
            if (isOriginDiffCalculation) {
                origin = calculateOriginDiff(ctx, origin);

                // 3.1.1 No differences. Cancel currently running stack.
                if (origin == null) {
                    ctx.putToStorage(StorageId.DATA_UPSERT_EXACT_ACTION, UpsertAction.NO_ACTION);
                    LOGGER.info("No changes by admin source system detected. Upsert skipped.");
                    return;
                }
            }

            putVersion(ctx, origin, DataShift.PRISTINE);

            // TODO Temporary, until full switch to ModBox
            origin.getInfoSection().withRevision(origin.getInfoSection().getRevision() + 1);

            // Save
            ctx.putToStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD, origin);

            // 3.2. Run post-upsert
            upsertRecordActionListener.after(ctx);

            // 4. Possibly upsert 'REVISED' origin(s)
            boolean hasUEModified = Boolean.TRUE.equals(ctx.getFromStorage(StorageId.DATA_UPSERT_IS_MODIFIED));
            if (hasUEModified) {
                origin = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
                putVersion(ctx, origin, DataShift.REVISED);
            }

            // 5. Return
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
    /**
     * Upserts a record.
     * @param ctx
     *            the request context
     */
    @Transactional(rollbackFor = Exception.class)
    public void upsertOrigin(UpsertRequestContext ctx) {
        upsertOriginImpl(ctx);
    }
    /**
     * Does origin upsert bypassing transaction. Read only queries are expected to be executed here.
     * @param ctx the request context
     */
    public void upsertOriginNoTransaction(UpsertRequestContext ctx) {
        upsertOriginImpl(ctx);
    }
    /**
     * Puts a version to DB.
     * @param ctx the context
     * @param data the data to save, may be null for deletes
     * @param shift data shift (PRISTINE/REVISED), may be null for deletes
     */
    public void putVersion(RecordIdentityContext ctx, OriginRecord data, DataShift shift) {

        RecordKeys keys = ctx.keys();
        boolean isUpsert = ctx instanceof UpsertRequestContext;
        boolean isBatch = false;

        OriginsVistoryRecordPO version;
        if (isUpsert) {

            UpsertRequestContext uCtx = (UpsertRequestContext) ctx;
            version = DataRecordUtils.createVistoryRecordPO(data, uCtx, shift);
            isBatch = uCtx.isBatchUpsert();
        } else {

            DeleteRequestContext dCtx = (DeleteRequestContext) ctx;
            Date mark = dCtx.getValidFrom() != null ? dCtx.getValidFrom() : dCtx.getValidTo();
            OriginRecord lastVersion = data != null
                    ? data
                    : loadOriginData(keys.getOriginKey().getId(), mark, false);

            version = DataRecordUtils.createInactiveVistoryRecordPO(keys.getOriginKey().getId(),
                    dCtx.getValidFrom(),
                    dCtx.getValidTo(), lastVersion, dCtx);

            isBatch = dCtx.isBatchUpsert();
        }

        if (isBatch) {

            RecordBatchSet batchSet = ((CommonRequestContext) ctx).getFromStorage(StorageId.DATA_BATCH_RECORDS);
            batchSet.getOriginsVistoryRecordPOs().add(version);
            return;
        }

        originsVistoryDao.putVersion(version);

        // Reload keys if needed
        if (version.getApproval() == ApprovalState.PENDING) {
           dataRecordsDao.changeEtalonApproval(keys.getEtalonKey().getId(), ApprovalState.PENDING);
           commonComponent.possiblyResetApprovalState(((CommonRequestContext) ctx), ctx.keysId(), ApprovalState.PENDING);
        }
    }

    private Date selectPeriodDeleteFrom(Date intervalFrom, DeleteRequestContext ctx) {

        if (ctx.getValidFrom() == null) {
            return null;
        }

        if (intervalFrom == null || ctx.getValidFrom().after(intervalFrom)) {
            return ctx.getValidFrom();
        }

        return ctx.getValidFrom().before(intervalFrom) ? intervalFrom : ctx.getValidFrom(); // <-- equals
    }

    private Date selectPeriodDeleteTo(Date intervalTo, DeleteRequestContext ctx) {

        if (ctx.getValidTo() == null) {
            return null;
        }

        if (intervalTo == null || ctx.getValidTo().before(intervalTo)) {
            return ctx.getValidTo();
        }

        return ctx.getValidTo().after(intervalTo) ? intervalTo : ctx.getValidTo(); // <-- equals
    }

    /**
     * Puts an inactive version for a source system to vistory.
     * @param ctx the context to use
     * @return true, if successful, false otherwise
     */
    private boolean processDeletePeriod(DeleteRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();

            // 1. Check for input. Stop on no input.
            WorkflowTimelineDTO timeline = ctx.getFromStorage(StorageId.DATA_TIMELINE_BEFORE);
            if (timeline.getIntervals().stream().noneMatch(TimeIntervalDTO::isActive)) {
                return false;
            }

            Map<TimeIntervalDTO, Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>>> data
                = ctx.getFromStorage(StorageId.DATA_INTERVALS_BEFORE);

            // 2. Collect versions
            List<OriginsVistoryRecordPO> versions = new ArrayList<>();
            for (TimeIntervalDTO interval : timeline.getIntervals()) {

                if (!interval.isActive()) {
                    continue;
                }

                Date from = selectPeriodDeleteFrom(interval.getValidFrom(), ctx);
                Date to = selectPeriodDeleteTo(interval.getValidTo(), ctx);

                Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> intervalData
                    = data.get(interval);

                for (CalculableHolder<OriginRecord> holder : intervalData.getValue()) {

                    OriginsVistoryRecordPO version
                        = DataRecordUtils.createInactiveVistoryRecordPO(
                            holder.getValue().getInfoSection().getOriginKey().getId(),
                            from,
                            to,
                            holder.getValue(),
                            ctx);

                    versions.add(version);
                }
            }

            // 3. Put to store
            originsVistoryDao.putVersions(versions);

            // 4. Reset keys, if WF should be started
            if (versions.stream().anyMatch(v -> v.getApproval() == ApprovalState.PENDING) && !keys.isPending()) {
                commonComponent.changeApproval(keys.getEtalonKey().getId(), ApprovalState.PENDING);
                commonComponent.possiblyResetApprovalState(ctx, StorageId.DATA_DELETE_KEYS, ApprovalState.PENDING);
            }

            // 5. Load updates
            loadAndSaveWorkflowTimeline(ctx,
                    StorageId.DATA_TIMELINE_AFTER,
                    StorageId.DATA_INTERVALS_AFTER, false, false, true);

            return !versions.isEmpty();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Prepare origin upsert between.
     * Creates O/E records if necessary and resets keys in the context.
     * @param ctx the context to prepare
     */
    public void ensureSystemRecords(UpsertRequestContext ctx) {

        // 1. Keys
        RecordKeys keys = ctx.keys();
        boolean hasEtalonRecord = keys != null && keys.getEtalonKey() != null && keys.getEtalonKey().getId() != null;
        boolean hasOriginRecord = keys != null && keys.getOriginKey() != null && keys.getOriginKey().getId() != null;
        if (!hasEtalonRecord) {

            EtalonRecordPO record = DataRecordUtils.createEtalonRecordPO(ctx, keys, RecordStatus.ACTIVE);
            keys = RecordKeys.builder()
                .etalonKey(EtalonKey.builder().id(record.getId()).build())
                .entityName(record.getName())
                .etalonStatus(record.getStatus())
                .etalonState(record.getApproval())
                .build();

            ctx.putToStorage(StorageId.DATA_UPSERT_KEYS, keys);

            if (!ctx.isBatchUpsert()) {
                dataRecordsDao.upsertEtalonRecords(Collections.singletonList(record), true);
            } else {
                RecordUpsertBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RECORDS);
                batchSet.setEtalonRecordInsertPO(record);
            }
        }

        if (!hasOriginRecord) {

            OriginRecordPO record = DataRecordUtils.createOriginRecordPO(ctx, keys, RecordStatus.ACTIVE);
            OriginRecordPO system = null;

            // Check for first upsert and create
            // UD origin, if the upsert is not a UD upsert.
            if (!hasEtalonRecord && !metaModelService.getAdminSourceSystem().getName().equals(record.getSourceSystem())) {

                UpsertRequestContext sysContext = UpsertRequestContext.builder()
                        .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                        .entityName(record.getName())
                        .externalId(IdUtils.v1String())
                        .build();

                ContextUtils.storageCopy(ctx, sysContext, StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

                system = DataRecordUtils.createOriginRecordPO(sysContext,
                    RecordKeys.builder().etalonKey(keys.getEtalonKey()).build(),
                    RecordStatus.ACTIVE);
            }

            keys = RecordKeys.builder(keys)
                    .originKey(OriginKey.builder()
                            .entityName(record.getName())
                            .externalId(record.getExternalId())
                            .id(record.getId())
                            .sourceSystem(record.getSourceSystem())
                            .build())
                    .originStatus(record.getStatus())
                    .supplementaryKeys(system == null
                            ? Collections.emptyList()
                            : Collections.singletonList(OriginKey.builder()
                            .entityName(system.getName())
                            .externalId(system.getExternalId())
                            .id(system.getId())
                            .sourceSystem(system.getSourceSystem())
                            .build()))
                    .build();

            ctx.putToStorage(StorageId.DATA_UPSERT_KEYS, keys);

            if (!ctx.isBatchUpsert()) {
                dataRecordsDao.upsertOriginRecords(system == null
                        ? Collections.singletonList(record)
                        : Arrays.asList(record, system), true);
            } else {
                RecordUpsertBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RECORDS);
                batchSet.getOriginRecordInsertPOs().add(record);
                if (Objects.nonNull(system)) {
                    batchSet.getOriginRecordInsertPOs().add(system);
                }
            }
        }

        // 2. Workflow flags TODO move this out of here
        WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_UPSERT_WF_ASSIGNMENTS);
        if (assignment != null) {
            ctx.putToStorage(StorageId.DATA_UPSERT_IS_PUBLISHED, Boolean.valueOf(hasEtalonRecord));
        }

        // 3. Reset keys on the data record.
        OriginRecord or = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
        or.getInfoSection()
            .withOriginKey(keys.getOriginKey())
            .withStatus(keys.getOriginStatus());
    }

    public void loadAndSaveWorkflowTimeline(RecordIdentityContext ctx,
            StorageId timelineKey,
            StorageId intervalsKey,
            boolean wholeTimeline,
            boolean approverView,
            boolean includeInactive) {

        MeasurementPoint.start();
        try {

            WorkflowTimelineDTO timeline;
            if (wholeTimeline || !(ctx instanceof ValidityRangeContext)) {
                timeline = loadWorkflowTimeline(ctx, approverView);
            } else {

                Date from = ((ValidityRangeContext) ctx).getValidFrom();
                Date to = ((ValidityRangeContext) ctx).getValidTo();

                timeline = loadAndReduceWorkflowTimeline(ctx, from, to, approverView);
            }

            ((CommonRequestContext) ctx).putToStorage(timelineKey, timeline);
            if (timeline.getIntervals().isEmpty()) {
                return;
            }

            RecordKeys keys = ctx.keys();
            Map<TimeIntervalDTO, Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>>> data
                = new IdentityHashMap<>(timeline.getIntervals().size());

            for (TimeIntervalDTO interval : timeline.getIntervals()) {

                Date asOf = interval.getValidFrom() != null
                        ? interval.getValidFrom()
                        : interval.getValidTo();

                Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> result
                    = etalonComponent.loadEtalonDataFull(keys.getEtalonKey().getId(),
                            asOf, null, null, null,
                            includeInactive, approverView);

                data.put(interval, result);
            }

            ((CommonRequestContext) ctx).putToStorage(intervalsKey, data);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Loads timeline extended with workflow information.
     * @param ctx the context
     * @return timeline
     */
    public WorkflowTimelineDTO loadAndReduceWorkflowTimeline(RecordIdentityContext ctx, Date from, Date to, Boolean isApproverView) {

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            if (keys == null) {

                keys = commonComponent.identify(ctx);
                if (keys == null) {
                    final String message = "Workflow timeline cannot identify record";
                    LOGGER.warn(message);
                    throw new DataProcessingException(message, ExceptionId.EX_DATA_TIMELINE_NO_IDENTITY);
                }
            }

            // TODO this is temporary until full migration to new timeline types is done.
            GetRequestContext gCtx = GetRequestContext.builder()
                    .includeDrafts(false)
                    .includeInactive(true)
                    .tasks(false)
                    .build();

            gCtx.putToStorage(gCtx.keysId(), keys);

            Timeline<TimeIntervalContributorInfo> timeline = commonComponent.loadTimeline(gCtx);
            timeline.reduceBy(from, to);

            WorkflowTimelineDTO result = new WorkflowTimelineDTO(keys.getEtalonKey().getId(), timeline.isPending(), timeline.isPublished());
            result.getIntervals().addAll(timeline.stream()
                    .map(interval -> {

                        WorkflowTimeIntervalDTO wi = new WorkflowTimeIntervalDTO(
                                interval.getValidFrom(),
                                interval.getValidTo(),
                                interval.getPeriodId(),
                                interval.isActive(),
                                interval.isPending());

                        wi.getContributors().addAll(interval.stream().map(TimelineConverter::to).collect(Collectors.toList()));
                        return wi;
                    })
                    .collect(Collectors.toList()));

            return result;

            /*
            WorkflowTimelineDTO timeline = loadWorkflowTimeline(ctx, isApproverView);
            if (!timeline.getIntervals().isEmpty()) {

                int left = from == null ? 0 : -1;
                int right = to == null ? timeline.getIntervals().size() - 1 : -1;
                for (int i = 0;
                     (left == -1 || right == -1) && i < timeline.getIntervals().size();
                     i++) {

                    TimeIntervalDTO interval = timeline.getIntervals().get(i);
                    if (left == -1 && interval.isInRange(from)) {
                        left = i;
                    }

                    if (right == -1 && interval.isInRange(to)) {
                        right = i;
                    }
                }

                if (left != -1 && right != -1) {

                    List<TimeIntervalDTO> reduced = new ArrayList<>(timeline.getIntervals().subList(left, right + 1));
                    if (CollectionUtils.isNotEmpty(reduced) && reduced.size() < timeline.getIntervals().size()) {
                        timeline.getIntervals().clear();
                        timeline.getIntervals().addAll(reduced);
                    }
                }
            }

            return timeline;
            */
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Loads timeline extended with workflow information.
     * @param ctx the context
     * @return timeline
     */
    public WorkflowTimelineDTO loadWorkflowTimeline(RecordIdentityContext ctx, Boolean isApproverView) {

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            if (keys == null) {

                keys = commonComponent.identify(ctx);
                if (keys == null) {
                    final String message = "Workflow timeline cannot identify record";
                    LOGGER.warn(message);
                    throw new DataProcessingException(message, ExceptionId.EX_DATA_TIMELINE_NO_IDENTITY);
                }
            }

            // TODO this is temporary until full migration to new timeline types is done.
            GetRequestContext gCtx = GetRequestContext.builder()
                    .includeDrafts(false)
                    .includeInactive(true)
                    .tasks(false)
                    .build();

            gCtx.putToStorage(gCtx.keysId(), keys);

            Timeline<TimeIntervalContributorInfo> timeline = commonComponent.loadTimeline(gCtx);
            WorkflowTimelineDTO result = new WorkflowTimelineDTO(keys.getEtalonKey().getId(), timeline.isPending(), timeline.isPublished());
            result.getIntervals().addAll(timeline.stream()
                    .map(interval -> {

                        WorkflowTimeIntervalDTO wi = new WorkflowTimeIntervalDTO(
                                interval.getValidFrom(),
                                interval.getValidTo(),
                                interval.getPeriodId(),
                                interval.isActive(),
                                interval.isPending());

                        wi.getContributors().addAll(interval.stream().map(TimelineConverter::to).collect(Collectors.toList()));
                        return wi;
                    })
                    .collect(Collectors.toList()));

            return result;
            /*
            // 2. Load from DB.
            List<TimeIntervalPO> intervals = originsVistoryDao
                    .loadContributingRecordsTimeline(keys.getEtalonKey().getId(), keys.getEntityName(), isApproverView);

            // 3. Build the timeline
            boolean timelineIsInPendingState = false;
            List<TimeIntervalDTO> result = new ArrayList<>(intervals != null ? intervals.size() : 0);
            for (int i = 0; intervals != null && i < intervals.size(); i++) {

                TimeIntervalPO tipo = intervals.get(i);
                List<ContributorDTO> contributors
                    = new ArrayList<>(tipo.getContributors() != null ? tipo.getContributors().length : 0);
                List<CalculableHolder<ContributorDTO>> calculables
                    = new ArrayList<>(tipo.getContributors() != null ? tipo.getContributors().length : 0);
                boolean hasPendingVersions = false;
                for (int j = 0; tipo.getContributors() != null && j < tipo.getContributors().length; j++) {
                    ContributorPO copo = tipo.getContributors()[j];
                    ContributorDTO cdto
                        = new ContributorDTO(copo.getOriginId(),
                            copo.getRevision(),
                            copo.getSourceSystem(),
                            copo.getStatus() == null ? null : copo.getStatus().toString(),
                            copo.getApproval() == null ? null : copo.getApproval().toString(),
                            copo.getOwner(),
                            copo.getLastUpdate(),
                            tipo.getName(),
                            copo.getOperationType());

                    calculables.add(new TimeIntervalContributorHolder(cdto));
                    contributors.add(cdto);
                    hasPendingVersions = !hasPendingVersions ? copo.getApproval() == ApprovalState.PENDING : hasPendingVersions;
                }

                WorkflowTimeIntervalDTO ti = new WorkflowTimeIntervalDTO(
                        tipo.getFrom(),
                        tipo.getTo(),
                        tipo.getPeriodId(),
                        etalonComposer.hasActive(EtalonCompositionDriverType.BVR, calculables), hasPendingVersions);

                timelineIsInPendingState = !timelineIsInPendingState ? hasPendingVersions : timelineIsInPendingState;
                if (hasPendingVersions) {
                    List<OriginsVistoryRecordPO> pending
                        = originsVistoryDao.loadPendingVersionsByEtalonId(
                                keys.getEtalonKey().getId(),
                                tipo.getFrom() != null ? tipo.getFrom() : tipo.getTo());

                    List<ContributorDTO> pendings = new ArrayList<>(pending.size());
                    for (OriginsVistoryRecordPO ppo : pending) {
                        ContributorDTO cdto
                            = new ContributorDTO(ppo.getOriginId(),
                                ppo.getRevision(),
                                ppo.getSourceSystem(),
                                ppo.getStatus() == null ? null : ppo.getStatus().toString(),
                                ppo.getApproval() == null ? null : ppo.getApproval().toString(),
                                ppo.getCreatedBy(),
                                ppo.getCreateDate(),
                                tipo.getName(),
                                ppo.getOperationType());

                        pendings.add(cdto);
                    }

                    ti.getPendings().addAll(pendings);
                }

                ti.getContributors().addAll(contributors);
                result.add(ti);
            }

            WorkflowTimelineDTO timeline
                = new WorkflowTimelineDTO(keys.getEtalonKey().getId(), timelineIsInPendingState, keys.isPublished());
            if (!result.isEmpty()) {
                timeline.getIntervals().addAll(result);
            }

            return timeline;
            */
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Returns true, if the time line has pending versions in one of the denoted periods.
     * TODO add DB function returning only pending periods.
     * @param etalonId the etalon id
     * @return true, if so, false otherwise
     */
    public boolean hasPendingVersions(String etalonId, String entityName) {

        List<TimeIntervalPO> timeline = originsVistoryDao
                .loadContributingRecordsTimeline(etalonId, entityName, true);

        for (int i = 0; timeline != null && i < timeline.size(); i++) {
            TimeIntervalPO ti = timeline.get(i);
            for (int j = 0; ti.getContributors() != null && j < ti.getContributors().length; j++) {

                ContributorPO co = ti.getContributors()[j];
                if (co.getApproval() == ApprovalState.PENDING) {
                    return true;
                }
            }
        }

        return false;
    }

   /**
    * Checks whether an etalon has pending top level versions for a given date.
    * @param etalonId the etalon id
    * @param asOf calculation date
    * @return true, if has, false otherwise
    */
   public boolean hasPendingVersions(String etalonId, Date asOf) {

       List<OriginsVistoryRecordPO> versions
           = originsVistoryDao
               .loadVersionsUnfilterdByEtalonId(etalonId, asOf);
       for (int i = 0; versions != null && i < versions.size(); i++) {
           if (versions.get(i).getApproval() == ApprovalState.PENDING
            && versions.get(i).getStatus() != RecordStatus.MERGED) {
               return true;
           }
       }

       return false;
   }

    /**
     * Deletes DB objects.
     * @param ctx the context
     */
    public void deleteOrigin(DeleteRequestContext ctx) {

        // Run pre-check, keys resolution and similar
        deleteRecordActionListener.before(ctx);

        RecordKeys keys = ctx.keys();

        Date ts = new Date();
        ctx.putToStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP, ts);

        if (ctx.isBatchUpsert()) {
            createDeleteBatchObjects(ctx);
            return;
        }

        if (ctx.isInactivateOrigin()) {
            dataRecordsDao.deleteOriginRecord(keys.getOriginKey().getId(), ts);
            if(commonComponent.allOriginsAlreadyInactive(keys)){
                dataRecordsDao.deleteEtalonRecord(keys.getEtalonKey().getId(), ctx.getOperationId(), keys.getEtalonState(), ts, false);
            }
        } else if (ctx.isInactivatePeriod()) {
            processDeletePeriod(ctx);
        } else if (ctx.isInactivateEtalon()) {

            WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_DELETE_WF_ASSIGNMENTS);
            ApprovalState state = DataRecordUtils.calculateRecordState(ctx, assignment);

            dataRecordsDao.deleteEtalonRecord(keys.getEtalonKey().getId(), ctx.getOperationId(), state, ts, ctx.isCascade());
            commonComponent.possiblyResetApprovalState(ctx, StorageId.DATA_DELETE_KEYS, state);

            if (state == ApprovalState.PENDING) {
                ctx.skipNotification();
            }

        } else if (ctx.isWipe()) {

            keys.getSupplementaryKeys().forEach(k -> dataRecordsDao.wipeOriginRecord(k.getId()));
            dataRecordsDao.wipeEtalonRecord(keys.getEtalonKey().getId());
        }
    }

    /**
     * Generates batch sets for various delete types.
     * Wipe is not supported by batches
     * @param ctx the context
     */
    private void createDeleteBatchObjects(DeleteRequestContext ctx) {

        final RecordKeys keys = ctx.keys();
        RecordDeleteBatchSet rbs = ctx.getFromStorage(StorageId.DATA_BATCH_RECORDS);

        if (ctx.isInactivateEtalon()) {

            // 1. Generate inactive etalon
            EtalonRecordPO etalon = DataRecordUtils.createEtalonRecordPO(ctx, keys, RecordStatus.INACTIVE);
            rbs.setEtalonRecordUpdatePO(etalon);

            // 2. Generate inactive record for main key.
            rbs.getOriginRecordUpdatePOs().add(DataRecordUtils.createOriginRecordPO(ctx, keys, RecordStatus.INACTIVE));

            // 3. Generate inactive records for all known keys.
            keys.getSupplementaryKeys().stream()
                .filter(k -> k.getStatus() != RecordStatus.INACTIVE)
                .map(k -> DataRecordUtils.createOriginRecordPO(ctx,
                        RecordKeys.builder()
                            .entityName(keys.getEntityName())
                            .etalonKey(keys.getEtalonKey())
                            .originKey(k)
                            .etalonStatus(RecordStatus.INACTIVE)
                            .originStatus(RecordStatus.INACTIVE)
                            .etalonState(ApprovalState.APPROVED)
                        .build(), RecordStatus.INACTIVE))
                .collect(Collectors.toCollection(rbs::getOriginRecordUpdatePOs));
        } else if (ctx.isInactivateOrigin()) {

            // 1. Turn off requested origin
            rbs.getOriginRecordUpdatePOs().add(DataRecordUtils.createOriginRecordPO(ctx, keys, RecordStatus.INACTIVE));

            // 2. Turn off the whole record, if the origin was the only active one
            if (commonComponent.allOriginsAlreadyInactive(keys)) {

                EtalonRecordPO etalon = DataRecordUtils.createEtalonRecordPO(ctx, keys, RecordStatus.INACTIVE);
                rbs.setEtalonRecordUpdatePO(etalon);
            }

        } else if (ctx.isInactivatePeriod()) {

            // 1. Will put the version to context
            putVersion(ctx, null, DataShift.PRISTINE);
        } else if (ctx.isWipe()) {
            rbs.addOriginsForWipeDelete(keys.getSupplementaryKeys().stream()
                    .map(OriginKey::getId)
                    .collect(Collectors.toList()));
            rbs.addEtalonsForWipeDelete(Collections.singletonList(keys.getEtalonKey().getId()));
        }
    }
    /**
     * Restores a period.
     * @param ctx the context
     * @return
     */
    public boolean restorePeriod(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            Date asOf = ctx.getValidFrom() == null ? ctx.getValidTo() : ctx.getValidFrom();
            List<OriginsVistoryRecordPO> versions
                = originsVistoryDao.loadVersionsUnfilterdByEtalonId(keys.getEtalonKey().getId(), asOf);

            List<String> ids = versions.stream()
                    .filter(v -> v.getStatus() == RecordStatus.INACTIVE)
                    .map(OriginsVistoryRecordPO::getId)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(ids)) {
                originsVistoryDao.updateVistoryStatus(ids, RecordStatus.ACTIVE);
            } else {
                return false;
            }

            loadAndSaveWorkflowTimeline(ctx,
                    StorageId.DATA_TIMELINE_AFTER,
                    StorageId.DATA_INTERVALS_AFTER, false, false, true);

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Returns approval state according to context state.
     * @param ctx the context
     * @param assignment current assignment
     * @return state
     */
    public ApprovalState calculateRecordState(ApprovalStateSettingContext ctx, WorkflowAssignmentDTO assignment) {

        // 1. Return state, if explicitly set by the context
        ApprovalState state = ctx.getApprovalState();
        if (!Objects.isNull(state)) {
            return state;
        }

        // 2. Return APPROVED, if no process type set for the action
        if (assignment == null) {
            return ApprovalState.APPROVED;
        }

        // 3. Pass through for admin user
        if (SecurityUtils.isAdminUser()) {
            return ApprovalState.APPROVED;
        }

        // 4. Special cases: EDIT type with trigger type VERSION_DATA_CONFLICT, let records be created normally.
        if (assignment.getType() == WorkflowProcessType.RECORD_EDIT
         && assignment.getTriggerType() == EditWorkflowProcessTriggerType.VERSION_CONFLICT) {
            return ApprovalState.APPROVED;
        }

        // 5. For now, all other combinations create pending records.
        return ApprovalState.PENDING;
    }

    /**
     * Returns version state.
     * @param ctx the context
     * @param keys the keys
     * @param assignment current assignment
     * @return state
     */
    public ApprovalState calculateVersionState(ApprovalStateSettingContext ctx, RecordKeys keys, WorkflowAssignmentDTO assignment) {

        // 1. Return state, if explicitly set by the context
        ApprovalState state = ctx.getApprovalState();
        if (!Objects.isNull(state)) {
            return state;
        }

        // 2. Keep state if already pending
        if (keys.isPending()) {
            return ApprovalState.PENDING;
        }

        // 3. Pass through for admin user
        if (SecurityUtils.isAdminUser()) {
            return ApprovalState.APPROVED;
        }

        // 4. Return APPROVED, if no process type set for the action
        if (assignment == null) {
            return ApprovalState.APPROVED;
        }

        // 5. Special cases: EDIT type with trigger type VERSION_DATA_CONFLICT, possibly trigger process
        // if the origin is not of the admin source system and the action is not insert
        // See UN-6046 for details
        if (assignment.getType() == WorkflowProcessType.RECORD_EDIT
         && assignment.getTriggerType() == EditWorkflowProcessTriggerType.VERSION_CONFLICT) {

            // 5.1 Skip record creation
            UpsertAction action = ((CommonRequestContext) ctx).getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
            if (action == UpsertAction.INSERT || action == UpsertAction.NO_ACTION) {
                return ApprovalState.APPROVED;
            }

            // 5.2 Skip admin source system updates
            if (metaModelService.isAdminSourceSystem(keys.getOriginKey().getSourceSystem())) {
                return ApprovalState.APPROVED;
            }

            // 5.3 Check for other versions, being present since previous update
            // - Load current versions
            // - Check, whether there were some updates, since last update
            // - Generate pending state, if so
            Date asOf = ((ValidityRangeContext) ctx).getValidFrom() == null
                    ? ((ValidityRangeContext) ctx).getValidTo()
                    : ((ValidityRangeContext) ctx).getValidFrom();

            List<OriginRecord> origins
                = loadOrigins(keys.getEtalonKey().getId(), asOf, null, null, null, true, SecurityUtils.getCurrentUserName());

            Comparator<OriginRecord> c = (o1, o2) -> {

                // Latest first
                if (o1.getInfoSection().getUpdateDate().after(o2.getInfoSection().getUpdateDate())) {
                    return -1;
                } else if (o1.getInfoSection().getUpdateDate().before(o2.getInfoSection().getUpdateDate())) {
                    return 1;
                }

                return 0;
            };

            origins.sort(c);

            // 5.4 If there were no updates from other source systems in the mean time
            // return APPROVED
            if (origins.get(0).getInfoSection().getOriginKey().getSourceSystem().equals(keys.getOriginKey().getSourceSystem())) {
                return ApprovalState.APPROVED;
            }

            // 5.5 Start process otherwise
            return ApprovalState.PENDING;
        }

        // 5. For now, all other combinations create pending records.
        return ApprovalState.PENDING;
    }

    @Transactional
    public EtalonKey splitOrigin(final SplitContext splitContext) {
        if (dataRecordsDao.loadRecordKeysByEtalonId(splitContext.getOldEtalonKey().getId()).size() < 2) {
            throw new BusinessException("Can't detach last origin from etalon.", ExceptionId.EX_DETACH_LAST_ORIGIN);
        }

        final UpsertRequestContext ctx = UpsertRequestContext.builder()
                .originKey(splitContext.getOriginKey())
                .entityName(splitContext.getEntityName())
                .build();

        ctx.putToStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP, new Date());

        final EtalonRecordPO newEtalonRecord = DataRecordUtils.createEtalonRecordPO(ctx, null, RecordStatus.ACTIVE);

        dataRecordsDao.upsertEtalonRecords(Collections.singletonList(newEtalonRecord), true);


        final OriginRecordPO originRecord = dataRecordsDao.findOriginRecordById(splitContext.getOriginKey());
        originRecord.setEtalonId(newEtalonRecord.getId());
        dataRecordsDao.upsertOriginRecords(Collections.singletonList(originRecord), false);

        return EtalonKey.builder()
                .id(newEtalonRecord.getId())
                .gsn(newEtalonRecord.getGsn())
                .status(newEtalonRecord.getStatus())
                .build();
    }

}
