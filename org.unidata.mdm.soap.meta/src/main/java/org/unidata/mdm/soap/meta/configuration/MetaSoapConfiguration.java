package org.unidata.mdm.soap.meta.configuration;

import javax.xml.ws.Endpoint;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.databinding.DataBinding;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unidata.mdm.meta.api.v1.MetaModelPortType;
import org.unidata.mdm.soap.meta.service.MetaModelSOAPServiceImpl;

/**
 * @author Alexander Malyshev
 */
@Configuration
public class MetaSoapConfiguration {

    @Bean
    public DataBinding metaDataBinding() {
        return new JAXBDataBinding();
    }

    @Bean
    public Endpoint metaEndpoint(
            final SpringBus cxf,
            final MetaModelPortType metaSoapService,
            final DataBinding metaDataBinding
    ) {
        final EndpointImpl endpoint = new EndpointImpl(cxf, metaSoapService);
        endpoint.setDataBinding(metaDataBinding);
        endpoint.publish("/public/meta/v1");
        return endpoint;
    }

    @Bean
    public MetaModelPortType metaSoapService() {
        return new MetaModelSOAPServiceImpl();
    }
}
