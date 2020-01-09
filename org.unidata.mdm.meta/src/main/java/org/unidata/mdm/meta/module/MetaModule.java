package org.unidata.mdm.meta.module;

import nl.myndocs.database.migrator.database.Selector;
import nl.myndocs.database.migrator.database.query.Database;
import nl.myndocs.database.migrator.processor.Migrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidata.mdm.core.dto.BusRoutesDefinition;
import org.unidata.mdm.core.service.BusConfigurationService;
import org.unidata.mdm.meta.configuration.MetaConfiguration;
import org.unidata.mdm.meta.configuration.MetaConfigurationConstants;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.meta.migration.InstallMetaSchemaMigrations;
import org.unidata.mdm.meta.migration.MetaMigrationContext;
import org.unidata.mdm.meta.migration.UninstallMetaSchemaMigrations;
import org.unidata.mdm.meta.service.MetaDraftService;
import org.unidata.mdm.meta.service.MetaMeasurementService;
import org.unidata.mdm.meta.service.MetaModelMappingService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.service.segments.ModelDeleteFinishExecutor;
import org.unidata.mdm.meta.service.segments.ModelDeleteStartExecutor;
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
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.module.AbstractModule;
import org.unidata.mdm.system.type.module.Dependency;
import org.unidata.mdm.system.util.DataSourceUtils;
import org.unidata.mdm.system.util.IOUtils;

import javax.sql.DataSource;
import java.io.IOException;
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

    // TODO: 20.12.2019 refactor this static array to annotation style and fill on start.
    //  ex: @PipelineSegmint(moduleLink=MetaModule.class)
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

    private static final String[] PIPELINES = {
            "org.unidata.mdm.meta[MODEL_UPSERT_START]",
            "org.unidata.mdm.meta[MODEL_PUBLISH_START]",
            "org.unidata.mdm.meta[MODEL_GET_START]",
            "org.unidata.mdm.meta[MODEL_DELETE_START]"
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

    private Migrator migrator;

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

        LOGGER.info("Started.");
    }

    @Override
    public void ready() {
        metaDraftService.initDraftService();
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping...");

        DataSourceUtils.shutdown(metaDataSource);

        LOGGER.info("Stopped.");
    }

    private Migrator getMigrator() throws SQLException {
        if (migrator != null) {
            return migrator;
        }

        Connection connection = metaDataSource.getConnection();
        Database database = new Selector()
                .loadFromConnection(connection, MetaConfigurationConstants.META_SCHEMA_NAME);

        return new Migrator(database, "meta_change_log");
    }
}
