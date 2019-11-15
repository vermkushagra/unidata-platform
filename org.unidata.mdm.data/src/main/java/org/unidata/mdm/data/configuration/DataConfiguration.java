package org.unidata.mdm.data.configuration;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.unidata.mdm.system.configuration.AbstractConfiguration;

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

    @Bean
    public DataSource storageDataSource() {
        JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
        jndiDataSourceLookup.setResourceRef(true);
        return jndiDataSourceLookup.getDataSource("module/org.unidata.mdm.data");
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();

        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.addBasenames("classpath:messages");

        return source;
    }
}
