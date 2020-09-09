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

/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import static java.lang.Boolean.TRUE;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.PeriodIdUtils;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.VistoryOperationType;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.search.SearchServiceExt;

/**
 * @author Mikhail Mikhailov
 * Listener for UPSERT ETALON record actions.
 */
public class DataRecordUpsertSearchAfterExecutor implements
        DataRecordAfterExecutor<UpsertRequestContext> {

    @Autowired
    private SearchServiceExt searchService;

    /**
     * Constructor.
     */
    public DataRecordUpsertSearchAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext context) {

        UpsertAction action = context.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
        EtalonRecord etalon = context.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
        if (etalon == null || action == UpsertAction.NO_ACTION) {
            return true;
        }

        MeasurementPoint.start();
        try {

            WorkflowTimeIntervalDTO interval = context.getFromStorage(StorageId.DATA_UPSERT_WORKFLOW_INTERVAL);
            WorkflowTimelineDTO timeline = context.getFromStorage(StorageId.DATA_RECORD_TIMELINE);
            RecordKeys keys = context.keys();

            Boolean isNew = UpsertAction.INSERT == action;
            Boolean isPending = timeline != null ? timeline.isPending() : keys.getEtalonState() == ApprovalState.PENDING;
            Boolean isPublished = timeline != null ? timeline.isPublished() :
                    isNew && isPending
                    ? false
                    : true; // <- This branch is expected to be executed for INSERT actions only.

            Boolean intervalIsPending = interval != null ? interval.isPending() : isPending;

            VistoryOperationType ctxType = context.getFromStorage(StorageId.DATA_UPSERT_VISTORY_OPERATION_TYPE);
            VistoryOperationType etalonType = etalon.getInfoSection().getOperationType();
            VistoryOperationType operationType = ctxType != null ? ctxType
                    : etalonType != null ? etalonType : VistoryOperationType.DIRECT;

            // Check condition for indexing. Skip data update for pending versions.
            // Pending update of a published etalon means submitting period, since we can currently
            // edit one pending period only.
            if (!context.isRecalculateWholeTimeline()) {
                if (intervalIsPending && isPublished) {
                    String id = keys.getEtalonKey().getId();
                    Map<RecordHeaderField, Object> fields = Collections.singletonMap(RecordHeaderField.FIELD_PENDING, TRUE);
                    searchService.mark(keys.getEntityName(), id, fields);
                    return true;
                }
                if (isPending && isPublished) {
                    return true;
                }
            }

            Map<RecordHeaderField, Object> fields = new EnumMap<>(RecordHeaderField.class);
            fields.put(RecordHeaderField.FIELD_FROM, context.getValidFrom());
            fields.put(RecordHeaderField.FIELD_TO, context.getValidTo());
            fields.put(RecordHeaderField.FIELD_PENDING, isPending);
            fields.put(RecordHeaderField.FIELD_PUBLISHED, isPublished);
            fields.put(RecordHeaderField.FIELD_PERIOD_ID, PeriodIdUtils.periodIdFromDate(etalon.getInfoSection().getValidTo()));
            fields.put(RecordHeaderField.FIELD_ORIGINATOR, etalon.getInfoSection().getUpdatedBy());
            fields.put(RecordHeaderField.FIELD_DELETED, keys.getEtalonStatus() == RecordStatus.INACTIVE);
            fields.put(RecordHeaderField.FIELD_INACTIVE, interval != null && interval.isDeleted());
            fields.put(RecordHeaderField.FIELD_ETALON_ID, keys.getEtalonKey().getId());
            fields.put(RecordHeaderField.FIELD_CREATED_AT, etalon.getInfoSection().getCreateDate());
            fields.put(RecordHeaderField.FIELD_OPERATION_TYPE, operationType);

            if(!isNew){
                fields.put(RecordHeaderField.FIELD_UPDATED_AT, etalon.getInfoSection().getUpdateDate());
            }

            context.putToStorage(StorageId.DATA_UPSERT_ETALON_INDEX_UPDATE, fields);

        } finally {
            MeasurementPoint.stop();
        }

        return true;
    }
}
