package org.unidata.mdm.soap.data.configuration;

import javax.xml.ws.Endpoint;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.databinding.DataBinding;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unidata.mdm.api.wsdl.v1.UnidataServicePortType;
import org.unidata.mdm.soap.data.service.SoapApiServiceImpl;

/**
 * @author Alexander Malyshev
 */
@Configuration
public class DataSoapConfiguration {

    @Bean
    public JAXBDataBinding dataDataBinding() {
        return new JAXBDataBinding();
    }

    @Bean
    public Endpoint loginEndpoint(
            final SpringBus cxf,
            final UnidataServicePortType dataSoapService,
            final DataBinding dataDataBinding
    ) {
        final EndpointImpl endpoint = new EndpointImpl(cxf, dataSoapService);
        endpoint.setDataBinding(dataDataBinding);
        endpoint.publish("/public/data/v1");
        return endpoint;
    }

    @Bean
    public UnidataServicePortType dataSoapService() {
        return new SoapApiServiceImpl();
    }
}
