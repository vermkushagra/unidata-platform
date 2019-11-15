package org.unidata.mdm.soap.meta.configuration;

import java.nio.charset.StandardCharsets;
import javax.xml.ws.Endpoint;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.databinding.DataBinding;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.unidata.mdm.meta.api.v1.MetaModelPortType;
import org.unidata.mdm.soap.meta.service.MetaModelSOAPServiceImpl;
import org.unidata.mdm.system.util.MessageUtils;

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



    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();

        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.addBasenames("classpath:messages");

        return source;
    }

    @Bean
    public MessageUtils messageUtils(MessageSource messageSource) {
        return new MessageUtils(messageSource);
    }
}
