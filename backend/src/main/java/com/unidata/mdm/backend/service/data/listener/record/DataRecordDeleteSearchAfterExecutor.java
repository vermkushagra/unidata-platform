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

package com.unidata.mdm.backend.service.data.listener.record;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonData;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_ETALON_ID;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_FROM;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_TO;
import static com.unidata.mdm.meta.SimpleDataType.TIMESTAMP;
import static java.lang.Boolean.TRUE;

import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.MatchingHeaderField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.search.id.RecordIndexId;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.VistoryOperationType;
import com.unidata.mdm.backend.service.data.batch.RecordDeleteBatchSet;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;

/**
 * @author Mikhail Mikhailov
 *         Listener for DELETE record actions.
 */
public class DataRecordDeleteSearchAfterExecutor implements DataRecordAfterExecutor<DeleteRequestContext> {
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchService;

    @Autowired
    private EtalonRecordsComponent etalonRecordsComnponent;
    /**
     * Constructor.
     */
    public DataRecordDeleteSearchAfterExecutor() {
        super();
    }

    /**
     * Tools support constructor.
     *
     * @param svc search service
     */
    public DataRecordDeleteSearchAfterExecutor(SearchServiceExt svc) {
        this();
        this.searchService = svc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRequestContext context) {
        MeasurementPoint.start();
        try {
            // Skip index context generation for batch updates.
            // This will be handled in batch* methods
            RecordKeys keys = context.keys();
            if (keys != null && !context.isInactivateOrigin()) {
                if (context.isInactivatePeriod()) {
                    handlePeriodDelete(context);
                } else {
                    handleRecordDelete(context);
                }
            }

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    private void handlePeriodDelete(DeleteRequestContext context) {

        RecordKeys keys = context.keys();
        String entityName = keys.getEntityName();
        String id = keys.getEtalonKey().getId();
        Date from = context.getValidFrom();
        Date to = context.getValidTo();
        Date updateDate = context.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

        FormField etalonId = FormField.strictString(FIELD_ETALON_ID.getField(), id);
        FormField fromField = FormField.range(TIMESTAMP, FIELD_FROM.getField(), null, to);
        FormField toField = FormField.range(TIMESTAMP, FIELD_TO.getField(), from, null);
        FormFieldsGroup group = createAndGroup(etalonId, fromField, toField);

        SearchRequestContext searchContext = forEtalonData(entityName)
                .form(group)
                .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                .build();

        if (context.isBatchUpsert()) {

            EtalonRecordInfoSection is = new EtalonRecordInfoSection()
                    .withApproval(keys.isPending() ? ApprovalState.PENDING : null)
                    .withPeriodId(Objects.isNull(to) ? ValidityPeriodUtils.TIMELINE_MAX_PERIOD_ID : to.getTime())
                    .withEntityName(keys.getEntityName())
                    .withEtalonKey(keys.getEtalonKey())
                    .withStatus(!keys.isPending() ? RecordStatus.INACTIVE : null)
                    .withUpdateDate(updateDate == null ? new Date() : updateDate)
                    .withUpdatedBy(SecurityUtils.getCurrentUserName());

            IndexRequestContext iCtx = IndexRequestContext.builder()
                    .entity(entityName)
                    .recordsToSysUpdate(Collections.singletonList(is))
                    .drop(!keys.isPending())
                    .build();

            RecordDeleteBatchSet batchSet = context.getFromStorage(StorageId.DATA_BATCH_RECORDS);
            batchSet.setIndexRequestContext(iCtx);

        } else {
            if (keys.isPending()) {
                Map<RecordHeaderField, Object> fields = new EnumMap<>(RecordHeaderField.class);
                fields.put(RecordHeaderField.FIELD_UPDATED_AT, updateDate == null ? new Date() : updateDate);
                fields.put(RecordHeaderField.FIELD_PENDING, TRUE);

                searchService.mark(searchContext, fields);
            } else {
                processPeriodInactivation(context);
            }

            // Matching
            group = FormFieldsGroup.createAndGroup(
                    FormField.strictString(MatchingHeaderField.FIELD_ETALON_ID.getField(), id),
                    FormField.range(TIMESTAMP, MatchingHeaderField.FIELD_FROM.getField(), null, to),
                    FormField.range(TIMESTAMP, MatchingHeaderField.FIELD_TO.getField(), from, null));

            searchContext = SearchRequestContext.forEtalon(EntitySearchType.MATCHING, entityName)
                    .form(group)
                    .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                    .build();

            searchService.deleteFoundResult(searchContext);
        }
    }

    private void handleRecordDelete(DeleteRequestContext context) {

        RecordKeys keys = context.keys();
        String entityName = keys.getEntityName();
        String id = keys.getEtalonKey().getId();
        Date updateDate = context.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);
        VistoryOperationType operationType = context.getFromStorage(StorageId.DATA_UPSERT_VISTORY_OPERATION_TYPE) == null ? VistoryOperationType.DIRECT
                : context.getFromStorage(StorageId.DATA_UPSERT_VISTORY_OPERATION_TYPE);

        if (context.isBatchUpsert()) {
            if (context.isWipe()) {
                // Will query for version ids the cluster
                EtalonRecordInfoSection is = new EtalonRecordInfoSection()
                        .withApproval(keys.isPending() ? ApprovalState.PENDING : null)
                        .withEntityName(keys.getEntityName())
                        .withEtalonKey(keys.getEtalonKey())
                        .withStatus(!keys.isPending() ? RecordStatus.INACTIVE : null)
                        .withUpdateDate(updateDate == null ? new Date() : updateDate)
                        .withOperationType(operationType)
                        .withUpdatedBy(SecurityUtils.getCurrentUserName());

                IndexRequestContext iCtx = IndexRequestContext.builder()
                        .entity(keys.getEntityName())
                        .recordsToQueryDelete(Collections.singletonList(is))
                        .matchingToQueryDelete(Collections.singletonList(is))
                        .drop(true)
                        .build();

                RecordDeleteBatchSet batchSet = context.getFromStorage(StorageId.DATA_BATCH_RECORDS);
                batchSet.setIndexRequestContext(iCtx);
            } else {
                // Will query for version ids the cluster
                EtalonRecordInfoSection is = new EtalonRecordInfoSection()
                        .withApproval(keys.isPending() ? ApprovalState.PENDING : null)
                        .withEntityName(keys.getEntityName())
                        .withEtalonKey(keys.getEtalonKey())
                        .withStatus(!keys.isPending() ? RecordStatus.INACTIVE : null)
                        .withUpdateDate(updateDate == null ? new Date() : updateDate)
                        .withOperationType(operationType)
                        .withUpdatedBy(SecurityUtils.getCurrentUserName());

                IndexRequestContext iCtx = IndexRequestContext.builder()
                        .entity(keys.getEntityName())
                        .recordsToSysUpdate(Collections.singletonList(is))
                        .matchingToQueryDelete(Collections.singletonList(is))
                        .build();

                RecordDeleteBatchSet batchSet = context.getFromStorage(StorageId.DATA_BATCH_RECORDS);
                batchSet.setIndexRequestContext(iCtx);
            }
        } else {
            SearchRequestContext ctx = forEtalonData(entityName)
                    .form(FormFieldsGroup.createAndGroup()
                            .addFormField(FormField.strictString(FIELD_ETALON_ID.getField(), id)))
                    .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                    .build();

        SearchRequestContext delMatchingCtx = SearchRequestContext.forEtalon(EntitySearchType.MATCHING, entityName)
                .form(FormFieldsGroup.createAndGroup()
                        .addFormField(FormField.strictString(FIELD_ETALON_ID.getField(), id)))
                .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                .build();
            // Wipe is not supported so far
            if (context.isWipe()) {
                searchService.deleteFoundResult(ctx);
            } else {

                RecordHeaderField searchField = keys.isPending() ? RecordHeaderField.FIELD_PENDING : RecordHeaderField.FIELD_DELETED;
                Map<RecordHeaderField, Object> fields = new EnumMap<>(RecordHeaderField.class);

                fields.put(RecordHeaderField.FIELD_OPERATION_TYPE, operationType);
                fields.put(RecordHeaderField.FIELD_UPDATED_AT, updateDate == null ? new Date() : updateDate);
                fields.put(searchField, TRUE);
                searchService.mark(ctx, fields);
            }
            // Delete matching data anyway.
            // It will be restored upon decline for pending records.
            searchService.deleteFoundResult(delMatchingCtx);
        }
    }

    private void processPeriodInactivation(DeleteRequestContext ctx) {

        RecordKeys keys = ctx.keys();

        WorkflowTimelineDTO prev = ctx.getFromStorage(StorageId.DATA_TIMELINE_BEFORE);
        WorkflowTimelineDTO next = ctx.getFromStorage(StorageId.DATA_TIMELINE_AFTER);

        if (prev.getIntervals().isEmpty() || next.getIntervals().isEmpty()) {
            return;
        }

        // Check the timelines for rough overlay
        WorkflowTimeIntervalDTO prevLeft = (WorkflowTimeIntervalDTO) prev.getIntervals().get(0);
        WorkflowTimeIntervalDTO nextLeft = (WorkflowTimeIntervalDTO) next.getIntervals().get(0);

        WorkflowTimeIntervalDTO prevRight = (WorkflowTimeIntervalDTO) prev.getIntervals().get(prev.getIntervals().size() - 1);
        WorkflowTimeIntervalDTO nextRight = (WorkflowTimeIntervalDTO) next.getIntervals().get(prev.getIntervals().size() - 1);

        WorkflowTimelineDTO adjusted = new WorkflowTimelineDTO(keys.getEtalonKey().getId(), keys.isPending(), true);
        if (nextLeft.getValidFrom() != null
         && (prevLeft.getValidFrom() == null || prevLeft.getValidFrom().before(nextLeft.getValidFrom()))) {

            WorkflowTimeIntervalDTO wti = new WorkflowTimeIntervalDTO(prevLeft.getValidFrom(), new Date(nextLeft.getValidFrom().getTime() - 1),
                    1, // TODO Refactor this!!
                    prevLeft.isActive(),
                    prevLeft.isPending());

            wti.getContributors().addAll(prevLeft.getContributors());
            wti.getPendings().addAll(prevLeft.getPendings());

            adjusted.getIntervals().add(wti);
        }

        adjusted.getIntervals().addAll(next.getIntervals());

        if (nextRight.getValidTo() != null
         && (prevRight.getValidTo() == null || prevRight.getValidTo().after(nextRight.getValidTo()))) {

           WorkflowTimeIntervalDTO wti = new WorkflowTimeIntervalDTO(new Date(nextRight.getValidTo().getTime() + 1), prevRight.getValidTo(),
                   1, // TODO Refactor this!!
                   prevRight.isActive(),
                   prevRight.isPending());

           wti.getContributors().addAll(prevRight.getContributors());
           wti.getPendings().addAll(prevRight.getPendings());

           adjusted.getIntervals().add(wti);
        }

        deleteBoundary(keys,
                adjusted.getIntervals().get(0).getValidFrom(),
                adjusted.getIntervals().get(0).getValidTo());

        final UpsertRequestContext uCtx = UpsertRequestContext.builder()
                .entityName(keys.getEntityName())
                .recalculateWholeTimeline(true)
                .skipCleanse(false)
                .skipConsistencyChecks(false)
                .skipMatching(true)
                .bypassExtensionPoints(true)
                .returnEtalon(true)
                .build();

        uCtx.setOperationId(ctx.getOperationId());
        uCtx.skipNotification();
        uCtx.putToStorage(uCtx.keysId(), keys);
        uCtx.putToStorage(StorageId.DATA_UPSERT_IS_PUBLISHED, adjusted.isPublished());

        final List<UpsertRequestContext> periodEtalons = etalonRecordsComnponent.calculatePeriods(uCtx, adjusted.getIntervals());
        final IndexRequestContext irc =
            etalonRecordsComnponent.collectUpdates(uCtx, periodEtalons,
                    prev.getIntervals().stream()
                        .map(interval -> RecordIndexId.of(keys.getEntityName(), keys.getEtalonKey().getId(), interval.getPeriodId()))
                        .collect(Collectors.toList()));

        searchService.index(irc);
    }

    private void deleteBoundary(RecordKeys keys, Date validFrom, Date validTo) {

        FormFieldsGroup group = createAndGroup(
                FormField.strictString(FIELD_ETALON_ID.getField(), keys.getEtalonKey().getId()),
                FormField.range(TIMESTAMP, FIELD_FROM.getField(), null, validTo),
                FormField.range(TIMESTAMP, FIELD_TO.getField(), validFrom, null));

        SearchRequestContext searchContext = forEtalonData(keys.getEntityName())
                .form(group)
                .build();

        searchService.deleteFoundResult(searchContext);
    }
}
