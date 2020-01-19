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

package org.unidata.mdm.data.configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.unidata.mdm.core.service.BusService;
import org.unidata.mdm.core.util.BusUtils;
import org.unidata.mdm.system.configuration.AbstractConfiguration;
import org.unidata.mdm.system.util.DataSourceUtils;

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

    @Bean("job-support-sql")
    public PropertiesFactoryBean jobSupportSql() {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/db/job-support-sql.xml"));
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
