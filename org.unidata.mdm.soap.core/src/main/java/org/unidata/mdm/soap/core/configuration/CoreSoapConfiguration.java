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

package org.unidata.mdm.soap.core.configuration;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.databinding.DataBinding;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unidata.mdm.soap.core.service.LoginSOAPServiceImpl;
import org.unidata.mdm.login.api.v1.LoginPortType;

import javax.xml.ws.Endpoint;

/**
 * @author Alexander Malyshev
 */
@Configuration
public class CoreSoapConfiguration {

    @Bean
    public JAXBDataBinding loginDataBinding() {
        //jaxbDataBinding.setContextProperties(jaxbProperties);
        return new JAXBDataBinding();
    }

    @Bean
    public Endpoint loginEndpoint(
            final SpringBus cxf,
            final LoginPortType loginSoapService,
            final DataBinding loginDataBinding
    ) {
        final EndpointImpl endpoint = new EndpointImpl(cxf, loginSoapService);
        endpoint.setDataBinding(loginDataBinding);
        endpoint.publish("/public/login/v1");
        return endpoint;
    }

    @Bean
    public LoginPortType loginSoapService() {
        return new LoginSOAPServiceImpl();
    }
}
