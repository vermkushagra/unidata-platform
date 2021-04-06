package com.unidata.mdm.backend.service.data.listener.record;

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonRestoreNotification;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.listener.AbstractExternalNotificationExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
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
		return createEtalonRestoreNotification(keys.getEtalonKey(),keys.getSupplementaryKeys(),keys.getEntityName(), ctx.getOperationId());
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
