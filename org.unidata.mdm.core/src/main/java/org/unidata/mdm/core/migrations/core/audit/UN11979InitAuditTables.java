package org.unidata.mdm.core.migrations.core.audit;

import nl.myndocs.database.migrator.MigrationScript;
import nl.myndocs.database.migrator.definition.Column;
import nl.myndocs.database.migrator.definition.Migration;

/**
 * @author Alexander Malyshev
 */
public class UN11979InitAuditTables implements MigrationScript {

    @Override
    public String migrationId() {
        return "UN-11979__InitAuditTables";
    }

    @Override
    public String author() {
        return "Alexander Malyshev";
    }

    @Override
    public void migrate(Migration migration) {
        migration.table("audit_event")
                .addColumn("type", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(255))
                .addColumn("parameters", Column.TYPE.TEXT)
                .addColumn("success", Column.TYPE.BOOLEAN, cb -> cb.notNull(true))
                .addColumn("user", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(255))
                .addColumn("client_id", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(255))
                .addColumn("server_ip", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(255))
                .addColumn("when", Column.TYPE.TIMESTAMP, cb -> cb.notNull(true));
    }
}
