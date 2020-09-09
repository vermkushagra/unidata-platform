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

import com.unidata.mdm.backend.common.context.SplitContext;
import com.unidata.mdm.backend.service.search.Event;
import com.unidata.mdm.backend.util.MessageUtils;

/**
 * @author Dmitry Kopin on 31.07.2018.
 */
public class SplitDataAuditAction extends DataAuditAction {

    private static final String ACTION_NAME = "SPLIT";

    @Override
    public void enrichEvent(Event event, Object... input) {
        SplitContext context = (SplitContext) input[0];
        event.putEntity(context.getEntityName());
        event.addEtalonId(context.getNewEtalonKey().getId());
        event.addOriginId(context.getOriginKey());
        event.addExternalId(context.getExternalId());
        event.putSourceSystem(context.getSourceSystem());

        event.putDetails(MessageUtils.getMessage("app.audit.record.operation.split.details", context.getOldEtalonKey().getId()));
        event.putOperationId(context.getOperationId());
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof SplitContext;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}
