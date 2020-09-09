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

/**
 *
 */
package com.unidata.mdm.backend.exchange;

import javax.sql.DataSource;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.unidata.mdm.backend.dao.JobDao;
import com.unidata.mdm.backend.service.data.xlsximport.DataImportService;
import com.unidata.mdm.backend.service.model.MetaDependencyService;
import com.unidata.mdm.backend.service.model.ie.MetaIEService;

/**
 * @author Mikhail Mikhailov
 * Spring configuration for the standalone (command line client).
 */
@Configuration
@Profile(StandaloneConfiguration.STANDALONE_PROFILE_NAME)
@PropertySource(ignoreResourceNotFound = false, value = "file:${unidata.conf}/backend.properties")
@ComponentScan(
    basePackages = {
            "com.unidata.mdm.backend.dao",
            "com.unidata.mdm.backend.service",
            "com.unidata.mdm.cleanse.postaladdress",
            "com.unidata.mdm.backend.converter"
    },
    excludeFilters = {
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.service.job.*"),
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.service.wf.*"),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value=com.unidata.mdm.backend.service.configuration.MetaDataReindexComponent.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = JobDao.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = MetaDependencyService.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = MetaIEService.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = DataImportService.class)
    }
)
@ImportResource({"classpath:/spring/core.xml", "classpath:/spring/matching.xml", "classpath:/spring/audit_stub.xml"})
@EnableTransactionManagement
public class StandaloneConfiguration {

    /**
     * Name of this profile.
     */
    public static final String STANDALONE_PROFILE_NAME = "standalone";

    /**
     * Exchange context bean name.
     */
    public static final String EXCHANGE_CONTEXT_BEAN_NAME = "exchangeContext";

    @Autowired
    @Qualifier(value = EXCHANGE_CONTEXT_BEAN_NAME)
    private ExchangeContext context;

    /**
     * Constructor.
     */
    public StandaloneConfiguration() {
        super();
    }
    /**
     * Gets data source.
     * @return data source
     */
    @Bean(name = "unidataDataSource")
    public DataSource getUnidataDataSource() {
        return context != null ? context.getUnidataDataSource() : null;
    }
    /**
     * Gets data source.
     * @return data source
     */
    /*
    @Bean(name = "landingDataSource")
    public DataSource getLandingDataSource() {
        return context != null ? context.getLandingDataSource() : null;
    }
    */
    /**
     * Gets search client.
     * @return client
     */
    @Bean
    public Client getClient() {
        return context != null ? context.getSearchClient() : null;
    }
    /**
     * Gets the platform transaction manager.
     * @return transaction manager
     */
    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(getUnidataDataSource());
    }

    /**
     * Properties placeholder configuration.
     * @return configurator
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
     return new PropertySourcesPlaceholderConfigurer();
    }
}
