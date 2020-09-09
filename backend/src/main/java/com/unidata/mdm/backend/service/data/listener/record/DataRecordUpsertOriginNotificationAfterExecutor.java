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

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createOriginUpsertNotification;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.listener.AbstractExternalNotificationExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.notification.ProcessedAction;

/**
 * @author Mikhail Mikhailov Upsert origin notification executor.
 */
public class DataRecordUpsertOriginNotificationAfterExecutor
		extends AbstractExternalNotificationExecutor<UpsertRequestContext>
		implements DataRecordAfterExecutor<UpsertRequestContext>{

	/**
	 * Constructor.
	 */
	public DataRecordUpsertOriginNotificationAfterExecutor() {
		super();
	}

	@Override
	protected UnidataMessageDef createMessage(UpsertRequestContext ctx) {
		OriginRecord origin = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
		UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
		if (origin != null
		 && action != UpsertAction.NO_ACTION) {
			RecordKeys keys = getRecordKeys(ctx);
			return createOriginUpsertNotification(origin, action, keys.getSupplementaryKeys(), ctx.getOperationId());
		}
		return null;
	}

    @Override
    protected ProcessedAction getProcessedAction() {
        return ProcessedAction.UPSERT_ORIGIN;
    }

    @Override
    protected RecordKeys getRecordKeys(UpsertRequestContext upsertRequestContext) {
        return upsertRequestContext.getFromStorage(StorageId.DATA_UPSERT_KEYS);
    }
}
