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

package com.unidata.mdm.backend.service.data.listener.classifier;

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonClassifierPeriodSoftDeleteNotification;
import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonClassifierSoftDeleteNotification;
import static com.unidata.mdm.backend.service.notification.NotificationUtils.createOriginClassifierSoftDeleteNotification;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.dto.DeleteClassifierDTO;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.data.listener.AbstractExternalNotificationExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.notification.ProcessedAction;

/**
 * @author Dmitry Kopin Delete classifier notification executor.
 */
public class ClassifierDeleteNotificationAfterExecutor
		extends AbstractExternalNotificationExecutor<DeleteClassifierDataRequestContext>
		implements DataRecordAfterExecutor<DeleteClassifierDataRequestContext>{

	/**
	 * Constructor.
	 */
	public ClassifierDeleteNotificationAfterExecutor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UnidataMessageDef createMessage(DeleteClassifierDataRequestContext ctx) {

	    DeleteClassifierDTO classifierResult = ctx.getFromStorage(StorageId.CLASSIFIERS_RESULT);
		if(classifierResult != null && classifierResult.getClassifierKeys() != null){

		    ClassifierKeys keys = classifierResult.getClassifierKeys();
			if (!ctx.sendNotification()) {
				return null;
			}

			if (keys.getOriginStatus() == RecordStatus.ACTIVE && ctx.isInactivateOrigin()) {
				return createOriginClassifierSoftDeleteNotification(keys, ctx.getOperationId());
			}

			if (keys.getEtalonStatus() == RecordStatus.ACTIVE && ctx.isInactivateEtalon()) {
				return createEtalonClassifierSoftDeleteNotification(keys, ctx.getOperationId());
			}

			if (ctx.isInactivatePeriod()) {
				return createEtalonClassifierPeriodSoftDeleteNotification(keys, ctx.getOperationId());
			}
		}
		return null;
	}

    @Override
    protected ProcessedAction getProcessedAction() {
        return ProcessedAction.DELETE_CLASSIFIER;
    }

    @Override
    protected RecordKeys getRecordKeys(DeleteClassifierDataRequestContext ctx) {
        return ctx.classifierKeys().getRecord();
    }
}
