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

package org.unidata.mdm.core.module;

import static org.unidata.mdm.system.exception.SystemExceptionIds.EX_MODULE_CANNOT_BE_INSTALLED;
import static org.unidata.mdm.system.exception.SystemExceptionIds.EX_MODULE_CANNOT_BE_UNINSTALLED;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.JobScope;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidata.mdm.core.dto.BusRoutesDefinition;
import org.unidata.mdm.core.configuration.CoreConfiguration;
import org.unidata.mdm.core.configuration.CoreConfigurationConstants;
import org.unidata.mdm.core.configuration.CoreConfigurationProperty;
import org.unidata.mdm.core.configuration.job.CustomJobRegistryBeanPostProcessor;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.migrations.CoreSchemaMigrations;
import org.unidata.mdm.core.migrations.UninstallCoreSchemaMigrations;
import org.unidata.mdm.core.service.BusConfigurationService;
import org.unidata.mdm.core.service.RoleService;
import org.unidata.mdm.core.service.impl.AsyncRareTaskExecutor;
import org.unidata.mdm.core.type.search.AuditHeaderField;
import org.unidata.mdm.core.type.search.AuditIndexType;
import org.unidata.mdm.core.util.CoreServiceUtils;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.search.context.MappingRequestContext;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.mapping.Mapping;
import org.unidata.mdm.search.type.mapping.impl.BooleanMappingField;
import org.unidata.mdm.search.type.mapping.impl.StringMappingField;
import org.unidata.mdm.search.type.mapping.impl.TimestampMappingField;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.migration.SpringContextAwareMigrationContext;
import org.unidata.mdm.system.service.ModularPostProcessingRegistrar;
import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.module.Dependency;
import org.unidata.mdm.system.type.module.Module;
import org.unidata.mdm.system.util.DataSourceUtils;
import org.unidata.mdm.system.util.IOUtils;
import org.unidata.mdm.system.util.JsonUtils;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import nl.myndocs.database.migrator.database.Selector;
import nl.myndocs.database.migrator.database.query.Database;
import nl.myndocs.database.migrator.processor.Migrator;

