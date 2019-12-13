package org.unidata.mdm.core.migrations.job.meta;

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
public class InitializationQuartzJobSchema implements MigrationScript {


    /**
     * Constructor.
     */
    public InitializationQuartzJobSchema() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String author() {
        return "Alexander Malyshev";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String migrationId() {
        return "InitializationQuartzJobSchema";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void migrate(Migration migration) {
        final SpringContextAwareMigrationContext mctx = (SpringContextAwareMigrationContext) migration.getContext();

        migration.raw()
                .sql(MigrationUtil.loadRawResources(mctx.getApplicationContext(), "quartz-db"))
                .save();


    }
}
