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

package org.unidata.mdm.data.module;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.unidata.mdm.core.dto.BusRoutesDefinition;
import org.unidata.mdm.core.service.BusConfigurationService;
import org.unidata.mdm.data.configuration.DataConfiguration;
import org.unidata.mdm.data.configuration.DataConfigurationConstants;
import org.unidata.mdm.data.convert.DataClusterConverter;
import org.unidata.mdm.data.dao.DataStorageDAO;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.migrations.data.DataMigrations;
import org.unidata.mdm.data.migrations.data.DataMigrations.DataMigrationContext;
import org.unidata.mdm.data.migrations.meta.MetaMigrations;
import org.unidata.mdm.data.notification.DataNotificationSegment;
import org.unidata.mdm.data.notification.DataSendNotificationFallback;
import org.unidata.mdm.data.po.storage.DataClusterPO;
import org.unidata.mdm.data.service.DataStorageService;
import org.unidata.mdm.data.service.impl.DataRenderingHandler;
import org.unidata.mdm.data.service.job.ReindexRelationsAccumulatorPostProcessor;
import org.unidata.mdm.data.service.segments.records.RecordDeleteAccessExecutor;
import org.unidata.mdm.data.service.segments.records.RecordDeleteDataConsistencyExecutor;
import org.unidata.mdm.data.service.segments.records.RecordDeleteFinishExecutor;
import org.unidata.mdm.data.service.segments.records.RecordDeleteIndexingExecutor;
import org.unidata.mdm.data.service.segments.records.RecordDeletePeriodCheckExecutor;
import org.unidata.mdm.data.service.segments.records.RecordDeletePersistenceExecutor;
import org.unidata.mdm.data.service.segments.records.RecordDeleteStartExecutor;
import org.unidata.mdm.data.service.segments.records.RecordGetAccessExecutor;
import org.unidata.mdm.data.service.segments.records.RecordGetAttributesPostProcessingExecutor;
import org.unidata.mdm.data.service.segments.records.RecordGetDiffExecutor;
import org.unidata.mdm.data.service.segments.records.RecordGetFinishExecutor;
import org.unidata.mdm.data.service.segments.records.RecordGetStartExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertAccessExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertFinishExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertIndexingExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertLobSubmitExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertMeasuredAttributesExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertModboxExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertPeriodCheckExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertPersistenceExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertResolveCodePointersExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertStartExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertTimelineExecutor;
import org.unidata.mdm.data.service.segments.records.RecordUpsertValidateExecutor;
import org.unidata.mdm.data.service.segments.records.batch.RecordsDeleteFinishExecutor;
import org.unidata.mdm.data.service.segments.records.batch.RecordsDeletePersistenceExecutor;
import org.unidata.mdm.data.service.segments.records.batch.RecordsDeleteProcessExecutor;
import org.unidata.mdm.data.service.segments.records.batch.RecordsDeleteStartExecutor;
import org.unidata.mdm.data.service.segments.records.batch.RecordsUpsertFinishExecutor;
import org.unidata.mdm.data.service.segments.records.batch.RecordsUpsertPersistenceExecutor;
import org.unidata.mdm.data.service.segments.records.batch.RecordsUpsertProcessExecutor;
import org.unidata.mdm.data.service.segments.records.batch.RecordsUpsertStartExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationCommonPeriodCheckExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteConnectorExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteContainmentExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteFinishExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteIndexingExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeletePersistenceExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteSecurityExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteStartExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteTimelineExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationGetConnectorExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationGetFinishExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationGetSecurityExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationGetStartExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertConnectorExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertContainmentExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertFinishExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertIndexingExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertModboxExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertPersistenceExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertSecurityExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertStartExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertTimelineExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertValidateExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsDeleteFinishExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsDeletePersistenceExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsDeleteProcessExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsDeleteStartExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsUpsertConnectorExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsUpsertFinishExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsUpsertPersistenceExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsUpsertProcessExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsUpsertStartExecutor;
import org.unidata.mdm.data.type.rendering.DataRenderingAction;
import org.unidata.mdm.data.type.storage.DataCluster;
import org.unidata.mdm.data.util.DataDiffUtils;
import org.unidata.mdm.data.util.RecordFactoryUtils;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.service.AfterContextRefresh;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.batch.BatchSetPostProcessor;
import org.unidata.mdm.system.type.module.AbstractModule;
import org.unidata.mdm.system.type.module.Dependency;
import org.unidata.mdm.system.type.rendering.RenderingAction;
import org.unidata.mdm.system.type.rendering.RenderingResolver;
import org.unidata.mdm.system.util.IOUtils;

