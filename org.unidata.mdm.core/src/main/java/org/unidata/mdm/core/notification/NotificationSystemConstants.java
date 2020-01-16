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

package org.unidata.mdm.core.notification;

/**
 * @author Alexander Malyshev
 */
public final class NotificationSystemConstants {
    private NotificationSystemConstants() { }

    public static final String LOGIN = "login";
    public static final String EXCEPTION = "exception";
    public static final String ERROR = "error";
    public static final String CLIENT_IP = "clientIp";
    public static final String SERVER_IP = "serverIp";
    public static final String ENDPOINT = "endpoint";
    public static final String WHEN_HAPPENED = "when_happened";
    public static final String TYPE = "type";
}
