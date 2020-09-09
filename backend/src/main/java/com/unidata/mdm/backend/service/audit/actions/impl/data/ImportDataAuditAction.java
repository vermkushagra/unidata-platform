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

import com.unidata.mdm.backend.po.ImportErrorPO;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class ImportDataAuditAction extends DataAuditAction {

    private static final String ACTION_NAME = "UPSERT";

    @Override
    public void enrichEvent(Event event, Object... input) {
        ImportErrorPO errorPO = (ImportErrorPO) input[0];
        event.putOperationId(errorPO.getOperationId());
        enrichByRowNum(event, errorPO.getIndex());
        Object existing = event.get(Event.DETAILS);
        String row = "|Запись: " + errorPO.getDescription();
        event.reclaim(Event.DETAILS, existing.toString() + row);
        event.putAction(UpsertDataAuditAction.DATA_INSERT_ACTION_NAME);
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof ImportErrorPO;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}
