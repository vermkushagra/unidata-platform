package org.unidata.mdm.meta.migration;

import nl.myndocs.database.migrator.MigrationScript;
import nl.myndocs.database.migrator.definition.Migration;

/**
 * migration for create tables
 *
 * @author maria.chistyakova
 * @since 11.10.2019
 */
public class UN12317InitializationMetaSchema implements MigrationScript {


    /**
     * Constructor.
     */
    public UN12317InitializationMetaSchema() {
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
        return "UN-12317__InitializationMetaSchema";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void migrate(Migration migration) {
        final MetaMigrationContext mctx = (MetaMigrationContext) migration.getContext();

        migration.raw()
                .sql(InstallMetaSchemaMigrations.loadRawResources(mctx.getApplicationContext(),
                        "UN-12317-meta-schema",
                        "UN-12317-meta-initialization-data"))
                .save();


    }
}
