package com.unidata.mdm.backend.service.data.listener.classifier;

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createClassifierUpsertNotification;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.dto.UpsertClassifierDTO;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.listener.AbstractExternalNotificationExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.notification.ProcessedAction;

/**
 * @author Dmitry Kopin Upsert classifier notification executor.
 */
public class ClassifierUpsertNotificationAfterExecutor
		extends AbstractExternalNotificationExecutor<UpsertClassifierDataRequestContext>
		implements DataRecordAfterExecutor<UpsertClassifierDataRequestContext>{

	/**
	 * Constructor.
	 */
	public ClassifierUpsertNotificationAfterExecutor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UnidataMessageDef createMessage(UpsertClassifierDataRequestContext ctx) {

		UpsertClassifierDTO classifierResult = ctx.getFromStorage(StorageId.CLASSIFIERS_RESULT);
		if (classifierResult != null) {

			EtalonClassifier etalonClassifier = classifierResult.getEtalon();
			UpsertAction action = ctx.getFromStorage(StorageId.CLASSIFIERS_UPSERT_EXACT_ACTION);

			if (etalonClassifier != null
					&& etalonClassifier.getInfoSection().getApproval() == ApprovalState.APPROVED
					&& action != UpsertAction.NO_ACTION) {

				ClassifierKeys keys = ctx.classifierKeys();
				return createClassifierUpsertNotification(etalonClassifier, keys, action, ctx.getOperationId());
			}
		}
		return null;
	}

    @Override
    protected ProcessedAction getProcessedAction() {
        return ProcessedAction.UPSERT_CLASSIFIER;
    }

    @Override
    protected RecordKeys getRecordKeys(UpsertClassifierDataRequestContext ctx) {
        return ctx.classifierKeys().getRecord();
    }
}
