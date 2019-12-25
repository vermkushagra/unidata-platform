package org.unidata.mdm.meta.configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.unidata.mdm.core.service.BusService;
import org.unidata.mdm.core.util.BusUtils;
import org.unidata.mdm.system.configuration.AbstractConfiguration;
import org.unidata.mdm.system.util.DataSourceUtils;

/**
 * @author Alexander Malyshev
 */
@Configuration
public class MetaConfiguration extends AbstractConfiguration {
    /**
	 * This id.
	 */
	private static final ConfigurationId ID = () -> "META_CONFIGURATION";

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

    @Bean(name = "metaDataSource")
    public DataSource metaDataSource() {
    	Properties properties = getAllPropertiesWithPrefix(MetaConfigurationConstants.META_DATASOURCE_PROPERTIES_PREFIX, true);
    	return DataSourceUtils.newPoolingNonXADataSource(properties);
    }

    @Bean(name = "metaTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("metaDataSource") final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * use this way, not     @PropertySource(name = "securitySql", value = "classpath:db/security-sql.xml" )
     * for old functionality support (like @Qualifier("security-sql") final Properties sql)
     * and for pleasure while autoset sql query by org.unidata.mdm.core.dao.@SqlQuery
     *
     * @return
     */
    @Bean("meta-sql")
    public PropertiesFactoryBean metaSql() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("db/meta-sql.xml"));
        return bean;
    }

    @Bean("measurement-sql")
    public PropertiesFactoryBean measurementSql() {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/db/measurement-sql.xml"));
        return propertiesFactoryBean;
    }

    @Bean("meta-draft-sql")
    public PropertiesFactoryBean metaDraftSql() {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/db/meta-draft-sql.xml"));
        return propertiesFactoryBean;
    }

    @Bean
    public BiConsumer<String, Object> metaSender(final BusService busService) {
        return BusUtils.senderWithType(busService.sender(MetaConfigurationConstants.META_TARGET));
    }
}
