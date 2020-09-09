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

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.listener.RequestContextSetup;
import com.unidata.mdm.conf.WorkflowProcessType;

public class DataRecordRestoreSecurityBeforeExecutor
        implements RequestContextSetup<UpsertRequestContext>, DataRecordBeforeExecutor<UpsertRequestContext> {

    @Override
    public boolean execute(UpsertRequestContext requestContext) {
        RecordKeys keys = requestContext.getFromStorage(StorageId.DATA_UPSERT_KEYS);
        String entityName = keys != null ? keys.getEntityName() : requestContext.getEntityName();

        if (requestContext.getFromStorage(StorageId.DATA_UPSERT_WF_ASSIGNMENTS) == null) {
            putWorkflowAssignments(
                    requestContext,
                    StorageId.DATA_UPSERT_WF_ASSIGNMENTS,
                    entityName,
                    WorkflowProcessType.RECORD_EDIT
            );
            //requestContext.putToStorage(StorageId.DATA_UPSERT_WORKFLOW_INTERVAL, requestContext.);
        }
        return true;
    }
}
