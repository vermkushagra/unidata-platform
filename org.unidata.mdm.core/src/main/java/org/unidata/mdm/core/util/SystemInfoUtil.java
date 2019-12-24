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
