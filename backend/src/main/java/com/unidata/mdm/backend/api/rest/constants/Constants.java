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

package com.unidata.mdm.backend.api.rest.constants;

/**
 * Created by Michael Yashin on 11.02.2015.
 */
public interface Constants {

    public final static String DATABASE_REQUIRED_SCHEMA_VERSION = "1.0.2";

    public final static int REST_DEFAULT_PAGE_SIZE = 100;

    public final static String HTTP_HEADER_REQUEST_UUID = "X-EGAIS-Request-Uuid";
    public final static String HTTP_HEADER_TOKEN_VALIDITY = "X-EGAIS-Token-Validity-Seconds";

    public static final String APP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public final static String USER_LOGIN_JOB = "timed-job";
}
