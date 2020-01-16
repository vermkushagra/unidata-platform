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

package org.unidata.mdm.data.notification;

import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.type.pipeline.PipelineInput;

/**
 * @author Alexander Malyshev
 */
public final class NotificationDataUtils {
    private NotificationDataUtils() { }

    public static String eventType(PipelineInput pipelineInput) {
        if (pipelineInput instanceof UpsertRequestContext) {
            return NotificationDataConstants.RECORD_UPSERT_EVENT_TYPE;
        }
        if (pipelineInput instanceof GetRequestContext) {
            return NotificationDataConstants.RECORD_GET_EVENT_TYPE;
        }
        if (pipelineInput instanceof DeleteRequestContext) {
            return NotificationDataConstants.RECORD_DELETE_EVENT_TYPE;
        }
        final String contextType = pipelineInput.getClass().getName();
        throw new PlatformFailureException(
                "Unknown context type " + contextType,
                DataExceptionIds.EX_DATA_AUDIT_UNKNOW_PIPELINE_EXECUTION_CONTEXT,
                contextType
        );
    }
}
