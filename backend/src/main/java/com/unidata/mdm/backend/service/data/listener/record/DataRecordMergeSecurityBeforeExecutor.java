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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
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
 * Merge request context security enrichment.
 */
public class DataRecordMergeSecurityBeforeExecutor
    implements RequestContextSetup<MergeRequestContext>, DataRecordBeforeExecutor<MergeRequestContext> {

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(DataRecordMergeSecurityBeforeExecutor.class);

    /**
     * Constructor.
     */
    public DataRecordMergeSecurityBeforeExecutor() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.data.listener.DataRecordExecutor#execute(com.unidata.mdm.backend.common.context.CommonRequestContext)
     */
    @Override
    public boolean execute(MergeRequestContext ctx) {

        RecordKeys keys = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS);

        putResourceRights(ctx, StorageId.DATA_MERGE_RIGHTS, keys.getEntityName());
        putWorkflowAssignments(ctx, StorageId.DATA_MERGE_WF_ASSIGNMENTS, keys.getEntityName(), WorkflowProcessType.RECORD_MERGE);

        Right rights = ctx.getFromStorage(StorageId.DATA_MERGE_RIGHTS);
        if (!rights.isUpdate()) {
            final String message = "The user '{}' has no or unsufficient merge rights for resource '{}'. Merge denied.";
            LOGGER.info(message, SecurityUtils.getCurrentUserName(), keys.getEntityName());
            throw new SystemSecurityException(message,
                    ExceptionId.EX_DATA_MERGE_NO_RIGHTS, SecurityUtils.getCurrentUserName(), keys.getEntityName());
        }

        return true;
    }

}
