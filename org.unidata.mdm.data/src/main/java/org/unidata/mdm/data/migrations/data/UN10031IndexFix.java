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

import java.util.stream.Collectors;
import java.util.stream.Stream;

import nl.myndocs.database.migrator.MigrationScript;
import nl.myndocs.database.migrator.definition.Index;
import nl.myndocs.database.migrator.definition.Migration;
import nl.myndocs.database.migrator.definition.Partition;
import nl.myndocs.database.migrator.definition.PartitionSet;
import nl.myndocs.database.migrator.definition.PartitionSpec;

import org.unidata.mdm.data.configuration.DataConfigurationConstants;
import org.unidata.mdm.data.migrations.data.DataMigrations.DataMigrationContext;
import org.unidata.mdm.data.type.storage.DataNode;

/**
 * @author Mikhail Mikhailov
 * Not used. Just sample. Remove afterwards.
 */
public class UN10031IndexFix implements MigrationScript {

    /**
     * Constructor.
     */
    public UN10031IndexFix() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String author() {
        return "mikhail";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String migrationId() {
        return "UN10031IndexFix";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void migrate(Migration migration) {

        final DataMigrationContext mctx = (DataMigrationContext) migration.getContext();
        final DataNode currentNode = mctx.getNode();

        // Record vistory
        migration.table("record_vistory")
            .addPartitions(PartitionSet.TYPE.LIST, pb -> pb.keyColumn("shard").partitions(() ->
                Stream.of(mctx.getCluster().getShards())
                        .map(sh -> {

                            String partitionName = "record_vistory_p" + sh.getNumber();
                            if (sh.getPrimary() == currentNode) {
                                return new Partition.Builder()
                                    .setPartitionName(partitionName)
                                    .setPartitionSpec(PartitionSpec.of(Integer.toString(sh.getNumber())))
                                    .dropIndex("ix_record_vistory_p" + sh.getNumber() + "_origin_id_revision")
                                    .addIndex("ix_record_vistory_p" + sh.getNumber() + "_origin_id_revision", Index.TYPE.UNIQUE, ib ->
                                        ib.columns("origin_id", "revision")
                                          .include("id", "origin_id", "valid_from", "valid_to", "revision", "status", "approval", "create_date"))
                                    .build();
                            } else {
                                return new Partition.Builder()
                                    .setPartitionName(partitionName)
                                    .setPartitionSpec(PartitionSpec.of(Integer.toString(sh.getNumber())))
                                    .setForeign(sh.getPrimary().getName())
                                    .addForeignOption("schema_name", DataConfigurationConstants.DATA_STORAGE_SCHEMA_NAME)
                                    .addForeignOption("table_name", partitionName)
                                    /*
                                    .addForeignOption("is_distributed", "true")
                                    */
                                    .build();
                            }
                        })
                        .collect(Collectors.toList())
                ))
                .save();

    }
}
