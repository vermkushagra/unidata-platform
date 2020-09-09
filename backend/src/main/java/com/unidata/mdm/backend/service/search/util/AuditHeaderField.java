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

package com.unidata.mdm.backend.service.search.util;

import com.unidata.mdm.backend.common.search.SearchObjectConvert;
import com.unidata.mdm.backend.common.search.types.SearchType;
import com.unidata.mdm.backend.common.search.types.ServiceSearchType;
import com.unidata.mdm.backend.service.search.Event;

public enum AuditHeaderField implements SearchObjectConvert<Event> {

    ETALON_ID(Event.ETALON_ID, "string"),

    ORIGIN_ID(Event.ORIGIN_ID, "string"),

    EXTERNAL_ID(Event.EXTERNAL_ID, "string"),

    OPERATION_ID(Event.OPERATION_ID, "string"),

    SERVER_IP(Event.SERVER_IP, "string"),

    CLIENT_IP(Event.CLIENT_IP, "string"),

    ENDPOINT(Event.ENDPOINT, "string"),

    DETAILS(Event.DETAILS, "string"),

    DATE(Event.DATE, "date"),

    USER(Event.USER, "string"),

    ENTITY(Event.ENTITY, "string"),

    SUB_SYSTEM(Event.SUB_SYSTEM, "string"),

    ACTION(Event.ACTION, "string"),

    SOURCE_SYSTEM(Event.SOURCE_SYSTEM, "string"),

    SUCCESS(Event.SUCCESS, "boolean"),

    TASK_ID(Event.TASK_ID, "string");

    private final String field;

    private final String type;

    AuditHeaderField(String field, String type) {
        this.field = field;
        this.type = type;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public SearchType linkedSearchType() {
        return ServiceSearchType.AUDIT;
    }

    public String getType() {
        return type;
    }

    @Override
    public Object getIndexedElement(Event indexedObject) {
        return indexedObject.get(this.getField());
    }
}
