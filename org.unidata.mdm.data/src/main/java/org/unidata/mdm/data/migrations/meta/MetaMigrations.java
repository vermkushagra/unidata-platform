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

package org.unidata.mdm.data.migrations.meta;

import nl.myndocs.database.migrator.MigrationScript;
import nl.myndocs.database.migrator.definition.Column;
import nl.myndocs.database.migrator.definition.Constraint;
import nl.myndocs.database.migrator.definition.Migration;

/**
 * @author Mikhail Mikhailov
 * Cluster schema migrations.
 */
public final class MetaMigrations {

    private static final MigrationScript[] MIGRATIONS = {
        new MigrationScript() {

            @Override
            public String migrationId() {
                return "UN-10031";
            }

            @Override
            public void migrate(Migration migration) {

                // Nodes table
                migration.table("nodes_info")
                    .addColumn("id", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                    .addColumn("name", Column.TYPE.TEXT, cb -> cb.notNull(true))
                    .addColumn("host", Column.TYPE.TEXT, cb -> cb.notNull(true))
                    .addColumn("port", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                    .addColumn("dbname", Column.TYPE.TEXT, cb -> cb.notNull(true))
                    .addColumn("username", Column.TYPE.TEXT, cb -> cb.notNull(true))
                    .addColumn("password", Column.TYPE.TEXT, cb -> cb.notNull(true))
                    .addColumn("settings", Column.TYPE.TEXT, cb -> cb.notNull(true))
                    .addColumn("create_date", Column.TYPE.TIMESTAMPTZ, cb -> cb.notNull(true).defaultValue("current_timestamp"))
                    .addColumn("update_date", Column.TYPE.TIMESTAMPTZ)
                    .addConstraint("pk_nodes_info_id", Constraint.TYPE.PRIMARY_KEY, "id")
                    .save();

                // Cluster table
                migration.table("cluster_info")
                    .addColumn("id", Column.TYPE.SMALL_INTEGER, cb -> cb.notNull(true).autoIncrement(true))
                    .addColumn("name", Column.TYPE.VARCHAR, cb -> cb.notNull(true).size(255))
                    .addColumn("shards_number", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                    .addColumn("distribution_factor", Column.TYPE.INTEGER, cb -> cb.notNull(true))
                    .addColumn("has_data", Column.TYPE.BOOLEAN, cb -> cb.notNull(true).defaultValue("false"))
                    .addColumn("initialized", Column.TYPE.BOOLEAN, cb -> cb.notNull(true).defaultValue("false"))
                    .addColumn("version", Column.TYPE.INTEGER, cb -> cb.notNull(true).autoIncrement(true))
                    .addConstraint("pk_cluster_info_id", Constraint.TYPE.PRIMARY_KEY, "id")
                    .save();
            }

            @Override
            public String author() {
                return "mikhail";
            }
        }
    };

    /**
     * Constructor.
     */
    private MetaMigrations() {
        super();
    }
    /**
     * Makes SONAR happy.
     * @return migrations
     */
    public static MigrationScript[] migrations() {
        return MIGRATIONS;
    }
}
