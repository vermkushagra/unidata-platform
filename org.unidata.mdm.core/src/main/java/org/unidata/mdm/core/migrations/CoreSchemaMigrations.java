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

package org.unidata.mdm.core.migrations;

import nl.myndocs.database.migrator.MigrationScript;
import org.unidata.mdm.core.migrations.audit.UN11979InitAuditTables;
import org.unidata.mdm.core.migrations.bus.InitBusConfigurationTables;
import org.unidata.mdm.core.migrations.event.meta.UN12296InitializationEventCoreSchema;
import org.unidata.mdm.core.migrations.job.meta.InitializationQuartzJobSchema;
import org.unidata.mdm.core.migrations.job.meta.UN12296InitializationJobCoreSchema;
import org.unidata.mdm.core.migrations.security.data.UN12296InsertSecurityDefaultData;
import org.unidata.mdm.core.migrations.security.meta.UN12296InitializationSecuritySchema;

/**
 * storage migrations to install security meta + admin login + resource
 *
 *
 * @author maria.chistyakova
 */
public final class CoreSchemaMigrations {

    private static final MigrationScript[] MIGRATIONS = {
            new UN12296InitializationSecuritySchema(),
            new UN12296InsertSecurityDefaultData(),
            new UN12296InitializationJobCoreSchema(),
            new UN12296InitializationEventCoreSchema(),
            new UN11979InitAuditTables(),
            new InitializationQuartzJobSchema(),
            new InitBusConfigurationTables()
    };

    /**
     * Constructor.
     */
    private CoreSchemaMigrations() {
        super();
    }

    /**
     * Makes SONAR happy.
     *
     * @return migrations
     */
    public static MigrationScript[] migrations() {
        return MIGRATIONS;
    }

}
