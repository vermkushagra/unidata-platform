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
