package org.unidata.mdm.system.module;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.unidata.mdm.system.configuration.SystemConfigurationConstants;
import org.unidata.mdm.system.migration.SpringContextAwareMigrationContext;
import org.unidata.mdm.system.migration.SystemMigrations;
import org.unidata.mdm.system.type.module.Module;
import org.unidata.mdm.system.util.IdUtils;

import nl.myndocs.database.migrator.database.Selector;
import nl.myndocs.database.migrator.database.query.Database;
import nl.myndocs.database.migrator.processor.Migrator;

/**
 * @author Alexander Malyshev
 */
public class SystemModule implements Module {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemModule.class);

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * This module id.
     */
    public static final String MODULE_ID = "org.unidata.mdm.system";

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
        return "Unidata system";
    }

    @Override
    public String getDescription() {
        return "Unidata system (root) module";
    }

    @Nullable
    @Override
    public String getTag() {
        return null;
    }

    @Override
    public void install() {
        LOGGER.info("Install");

        DataSource coreDataSource = (DataSource) applicationContext.getBean("systemDataSource");
        SpringContextAwareMigrationContext ctx = applicationContext.getBean(SpringContextAwareMigrationContext.class);

        try (Connection connection = coreDataSource.getConnection()) {

            Database database = new Selector().loadFromConnection(connection, SystemConfigurationConstants.UNIDATA_SYSTEM_SCHEMA_NAME);
            Migrator migrator = new Migrator(database, SystemConfigurationConstants.SYSTEM_MIGRATION_LOG_NAME);

            migrator.migrate(ctx, SystemMigrations.migrations());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void uninstall() {
        // NO-OP
    }

    @Override
    public void start() {
        IdUtils.init();
    }

    @Override
    public void stop() {
        // NO-OP
    }
}
