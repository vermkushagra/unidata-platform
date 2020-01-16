/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
