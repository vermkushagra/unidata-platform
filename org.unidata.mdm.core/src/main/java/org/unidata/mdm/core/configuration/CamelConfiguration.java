package org.unidata.mdm.core.configuration;

import org.apache.camel.management.JmxManagementStrategyFactory;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.apache.camel.spring.CamelContextFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"org.unidata.mdm.bus"})
public class CamelConfiguration {

    @Bean
    public JmxManagementStrategyFactory jmxManagementStrategyFactory() {
        return new JmxManagementStrategyFactory();
    }

    @Bean("default-camel-context")
    public CamelContextFactoryBean camelContext() {
        final CamelContextFactoryBean camelContextFactoryBean = new CamelContextFactoryBean();
        camelContextFactoryBean.setId("default-camel-context");
        camelContextFactoryBean.setTrace("true");
        return camelContextFactoryBean;
    }

    @Bean
    public GroupedBodyAggregationStrategy groupedBodyAggregationStrategy() {
        return new GroupedBodyAggregationStrategy();
    }
}
