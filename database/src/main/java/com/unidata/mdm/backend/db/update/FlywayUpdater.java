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

package com.unidata.mdm.backend.db.update;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

// Last migrate version V1.0.539__remove_clsf_id.sql
// Please update this comment after adding a new script.
/**
 * @author Pavel Alexeev.
 * @created 2015-12-28 15:23.
 */
public class FlywayUpdater {

    /**
     * Constructor.
     */
    private FlywayUpdater() {
        super();
    }

    public static void main(String[] args) {

        System.out.println("Start DB update");

        FlywayUpdater.migrate(UpdateOptions.parse(args), "classpath:db.migration");

        System.out.println("DB update done");
    }

    /**
     * Migrate schema(s).
     * @param parameters parameters to use
     * @param locations locations to use
     */
    public static void migrate(Map<String, String> parameters, String locations) {

        Properties props = new Properties();
        props.putAll(parameters);

        Flyway flyway = new Flyway();
        flyway.configure(props);
        flyway.setLocations(locations);

        migrate(flyway, false, true);
    }

    /**
     * Migrates schema(s).
     * @param ds the data source to use
     * @param locations locations to scan
     * @param schemas schemas to clean
     * @param clean clean before migrate or not
     * @param migrate perform migrations or not
     */
    public static void migrate(DataSource ds, String locations, String[] schemas, boolean clean, boolean migrate) {

        Flyway flyway = new Flyway();
        flyway.setDataSource(ds);
        flyway.setSchemas(schemas);
        flyway.setLocations(locations);

        migrate(flyway, clean, migrate);
    }

    /**
     * Executes migrations.
     * @param flyway initialized flyway instance
     * @param clean clean scheams or not
     * @param migrate perform migration or not
     */
    private static void migrate(Flyway flyway, boolean clean, boolean migrate) {

        if (clean) {
            flyway.clean();
        }

        if (migrate) {
            flyway.setSchemas("public");
            flyway.migrate();
            flyway.setSchemas("public");
        }
    }
}
