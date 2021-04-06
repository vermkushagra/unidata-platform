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
