package org.unidata.mdm.data.migrations.data;

import nl.myndocs.database.migrator.definition.Partition;
import nl.myndocs.database.migrator.definition.PartitionSpec;

import org.unidata.mdm.data.configuration.DataConfigurationConstants;
import org.unidata.mdm.data.type.storage.DataShard;

/**
 * @author Mikhail Mikhailov
 * Common stuff for data migrations, including static methods, what can called from elsewhere.
 */
public class BaseDataMigration {
    /**
     * Constructor.
     */
    protected BaseDataMigration() {
        super();
    }
    /**
     * Creates foreign partition.
     * @param sh
     * @param partitionName
     * @param spec
     * @return
     */
    public static Partition createCurrentForeignPartition(DataShard sh, String partitionName, PartitionSpec spec) {
        return new Partition.Builder()
                .setPartitionName(partitionName)
                .setPartitionSpec(spec)
                .setForeign(sh.getPrimary().getName())
                .addForeignOption("schema_name", DataConfigurationConstants.DATA_STORAGE_SCHEMA_NAME)
                .addForeignOption("table_name", partitionName)
                .addForeignOption("is_distributed", "true")
                .build();
    }
}
