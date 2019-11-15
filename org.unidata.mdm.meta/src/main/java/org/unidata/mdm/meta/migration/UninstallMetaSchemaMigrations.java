package org.unidata.mdm.meta.migration;

import nl.myndocs.database.migrator.MigrationScript;

/**
 * storage migrations for uninstall core security
 *
 * @author maria.chistyakova
 */
public final class UninstallMetaSchemaMigrations {

    private static final MigrationScript[] MIGRATIONS = {
        new UN12317UninstallMetaModule()
    };
    /**
     * Constructor.
     */
    private UninstallMetaSchemaMigrations() {
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
