package org.unidata.mdm.data.configuration;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.unidata.mdm.core.service.BusService;
import org.unidata.mdm.core.util.BusUtils;
import org.unidata.mdm.system.configuration.AbstractConfiguration;
import org.unidata.mdm.system.util.DataSourceUtils;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

/**
 * @author Alexander Malyshev
 */
@Configuration
public class DataConfiguration extends AbstractConfiguration {
    /**
     * Id.
     */
    private static final ConfigurationId ID = () -> "DATA_CONFIGURATION";
    /**
     * Constructor.
     */
    public DataConfiguration() {
        super();
    }

    @Override
    protected ConfigurationId getId() {
        return ID;
    }

    public static ApplicationContext getApplicationContext() {
        return CONFIGURED_CONTEXT_MAP.get(ID);
    }
    /**
     * Gets a bean.
     *
     * @param <T>
     * @param beanClass the bean class
     * @return bean
     */
    public static <T> T getBean(Class<T> beanClass) {
        if (CONFIGURED_CONTEXT_MAP.containsKey(ID)) {
            return CONFIGURED_CONTEXT_MAP.get(ID).getBean(beanClass);
        }

        return null;
    }

    /**
     * Gets beans of type.
     *
     * @param <T>
     * @param beanClass the bean class
     * @return bean
     */
    public static <T> Map<String, T> getBeans(Class<T> beanClass) {
        if (CONFIGURED_CONTEXT_MAP.containsKey(ID)) {
            return CONFIGURED_CONTEXT_MAP.get(ID).getBeansOfType(beanClass);
        }

        return Collections.emptyMap();
    }

    @Bean("data-sql")
    public PropertiesFactoryBean dataSql() {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/db/data-sql.xml"));
        return propertiesFactoryBean;
    }

    @Bean("records-sql")
    public PropertiesFactoryBean recordsSql() {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/db/records-sql.xml"));
        return propertiesFactoryBean;
    }

    @Bean("relations-sql")
    public PropertiesFactoryBean relationsSql() {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/db/relations-sql.xml"));
        return propertiesFactoryBean;
    }

    @Bean(name = "storageDataSource")
    public DataSource storageDataSource() {
    	Properties properties = getAllPropertiesWithPrefix(DataConfigurationConstants.DATA_DATASOURCE_PROPERTIES_PREFIX, true);
    	return DataSourceUtils.newPoolingNonXADataSource(properties);
    }

    @Bean
    public BiConsumer<String, Object> dataSender(final BusService busService) {
        return BusUtils.senderWithType(busService.sender(DataConfigurationConstants.DATA_TARGET));
    }
}
