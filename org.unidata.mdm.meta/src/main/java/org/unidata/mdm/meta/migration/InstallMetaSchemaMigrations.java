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

package org.unidata.mdm.meta.migration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import nl.myndocs.database.migrator.MigrationScript;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

/**
 * storage migrations to install security meta + admin login + resource
 *
 *
 * @author maria.chistyakova
 */
public final class InstallMetaSchemaMigrations {

    private static final MigrationScript[] MIGRATIONS = {
            new UN12317InitializationMetaSchema()

    };

    /**
     * Constructor.
     */
    private InstallMetaSchemaMigrations() {
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

    public static InputStream[] loadRawResources(ApplicationContext ctx, String... names) {

        Resource[] raw = Arrays.stream(names)
                .map(name -> ctx.getResource("classpath:/storage/migration/" + name + ".sql"))
                .toArray(Resource[]::new);

        if (raw.length > 0) {
            return Stream.of(raw)
                    .map(r -> { try { return r.getInputStream(); } catch (IOException ioe) { return null; } })
                    .filter(Objects::nonNull)
                    .toArray(InputStream[]::new);
        }

        return (InputStream[]) ArrayUtils.EMPTY_OBJECT_ARRAY;
    }


}
