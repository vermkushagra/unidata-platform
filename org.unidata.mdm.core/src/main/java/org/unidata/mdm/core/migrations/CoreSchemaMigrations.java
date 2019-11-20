package org.unidata.mdm.core.migrations;

import nl.myndocs.database.migrator.MigrationScript;
import org.unidata.mdm.core.migrations.core.audit.UN11979InitAuditTables;
import org.unidata.mdm.core.migrations.event.meta.UN12296InitializationEventCoreSchema;
import org.unidata.mdm.core.migrations.job.meta.UN12296InitializationJobCoreSchema;
import org.unidata.mdm.core.migrations.security.data.UN12296InsertSecurityDefaultData;
import org.unidata.mdm.core.migrations.security.meta.UN12296InitializationSecuritySchema;

/**
 * storage migrations to install security meta + admin login + resource
 *
 *
 * @author maria.chistyakova
 */
public final class CoreSchemaMigrations {

    private static final MigrationScript[] MIGRATIONS = {
            new UN12296InitializationSecuritySchema(),
            new UN12296InsertSecurityDefaultData(),
            new UN12296InitializationJobCoreSchema(),
            new UN12296InitializationEventCoreSchema(),
            new UN11979InitAuditTables()

    };

    /**
     * Constructor.
     */
    private CoreSchemaMigrations() {
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
