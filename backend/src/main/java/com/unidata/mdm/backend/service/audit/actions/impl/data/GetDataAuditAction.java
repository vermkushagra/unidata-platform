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

import static com.unidata.mdm.backend.service.search.Event.DATE_FORMATTER;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class GetDataAuditAction extends DataAuditAction {

    private static final String ACTION_NAME = "GET";

    @Override
    public void enrichEvent(Event event, Object... input) {
        GetRequestContext context = (GetRequestContext) input[0];
        putRecordInfo(context, event);
        Date forDate = context.getForDate();
        Date forLastUpdate = context.getForLastUpdate();
        String details = forDate == null ? StringUtils.EMPTY : "На " + DATE_FORMATTER.get().format(forDate) + ". ";
        String lastUpdateDetails = forLastUpdate == null ?
                StringUtils.EMPTY :
                "С последней датой обновления " + DATE_FORMATTER.get().format(forLastUpdate) + ".";
        details = details + lastUpdateDetails;
        event.putDetails(details);
        event.putOperationId(context.getOperationId());
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof GetRequestContext;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}
