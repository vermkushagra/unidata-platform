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

import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class MergeDataAuditAction extends DataAuditAction {

    private static final String ACTION_NAME = "MERGE";

    @Override
    public void enrichEvent(Event event, Object... input) {
        MergeRequestContext context = (MergeRequestContext) input[0];
        putRecordInfo(context, event);
        context.getDuplicates().forEach(ctx -> putRecordInfo(ctx, event));

        if (context.isManual()) {
            event.putDetails("Ручное объединение");
        } else {
            event.putDetails("Автоматическое объединение");
        }
        event.putOperationId(context.getOperationId());
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof MergeRequestContext;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}
