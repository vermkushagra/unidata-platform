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

package org.unidata.mdm.data.notification;

/**
 * @author Alexander Malyshev
 */
public final class NotificationDataConstants {
    private NotificationDataConstants() { }

    public static final String CONTEXT_FILED = "context";

    public static final String RECORD_UPSERT_EVENT_TYPE = "record-upsert";
    public static final String RECORD_GET_EVENT_TYPE = "record-get";
    public static final String RECORD_DELETE_EVENT_TYPE = "record-delete";
}
