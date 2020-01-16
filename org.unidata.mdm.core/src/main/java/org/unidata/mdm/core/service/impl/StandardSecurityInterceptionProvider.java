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

package org.unidata.mdm.core.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.unidata.mdm.core.service.SecurityInterceptionProvider;
import org.unidata.mdm.core.service.SecurityService;
import org.unidata.mdm.core.type.security.SecurityToken;
import org.unidata.mdm.core.type.security.interceptor.AuthRequestHandleResult;
import org.unidata.mdm.core.type.security.interceptor.NotAuthRequestHandleResult;
import org.unidata.mdm.core.type.security.interceptor.RequestHandleResult;
import org.unidata.mdm.system.util.ClientIpUtil;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StandardSecurityInterceptionProvider implements SecurityInterceptionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardSecurityInterceptionProvider.class);

    /** The Constant TOKEN. */
    private static final String TOKEN = "token";

    /**  Prolong TTL header name. */
    private static final String PROLONG_TTL_HEADER = "PROLONG_TTL";

    private SecurityService securityService;

    public StandardSecurityInterceptionProvider(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public RequestHandleResult handleRequest(final HttpServletRequest request) {
        List<String> token;
        boolean isAuthorized = false;

        final Map<String, List<String>> queryParams =
                JAXRSUtils.getStructuredParams(request.getQueryString(), "&", false, false);
        if (queryParams.containsKey(TOKEN)) {
            token = queryParams.get(TOKEN);
        } else {
            token = Collections.list(request.getHeaders(HttpHeaders.AUTHORIZATION));
        }

        SecurityToken current = null;
        if (token != null && token.size() == 1) {
            try {

                // authentication with spring
                // UN-7140
                String prolongTTLAsString = request.getHeader(PROLONG_TTL_HEADER);
                boolean prolongTTL = StringUtils.isBlank(prolongTTLAsString) || BooleanUtils.toBoolean(prolongTTLAsString);

                isAuthorized = securityService.authenticate(token.get(0), prolongTTL);
                if (isAuthorized) {

                    String serverIp = request.getLocalAddr();
                    String userIp = ClientIpUtil.clientIp(request);
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null && authentication.getDetails() instanceof SecurityToken) {
                        current = (SecurityToken) authentication.getDetails();
                        current.setUserIp(userIp);
                        current.setServerIp(serverIp);
                        if (!isRestUser(current)) {
                            return NotAuthRequestHandleResult.get();
                        }
                    }

                    return AuthRequestHandleResult.get();
                }
            } catch (Exception e) {
                LOGGER.error("Token: {} not valid!", token.get(0));
            }
        }

        return NotAuthRequestHandleResult.get();
    }

    /**
     * Checks if is rest user.
     *
     * @param token the token string
     * @return true, if is rest user
     */
    private boolean isRestUser(@Nullable SecurityToken token) {

        List<org.unidata.mdm.core.type.security.Endpoint> endpoints
                = Objects.isNull(token)
                ? null
                : token.getUser().getEndpoints();

        if (CollectionUtils.isEmpty(endpoints)) {
            return false;
        }

        if (token.isInner()) {
            return false;
        }

        for (org.unidata.mdm.core.type.security.Endpoint endpoint : endpoints) {
            if (StringUtils.equals(endpoint.getName(), org.unidata.mdm.core.type.security.EndpointType.REST.name())) {
                return true;
            }
        }
        return false;
    }
}
