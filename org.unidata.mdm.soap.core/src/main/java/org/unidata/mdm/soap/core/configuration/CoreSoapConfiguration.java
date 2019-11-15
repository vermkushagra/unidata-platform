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
