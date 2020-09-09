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

package com.unidata.mdm.backend.util;

import org.slf4j.MDC;

import com.unidata.mdm.backend.MDCKeys;

/**
 * @author Michael Yashin. Created on 29.03.2015.
 */
public class MDCUtils {

    public static String generateRequestUuid() {
        return IdUtils.v1String();
    }

    public static String getRequestUuid() {
        return MDC.get(MDCKeys.REQUEST_ID);
    }

    public static void setCommonMdcInfo(String requestId, String userLogin) {
        MDC.put(MDCKeys.REQUEST_ID, requestId);
        MDC.put(MDCKeys.USER_LOGIN, userLogin);
    }

    public static void setUserLogin(String userLogin) {
        MDC.put(MDCKeys.USER_LOGIN, userLogin);
    }

    public static void removeCommonMdcInfo() {
        MDC.remove(MDCKeys.REQUEST_ID);
        MDC.remove(MDCKeys.USER_LOGIN);
    }

}
