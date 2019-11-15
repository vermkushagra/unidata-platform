package org.unidata.mdm.system.migration;

import nl.myndocs.database.migrator.MigrationScript;
import nl.myndocs.database.migrator.definition.Migration;

/**
 * @author Mikhail Mikhailov on Oct 22, 2019
 */
public class UN12296InitSystemSchema implements MigrationScript {
    /**
     * Constructor.
     */
    public UN12296InitSystemSchema() {
        super();
    }

    @Override
    public String author() {
        return "mikhail.mikhailov";
    }

    @Override
    public void migrate(Migration m) {
        m.raw()
            .sql("create table modules_info ("
                + "module_id text primary key, "
                + "version text not null, "
                + "tag text, "
                + "status text" // Better use pg enum.
                + ")")
            .save();
    }

    @Override
    public String migrationId() {
        return "UN-12296__InitSystemSchema";
    }
}
