package org.unidata.mdm.core.migrations.bus;

import nl.myndocs.database.migrator.MigrationScript;
import nl.myndocs.database.migrator.definition.Column;
import nl.myndocs.database.migrator.definition.Migration;

/**
 * @author Alexander Malyshev
 */
public class InitBusConfigurationTables implements MigrationScript {

    @Override
    public String migrationId() {
        return "InitBusConfigurationTables";
    }

    @Override
    public String author() {
        return "Alexander Malyshev";
    }

    @Override
    public void migrate(Migration migration) {
        migration.table("bus_routes")
                .addColumn("route_id", Column.TYPE.VARCHAR, cb -> cb.primary(true).notNull(true).size(255))
                .addColumn("route_definition", Column.TYPE.TEXT, cb -> cb.notNull(true))
                .save();
    }
}
