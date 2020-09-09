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

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonRestoreNotification;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.service.data.listener.AbstractExternalNotificationExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.notification.NotificationUtils;
import com.unidata.mdm.backend.service.notification.ProcessedAction;

/**
 * Data record restore
 */
public class DataRecordRestoreEtalonNotificationExecutor
		extends AbstractExternalNotificationExecutor<UpsertRequestContext>
		implements DataRecordAfterExecutor<UpsertRequestContext> {

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.backend.service.data.listener.
	 * AbstractExternalNotificationExecutor#createMessage(com.unidata.mdm.
	 * backend.service.ctx.CommonRequestContext)
	 */
	@Override
	protected UnidataMessageDef createMessage(UpsertRequestContext ctx) {

	    RecordKeys keys = ctx.keys();

	    Map<TimeIntervalDTO, Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>>> data
            = ctx.getFromStorage(StorageId.DATA_INTERVALS_AFTER);

        Collection<EtalonRecord> etalons = Collections.emptyList();
        if (MapUtils.isNotEmpty(data)) {

            etalons = data.entrySet().stream()
                    .map(Entry::getValue)
                    .map(Pair::getKey)
                    .collect(Collectors.toList());
        }

	    // 1. Period
	    if (ctx.isPeriodRestore()) {
	        return NotificationUtils
	            .createEtalonPeriodRestoreNotification(
	                    keys.getEtalonKey(), keys.getSupplementaryKeys(), etalons, ctx.getOperationId());
	    }

	    // 2. Record
		return createEtalonRestoreNotification(
		        keys.getEtalonKey(), keys.getSupplementaryKeys(), etalons, ctx.getOperationId());
	}

    @Override
    protected ProcessedAction getProcessedAction() {
        return ProcessedAction.RESTORE;
    }

    @Override
    protected RecordKeys getRecordKeys(UpsertRequestContext upsertRequestContext) {
        return upsertRequestContext.keys();
    }
}
