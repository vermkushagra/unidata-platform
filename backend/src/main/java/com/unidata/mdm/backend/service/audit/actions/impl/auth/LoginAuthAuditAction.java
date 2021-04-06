package com.unidata.mdm.backend.service.audit.actions.impl.auth;

import static com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter.PARAM_CLIENT_IP;
import static com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter.PARAM_ENDPOINT;
import static com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter.PARAM_SERVER_IP;
import static com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter.PARAM_USER_NAME;

import java.util.Map;

import com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter;
import com.unidata.mdm.backend.common.security.Endpoint;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.dto.storage.UserInfo;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class LoginAuthAuditAction extends AuthAuditAction {
    public static final String ACTION_NAME = "LOGIN";


    @Override
    public void enrichEvent(Event event, Object... input) {
        Map<AuthenticationSystemParameter, Object> params = (Map<AuthenticationSystemParameter, Object>) input[0];
        SecurityToken securityToken = new SecurityToken();
        UserInfo userInfo = new UserInfo();
        String login = (String) params.get(PARAM_USER_NAME);
        login = login == null ? NO_LOGIN : login;
        userInfo.setLogin(login);
        securityToken.setUser(userInfo);
        securityToken.setEndpoint((Endpoint) params.get(PARAM_ENDPOINT));
        securityToken.setUserIp((String) params.get(PARAM_CLIENT_IP));
        securityToken.setServerIp((String) params.get(PARAM_SERVER_IP));
        event.setUserDetails(securityToken);
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}
