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

package com.unidata.mdm.login.api.v5;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import com.unidata.mdm.util.ClientIpUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.annotations.SchemaValidation;
import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.api.v5.CredentialsDef;
import com.unidata.mdm.api.v5.SessionTokenDef;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter;
import com.unidata.mdm.backend.common.security.Endpoint;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.error_handling.v5.ApiFaultType;
import com.unidata.mdm.security.v5.InfoType;

/**
 * The Class LoginSOAPServiceImpl.
 */
@SchemaValidation(type = SchemaValidationType.NONE)
public class LoginSOAPServiceImpl extends LoginPortImpl {
	
	/** The security service. */
	@Autowired
	private SecurityService securityService;
	
	/** The jaxws context. */
	@Resource
	private WebServiceContext jaxwsContext;

	/* (non-Javadoc)
	 * @see com.unidata.mdm.login.api.v5.LoginPortImpl#login(com.unidata.mdm.api.v5.CredentialsDef, javax.xml.ws.Holder)
	 */
	@Override
	public SessionTokenDef login(CredentialsDef request, Holder<InfoType> info) throws ApiFault {
		if (StringUtils.isEmpty(request.getUsername()) || StringUtils.isEmpty(request.getPassword())) {
			throw new ApiFault("Username or password not provided!", new ApiFaultType().withErrorCode("INVALID_LOGIN")
					.withErrorMessage("Username or password not provided!"));
		}
		SessionTokenDef result = new SessionTokenDef();
		SecurityToken securityToken = securityService
				.login(fillSecurityParams(null, request.getUsername(), request.getPassword(), "Вход"));
		result.setToken(securityToken.getToken());
		return result;
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.login.api.v5.LoginPortImpl#logout(com.unidata.mdm.api.v5.SessionTokenDef, javax.xml.ws.Holder)
	 */
	@Override
	public SessionTokenDef logout(SessionTokenDef request, Holder<InfoType> info) throws ApiFault {
		String token = request.getToken();
		if (StringUtils.isEmpty(token)) {
			throw new ApiFault("Token not provided!",
					new ApiFaultType().withErrorCode("INVALID_TOKEN").withErrorMessage("Token not provided!"));
		}
		boolean isLoggedOut = securityService.logout(request.getToken(),
				fillSecurityParams(request.getToken(), null, null, "Самостоятельный выход"));
		if (!isLoggedOut) {
			throw new ApiFault("Token invalid!",
					new ApiFaultType().withErrorCode("INVALID_TOKEN").withErrorMessage("Token invalid!"));
		}
		return request;
	}

	/**
	 * Fill security params.
	 *
	 * @param token the token
	 * @param userName the user name
	 * @param password the password
	 * @param details the details
	 * @return the map
	 */
	private Map<AuthenticationSystemParameter, Object> fillSecurityParams(String token, String userName,
			String password, String details) {
		HttpServletRequest h = (HttpServletRequest) jaxwsContext.getMessageContext()
				.get(MessageContext.SERVLET_REQUEST);
		if (StringUtils.isEmpty(userName)) {
			userName = securityService.getUserByToken(token).getLogin();
		}
		Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
		params.put(AuthenticationSystemParameter.PARAM_USER_NAME, userName);
		if (!StringUtils.isEmpty(password)) {
			params.put(AuthenticationSystemParameter.PARAM_USER_PASSWORD, password);
		}
		params.put(AuthenticationSystemParameter.PARAM_HTTP_SERVLET_REQUEST, h);
		params.put(AuthenticationSystemParameter.PARAM_CLIENT_IP, ClientIpUtil.clientIp(h));
		params.put(AuthenticationSystemParameter.PARAM_SERVER_IP, h.getLocalAddr());
		params.put(AuthenticationSystemParameter.PARAM_ENDPOINT, Endpoint.SOAP);
		params.put(AuthenticationSystemParameter.PARAM_DETAILS, details);
		return params;
	}
}
