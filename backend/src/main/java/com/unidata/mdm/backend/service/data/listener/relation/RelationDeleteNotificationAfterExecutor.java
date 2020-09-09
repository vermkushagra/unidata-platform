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
