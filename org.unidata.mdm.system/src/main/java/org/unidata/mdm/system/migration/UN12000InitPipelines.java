package org.unidata.mdm.system.migration;

import nl.myndocs.database.migrator.MigrationScript;
import nl.myndocs.database.migrator.definition.Migration;

/**
 * UN-12000
 * @author Mikhail Mikhailov on Nov 26, 2019
 */
public class UN12000InitPipelines implements MigrationScript  {

    @Override
    public String author() {
        return "mikhail";
    }

    @Override
    public void migrate(Migration m) {
        m.raw()
            .sql("create table if not exists pipelines_info (\n" +
                 "    start_id text not null,\n" +
                 "    subject text,\n" +
                 "    content text not null,\n" +
                 "    constraint pk_pipelines_info_start_id_subject primary key(start_id, subject)\n" +
                 ")")
            .save();
    }

    @Override
    public String migrationId() {
        return "UN-12000-pipelines";
    }
}
