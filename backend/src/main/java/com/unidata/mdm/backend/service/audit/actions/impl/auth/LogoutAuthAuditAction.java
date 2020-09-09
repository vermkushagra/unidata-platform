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

package com.unidata.mdm.backend.service.audit.actions.impl.auth;

import static com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter.PARAM_CLIENT_IP;
import static com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter.PARAM_DETAILS;
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

public class LogoutAuthAuditAction extends AuthAuditAction {
    public static final String ACTION_NAME = "LOGOUT";

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
        if (params.get(PARAM_DETAILS) != null) {
            event.putDetails(params.get(PARAM_DETAILS).toString());
        }
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}
