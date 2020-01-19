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
public final class SecurityEventTypeConstants {
    private SecurityEventTypeConstants() {}

    public static final String ROLE_CREATE_EVENT_TYPE = "role_create";
    public static final String ROLE_DELETE_EVENT_TYPE = "role_delete";
    public static final String ROLE_UPDATE_EVENT_TYPE = "role_update";
    public static final String ROLE_LABEL_ATTACH_EVENT_TYPE = "role_label_attach";
    public static final String LABEL_CREATE_EVENT_TYPE = "label_create";
    public static final String LABEL_UPDATE_EVENT_TYPE = "label_update";
    public static final String LABEL_DELETE_EVENT_TYPE = "label_delete";


    public static final String LOGIN_TYPE = "login";
    public static final String LOGOUT_TYPE = "logout";
}
