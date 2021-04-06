/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonMergeNotification;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.listener.AbstractExternalNotificationExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.notification.ProcessedAction;


/**
 * The Class DataRecordMergeEtalonNotificationAfterExecutor.
 *
 * @author Mikhail Mikhailov Merge notification.
 */
public class DataRecordMergeEtalonNotificationAfterExecutor
	extends AbstractExternalNotificationExecutor<MergeRequestContext>
    implements DataRecordAfterExecutor<MergeRequestContext>  {

    /**
     * Constructor.
     */
    public DataRecordMergeEtalonNotificationAfterExecutor() {
        super();
    }


	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.service.data.listener.AbstractExternalNotificationExecutor#createMessage(com.unidata.mdm.backend.common.context.CommonRequestContext)
	 */
	@Override
	protected UnidataMessageDef createMessage(MergeRequestContext ctx) {

        RecordKeys master = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS);
        List<RecordKeys> duplicates = ctx.getFromStorage(StorageId.DATA_MERGE_DUPLICATES_KEYS);
        List<EtalonKey> duplicatesEtalonKeys = duplicates.stream().map(RecordKeys::getEtalonKey).collect(toList());
        List<OriginKey> allAffectedOriginKeys = duplicates.stream()
                                                          .map(RecordKeys::getSupplementaryKeys)
                                                          .flatMap(Collection::stream)
                                                          .collect(toList());
        allAffectedOriginKeys.addAll(master.getSupplementaryKeys());
        return createEtalonMergeNotification(duplicatesEtalonKeys, master.getEtalonKey(),allAffectedOriginKeys, ctx.getOperationId());
    }

    @Override
    protected ProcessedAction getProcessedAction() {
        return ProcessedAction.MERGE;
    }

    @Override
    protected RecordKeys getRecordKeys(MergeRequestContext mergeRequestContext) {
        return mergeRequestContext.keys();
    }
}
