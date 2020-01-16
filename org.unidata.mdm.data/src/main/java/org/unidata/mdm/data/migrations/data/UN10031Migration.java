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

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nl.myndocs.database.migrator.MigrationScript;
import nl.myndocs.database.migrator.definition.Column;
import nl.myndocs.database.migrator.definition.Constraint;
import nl.myndocs.database.migrator.definition.Index;
import nl.myndocs.database.migrator.definition.Migration;
import nl.myndocs.database.migrator.definition.Partition;
import nl.myndocs.database.migrator.definition.PartitionSet;
import nl.myndocs.database.migrator.definition.PartitionSpec;

import org.unidata.mdm.data.configuration.DataConfigurationConstants;
import org.unidata.mdm.data.type.storage.DataNode;
import org.unidata.mdm.data.type.storage.DataShard;

public final class UN10031Migration extends BaseDataMigration implements MigrationScript {
    @Override
    public String migrationId() {
        return "UN-10031";
    }

    @Override
    public String author() {
        return "mikhail";
    }

    @Override
    public void migrate(Migration migration) {

        final DataMigrations.DataMigrationContext mctx = (DataMigrations.DataMigrationContext) migration.getContext();
        final DataNode currentNode = mctx.getNode();

        migration.raw()
                .sql("CREATE TYPE approval_state AS ENUM ('PENDING', 'APPROVED', 'DECLINED', '')")
                .sql("CREATE TYPE data_shift AS ENUM ('PRISTINE', 'REVISED', '')")
                .sql("CREATE TYPE record_status AS ENUM ('ACTIVE', 'INACTIVE', 'MERGED', '')")
                .sql("CREATE TYPE relation_type AS ENUM ('MANY_TO_MANY', 'CONTAINS', 'REFERENCES', '')")
                .sql("CREATE TYPE operation_type AS ENUM ('DIRECT', 'CASCADED', 'COPY')")
                .sql("CREATE TYPE reference_type AS ENUM ('ORIGIN', 'ETALON')")
                .sql("CREATE TYPE status_update AS (id uuid, lsn integer, shard integer, update_date timestamptz, updated_by varchar, status record_status)")
                .sql("CREATE TYPE approval_update AS (id uuid, lsn integer, shard integer, update_date timestamptz, updated_by varchar, approval approval_state)")
                .sql("CREATE EXTENSION IF NOT EXISTS postgres_fdw")
                .save();

        migration.raw()
                .sql(ctx -> {

                    DataMigrations.DataMigrationContext dCtx = (DataMigrations.DataMigrationContext) ctx;
                    Collection<String> result = new ArrayList<>();
                    DataNode thisNode = dCtx.getNode();

                    for (DataNode n : dCtx.getCluster().getNodes()) {

                        if (n.getNumber() == thisNode.getNumber()) {
                            continue;
                        }

                        StringBuilder svb = new StringBuilder();
                        StringBuilder umb = new StringBuilder();

                        svb.append("CREATE SERVER IF NOT EXISTS ")
                                .append(n.getName())
                                .append(" FOREIGN DATA WRAPPER postgres_fdw OPTIONS (host '")
                                .append(n.getHost())
                                .append("', dbname '")
                                .append(n.getDatabase())
                                .append("', port '")
                                .append(n.getPort())
                                .append("'")
                                /*
                                .append("', schema_name '")
                                .append(StorageUtils.DATA_STORAGE_SCHEMA_NAME)
                                .append("', is_distributed 'true'")
                                */
                                .append(")");

                        umb.append("CREATE USER MAPPING IF NOT EXISTS FOR CURRENT_USER SERVER ")
                                .append(n.getName())
                                .append(" OPTIONS (user '")
                                .append(n.getUser())
                                .append("', password '")
                                .append(n.getPassword())
                                .append("')");

                        result.add(svb.toString());
                        result.add(umb.toString());
                    }

                    return result;
                })
                .save();

        // Record "etalons"
        migration.table("record_etalons")
                .addColumn("shard", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                .addColumn("lsn", Column.TYPE.BIG_INTEGER, cb -> cb.autoIncrement(true))
                .addColumn("id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("name", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("create_date", Column.TYPE.TIMESTAMPTZ, cb -> cb.notNull(true))
                .addColumn("update_date", Column.TYPE.TIMESTAMPTZ, cb -> cb.isNull(true))
                .addColumn("created_by", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("updated_by", Column.TYPE.VARCHAR, cb -> cb.isNull(true).size(256))
                .addColumn("status", Column.TYPE.UDT, cb -> cb.notNull(true).udt("record_status").defaultValue("'ACTIVE'::record_status"))
                .addColumn("approval", Column.TYPE.UDT, cb -> cb.notNull(true).udt("approval_state").defaultValue("'APPROVED'::approval_state"))
                .addColumn("operation_id", Column.TYPE.VARCHAR, cb -> cb.isNull(true).size(128))
                .addPartitions(PartitionSet.TYPE.LIST, pb -> pb.keyColumn("shard").partitions(() ->
                        Stream.of(mctx.getCluster().getShards())
                                .map(sh -> {

                                    String partitionName = "record_etalons_p" + sh.getNumber();
                                    if (currentNode.getNumber() == sh.getPrimary().getNumber()) {
                                        return new Partition.Builder()
                                                .setPartitionName(partitionName)
                                                .setPartitionSpec(PartitionSpec.of(Integer.toString(sh.getNumber())))
                                                .addConstraint("pk_record_etalons_p" + sh.getNumber() + "_shard_lsn", Constraint.TYPE.PRIMARY_KEY, cb -> cb
                                                        .columns("shard", "lsn")
                                                        .include("id"))
                                                .addIndex("ix_record_etalons_p" + sh.getNumber() + "_id", Index.TYPE.HASH, "id")
                                                .build();
                                    } else {
                                        return createForeignPartition(sh, partitionName, PartitionSpec.of(Integer.toString(sh.getNumber())));
                                    }
                                })
                                .collect(Collectors.toList())
                ))
                .save();

        // Record origins
        migration.table("record_origins")
                .addColumn("id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("etalon_id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("initial_owner", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("shard", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                .addColumn("name", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("source_system", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("external_id", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(512))
                .addColumn("create_date", Column.TYPE.TIMESTAMPTZ, cb -> cb.notNull(true))
                .addColumn("update_date", Column.TYPE.TIMESTAMPTZ, cb -> cb.isNull(true))
                .addColumn("created_by", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("updated_by", Column.TYPE.VARCHAR, cb -> cb.isNull(true).size(256))
                .addColumn("status", Column.TYPE.UDT, cb -> cb.notNull(true).udt("record_status").defaultValue("'ACTIVE'::record_status"))
                .addColumn("enrichment", Column.TYPE.BOOLEAN, cb -> cb.notNull(true).defaultValue("false"))
                .addPartitions(PartitionSet.TYPE.LIST, pb -> pb.keyColumn("shard").partitions(() ->
                        Stream.of(mctx.getCluster().getShards())
                                .map(sh -> {

                                    String partitionName = "record_origins_p" + sh.getNumber();
                                    if (sh.getPrimary().getNumber() == currentNode.getNumber()) {
                                        return new Partition.Builder()
                                                .setPartitionName(partitionName)
                                                .setPartitionSpec(PartitionSpec.of(Integer.toString(sh.getNumber())))
                                                .addConstraint("pk_record_origins_p" + sh.getNumber() + "_shard_id", Constraint.TYPE.PRIMARY_KEY, cb -> cb.columns("shard", "id"))
                                                .addIndex("ix_record_origins_p" + sh.getNumber() + "_etalon_id", Index.TYPE.HASH, "etalon_id")
                                                .build();
                                    } else {
                                        return createForeignPartition(sh, partitionName, PartitionSpec.of(Integer.toString(sh.getNumber())));
                                    }
                                })
                                .collect(Collectors.toList())
                ))
                .save();

        // Record vistory
        migration.table("record_vistory")
                .addColumn("id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("origin_id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("shard", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                .addColumn("revision", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                .addColumn("valid_from", Column.TYPE.TIMESTAMPTZ, cb -> cb.isNull(true))
                .addColumn("valid_to", Column.TYPE.TIMESTAMPTZ, cb -> cb.isNull(true))
                .addColumn("data_a", Column.TYPE.CLOB, cb -> cb.isNull(true))
                .addColumn("data_b", Column.TYPE.BLOB, cb -> cb.isNull(true))
                .addColumn("create_date", Column.TYPE.TIMESTAMPTZ, cb -> cb.notNull(true).defaultValue("current_timestamp"))
                .addColumn("created_by", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("status", Column.TYPE.UDT, cb -> cb.notNull(true).udt("record_status").defaultValue("'ACTIVE'::record_status"))
                .addColumn("approval", Column.TYPE.UDT, cb -> cb.notNull(true).udt("approval_state").defaultValue("'APPROVED'::approval_state"))
                .addColumn("shift", Column.TYPE.UDT, cb -> cb.notNull(true).udt("data_shift").defaultValue("'PRISTINE'::data_shift"))
                .addColumn("operation_type", Column.TYPE.UDT, cb -> cb.notNull(true).udt("operation_type").defaultValue("'DIRECT'::operation_type"))
                .addColumn("operation_id", Column.TYPE.VARCHAR, cb -> cb.isNull(true).size(128))
                .addColumn("major", Column.TYPE.INTEGER, cb -> cb.notNull(true).defaultValue("0"))
                .addColumn("minor", Column.TYPE.INTEGER, cb -> cb.notNull(true).defaultValue("0"))
                .addPartitions(PartitionSet.TYPE.LIST, pb -> pb.keyColumn("shard").partitions(() ->
                        Stream.of(mctx.getCluster().getShards())
                                .map(sh -> {

                                    String partitionName = "record_vistory_p" + sh.getNumber();
                                    if (sh.getPrimary().getNumber() == currentNode.getNumber()) {
                                        return new Partition.Builder()
                                                .setPartitionName(partitionName)
                                                .setPartitionSpec(PartitionSpec.of(Integer.toString(sh.getNumber())))
                                                .addConstraint("pk_record_vistory_p" + sh.getNumber() + "_shard_id", Constraint.TYPE.PRIMARY_KEY, cb -> cb.columns("shard", "id"))
                                                .addIndex("ix_record_vistory_p" + sh.getNumber() + "_origin_id_revision", Index.TYPE.UNIQUE, ib -> ib
                                                        .columns("origin_id", "revision")
                                                        .include("id", "valid_from", "valid_to", "status", "approval", "create_date"))
                                                .addIndex("ix_record_vistory_p" + sh.getNumber() + "_valid_from_valid_to", Index.TYPE.BTREE, ib -> ib.columns("valid_from", "valid_to"))
                                                .build();
                                    } else {
                                        return createForeignPartition(sh, partitionName, PartitionSpec.of(Integer.toString(sh.getNumber())));
                                    }
                                })
                                .collect(Collectors.toList())
                ))
                .save();

        // Record keys
        migration.table("record_external_keys")
                .addColumn("ext_shard", Column.TYPE.INTEGER, cb -> cb.notNull(true)) // Key shard
                .addColumn("ext_key", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(1024))
                .addColumn("etalon_id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addPartitions(PartitionSet.TYPE.LIST, pb -> pb.keyColumn("ext_shard").partitions(() ->
                        Stream.of(mctx.getCluster().getShards())
                                .map(sh -> {

                                    String partitionName = "record_external_keys_p" + sh.getNumber();
                                    if (sh.getPrimary().getNumber() == currentNode.getNumber()) {
                                        return new Partition.Builder()
                                                .setPartitionName(partitionName)
                                                .setPartitionSpec(PartitionSpec.of(Integer.toString(sh.getNumber())))
                                                .addConstraint("pk_record_external_keys_p" + sh.getNumber() + "_ext_shard_ext_key", Constraint.TYPE.PRIMARY_KEY, ib -> ib
                                                        .columns("ext_shard", "ext_key")
                                                        .include("etalon_id"))
                                                .build();
                                    } else {
                                        return createForeignPartition(sh, partitionName, PartitionSpec.of(Integer.toString(sh.getNumber())));
                                    }
                                })
                                .collect(Collectors.toList())
                ))
                .save();


        // Relation "etalons"
        migration.table("relation_etalons")
                .addColumn("shard", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                .addColumn("lsn", Column.TYPE.BIG_INTEGER, cb -> cb.autoIncrement(true))
                .addColumn("id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("name", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("etalon_id_from", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("etalon_id_to", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("create_date", Column.TYPE.TIMESTAMPTZ, cb -> cb.notNull(true))
                .addColumn("update_date", Column.TYPE.TIMESTAMPTZ, cb -> cb.isNull(true))
                .addColumn("created_by", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("updated_by", Column.TYPE.VARCHAR, cb -> cb.isNull(true).size(256))
                .addColumn("status", Column.TYPE.UDT, cb -> cb.notNull(true).udt("record_status").defaultValue("'ACTIVE'::record_status"))
                .addColumn("approval", Column.TYPE.UDT, cb -> cb.notNull(true).udt("approval_state").defaultValue("'APPROVED'::approval_state"))
                .addColumn("reltype", Column.TYPE.UDT, cb -> cb.notNull(true).udt("relation_type"))
                .addColumn("operation_id", Column.TYPE.VARCHAR, cb -> cb.isNull(true).size(128))
                .addPartitions(PartitionSet.TYPE.LIST, pb -> pb.keyColumn("shard").partitions(() ->
                        Stream.of(mctx.getCluster().getShards())
                                .map(sh -> {

                                    String partitionName = "relation_etalons_p" + sh.getNumber();
                                    if (sh.getPrimary().getNumber() == currentNode.getNumber()) {
                                        return new Partition.Builder()
                                                .setPartitionName(partitionName)
                                                .setPartitionSpec(PartitionSpec.of(Integer.toString(sh.getNumber())))
                                                .addConstraint("pk_relation_etalons_p" + sh.getNumber() + "_shard_lsn", Constraint.TYPE.PRIMARY_KEY, cb -> cb
                                                        .columns("shard", "lsn")
                                                        .include("id", "etalon_id_from", "etalon_id_to", "reltype"))
                                                .addIndex("ix_relation_etalons_p" + sh.getNumber() + "_id", Index.TYPE.HASH, "id")
                                                .build();
                                    } else {
                                        return createForeignPartition(sh, partitionName, PartitionSpec.of(Integer.toString(sh.getNumber())));
                                    }
                                })
                                .collect(Collectors.toList())
                ))
                .save();

        // Relation origins
        migration.table("relation_origins")
                .addColumn("id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("etalon_id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("initial_owner", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("shard", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                .addColumn("name", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("origin_id_from", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("origin_id_to", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("source_system", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("create_date", Column.TYPE.TIMESTAMPTZ, cb -> cb.notNull(true))
                .addColumn("update_date", Column.TYPE.TIMESTAMPTZ, cb -> cb.isNull(true))
                .addColumn("created_by", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("updated_by", Column.TYPE.VARCHAR, cb -> cb.isNull(true).size(256))
                .addColumn("status", Column.TYPE.UDT, cb -> cb.notNull(true).udt("record_status").defaultValue("'ACTIVE'::record_status"))
                .addColumn("enrichment", Column.TYPE.BOOLEAN, cb -> cb.notNull(true).defaultValue("false"))
                .addPartitions(PartitionSet.TYPE.LIST, pb -> pb.keyColumn("shard").partitions(() ->
                        Stream.of(mctx.getCluster().getShards())
                                .map(sh -> {

                                    String partitionName = "relation_origins_p" + sh.getNumber();
                                    if (sh.getPrimary().getNumber() == currentNode.getNumber()) {
                                        return new Partition.Builder()
                                                .setPartitionName(partitionName)
                                                .setPartitionSpec(PartitionSpec.of(Integer.toString(sh.getNumber())))
                                                .addConstraint("pk_relation_origins_p" + sh.getNumber() + "_shard_id", Constraint.TYPE.PRIMARY_KEY, cb -> cb.columns("shard", "id"))
                                                .addIndex("ix_relation_origins_p" + sh.getNumber() + "_etalon_id", Index.TYPE.HASH, "etalon_id")
                                                .build();
                                    } else {
                                        return createForeignPartition(sh, partitionName, PartitionSpec.of(Integer.toString(sh.getNumber())));
                                    }
                                })
                                .collect(Collectors.toList())
                ))
                .save();

        // Classifiers vistory
        migration.table("relation_vistory")
                .addColumn("id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("origin_id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("shard", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                .addColumn("revision", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                .addColumn("valid_from", Column.TYPE.TIMESTAMPTZ, cb -> cb.isNull(true))
                .addColumn("valid_to", Column.TYPE.TIMESTAMPTZ, cb -> cb.isNull(true))
                .addColumn("data_a", Column.TYPE.CLOB, cb -> cb.isNull(true))
                .addColumn("data_b", Column.TYPE.BLOB, cb -> cb.isNull(true))
                .addColumn("create_date", Column.TYPE.TIMESTAMPTZ, cb -> cb.notNull(true).defaultValue("current_timestamp"))
                .addColumn("created_by", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("status", Column.TYPE.UDT, cb -> cb.notNull(true).udt("record_status").defaultValue("'ACTIVE'::record_status"))
                .addColumn("approval", Column.TYPE.UDT, cb -> cb.notNull(true).udt("approval_state").defaultValue("'APPROVED'::approval_state"))
                .addColumn("shift", Column.TYPE.UDT, cb -> cb.notNull(true).udt("data_shift").defaultValue("'PRISTINE'::data_shift"))
                .addColumn("operation_type", Column.TYPE.UDT, cb -> cb.notNull(true).udt("operation_type").defaultValue("'DIRECT'::operation_type"))
                .addColumn("operation_id", Column.TYPE.VARCHAR, cb -> cb.isNull(true).size(128))
                .addColumn("major", Column.TYPE.INTEGER, cb -> cb.notNull(true).defaultValue("0"))
                .addColumn("minor", Column.TYPE.INTEGER, cb -> cb.notNull(true).defaultValue("0"))
                .addPartitions(PartitionSet.TYPE.LIST, pb -> pb.keyColumn("shard").partitions(() ->
                        Stream.of(mctx.getCluster().getShards())
                                .map(sh -> {

                                    String partitionName = "relation_vistory_p" + sh.getNumber();
                                    if (sh.getPrimary().getNumber() == currentNode.getNumber()) {
                                        return new Partition.Builder()
                                                .setPartitionName(partitionName)
                                                .setPartitionSpec(PartitionSpec.of(Integer.toString(sh.getNumber())))
                                                .addConstraint("pk_relation_vistory_p" + sh.getNumber() + "_shard_id", Constraint.TYPE.PRIMARY_KEY, cb -> cb.columns("shard", "id"))
                                                .addIndex("ix_relation_vistory_origin_p" + sh.getNumber() + "_origin_id_revision", Index.TYPE.UNIQUE, ib -> ib.columns("origin_id", "revision"))
                                                .addIndex("ix_relation_vistory_p" + sh.getNumber() + "_valid_from_valid_to", Index.TYPE.BTREE, ib -> ib.columns("valid_from", "valid_to"))
                                                .build();
                                    } else {
                                        return createForeignPartition(sh, partitionName, PartitionSpec.of(Integer.toString(sh.getNumber())));
                                    }
                                })
                                .collect(Collectors.toList())
                ))
                .save();

        // Relation from keys
        migration.table("relation_from_keys")
                .addColumn("shard", Column.TYPE.INTEGER, cb -> cb.notNull(true)) // From key shard number. Distribution may be not that even.
                .addColumn("from_id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("name", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("to_id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("etalon_id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addPartitions(PartitionSet.TYPE.LIST, pb -> pb.keyColumns("shard").partitions(() ->
                        Stream.of(mctx.getCluster().getShards())
                                .map(sh -> {

                                    String partitionName = "relation_from_keys_p" + sh.getNumber();
                                    if (sh.getPrimary().getNumber() == currentNode.getNumber()) {
                                        return new Partition.Builder()
                                                .setPartitionName(partitionName)
                                                .setPartitionSpec(PartitionSpec.of(Integer.toString(sh.getNumber())))
                                                .addConstraint("pk_relation_from_keys_p" + sh.getNumber() + "_shard_from_id_name_to_id", Constraint.TYPE.PRIMARY_KEY, ib -> ib
                                                        .columns("shard", "from_id", "name", "to_id")
                                                        .include("etalon_id"))
                                                .build();
                                    } else {
                                        return createForeignPartition(sh, partitionName, PartitionSpec.of(Integer.toString(sh.getNumber())));
                                    }
                                })
                                .collect(Collectors.toList())
                ))
                .save();

        // Relation from keys
        migration.table("relation_to_keys")
                .addColumn("shard", Column.TYPE.INTEGER, cb -> cb.notNull(true)) // To key shard number. Distribution may be not that even.
                .addColumn("to_id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("name", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(256))
                .addColumn("from_id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addColumn("etalon_id", Column.TYPE.UUID, cb -> cb.notNull(true))
                .addPartitions(PartitionSet.TYPE.LIST, pb -> pb.keyColumns("shard").partitions(() ->
                        Stream.of(mctx.getCluster().getShards())
                                .map(sh -> {

                                    String partitionName = "relation_to_keys_p" + sh.getNumber();
                                    if (sh.getPrimary().getNumber() == currentNode.getNumber()) {
                                        return new Partition.Builder()
                                                .setPartitionName(partitionName)
                                                .setPartitionSpec(PartitionSpec.of(Integer.toString(sh.getNumber())))
                                                .addConstraint("pk_relation_to_keys_p" + sh.getNumber() + "_shard_to_id_name_from_id", Constraint.TYPE.PRIMARY_KEY, ib -> ib
                                                        .columns("shard", "to_id", "name", "from_id")
                                                        .include("etalon_id"))
                                                .build();
                                    } else {
                                        return createForeignPartition(sh, partitionName, PartitionSpec.of(Integer.toString(sh.getNumber())));
                                    }
                                })
                                .collect(Collectors.toList())
                ))
                .save();

        migration.raw()
                .sql(DataMigrations.loadRawResources(mctx.getApplicationContext(),
                        "UN-10031-update-mark",
                        "UN-10031-keys",
                        "UN-10031-timeline",
                        "UN-10031-interval"))
                .save();
    }

    /**
     * Creates foreign partition.
     *
     * @param sh
     * @param partitionName
     * @param spec
     * @return
     */
    private Partition createForeignPartition(DataShard sh, String partitionName, PartitionSpec spec) {
        return new Partition.Builder()
                .setPartitionName(partitionName)
                .setPartitionSpec(spec)
                .setForeign(sh.getPrimary().getName())
                .addForeignOption("schema_name", DataConfigurationConstants.DATA_STORAGE_SCHEMA_NAME)
                .addForeignOption("table_name", partitionName)
                /*
                .addForeignOption("is_distributed", "true")
                */
                .build();
    }
}