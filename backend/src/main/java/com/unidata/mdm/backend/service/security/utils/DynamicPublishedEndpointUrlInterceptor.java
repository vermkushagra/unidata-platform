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

import java.util.List;
import java.util.Map;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.spring.NamespaceHandler;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.servlet.ServletDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * Interceptor to automatically inject port into WS Port URL by forwarded X-front-port HTTP Header value (from balancer).
 * If port is not forwarded - nothing will be done.
 *
 * @author Aleksandr Magdenko
 */
public class DynamicPublishedEndpointUrlInterceptor extends AbstractSoapInterceptor implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(DynamicPublishedEndpointUrlInterceptor.class);
    private static final String DEFAULT_HEADER_NAME = "X-Forwarded-Proto";

    private ApplicationContext applicationContext;

    private String serviceBeanName;
    private String headerName = DEFAULT_HEADER_NAME;

    public DynamicPublishedEndpointUrlInterceptor(String serviceBeanName) {
        super(Phase.RECEIVE);
        this.serviceBeanName = serviceBeanName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public void handleMessage(SoapMessage soapMessage) throws Fault {
        // Ignore for empty header name.
        if (StringUtils.isEmpty(headerName)) {
            return;
        }

        try {
            log.debug((String)soapMessage.get("org.apache.cxf.request.url"));
            List protocols = (List)((Map)soapMessage.get(Message.PROTOCOL_HEADERS)).get(headerName);

            if (protocols != null && protocols.get(0) != null){
                ((ServletDestination) (((NamespaceHandler.SpringServerFactoryBean) applicationContext.
                        getBean(serviceBeanName)).getServer())
                        .getDestination()).getEndpointInfo().setProperty("publishedEndpointUrl",
                        ((String) soapMessage.get("http.base.path")).replaceAll("(.*://)([^/]+)(:\\d+)?/.*$",
                                protocols.get(0) + "://$2$3" +
                                        soapMessage.get("org.apache.cxf.message.Message.BASE_PATH"))
                );
            }
        } catch (Exception ex) {
            log.warn("Could not handle SOAP message (IN)", ex);
            throw new Fault(ex);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
