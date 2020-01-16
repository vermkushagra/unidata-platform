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

package org.unidata.mdm.core.util;

import org.unidata.mdm.core.notification.NotificationSystemConstants;
import org.unidata.mdm.core.type.security.SecurityToken;

import java.util.Map;

public final class SystemInfoUtil {
    private SystemInfoUtil() {}

    private static final String UNKNOWN = "unknown";

    public static Map<String, String> systemInfo() {
        final SecurityToken securityTokenForCurrentUser = SecurityUtils.getSecurityTokenForCurrentUser();
        return Maps.of(
                NotificationSystemConstants.LOGIN, SecurityUtils.getCurrentUserName(),
                NotificationSystemConstants.CLIENT_IP, clientIp(securityTokenForCurrentUser),
                NotificationSystemConstants.SERVER_IP, serverIp(securityTokenForCurrentUser),
                NotificationSystemConstants.ENDPOINT, endpoint(securityTokenForCurrentUser)
        );
    }

    private static String clientIp(SecurityToken securityTokenForCurrentUser) {
        return securityTokenForCurrentUser != null && securityTokenForCurrentUser.getUserIp() != null ?
                securityTokenForCurrentUser.getUserIp() :
                UNKNOWN;
    }

    private static String serverIp(SecurityToken securityTokenForCurrentUser) {
        return securityTokenForCurrentUser != null && securityTokenForCurrentUser.getServerIp() != null ?
                securityTokenForCurrentUser.getServerIp() :
                UNKNOWN;
    }

    private static String endpoint(SecurityToken securityTokenForCurrentUser) {
        return securityTokenForCurrentUser != null && securityTokenForCurrentUser.getEndpoint() != null ?
                securityTokenForCurrentUser.getEndpoint().name() :
                UNKNOWN;
    }
}
