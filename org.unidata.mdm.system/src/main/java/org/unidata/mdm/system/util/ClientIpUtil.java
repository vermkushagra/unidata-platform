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

package org.unidata.mdm.system.util;

import javax.servlet.http.HttpServletRequest;

public final class ClientIpUtil {
    private ClientIpUtil() { }

    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    public static String clientIp(final HttpServletRequest httpServletRequest) {
        if (httpServletRequest == null) {
            return null;
        }
        return httpServletRequest.getHeader(X_FORWARDED_FOR_HEADER) != null ?
                httpServletRequest.getHeader(X_FORWARDED_FOR_HEADER).split(",")[0] :
                httpServletRequest.getRemoteAddr();
    }
}
