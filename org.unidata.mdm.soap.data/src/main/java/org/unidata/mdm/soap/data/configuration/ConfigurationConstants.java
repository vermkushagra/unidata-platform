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

package org.unidata.mdm.soap.data.configuration;

/**
 * @author Alexander Malyshev
 */
@Deprecated
public final class ConfigurationConstants {
    private ConfigurationConstants() { }

    /**
     * Current main API version..
     */
    public static final String API_VERSION_PROPERTY = "unidata.api.version";

    /**
     * Current main platform version..
     */
    public static final String PLATFORM_VERSION_PROPERTY = "unidata.platform.version";

    public static final String DATA_SOAP_UPSERT_MAX_ATTEMPT_COUNT = "unidata.data.soap.upsert.max.attempt.count";
}
