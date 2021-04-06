package com.unidata.mdm.backend.service.data.listener.relation;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.service.data.listener.AbstractExternalNotificationExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.notification.ProcessedAction;
import com.unidata.mdm.meta.RelationDef;

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonRelationPeriodSoftDeleteNotification;
import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonRelationSoftDeleteNotification;
import static com.unidata.mdm.backend.service.notification.NotificationUtils.createOriginRelationSoftDeleteNotification;

/**
 * Sending notification after delete relation
 * @author Dmitry Kopin on 20.04.2017.
 */
public class RelationDeleteNotificationAfterExecutor extends AbstractExternalNotificationExecutor<DeleteRelationRequestContext>
        implements DataRecordAfterExecutor<DeleteRelationRequestContext> {
    /**
     * Constructor.
     */
    public RelationDeleteNotificationAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UnidataMessageDef createMessage(DeleteRelationRequestContext ctx) {

            if (!ctx.sendNotification()) {
                return null;
            }


            RelationDef relationDef = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
            if(relationDef != null){
                RelationKeys keys = ctx.relationKeys();
                RelationType relationType = RelationType.valueOf(relationDef.getRelType().name());
                if (keys.getOriginStatus() == RecordStatus.ACTIVE && ctx.isInactivateOrigin()) {
                    return createOriginRelationSoftDeleteNotification(keys, relationType, ctx.getOperationId());
                }

                if (keys.getEtalonStatus() == RecordStatus.ACTIVE && ctx.isInactivateEtalon()) {
                    return createEtalonRelationSoftDeleteNotification(keys, relationType, ctx.getOperationId());
                }

                if (ctx.isInactivatePeriod()) {
                    return createEtalonRelationPeriodSoftDeleteNotification(keys, relationType, ctx.getOperationId());
                }

            }

        return null;
    }

    @Override
    protected ProcessedAction getProcessedAction() {
        return ProcessedAction.DELETE_RELATION;
    }

    @Override
    protected RecordKeys getRecordKeys(DeleteRelationRequestContext ctx) {
        return ((RelationKeys)ctx.getFromStorage(StorageId.RELATIONS_RELATION_KEY)).getFrom();
    }
}
