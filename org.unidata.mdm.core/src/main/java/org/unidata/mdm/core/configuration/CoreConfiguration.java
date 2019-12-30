package org.unidata.mdm.core.configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.unidata.mdm.core.service.BusService;
import org.unidata.mdm.core.service.SecurityService;
import org.unidata.mdm.core.service.impl.StandardSecurityInterceptionProvider;
import org.unidata.mdm.core.util.BusUtils;
import org.unidata.mdm.system.configuration.AbstractConfiguration;
import org.unidata.mdm.system.util.DataSourceUtils;

/**
 * @author Alexander Malyshev
 */
@Configuration
public class CoreConfiguration extends AbstractConfiguration {

    private static final ConfigurationId ID = () -> "CORE_CONFIGURATION";

    /**
     * Custom table prefix.
     */
    public static final String UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX = "BATCH_";
//    /**
//     * Minimal pool size.
//     */
//    @Value("${" + CoreConfigurationConstants.PROP_NAME_MIN_THREAD_POOL_SIZE + "?:4}")
//    private int coreJobPoolSize;
//    /**
//     * Maximum pool size.
//     */
//    @Value("${" + CoreConfigurationConstants.PROP_NAME_MAX_THREAD_POOL_SIZE + "?:32}")
//    private int maxJobPoolSize;
//    /**
//     * Queue capacity.
//     */
//    @Value("${" + CoreConfigurationConstants.PROP_NAME_QUEUE_SIZE + "?:100}")
//    private int jobQueueCapacity;

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

    @Bean(name = "coreTransactionManager")
    public PlatformTransactionManager platformTransactionManager(@Autowired DataSource coreDataSource) {
        return new DataSourceTransactionManager(coreDataSource);
    }

    @Bean
    public StandardSecurityInterceptionProvider standardSecurityInterceptionProvider(final SecurityService securityService) {
        return new StandardSecurityInterceptionProvider(securityService);
    }

    @Bean
    public ProviderManager authenticationManager(final AuthenticationProvider authenticationProvider) {
        return new ProviderManager(Collections.singletonList(authenticationProvider));
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

    @Bean("coreSender")
    public BiConsumer<String, Object> coreSender(final BusService busService) {
        return BusUtils.senderWithType(busService.sender(CoreConfigurationConstants.CORE_TARGET));
    }
}