public class CoreModule implements Module {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreModule.class);
    /**
     * This module id.
     */
    public static final String MODULE_ID = "org.unidata.mdm.core";

    private static final List<Dependency> DEPENDENCIES = Arrays.asList(
            new Dependency("org.unidata.mdm.system", "5.2"),
            new Dependency("org.unidata.mdm.search", "5.2")
    );

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private SearchService searchService;

    @Autowired
    private DataSource coreDataSource;

    @Autowired
    private CoreConfiguration coreConfiguration;

    @Autowired
    private AsyncRareTaskExecutor asyncRareTaskExecutor;

    @Autowired
    private BusConfigurationService busConfigurationService;

    @Autowired
    private CustomJobRegistryBeanPostProcessor customJobRegistryBeanPostProcessor;

    @Autowired
    private JobScope jobScope;

    @Autowired
    private StepScope stepScope;

    @Autowired
    private ModularPostProcessingRegistrar modularPostProcessingRegistrar;

    @Autowired
    private RoleService roleService;

    /**
     * Lock name.
     */
    private static final String CREATE_INDEX_LOCK_NAME = "createAuditIndexLock";

    @Override
    public String getId() {
        return MODULE_ID;
    }

    @Override
    public String getVersion() {
        return "5.2";
    }

    @Override
    public String getName() {
        return "Unidata core";
    }

    @Override
    public String getDescription() {
        return "Unidata core module";
    }

    @Override
    public Collection<Dependency> getDependencies() {
        return DEPENDENCIES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getResourceBundleBasenames() {
        return new String[]{ "core_messages" };
    }

    @Override
    public ApplicationConfigurationProperty[] configurationProperties() {
        return CoreConfigurationProperty.values();
    }

    private Migrator migrator;

    /**
     * The mapping.
     */
    // FIXME create dynamically
    private static final Mapping AUDIT_INDEX_MAPPING = new Mapping(AuditIndexType.AUDIT)
            .withFields(
                    new StringMappingField(AuditHeaderField.TYPE.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.PARAMETERS.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new BooleanMappingField(AuditHeaderField.SUCCESS.getName())
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.LOGIN.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.CLIENT_IP.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.SERVER_IP.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.ENDPOINT.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new TimestampMappingField(AuditHeaderField.WHEN_HAPPENED.getName())
                            .withIndexType(AuditIndexType.AUDIT)
            );

    @Override
    public void install() {
        LOGGER.info("Install");

        try {
            getMigrator().migrate(
                    coreConfiguration.getBeanByClass(SpringContextAwareMigrationContext.class),
                    CoreSchemaMigrations.migrations()
            );
        } catch (SQLException e) {
            throw new PlatformFailureException(
                    "cannot install core module",
                    e,
                    EX_MODULE_CANNOT_BE_INSTALLED
            );
        }

        busConfigurationService.upsertBusRoutesDefinition(
                new BusRoutesDefinition(
                        "core",
                        IOUtils.readFromClasspath("routes/core.xml")
                )
        );
    }

    @Override
    public void uninstall() {
        LOGGER.info("Uninstall");
        try {
            getMigrator().migrate(
                    coreConfiguration.getBeanByClass(SpringContextAwareMigrationContext.class),
                    UninstallCoreSchemaMigrations.migrations()
            );
        } catch (SQLException e) {
            throw new PlatformFailureException(
                    "cannot uninstall core module",
                    e,
                    EX_MODULE_CANNOT_BE_UNINSTALLED
            );
        }
    }

    @Override
    public void start() {
        LOGGER.info("Starting...");

        SecurityUtils.init();
        CoreServiceUtils.init();
        JsonUtils.init();

        // ???
        // * TODO: Temporary! Move the content to Audit and kill this service after Audit cleanup.
        final ILock createIndexLock = hazelcastInstance.getLock(CREATE_INDEX_LOCK_NAME);
        try {
            if (createIndexLock.tryLock(1, TimeUnit.SECONDS)) {
                try {

                    MappingRequestContext mCtx = MappingRequestContext.builder()
                            .entity(AuditIndexType.INDEX_NAME)
                            .storageId(SecurityUtils.getCurrentUserStorageId())
                            .mapping(AUDIT_INDEX_MAPPING)
                            .build();

                    searchService.process(mCtx);
                } finally {
                    createIndexLock.unlock();
                }
            } else {
                final String message = "Cannot aquire audit index create lock.";
                LOGGER.error(message);
                throw new PlatformFailureException(message, CoreExceptionIds.EX_SYSTEM_INDEX_LOCK_TIME_OUT);
            }
        } catch (InterruptedException e) {
            final String message = "Cannot aquire audit index create lock.";
            LOGGER.error(message, e);
            throw new PlatformFailureException(message, e, CoreExceptionIds.EX_SYSTEM_INDEX_LOCK_TIME_OUT);
        }

        // Register custom job post-processor
        modularPostProcessingRegistrar.registerBeanPostProcessor(customJobRegistryBeanPostProcessor);
        modularPostProcessingRegistrar.registerBeanFactoryPostProcessor(jobScope);
        modularPostProcessingRegistrar.registerBeanFactoryPostProcessor(stepScope);

        roleService.init();

        LOGGER.info("Started");
    }

    @Override
    public void ready() {
        busConfigurationService.loadBusRoutesDefinitions();
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping...");
        asyncRareTaskExecutor.shutdown();
        Hazelcast.shutdownAll();
        DataSourceUtils.shutdown(coreDataSource);
        LOGGER.info("Stopped.");
    }

    private Migrator getMigrator() throws SQLException {
        if (migrator != null) {
            return migrator;
        }

        Connection connection = coreDataSource.getConnection();

        Database database = new Selector()
                .loadFromConnection(connection, CoreConfigurationConstants.CORE_SCHEMA_NAME);

        return new Migrator(database, "change_log");
    }
}
