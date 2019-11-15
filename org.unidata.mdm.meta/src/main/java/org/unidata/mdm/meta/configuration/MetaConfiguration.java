package org.unidata.mdm.meta.configuration;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.unidata.mdm.meta.service.MetaDraftService;
import org.unidata.mdm.meta.service.MetaMeasurementService;
import org.unidata.mdm.meta.service.MetaModelMappingService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.service.AfterContextRefresh;
import org.unidata.mdm.system.util.MessageUtils;

/**
 * @author Alexander Malyshev
 */
@Configuration
public class MetaConfiguration implements ApplicationContextAware {
    /**
     * The spring app. context.
     */
    private static ApplicationContext applicationContext;
    /**
     * {@link AfterContextRefresh} classes.
     */
    private static Class<?> refreshOnStartupClasses[] = {
            MetaMeasurementService.class,
            MetaModelService.class,
            MetaDraftService.class
    };

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // The usual trick.
        MetaConfiguration.applicationContext = applicationContext;
    }
    /**
     * Gets a bean.
     * @param <T>
     * @param name the name
     * @param beanClass the bean class
     * @return bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name, Class<T> beanClass) {

        if (Objects.isNull(name) && Objects.isNull(beanClass)) {
            return null;
        }

        if (Objects.isNull(beanClass)) {
            return (T) applicationContext.getBean(name);
        } else if (Objects.isNull(name)) {
            return getBean(beanClass);
        }

        return applicationContext.getBean(name, beanClass);
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
    public DataSource metaDataSource() {
        JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
        jndiDataSourceLookup.setResourceRef(true);
        return jndiDataSourceLookup.getDataSource("module/org.unidata.mdm.meta");
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
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();

        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.addBasenames("classpath:messages");

        return source;
    }

    public void startImpl() {

        ensureMetaModelIndex();
        ensureAfterContextRefresh();
    }

    public void stopImpl() {
        // NOP so far
    }

    private void ensureAfterContextRefresh() {
        for (Class<?> klass : refreshOnStartupClasses) {
            AfterContextRefresh r = (AfterContextRefresh) applicationContext.getBean(klass);
            r.afterContextRefresh();
        }
    }

    private void ensureMetaModelIndex() {
        getBean(MetaModelMappingService.class).ensureMetaModelIndex();
    }

    @Bean
    public MessageUtils messageUtils(MessageSource messageSource) {
        return new MessageUtils(messageSource);
    }
}
