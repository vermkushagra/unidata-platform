package org.unidata.mdm.meta.module;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidata.mdm.meta.configuration.MetaConfiguration;
import org.unidata.mdm.meta.configuration.MetaConfigurationConstants;
import org.unidata.mdm.meta.migration.InstallMetaSchemaMigrations;
import org.unidata.mdm.meta.migration.MetaMigrationContext;
import org.unidata.mdm.meta.migration.UninstallMetaSchemaMigrations;
import org.unidata.mdm.meta.util.ModelUtils;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.dto.PipelineExecutionResult;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.exception.SystemExceptionIds;
import org.unidata.mdm.system.type.module.Dependency;
import org.unidata.mdm.system.type.module.Module;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.util.DataSourceUtils;

import nl.myndocs.database.migrator.database.Selector;
import nl.myndocs.database.migrator.database.query.Database;
import nl.myndocs.database.migrator.processor.Migrator;

public class MetaModule implements Module {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaModule.class);
    
    public static final String MODULE_ID = "org.unidata.mdm.meta";

    private static final Set<Dependency> DEPENDENCIES = Collections.singleton(
            new Dependency("org.unidata.mdm.core", "5.2")
    );

    @Autowired
    private DataSource metaDataSource;
    
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

    private Migrator migrator;


    @Override
    public void install() {
        LOGGER.info("Install");

        ModelUtils.init(MetaConfiguration.getApplicationContext());

        try {
            getMigrator().migrate(
                    MetaConfiguration.getBean(MetaMigrationContext.class),
                    InstallMetaSchemaMigrations.migrations());
        } catch (SQLException e) {
            throw new PlatformFailureException(
                    "cannot install data module",
                    e,
                    SystemExceptionIds.EX_MODULE_CANNOT_BE_INSTALLED
            );
        }

    }

    @Override
    public void uninstall() {
        LOGGER.info("Uninstall");
        try {
            getMigrator().migrate(
                    MetaConfiguration.getBean(MetaMigrationContext.class),
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
        ModelUtils.init(MetaConfiguration.getApplicationContext());
        MetaConfiguration.getBean(MetaConfiguration.class).startImpl();
        LOGGER.info("Started.");
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping...");
        MetaConfiguration.getBean(MetaConfiguration.class).stopImpl();
        DataSourceUtils.shutdown(metaDataSource);
        LOGGER.info("Stopped.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Start<PipelineExecutionContext>> getStartTypes() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Finish<PipelineExecutionContext, PipelineExecutionResult>> getFinishTypes() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Point<PipelineExecutionContext>> getPointTypes() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Connector<PipelineExecutionContext, PipelineExecutionResult>> getConnectorTypes() {
        return Collections.emptyList();
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
