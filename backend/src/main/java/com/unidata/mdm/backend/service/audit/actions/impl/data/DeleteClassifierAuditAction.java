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

import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Mikhail Mikhailov
 *
 */
public class DeleteClassifierAuditAction extends DataAuditAction {
    /**
     * Action name.
     */
    private static final String ACTION_NAME = "DELETE_CLASSIFIER";

    /**
     * {@inheritDoc}
     */
    @Override
    public void enrichEvent(Event event, Object... input) {

        DeleteClassifierDataRequestContext context = (DeleteClassifierDataRequestContext) input[0];
        putRecordInfo(context, event);

        if (context.isInactivateEtalon() || context.isWipe()) {
            String type = context.isWipe() ? "Физическое " : "Логическое ";
            event.putDetails(type + "удаление записи данных классификатора.");
        } else if (context.isInactivateOrigin()) {
            event.putDetails("Удаление оригинальной записи данных классификатора.");
        } else if (context.isInactivatePeriod()) {
            String details = "Инактивация периода записи данных классификатора.";
            String range = getValidityRange(context);
            event.putDetails(details + "|" + range);
        }

        enrichByRowNum(event, context.getFromStorage(StorageId.IMPORT_ROW_NUM));
        enrichByImportSource(event, context.getFromStorage(StorageId.IMPORT_RECORD_SOURCE));
        event.putOperationId(context.getOperationId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof UpsertClassifierDataRequestContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return ACTION_NAME;
    }

}
