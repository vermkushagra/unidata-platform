package org.unidata.mdm.core.migrations.event.meta;

import nl.myndocs.database.migrator.MigrationScript;
import nl.myndocs.database.migrator.definition.Migration;
import org.unidata.mdm.system.migration.SpringContextAwareMigrationContext;
import org.unidata.mdm.system.migration.util.MigrationUtil;

/**
 * @author maria.chistyakova
 * @since 11.10.2019
 */
public class UN12296InitializationEventCoreSchema implements MigrationScript {


    /**
     * Constructor.
     */
    public UN12296InitializationEventCoreSchema() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String author() {
        return "maria.chistyakova";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String migrationId() {
        return "UN-12296__UN12296InitializationEventCoreSchema";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void migrate(Migration migration) {
        final SpringContextAwareMigrationContext mctx = (SpringContextAwareMigrationContext) migration.getContext();

        migration.raw()
                .sql(MigrationUtil.loadRawResources(mctx.getApplicationContext(),
                        "UN-11830-binary-data-structure"))
                .save();


    }
}
