/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.system.configuration;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * @author Mikhail Mikhailov
 * Root spring context link.
 */
@Configuration
public class SystemConfiguration extends AbstractConfiguration {

    private static final ConfigurationId ID = () -> "SYSTEM_CONFIGURATION";

    private ResourceBundleMessageSource systemMessageSource;

    /**
     * Constructor.
     */
    public SystemConfiguration() {
        super();

        // Do it here, because otherwise, we can not control bundles joining
        systemMessageSource = new ResourceBundleMessageSource();
        systemMessageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        systemMessageSource.addBasenames("classpath:system_messages");
    }

    /**
     * Link to system bundles.
     */
    public ResourceBundleMessageSource getSystemMessageSource() {
        return systemMessageSource;
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
    public DataSource systemDataSource(@Autowired Environment env) {

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
        return systemMessageSource;
    }
}
