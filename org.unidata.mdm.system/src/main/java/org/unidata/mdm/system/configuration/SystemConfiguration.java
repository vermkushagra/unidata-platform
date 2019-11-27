package org.unidata.mdm.system.configuration;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.unidata.mdm.system.util.MessageUtils;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * @author Mikhail Mikhailov
 * Root spring context link.
 */
@Configuration
public class SystemConfiguration implements ApplicationContextAware {
    /**
     * The spring app. context.
     */
    private static ApplicationContext applicationContext;

    @Autowired
    private Environment env;
    /**
     * Constructor.
     */
    public SystemConfiguration() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // The usual trick.
        SystemConfiguration.applicationContext = applicationContext;
    }
    /**
     * Gets a bean.
     * @param <T>
     * @param beanClass the bean class
     * @return bean
     */
    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }
    /**
     * Gets beans of type.
     * @param <T>
     * @param beanClass the bean class
     * @return bean
     */
    public static <T> Map<String, T> getBeans(Class<T> beanClass) {
        return applicationContext.getBeansOfType(beanClass);
    }

    @Bean
    public HazelcastInstance hazelcastInstance(
            @Value("${unidata.cache.port:5701}") final int port,
            @Value("${unidata.cache.tcp-ip.enabled:false}") final boolean tpcIpEnabled,
            @Value("${unidata.cache.multicast.enabled:false}") final boolean multicastEnabled
    ) {
        final Config unidataHzConfig = new Config()
                .setInstanceName("unidata");
        final JoinConfig join = unidataHzConfig.getNetworkConfig()
                .setPort(port)
                .setPortAutoIncrement(false)
                .getJoin();
        join.getTcpIpConfig()
                .setEnabled(tpcIpEnabled);
        join.getMulticastConfig()
                .setEnabled(multicastEnabled);
        join.getAwsConfig()
                .setEnabled(false);
        return Hazelcast.getOrCreateHazelcastInstance(
                unidataHzConfig
        );
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer =
                new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setEnvironment(new StandardEnvironment());
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean("systemDataSource")
    public DataSource systemDataSource() {

        // Single connection data source
        String url = env.getProperty(SystemConfigurationConstants.UNIDATA_SYSTEM_URL);
        String username = env.getProperty(SystemConfigurationConstants.UNIDATA_SYSTEM_SCHEMA_USERNAME);
        String password = env.getProperty(SystemConfigurationConstants.UNIDATA_SYSTEM_SCHEMA_PASSWORD);

        SingleConnectionDataSource scds = new SingleConnectionDataSource(url, username, password, true);
        scds.setDriverClassName("org.postgresql.Driver");

        return scds;
    }

    @Bean("configuration-sql")
    public PropertiesFactoryBean configurationSql() {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/db/configuration-sql.xml"));
        return propertiesFactoryBean;
    }

    @Bean("pipelines-sql")
    public PropertiesFactoryBean pipelinesSql() {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/db/pipelines-sql.xml"));
        return propertiesFactoryBean;
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
