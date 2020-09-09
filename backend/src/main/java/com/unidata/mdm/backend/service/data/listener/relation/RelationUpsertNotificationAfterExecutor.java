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
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.listener.AbstractExternalNotificationExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.notification.ProcessedAction;

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createRelationUpsertNotification;

/**
 * Sending notification after update relation
 * @author Dmitry Kopin on 20.04.2017.
 */
public class RelationUpsertNotificationAfterExecutor extends AbstractExternalNotificationExecutor<UpsertRelationRequestContext>
        implements DataRecordAfterExecutor<UpsertRelationRequestContext> {
    /**
     * Constructor.
     */
    public RelationUpsertNotificationAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UnidataMessageDef createMessage(UpsertRelationRequestContext ctx) {
        EtalonRelation etalonRelation = ctx.getFromStorage(StorageId.RELATIONS_ETALON_DATA);
        UpsertAction action = ctx.getFromStorage(StorageId.RELATIONS_UPSERT_EXACT_ACTION);

        if (etalonRelation != null
                && etalonRelation.getInfoSection().getApproval() == ApprovalState.APPROVED
                && action != UpsertAction.NO_ACTION) {
            RelationKeys keys = ctx.relationKeys();
            return createRelationUpsertNotification(etalonRelation, keys, action, ctx.getOperationId());
        }
        return null;
    }

    @Override
    protected ProcessedAction getProcessedAction() {
        return ProcessedAction.UPSERT_RELATION;
    }

    @Override
    protected RecordKeys getRecordKeys(UpsertRelationRequestContext ctx) {
        return ((RelationKeys)ctx.getFromStorage(StorageId.RELATIONS_RELATION_KEY)).getFrom();
    }
}
