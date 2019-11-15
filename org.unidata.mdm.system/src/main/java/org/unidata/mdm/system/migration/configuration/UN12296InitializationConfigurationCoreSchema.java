package org.unidata.mdm.system.migration.configuration;

import nl.myndocs.database.migrator.MigrationScript;
import nl.myndocs.database.migrator.definition.Migration;
import org.unidata.mdm.system.migration.SpringContextAwareMigrationContext;
import org.unidata.mdm.system.migration.util.MigrationUtil;

/**
 * migration for create tables
 *
 * @author maria.chistyakova
 * @since 11.10.2019
 */
public class UN12296InitializationConfigurationCoreSchema implements MigrationScript {


    /**
     * Constructor.
     */
    public UN12296InitializationConfigurationCoreSchema() {
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
        return "UN-12296__InitializationConfigurationCoreSchema";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void migrate(Migration migration) {
        final SpringContextAwareMigrationContext mctx = (SpringContextAwareMigrationContext) migration.getContext();

        migration.raw()
                .sql(MigrationUtil.loadRawResources(mctx.getApplicationContext(),
                        "UN-12296-configuration-structure"))
                .save();


    }
}
