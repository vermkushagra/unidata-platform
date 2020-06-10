package com.unidata.mdm.util;

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
