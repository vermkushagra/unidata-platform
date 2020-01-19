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

package org.unidata.mdm.meta.module;

import nl.myndocs.database.migrator.database.Selector;
import nl.myndocs.database.migrator.database.query.Database;
import nl.myndocs.database.migrator.processor.Migrator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.unidata.mdm.core.dto.BusRoutesDefinition;
import org.unidata.mdm.core.service.BusConfigurationService;
import org.unidata.mdm.meta.configuration.MetaConfiguration;
import org.unidata.mdm.meta.configuration.MetaConfigurationConstants;
import org.unidata.mdm.meta.context.CreateDraftModelRequestContext;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.meta.migration.InstallMetaSchemaMigrations;
import org.unidata.mdm.meta.migration.MetaMigrationContext;
import org.unidata.mdm.meta.migration.UninstallMetaSchemaMigrations;
import org.unidata.mdm.meta.service.MetaDraftService;
import org.unidata.mdm.meta.service.MetaMeasurementService;
import org.unidata.mdm.meta.service.MetaModelImportService;
import org.unidata.mdm.meta.service.MetaModelMappingService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.service.segments.ModelCreateDraftFinishExecutor;
import org.unidata.mdm.meta.service.segments.ModelCreateDraftStartExecutor;
import org.unidata.mdm.meta.service.segments.ModelDeleteFinishExecutor;
import org.unidata.mdm.meta.service.segments.ModelDeleteStartExecutor;
import org.unidata.mdm.meta.service.segments.ModelDropDraftFinishExecutor;
import org.unidata.mdm.meta.service.segments.ModelDropDraftStartExecutor;
import org.unidata.mdm.meta.service.segments.ModelGetFinishExecutor;
import org.unidata.mdm.meta.service.segments.ModelGetStartExecutor;
import org.unidata.mdm.meta.service.segments.ModelPublishFinishExecutor;
import org.unidata.mdm.meta.service.segments.ModelPublishStartExecutor;
import org.unidata.mdm.meta.service.segments.ModelUpsertFinishExecutor;
import org.unidata.mdm.meta.service.segments.ModelUpsertStartExecutor;
import org.unidata.mdm.meta.util.ModelUtils;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.exception.SystemExceptionIds;
import org.unidata.mdm.system.service.AfterContextRefresh;
import org.unidata.mdm.system.service.ExecutionService;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.module.AbstractModule;
import org.unidata.mdm.system.type.module.Dependency;
import org.unidata.mdm.system.util.DataSourceUtils;
import org.unidata.mdm.system.util.IOUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class MetaModule extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaModule.class);

    public static final String MODULE_ID = "org.unidata.mdm.meta";

    private static final Set<Dependency> DEPENDENCIES = Collections.singleton(
            new Dependency("org.unidata.mdm.core", "5.2")
    );

    /**
     * {@link AfterContextRefresh} classes.
     */
    private static final Class<?>[] REFRESH_ON_STARTUP_CLASSES = {
            MetaMeasurementService.class,
            MetaModelService.class
    };

    private static final String[] SEGMENTS = {
            // 1. Start segments
            ModelGetStartExecutor.SEGMENT_ID,

            // 5. Finish segments
            ModelGetFinishExecutor.SEGMENT_ID,

            ModelUpsertStartExecutor.SEGMENT_ID,

            ModelUpsertFinishExecutor.SEGMENT_ID,

            ModelDeleteStartExecutor.SEGMENT_ID,

            ModelDeleteFinishExecutor.SEGMENT_ID,

            ModelPublishStartExecutor.SEGMENT_ID,

            ModelPublishFinishExecutor.SEGMENT_ID,

            ModelPublishStartExecutor.SEGMENT_ID,

            ModelPublishFinishExecutor.SEGMENT_ID,

            ModelCreateDraftStartExecutor.SEGMENT_ID,

            ModelCreateDraftFinishExecutor.SEGMENT_ID,

            ModelDropDraftStartExecutor.SEGMENT_ID,

            ModelDropDraftFinishExecutor.SEGMENT_ID,
    };

    @Autowired
    private DataSource metaDataSource;

    @Autowired
    private MetaModelMappingService metaModelMappingService;

    @Autowired
    private MetaConfiguration configuration;

    @Autowired
    private MetaDraftService metaDraftService;

    @Autowired
    private BusConfigurationService busConfigurationService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private MetaModelImportService metaModelImportService;

    @Value("${unidata.smoke.measureunits}")
    private String measureUnitsFilePath;

    @Value("${unidata.smoke.model}")
    private String metaModelFilePath;

    @Autowired
    private ExecutionService executionService;

    private static final String[] PIPELINES = {
            "org.unidata.mdm.meta[MODEL_UPSERT_START]",
            "org.unidata.mdm.meta[MODEL_PUBLISH_START]",
            "org.unidata.mdm.meta[MODEL_GET_START]",
            "org.unidata.mdm.meta[MODEL_DELETE_START]",
            "org.unidata.mdm.meta[MODEL_CREATE_DRAFT_START]"
    };

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
        return "Unidata Meta module";
    }

    @Override
    public String getDescription() {
        return "Meta";
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
        return new String[]{ "meta_messages" };
    }

    @Override
    public void install() {
        LOGGER.info("Install");

        ModelUtils.init();

        try {
            getMigrator().migrate(
                    configuration.getBeanByClass(MetaMigrationContext.class),
                    InstallMetaSchemaMigrations.migrations());
        } catch (SQLException e) {
            throw new PlatformFailureException(
                    "cannot install data module",
                    e,
                    SystemExceptionIds.EX_MODULE_CANNOT_BE_INSTALLED
            );
        }

        busConfigurationService.upsertBusRoutesDefinition(
                new BusRoutesDefinition(
                        "meta",
                        IOUtils.readFromClasspath("routes/meta.xml")
                )
        );

        for (String pipeline : PIPELINES) {
            try {
                pipelineService.load(
                        pipeline,
                        "",
                        Thread.currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream("pipelines/" + pipeline + ".json")
                );
            } catch (IOException e) {
                throw new PlatformFailureException(
                        "Error while loading pipeline" + pipeline,
                        e,
                        MetaExceptionIds.EX_META_PIPELINE_LOADING_ERROR,
                        pipeline
                );
            }
        }
    }

    @Override
    public void uninstall() {
        LOGGER.info("Uninstall");
        try {
            getMigrator().migrate(
                    configuration.getBeanByClass(MetaMigrationContext.class),
                    UninstallMetaSchemaMigrations.migrations());
        } catch (SQLException e) {
            throw new PlatformFailureException(
                    "cannot uninstall data module",
                    e,
                    SystemExceptionIds.EX_MODULE_CANNOT_BE_UNINSTALLED
            );
        }
    }

    @Override
    public void start() {
        LOGGER.info("Starting...");

        // Utils and indexes
        ModelUtils.init();

        // Call after context refresh
        for (Class<?> klass : REFRESH_ON_STARTUP_CLASSES) {
            AfterContextRefresh r = (AfterContextRefresh) configuration.getConfiguredApplicationContext().getBean(klass);
            r.afterContextRefresh();
        }

        // Ensure, service indexes created
        metaModelMappingService.ensureMetaModelIndex();

        // Publish segments
        addSegments(configuration.getBeansByNames(SEGMENTS));

        if (StringUtils.isNoneBlank(measureUnitsFilePath)) {
            try (final InputStream metaModelInputStream = new URL(measureUnitsFilePath).openStream()) {
                metaModelImportService.importMeasureUnits(metaModelInputStream);
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (StringUtils.isNoneBlank(metaModelFilePath)) {
            try (final InputStream metaModelInputStream = new URL(metaModelFilePath).openStream()) {
                metaModelImportService.importModel(metaModelInputStream, true);
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        LOGGER.info("Started.");
    }

    @Override
    public void ready() {
        metaDraftService.initDraftService();

        CreateDraftModelRequestContext createDraftModelRequestContext = CreateDraftModelRequestContext
                .builder()
                .changeActive(true)
                .build();

        executionService.execute(
                CreateDraftModelRequestContext
                        .builder()
                        .changeActive(true)
                        .fragment(createDraftModelRequestContext)
                        .build());
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping...");

        DataSourceUtils.shutdown(metaDataSource);

        LOGGER.info("Stopped.");
    }

    private Migrator getMigrator() throws SQLException {

        Connection connection = metaDataSource.getConnection();
        Database database = new Selector()
                .loadFromConnection(connection, MetaConfigurationConstants.META_SCHEMA_NAME);

        return new Migrator(database, "meta_change_log");
    }
}
