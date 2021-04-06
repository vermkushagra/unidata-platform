package com.unidata.mdm.backend.service.data.listener.record;

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonPeriodSoftDeleteNotification;
import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonSoftDeleteNotification;
import static com.unidata.mdm.backend.service.notification.NotificationUtils.createOriginSoftDeleteNotification;
import static com.unidata.mdm.backend.service.notification.NotificationUtils.createWipeDeleteNotification;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.data.listener.AbstractExternalNotificationExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.notification.ProcessedAction;

/**
 * @author Mikhail Mikhailov Delete notification.
 */
public class DataRecordDeleteNotificationAfterExecutor
		extends AbstractExternalNotificationExecutor<DeleteRequestContext>
		implements DataRecordAfterExecutor<DeleteRequestContext>{

	/**
	 * Constructor.
	 */
	public DataRecordDeleteNotificationAfterExecutor() {
		super();
	}


	@Override
	protected UnidataMessageDef createMessage(DeleteRequestContext ctx) {

	    if (!ctx.sendNotification()) {
	        return null;
	    }

		RecordKeys keys = ctx.getFromStorage(StorageId.DATA_DELETE_KEYS);

		if (keys.getOriginStatus() == RecordStatus.ACTIVE && ctx.isInactivateOrigin()) {
			return createOriginSoftDeleteNotification(keys.getOriginKey(), keys.getEtalonKey(), keys.getSupplementaryKeys(), ctx.getOperationId());
		}

		if (keys.getEtalonStatus() == RecordStatus.ACTIVE && ctx.isInactivateEtalon()) {
		    return createEtalonSoftDeleteNotification(keys.getEtalonKey(), keys.getSupplementaryKeys(), ctx.getOperationId());
		}

		if (ctx.isInactivatePeriod()) {
		    return createEtalonPeriodSoftDeleteNotification(
		            keys.getEtalonKey(), ctx.getFromStorage(StorageId.DATA_DELETE_ETALON_RECORD), keys.getSupplementaryKeys(), ctx.getOperationId());
		}

		if (ctx.isWipe()) {
			return createWipeDeleteNotification(keys.getOriginKey(), keys.getEtalonKey(), keys.getSupplementaryKeys(), ctx.getOperationId());
		}

		return null;
	}

    @Override
    protected ProcessedAction getProcessedAction() {
        return ProcessedAction.DELETE;
    }

    @Override
    protected RecordKeys getRecordKeys(DeleteRequestContext deleteRequestContext) {
        return deleteRequestContext.getFromStorage(StorageId.DATA_DELETE_KEYS);
    }
}
