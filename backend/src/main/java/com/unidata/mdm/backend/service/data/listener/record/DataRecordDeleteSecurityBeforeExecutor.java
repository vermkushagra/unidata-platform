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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemSecurityException;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.listener.RequestContextSetup;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.conf.WorkflowProcessType;


/**
 * @author Mikhail Mikhailov
 *
 */
public class DataRecordDeleteSecurityBeforeExecutor
    implements RequestContextSetup<DeleteRequestContext>, DataRecordBeforeExecutor<DeleteRequestContext> {

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(DataRecordDeleteSecurityBeforeExecutor.class);

    /**
     * Constructor.
     */
    public DataRecordDeleteSecurityBeforeExecutor() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.data.listener.DataRecordExecutor#execute(com.unidata.mdm.backend.common.context.CommonRequestContext)
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {
        RecordKeys keys = ctx.getFromStorage(StorageId.DATA_DELETE_KEYS);

        putResourceRights(ctx, StorageId.DATA_DELETE_RIGHTS, keys.getEntityName());
        Right rights = ctx.getFromStorage(StorageId.DATA_DELETE_RIGHTS);
        if (!rights.isDelete()) {
            if (ctx.isInactivatePeriod()) {
                if (!rights.isUpdate()) {
                    final String message = "The user '{}' has no or unsufficient update rights for resource '{}'. Delete denied.";
                    LOGGER.info(message, SecurityUtils.getCurrentUserName(), keys.getEntityName());
                    throw new SystemSecurityException(message,
                            ExceptionId.EX_DATA_UPSERT_UPDATE_NO_RIGHTS, SecurityUtils.getCurrentUserName(), keys.getEntityName());
                }
            } else {
                final String message = "The user '{}' has no or unsufficient delete rights for resource '{}'. Delete denied.";
                LOGGER.info(message, SecurityUtils.getCurrentUserName(), keys.getEntityName());
                throw new SystemSecurityException(message,
                        ExceptionId.EX_DATA_DELETE_NO_RIGHTS, SecurityUtils.getCurrentUserName(), keys.getEntityName());
            }
        }

        // Don't overwrite assignments, since they can be set somewhere else
        if (ctx.getFromStorage(StorageId.DATA_DELETE_WF_ASSIGNMENTS) == null) {
            // Set RECORD_EDIT for period delete
            if (ctx.isInactivatePeriod()) {
                putWorkflowAssignments(ctx, StorageId.DATA_DELETE_WF_ASSIGNMENTS,
                    keys.getEntityName(),
                    WorkflowProcessType.RECORD_EDIT);
            // Set RECORD_DELETE otherwise
            } else {
                putWorkflowAssignments(ctx, StorageId.DATA_DELETE_WF_ASSIGNMENTS,
                    keys.getEntityName(),
                    WorkflowProcessType.RECORD_DELETE);
            }
        }

        return true;
    }
}
