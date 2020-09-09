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

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class DeleteDataAuditAction extends DataAuditAction {

    private static final String ACTION_NAME = "DELETE";

    @Override
    public void enrichEvent(Event event, Object... input) {
        DeleteRequestContext context = (DeleteRequestContext) input[0];
        putRecordInfo(context, event);
        if (context.isInactivateEtalon() || context.isWipe()) {
            String type = context.isWipe() ? "Физическое " : "Логическое ";
            event.putDetails(type + "удаление мастер записи.");
        } else if (context.isInactivateOrigin()) {
            event.putDetails("Удаление оригинальной записи");
        } else if (context.isInactivatePeriod()) {
            String details = "Инактивация периода мастер записи.";
            String range = getValidityRange(context);
            event.putDetails(details + "|" + range);
        }
        event.putOperationId(context.getOperationId());
        enrichByRowNum(event, context.getFromStorage(StorageId.IMPORT_ROW_NUM));
        enrichByImportSource(event, context.getFromStorage(StorageId.IMPORT_RECORD_SOURCE));
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof DeleteRequestContext;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}
