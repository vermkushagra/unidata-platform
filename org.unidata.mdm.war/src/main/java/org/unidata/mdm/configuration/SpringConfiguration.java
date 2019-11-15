package org.unidata.mdm.configuration;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.support.StandardServletEnvironment;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan({"org.unidata.mdm.system"})
@Import({
        SpringDbConfiguration.class
})
@PropertySource("file:///${unidata.conf}/backend.properties")
@ImportResource({
        "classpath:META-INF/cxf/cxf.xml"
})
public class SpringConfiguration {

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer =
                new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setEnvironment(new StandardServletEnvironment());
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public SpringBus cxf() {
        return new SpringBus();
    }

    @Bean
    public JacksonJaxbJsonProvider jacksonJaxbJsonProvider() {
        return new JacksonJaxbJsonProvider();
    }
}
