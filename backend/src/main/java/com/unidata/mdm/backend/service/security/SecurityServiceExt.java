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

package com.unidata.mdm.backend.service.security;

import java.util.Locale;

import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

public interface SecurityServiceExt extends SecurityService, AfterContextRefresh {

    /**
     * Gets the token ttl.
     *
     * @return the tokenTTL
     */
    long getTokenTTL();

    /**
     * Sets the token ttl.
     *
     * @param tokenTTL the tokenTTL to set
     */
    void setTokenTTL(long tokenTTL);

    /**
     * @return the clusterName
     */
    String getClusterName();

    /**
     * @param clusterName the clusterName to set
     */
    void setClusterName(String clusterName);

    /**
     * @return the mapName
     */
    String getMapName();

    /**
     * @param mapName the mapName to set
     */
    void setMapName(String mapName);

    /**
     * Change user locale on token
     * @param login user login
     * @param locale new locale
     */
    void changeLocale(String login, Locale locale);
}