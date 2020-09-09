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

package com.unidata.mdm.backend.service.audit.actions.impl.data;

import static com.unidata.mdm.backend.common.types.UpsertAction.INSERT;
import static com.unidata.mdm.backend.common.types.UpsertAction.NO_ACTION;
import static com.unidata.mdm.backend.common.types.UpsertAction.UPDATE;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Mikhail Mikhailov
 * Upsert relation audit action.
 */
public class UpsertRelationAuditAction extends DataAuditAction {
    /**
     * Action name.
     */
    public static final String ACTION_NAME = "UPSERT_RELATION";

    public static final String RELATION_INSERT_ACTION_NAME = "RELATION_" + INSERT.name() + "_ACTION";

    public static final String RELATION_UPDATE_ACTION_NAME = "RELATION_" + UPDATE.name() + "_ACTION";

    /**
     * {@inheritDoc}
     */
    @Override
    public void enrichEvent(Event event, Object... input) {

        UpsertRelationRequestContext context = (UpsertRelationRequestContext) input[0];
        putRecordInfo(context, event);

        UpsertAction action = context.getFromStorage(StorageId.RELATIONS_UPSERT_EXACT_ACTION);
        action = action == null ? NO_ACTION : action;
        String actionDetails = action == INSERT ? NEW_RECORD : action == UPDATE ? UPDATE_RECORD : "";
        String range = getValidityRange(context);
        event.putDetails(actionDetails + "|" + range);
        event.putAction(
                action == INSERT ?
                        RELATION_INSERT_ACTION_NAME :
                        action == UPDATE ? RELATION_UPDATE_ACTION_NAME : action.name()
        );

        enrichByRowNum(event, context.getFromStorage(StorageId.IMPORT_ROW_NUM));
        enrichByImportSource(event, context.getFromStorage(StorageId.IMPORT_RECORD_SOURCE));
        event.putOperationId(context.getOperationId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof UpsertRelationRequestContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return ACTION_NAME;
    }

}
