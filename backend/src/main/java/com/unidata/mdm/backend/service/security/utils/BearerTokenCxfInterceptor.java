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

package com.unidata.mdm.backend.service.security.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import com.unidata.mdm.backend.common.security.token.AuthAndRedirectRequestHandleResult;
import com.unidata.mdm.backend.common.security.token.AuthRequestHandleResult;
import com.unidata.mdm.backend.common.security.token.NotAuthRequestHandleResult;
import com.unidata.mdm.backend.common.security.token.RedirectRequestHandleResult;
import com.unidata.mdm.backend.common.security.token.RequestHandleResult;
import com.unidata.mdm.backend.service.configuration.ConfigurationServiceExt;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * The Class TokenInterceptor.
 */
public class BearerTokenCxfInterceptor extends AbstractPhaseInterceptor<Message> {

    /** Login URI. */
    private static final String URI_LOGIN = "/login";
    /** Logout URI. */
    private static final String URI_LOGOUT = "/logout";
    /** API-Docs API. */
    private static final String URI_API_DOCS = "/swagger.";
    /** API-Docs API. */
    private static final String URI_HEALTH_CHECK = "/healthcheck";
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BearerTokenCxfInterceptor.class);

    /**
     * Configuration service.
     */
    private final ConfigurationServiceExt configurationService;
//    private final List<SecurityInterceptionProvider> securityInterceptionProviders = new ArrayList<>();

    /**
     * Instantiates a new token interceptor.
     */
    public BearerTokenCxfInterceptor(final ConfigurationServiceExt configurationService) {
        super(Phase.RECEIVE);
        LOGGER.info("Register security interceptor {}", BearerTokenCxfInterceptor.class.getCanonicalName());
        this.configurationService = configurationService;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message
     * .Message)
     */
    @Override
    public void handleMessage(Message inMessage) {
        String requestURI = (String) inMessage.get(Message.REQUEST_URI);
        String queryString = (String) inMessage.get(Message.QUERY_STRING);
        String type = (String) inMessage.get(Message.CONTENT_TYPE);

        // don't check token if
        if (passThrough(type, requestURI, queryString)) {
            return;
        }

        final HttpServletRequest request = (HttpServletRequest) inMessage.get("HTTP.REQUEST");
        final RequestHandleResult result = configurationService.getSecurityInterceptionProviders().stream()
                .map(provider -> provider.handleRequest(request))
                .filter(checkResult -> !(checkResult instanceof NotAuthRequestHandleResult))
                .findFirst()
                .orElse(NotAuthRequestHandleResult.get());

        if (result instanceof AuthRequestHandleResult) {
            inMessage.getExchange().put(Authentication.class, SecurityContextHolder.getContext().getAuthentication());

            if (result instanceof AuthAndRedirectRequestHandleResult) {
                handleRedirectResult(
                        inMessage,
                        ((AuthAndRedirectRequestHandleResult) result).getRedirectTokenCheckResult()
                );
            }
            return;
        }

        if (result instanceof NotAuthRequestHandleResult) {
            Message outMessage = getOutMessage(inMessage);
            outMessage.put(Message.RESPONSE_CODE, HttpURLConnection.HTTP_UNAUTHORIZED);

            stopChain(inMessage, outMessage);
            return;
        }

        if (result instanceof RedirectRequestHandleResult) {
            RedirectRequestHandleResult redirectTokenCheckResult = (RedirectRequestHandleResult) result;

            handleRedirectResult(inMessage, redirectTokenCheckResult);
        }
    }

    private void handleRedirectResult(Message inMessage, RedirectRequestHandleResult redirectTokenCheckResult) {
        Message outMessage = getOutMessage(inMessage);
        outMessage.put(AbstractHTTPDestination.REQUEST_REDIRECTED, true);
        outMessage.put(Message.RESPONSE_CODE, HttpURLConnection.HTTP_MOVED_PERM);
        Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>) outMessage.get(Message.PROTOCOL_HEADERS));

        headers.put("Location", Collections.singletonList(redirectTokenCheckResult.getLocation()));
        headers.putAll(redirectTokenCheckResult.getHeaders());

        stopChain(inMessage, outMessage);
    }

    private void stopChain(Message inMessage, Message outMessage) {
        inMessage.getInterceptorChain().abort();
        try {
            getConduit(inMessage).prepare(outMessage);
            close(outMessage);
        } catch (IOException ioe) {
            throw new Fault(ioe);
        }
    }

    /**
     * Paa-through without token check.
     * @param type request type
     * @param uri request URI
     * @param queryString the query string
     * @return true for skip, flase otherwise
     */
    private boolean passThrough(String type, String uri, String queryString) {

        if (StringUtils.contains(type, MediaType.TEXT_XML)) {
            return true;
        }

        if (uri.contains(URI_LOGIN) || uri.contains(URI_LOGOUT) || uri.contains(URI_API_DOCS) ||
                uri.contains(URI_HEALTH_CHECK)) {
            return true;
        }

        if ("wsdl".equals(queryString) || StringUtils.startsWith(queryString, "xsd=")) {
            return true;
        }

        return false;
    }
    /**
     * Gets the out message.
     *
     * @param inMessage
     *            the in message
     * @return the out message
     */
    private Message getOutMessage(Message inMessage) {
        Exchange exchange = inMessage.getExchange();
        Message outMessage = exchange.getOutMessage();
        if (outMessage == null) {
            Endpoint endpoint = exchange.get(Endpoint.class);
            outMessage = endpoint.getBinding().createMessage();
            exchange.setOutMessage(outMessage);
        }
        outMessage.putAll(inMessage);
        return outMessage;
    }

    /**
     * Gets the conduit.
     *
     * @param inMessage
     *            the in message
     * @return the conduit
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private Conduit getConduit(Message inMessage) throws IOException {
        Exchange exchange = inMessage.getExchange();
        Conduit conduit = exchange.getDestination().getBackChannel(inMessage);
        exchange.setConduit(conduit);
        return conduit;
    }

    /**
     * Close.
     *
     * @param outMessage
     *            the out message
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void close(Message outMessage) throws IOException {
        OutputStream os = outMessage.getContent(OutputStream.class);
        os.flush();
        os.close();
    }
}