import nl.myndocs.database.migrator.database.Selector;
import nl.myndocs.database.migrator.database.query.Database;
import nl.myndocs.database.migrator.processor.Migrator;

public class DataModule extends AbstractModule {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataModule.class);

    private static final Set<Dependency> DEPENDENCIES = Collections.singleton(
            new Dependency("org.unidata.mdm.meta", "6.0")
    );
    /**
     * This module id.
     */
    public static final String MODULE_ID = "org.unidata.mdm.data";

    @SuppressWarnings("rawtypes")
    private static final Collection<Class<? extends BatchSetPostProcessor>> JOB_POST_PROCESSORS =
            Collections.singletonList(ReindexRelationsAccumulatorPostProcessor.class);
    /**
     * Classes, implementing {@link AfterContextRefresh} must to be executed on startup.
     */
    private static final Class<?>[] CONTEXT_REFRESH_CLASSES = {
        DataStorageService.class
    };
    /**
     * Start segment names.
     */
    private static final String[] SEGMENTS = {

        // 1. Start
        // Record
        // Record upsert start executor
        RecordUpsertStartExecutor.SEGMENT_ID,
        // Record get start executor
        RecordGetStartExecutor.SEGMENT_ID,
        // Record delete start executor
        RecordDeleteStartExecutor.SEGMENT_ID,

        // Relation
        // Upsert start
        RelationUpsertStartExecutor.SEGMENT_ID,
        // Get start
        RelationGetStartExecutor.SEGMENT_ID,
        // Delete start
        RelationDeleteStartExecutor.SEGMENT_ID,

        // 2. Points
        // Upsert record
        // Validate
        RecordUpsertValidateExecutor.SEGMENT_ID,
        // Security
        RecordUpsertAccessExecutor.SEGMENT_ID,
        // Period check and possibly fix
        RecordUpsertPeriodCheckExecutor.SEGMENT_ID,
        // Resolve right code attribute by code pointer, if used
        RecordUpsertResolveCodePointersExecutor.SEGMENT_ID,
        // Fix measured attributes, if used
        RecordUpsertMeasuredAttributesExecutor.SEGMENT_ID,
        // Create origin modbox
        RecordUpsertModboxExecutor.SEGMENT_ID,
        // Submit pending LOB values, if used (generates persistent objects)
        RecordUpsertLobSubmitExecutor.SEGMENT_ID,
        // Merges mod box to the NEXT timeline (former etalon phase start)
        RecordUpsertTimelineExecutor.SEGMENT_ID,
        // Generates indexing information
        RecordUpsertIndexingExecutor.SEGMENT_ID,
        // Generates persistemnce objects
        RecordUpsertPersistenceExecutor.SEGMENT_ID,

        // Get record
        // Security checker
        RecordGetAccessExecutor.SEGMENT_ID,
        // Attributes post processing (enum / lookup titles etc.)
        RecordGetAttributesPostProcessingExecutor.SEGMENT_ID,
        // Adds diff to draft to the context
        RecordGetDiffExecutor.SEGMENT_ID,

        // Delete record
        // Security check
        RecordDeleteAccessExecutor.SEGMENT_ID,
        // Period check
        RecordDeletePeriodCheckExecutor.SEGMENT_ID,
        // Data (records and rels consistency check)
        RecordDeleteDataConsistencyExecutor.SEGMENT_ID,
        // Generates index updates.
        RecordDeleteIndexingExecutor.SEGMENT_ID,
        // Data delete persistence executor
        RecordDeletePersistenceExecutor.SEGMENT_ID,

        // Common
        RelationCommonPeriodCheckExecutor.SEGMENT_ID,

        // Get relation
        // Security check
        RelationGetSecurityExecutor.SEGMENT_ID,

        // Upsert relation
        RelationUpsertSecurityExecutor.SEGMENT_ID,
        // Validate input data
        RelationUpsertValidateExecutor.SEGMENT_ID,
        // Containment support.
        RelationUpsertContainmentExecutor.SEGMENT_ID,
        // Modbox init
        RelationUpsertModboxExecutor.SEGMENT_ID,
        // Next (resulting) timeline
        RelationUpsertTimelineExecutor.SEGMENT_ID,
        // Indexing info creation
        RelationUpsertIndexingExecutor.SEGMENT_ID,
        // Persistence executor
        RelationUpsertPersistenceExecutor.SEGMENT_ID,

        // Delete relation
        // Sec. check
        RelationDeleteSecurityExecutor.SEGMENT_ID,
        // Timeline creator
        RelationDeleteTimelineExecutor.SEGMENT_ID,
        // Process containment
        RelationDeleteContainmentExecutor.SEGMENT_ID,
        // Index updates
        RelationDeleteIndexingExecutor.SEGMENT_ID,
        // Persistence executor
        RelationDeletePersistenceExecutor.SEGMENT_ID,

        // Audit
        DataNotificationSegment.SEGMENT_ID,

        // 3. Connectors
        // Upsert relations connector
        RelationUpsertConnectorExecutor.SEGMENT_ID,
        // Get relations connector
        RelationGetConnectorExecutor.SEGMENT_ID,
        // Delete relations connector
        RelationDeleteConnectorExecutor.SEGMENT_ID,

        // 4. Fallbacks
        // Audit data fallback
        DataSendNotificationFallback.SEGMENT_ID,

        // 5. Finish
        // Record
        // Upsert result creator
        RecordUpsertFinishExecutor.SEGMENT_ID,
        // Get result creator
        RecordGetFinishExecutor.SEGMENT_ID,
        // Delete result creator
        RecordDeleteFinishExecutor.SEGMENT_ID,

        // Relation
        // Upsert finish
        RelationUpsertFinishExecutor.SEGMENT_ID,
        // Get finish
        RelationGetFinishExecutor.SEGMENT_ID,
        // Delete finish
        RelationDeleteFinishExecutor.SEGMENT_ID,

        // 6. Batched segments
        // Records
        // Upsert
        RecordsUpsertStartExecutor.SEGMENT_ID,
        RecordsUpsertProcessExecutor.SEGMENT_ID,
        RecordsUpsertPersistenceExecutor.SEGMENT_ID,
        RecordsUpsertFinishExecutor.SEGMENT_ID,

        // Delete
        RecordsDeleteStartExecutor.SEGMENT_ID,
        RecordsDeleteProcessExecutor.SEGMENT_ID,
        RecordsDeletePersistenceExecutor.SEGMENT_ID,
        RecordsDeleteFinishExecutor.SEGMENT_ID,

        // Relations
        // Upsert
        RelationsUpsertStartExecutor.SEGMENT_ID,
        RelationsUpsertConnectorExecutor.SEGMENT_ID,
        RelationsUpsertProcessExecutor.SEGMENT_ID,
        RelationsUpsertPersistenceExecutor.SEGMENT_ID,
        RelationsUpsertFinishExecutor.SEGMENT_ID,

        // Delete
        RelationsDeleteStartExecutor.SEGMENT_ID,
        // RelationsDeleteConnectorExecutor.SEGMENT_ID,
        RelationsDeleteProcessExecutor.SEGMENT_ID,
        RelationsDeletePersistenceExecutor.SEGMENT_ID,
        RelationsDeleteFinishExecutor.SEGMENT_ID
    };

    private static final String[] PIPELINES = {
            "org.unidata.mdm.data[RECORD_UPSERT_START]",
            "org.unidata.mdm.data[RECORD_GET_START]",
            "org.unidata.mdm.data[RECORD_DELETE_START]"
    };

    /**
     * This configuration.
     */
    @Autowired
    private DataConfiguration configuration;
    /**
     * Storage metadata DAO.
     */
    @Autowired
    private DataStorageDAO dataStorageDAO;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private BusConfigurationService busConfigurationService;

    @Autowired
    private DataRenderingHandler dataRenderingHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return MODULE_ID;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion() {
        return "6.0";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Data";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Unidata Data module";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Dependency> getDependencies() {
        return DEPENDENCIES;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getResourceBundleBasenames() {
        return new String[]{ "data_messages" };
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RenderingAction> getRenderingActions() {
        return Arrays.asList(DataRenderingAction.values());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RenderingResolver getRenderingResolver() {
        return dataRenderingHandler;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Collection<Class<? extends BatchSetPostProcessor>> getBatchSetPostProcessors() {
        return JOB_POST_PROCESSORS;
    }
    /**
     * Initialization logic is quite complicated:
     * - schema may not exist
     * - cluster metadata may not exist
     * {@inheritDoc}
     */
    @Override
    public void install() {

        LOGGER.info("Install");

        // 1. Install/Migrate storage metadata schema.
        try (Connection connection = dataStorageDAO.getBareConnection()) {

            Database database = new Selector().loadFromConnection(connection, DataConfigurationConstants.DATA_STORAGE_SCHEMA_NAME);
            Migrator migrator = new Migrator(database, DataConfigurationConstants.META_LOG_NAME);

            migrator.migrate(MetaMigrations.migrations());
        } catch (Exception exc) {
            throw new PlatformFailureException("Failed to migrate storage metadata schema.", DataExceptionIds.EX_DATA_STORAGE_MIGRATE_META_FAILED, exc);
        }

        // 2. Load-or-default cluster info. Stuff is temporary.
        DataClusterPO clusterPO = dataStorageDAO.loadAndInit();
        if (clusterPO == null) {

            ApplicationContext ctx = configuration.getConfiguredApplicationContext();
            Environment env = ctx.getEnvironment();

            if (BooleanUtils.toBoolean(env.getProperty("unidata.data.temp.init"))) {

                Integer shardNumber = Integer.valueOf(env.getProperty("unidata.data.shards"));
                String[] nodes = env.getProperty("unidata.data.nodes", String[].class, ArrayUtils.EMPTY_STRING_ARRAY);
                String nodeId = env.getProperty("unidata.node.id");

                clusterPO = DataClusterConverter.of(shardNumber, nodes, nodeId);
                clusterPO.setVersion(1); // Avoid reload of spec on the very first run

                dataStorageDAO.save(clusterPO);
            } else {
                throw new PlatformFailureException(
                        "Data storage configuration not found in both DB and system properties.",
                        DataExceptionIds.EX_DATA_STORAGE_NOT_CONFIGURED);
            }
        }

        // 3. Install/Migrate data schema.
        DataCluster tempCluster = DataClusterConverter.of(clusterPO);
        List<DataMigrationContext> migrations = DataMigrations.of(tempCluster, configuration.getConfiguredApplicationContext());
        for (DataMigrations.DataMigrationContext ctx : migrations) {

            try (Connection connection = dataStorageDAO.nodeSelect(ctx.getNode().getNumber()).dataSource().getConnection()) {

                Database database = new Selector().loadFromConnection(connection, DataConfigurationConstants.DATA_STORAGE_SCHEMA_NAME);
                Migrator migrator = new Migrator(database, DataConfigurationConstants.DATA_LOG_NAME);

                migrator.migrate(ctx, DataMigrations.migrations());

            } catch (Exception exc) {
                throw new PlatformFailureException(
                        "Failed to migrate storage data schema for node {}.", exc,
                        DataExceptionIds.EX_DATA_STORAGE_MIGRATE_DATA_FAILED, ctx.getNode().getNumber());
            }
        }

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
                        DataExceptionIds.EX_DATA_PIPELINE_LOADING_ERROR,
                        pipeline
                );
            }
        }

        busConfigurationService.upsertBusRoutesDefinition(
                new BusRoutesDefinition(
                        "data",
                        IOUtils.readFromClasspath("routes/data.xml")
                )
        );
    }

    @Override
    public void uninstall() {
        LOGGER.info("Uninstall");
        // TODO: UN-11830 Uninstall schema
    }

    @Override
    public void start() {

        LOGGER.info("Starting...");

        // 1. Static utils
        StorageUtils.init();
        RecordFactoryUtils.init();
        DataDiffUtils.init();

        // 2. After context refresh. Special post init beans.
        for (Class<?> klass : CONTEXT_REFRESH_CLASSES) {

            Object o = configuration.getBeanByClass(klass);
            if (!AfterContextRefresh.class.isAssignableFrom(o.getClass())) {
                LOGGER.warn("Class '{}' does not implement AfterContextRefresh, although claims to implement it!", o.getClass().getName());
                continue;
            }

            AfterContextRefresh r = (AfterContextRefresh) o;
            r.afterContextRefresh();
        }

        // 3. Add segments
        addSegments(configuration.getBeansByNames(SEGMENTS));

//        auditEventBuildersRegistryService.registerEventBuilder(
//                NotificationDataConstants.RECORD_UPSERT_EVENT_TYPE,
//                DataRecordUpsertAuditEventBuilder.INSTANCE
//        );
//        auditEventBuildersRegistryService.registerEventBuilder(
//                NotificationDataConstants.RECORD_GET_EVENT_TYPE,
//                DataRecordGetAuditEventBuilder.INSTANCE
//        );
//        auditEventBuildersRegistryService.registerEventBuilder(
//                NotificationDataConstants.RECORD_DELETE_EVENT_TYPE,
//                DataRecordDeleteAuditEventBuilder.INSTANCE
//        );

        LOGGER.info("Started.");
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping...");

        dataStorageDAO.shutdown();

        LOGGER.info("Stopped.");
    }
}
