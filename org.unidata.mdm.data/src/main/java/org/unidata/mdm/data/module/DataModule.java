package org.unidata.mdm.data.module;

import java.io.IOException;
import java.sql.Connection;
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
import org.unidata.mdm.core.service.BusService;
import org.unidata.mdm.data.notification.DataSendNotificationFallback;
import org.unidata.mdm.data.notification.DataNotificationSegment;
import org.unidata.mdm.data.configuration.DataConfiguration;
import org.unidata.mdm.data.configuration.DataConfigurationConstants;
import org.unidata.mdm.data.convert.DataClusterConverter;
import org.unidata.mdm.data.dao.DataStorageDAO;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.migrations.data.DataMigrations;
import org.unidata.mdm.data.migrations.data.DataMigrations.DataMigrationContext;
import org.unidata.mdm.data.migrations.meta.MetaMigrations;
import org.unidata.mdm.data.po.storage.DataClusterPO;
import org.unidata.mdm.data.service.DataStorageService;
import org.unidata.mdm.data.service.segments.RecordDeleteDataConsistencyExecutor;
import org.unidata.mdm.data.service.segments.RecordDeleteFinishExecutor;
import org.unidata.mdm.data.service.segments.RecordDeleteIndexingExecutor;
import org.unidata.mdm.data.service.segments.RecordDeletePeriodCheckExecutor;
import org.unidata.mdm.data.service.segments.RecordDeletePersistenceExecutor;
import org.unidata.mdm.data.service.segments.RecordDeleteSecurityExecutor;
import org.unidata.mdm.data.service.segments.RecordDeleteStartExecutor;
import org.unidata.mdm.data.service.segments.RecordGetAttributesPostProcessingExecutor;
import org.unidata.mdm.data.service.segments.RecordGetDiffExecutor;
import org.unidata.mdm.data.service.segments.RecordGetFinishExecutor;
import org.unidata.mdm.data.service.segments.RecordGetSecurityExecutor;
import org.unidata.mdm.data.service.segments.RecordGetStartExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertFinishExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertIndexingExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertLobSubmitExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertMeasuredAttributesExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertMergeTimelineExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertModboxExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertPeriodCheckExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertPersistenceExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertResolveCodePointersExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertSecurityExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertStartExecutor;
import org.unidata.mdm.data.service.segments.RecordUpsertValidateExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationCommonPeriodCheckExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteContainmentExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteFinishExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteIndexingExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeletePersistenceExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteSecurityExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteStartExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationDeleteTimelineExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationGetFinishExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationGetSecurityExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationGetStartExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertContainmentExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertFinishExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertIndexingExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertModboxExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertPersistenceExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertSecurityExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertStartExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertTimelineExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertValidateExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationsDeleteConnectorExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationsGetConnectorExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationsUpsertConnectorExecutor;
import org.unidata.mdm.data.type.storage.DataCluster;
import org.unidata.mdm.data.util.DataDiffUtils;
import org.unidata.mdm.data.util.RecordFactoryUtils;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.service.AfterContextRefresh;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.module.AbstractModule;
import org.unidata.mdm.system.type.module.Dependency;

import nl.myndocs.database.migrator.database.Selector;
import nl.myndocs.database.migrator.database.query.Database;
import nl.myndocs.database.migrator.processor.Migrator;
import org.unidata.mdm.system.util.IOUtils;

public class DataModule extends AbstractModule {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataModule.class);

    private static final Set<Dependency> DEPENDENCIES = Collections.singleton(
            new Dependency("org.unidata.mdm.meta", "5.2")
    );
    /**
     * This module id.
     */
    public static final String MODULE_ID = "org.unidata.mdm.data";
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
        RecordUpsertSecurityExecutor.SEGMENT_ID,
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
        RecordUpsertMergeTimelineExecutor.SEGMENT_ID,
        // Generates indexing information
        RecordUpsertIndexingExecutor.SEGMENT_ID,
        // Generates persistemnce objects
        RecordUpsertPersistenceExecutor.SEGMENT_ID,

        // Get record
        // Security checker
        RecordGetSecurityExecutor.SEGMENT_ID,
        // Attributes post processing (enum / lookup titles etc.)
        RecordGetAttributesPostProcessingExecutor.SEGMENT_ID,
        // Adds diff to draft to the context
        RecordGetDiffExecutor.SEGMENT_ID,

        // Delete record
        // Security check
        RecordDeleteSecurityExecutor.SEGMENT_ID,
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
        RelationsUpsertConnectorExecutor.SEGMENT_ID,
        // Get relations connector
        RelationsGetConnectorExecutor.SEGMENT_ID,
        // Delete relations connector
        RelationsDeleteConnectorExecutor.SEGMENT_ID,

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
        RelationDeleteFinishExecutor.SEGMENT_ID
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
        return "5.2";
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
