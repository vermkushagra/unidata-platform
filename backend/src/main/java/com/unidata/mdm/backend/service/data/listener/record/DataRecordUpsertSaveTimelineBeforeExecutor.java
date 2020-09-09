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

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;

/**
 * @author Mikhail Mikhailov
 * Save old timeline for various purposes (ES and notifications processing).
 */
public class DataRecordUpsertSaveTimelineBeforeExecutor implements DataRecordBeforeExecutor<UpsertRequestContext> {
    /**
     * ORC.
     */
    @Autowired
    private OriginRecordsComponent originRecordsComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {
            if (ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION) == UpsertAction.INSERT) {
                return true;
            }

            final GetRequestContext gCtx = GetRequestContext.builder()
                    .build();

            gCtx.putToStorage(gCtx.keysId(), ctx.keys());

            WorkflowTimelineDTO workflowTimelineDTO = originRecordsComponent.loadWorkflowTimeline(gCtx, false);
            ctx.putToStorage(StorageId.DATA_UPSERT_PREVIOUS_TIMELINE, workflowTimelineDTO);

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

}
