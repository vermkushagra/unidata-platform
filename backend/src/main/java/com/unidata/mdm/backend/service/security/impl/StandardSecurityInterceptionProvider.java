package com.unidata.mdm.backend.service.security.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.unidata.mdm.backend.common.security.SecurityInterceptionProvider;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.common.security.token.AuthRequestHandleResult;
import com.unidata.mdm.backend.common.security.token.NotAuthRequestHandleResult;
import com.unidata.mdm.backend.common.security.token.RequestHandleResult;
import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.backend.util.ClientIpUtil;

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
                boolean prolongTTL = StringUtils.isBlank(prolongTTLAsString)
                        ? true
                        : BooleanUtils.toBoolean(prolongTTLAsString);

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

        List<com.unidata.mdm.backend.common.integration.auth.Endpoint> endpoints
                = Objects.isNull(token)
                ? null
                : token.getUser().getEndpoints();

        if (CollectionUtils.isEmpty(endpoints)) {
            return false;
        }

        for (com.unidata.mdm.backend.common.integration.auth.Endpoint endpoint : endpoints) {
            if (StringUtils.equals(endpoint.getName(), com.unidata.mdm.backend.common.security.Endpoint.REST.name())) {
                return true;
            }
        }
        return false;
    }
}
