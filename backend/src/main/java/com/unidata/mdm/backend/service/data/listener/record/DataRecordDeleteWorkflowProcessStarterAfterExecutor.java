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

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext.GetProcessRequestContextBuilder;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowProcessDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.service.data.listener.AbstractWorkflowProcessStarterAfterExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.wf.WorkflowServiceExt;

/**
 * @author Mikhail Mikhailov
 * Workflow starter on delete operations.
 */
public class DataRecordDeleteWorkflowProcessStarterAfterExecutor
        extends AbstractWorkflowProcessStarterAfterExecutor
        implements DataRecordAfterExecutor<DeleteRequestContext>, AbstractDataRecordDeleteCommonExecutor<DeleteRequestContext> {

    /**
     * Workflow service.
     */
    @Autowired(required = false)
    private WorkflowServiceExt workflowService;
    /**
     * Constructor.
     */
    public DataRecordDeleteWorkflowProcessStarterAfterExecutor() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.data.listener.DataRecordExecutor#execute(com.unidata.mdm.backend.common.context.CommonRequestContext)
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {
        // Skip on origin deactivate.
        if (ctx.isWorkflowAction() || ctx.isInactivateOrigin() || ctx.isBatchUpsert()) {
            return true;
        }

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            if (keys.isPending()) {

                // This may be either RECORD_EDIT for periods or RECORD_DELETE for delete operations
                WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_DELETE_WF_ASSIGNMENTS);
                if (assignment == null || workflowService == null) {
                    return true;
                }

                // Skip if a process instance is already running for this etalon id
                GetProcessRequestContext pCtx = new GetProcessRequestContextBuilder()
                        .processKey(keys.getEtalonKey().getId())
                        .skipVariables(true)
                        .build();

                WorkflowProcessDTO current = workflowService.process(pCtx);
                if (current != null) {
                    return true;
                }

                ctx.skipNotification();

                EtalonRecord etalon = getCurrentEtalonRecord(ctx);
                return workflowService.start(
                    createStartWorkflowContext(assignment, keys, etalon, true, true,
                        ctx.getOperationId(), ctx.getValidFrom(), ctx.getValidTo()));
            }

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

}
