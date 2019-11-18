package org.unidata.mdm.core.configuration;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.unidata.mdm.core.service.SecurityService;
import org.unidata.mdm.core.service.impl.StandardSecurityInterceptionProvider;
import org.unidata.mdm.system.configuration.AbstractConfiguration;
import org.unidata.mdm.system.util.DataSourceUtils;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
 
/**
 * @author Alexander Malyshev
 */
@Configuration
public class CoreConfiguration extends AbstractConfiguration {

    private static final ConfigurationId ID = () -> "CORE_CONFIGURATION";

    public CoreConfiguration() {
        super();
    }
    /**
     * {@inheritDoc}
     */
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

    @Bean(name = "coreDataSource")
    public DataSource coreDataSource() {
    	Properties properties = getAllPropertiesWithPrefix(CoreConfigurationConstants.CORE_DATASOURCE_PROPERTIES_PREFIX, true);
    	return DataSourceUtils.newPoolingNonXADataSource(properties);
    }

    @Bean("binary-data-sql")
    public PropertiesFactoryBean binaryDataSql() {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/db/binary-data-sql.xml"));
        return propertiesFactoryBean;
    }

    @Bean("security-sql")
    public PropertiesFactoryBean securitySql() {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/db/security-sql.xml"));
        return propertiesFactoryBean;
    }

    @Bean("backendProperties")
    public PropertiesFactoryBean backendProperties() throws MalformedURLException {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new FileUrlResource("file:///${unidata.conf}/backend.properties"));
        propertiesFactoryBean.setIgnoreResourceNotFound(true);
        return propertiesFactoryBean;
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

    @Bean(name = "coreTransactionManager")
    public PlatformTransactionManager platformTransactionManager() {
        return new bitronix.tm.integration.spring.PlatformTransactionManager();
    }

    @Bean
    public StandardSecurityInterceptionProvider standardSecurityInterceptionProvider(final SecurityService securityService) {
        return new StandardSecurityInterceptionProvider(securityService);
    }


    @Bean
    public ProviderManager authenticationManager(final AuthenticationProvider authenticationProvider) {
        return new ProviderManager(Collections.singletonList(authenticationProvider));
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();

        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.addBasenames("classpath:messages");

        return source;
    }

    /**
     * use this way, not     @PropertySource(name = "securitySql", value = "classpath:db/security-sql.xml" )
     * for old functionality support (like @Qualifier("security-sql") final Properties sql)
     * and for pleasure while autoset sql query by org.unidata.mdm.core.dao.@SqlQuery
     *
     * @return
     */
    @Bean("securitySql")
    public PropertiesFactoryBean securitySqlProperties() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("db/security-sql.xml"));
        return bean;
    }

    @Bean("job-sql")
    public PropertiesFactoryBean jobSqlProperties() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("db/job-sql.xml"));
        return bean;
    }

    @Bean("configuration-sql")
    public PropertiesFactoryBean configurationSqlProperties() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("db/configuration-sql.xml"));
        return bean;
    }

    @Bean("custom-storage-sql")
    public PropertiesFactoryBean customStorageSqlProperties() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("db/custom-storage-sql.xml"));
        return bean;
    }

    @Bean("audit-sql")
    public PropertiesFactoryBean auditSqlProperties() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("db/audit-sql.xml"));
        return bean;
    }

    @Bean("binary-data-sql")
    public PropertiesFactoryBean binaryDataSqlProperties() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("db/binary-data-sql.xml"));
        return bean;
    }
}
