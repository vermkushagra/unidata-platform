package org.unidata.mdm.system.migration;

import nl.myndocs.database.migrator.MigrationScript;
import org.unidata.mdm.system.migration.configuration.UN12296InitializationConfigurationCoreSchema;

/**
 * @author Mikhail Mikhailov on Oct 22, 2019
 */
public final class SystemMigrations {

    /**
     * Migrations so far.
     */
    private static final MigrationScript[] MIGRATIONS = {
            new UN12296InitSystemSchema(),
            new UN12296InitializationConfigurationCoreSchema()
    };
    /**
     * Constructor.
     */
    private SystemMigrations() {
        super();
    }
    /**
     * Makes SONAR happy.
     *
     * @return migrations
     */
    public static MigrationScript[] migrations() {
        return MIGRATIONS;
    }
}
