package org.unidata.mdm.core.migrations;

import nl.myndocs.database.migrator.MigrationScript;
import org.unidata.mdm.core.migrations.core.UN12296UninstallCoreModule;

/**
 * storage migrations for uninstall core security
 *
 * @author maria.chistyakova
 */
public final class UninstallCoreSchemaMigrations {

    private static final MigrationScript[] MIGRATIONS = {
        new UN12296UninstallCoreModule()
    };
    /**
     * Constructor.
     */
    private UninstallCoreSchemaMigrations() {
        super();
    }

    /**
     * Makes SONAR happy.
     * @return migrations
     */
    public static MigrationScript[] migrations() {
        return MIGRATIONS;
    }


}
