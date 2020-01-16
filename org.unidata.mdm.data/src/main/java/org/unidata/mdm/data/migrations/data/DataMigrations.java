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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.unidata.mdm.data.type.storage.DataCluster;
import org.unidata.mdm.data.type.storage.DataNode;

import nl.myndocs.database.migrator.MigrationScript;
import nl.myndocs.database.migrator.processor.MigrationContext;

/**
 * @author Mikhail Mikhailov
 * Data migrations.
 */
public final class DataMigrations {


    private static final MigrationScript[] MIGRATIONS = {
        new UN10031Migration()
    };
    /**
     * Constructor.
     */
    private DataMigrations() {
        super();
    }

    static InputStream[] loadRawResources(ApplicationContext ctx, String... names) {

        Resource[] raw = Arrays.stream(names)
                .map(name -> ctx.getResource("classpath:/storage/migrations/" + name + ".sql"))
                .toArray(Resource[]::new);

        if (raw.length > 0) {
            return Stream.of(raw)
                .map(r -> { try { return r.getInputStream(); } catch (IOException ioe) { return null; } })
                .filter(Objects::nonNull)
                .toArray(InputStream[]::new);
        }

        return (InputStream[]) ArrayUtils.EMPTY_OBJECT_ARRAY;
    }

    static InputStream[] loadRawResources(String tag, ApplicationContext ctx) {

        try {
            Resource[] raw = ctx.getResources("classpath:/storage/migrations/" + tag + "*.sql");
            if (raw.length > 0) {
                return Stream.of(raw)
                    .map(r -> { try { return r.getInputStream(); } catch (IOException ioe) { return null; } })
                    .filter(Objects::nonNull)
                    .toArray(InputStream[]::new);
            }
        } catch (IOException ioe) {
            // Exit silently
        }

        return (InputStream[]) ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
    /**
     * Makes SONAR happy.
     * @return migrations
     */
    public static MigrationScript[] migrations() {
        return MIGRATIONS;
    }
    /**
     * Creates migration contexts for all the nodes, participating inthe cluster
     * @param cluster the cluster to migrate
     * @param applicationContext current context
     * @return list of contexts
     */
    public static List<DataMigrationContext> of(DataCluster cluster, ApplicationContext applicationContext) {
        return Arrays.stream(cluster.getNodes())
            .map(node -> new DataMigrationContext(cluster, node, applicationContext))
            .collect(Collectors.toList());
    }
    /**
     * @author Mikhail Mikhailov
     * Just a placeholder for future use.
     *
     */
    public static class DataMigrationContext extends MigrationContext {
        /**
         * The cluster we're migrating.
         */
        private final DataCluster cluster;
        /**
         * Particular node, we are running on.
         */
        private final DataNode node;
        /**
         * The app context.
         */
        private final ApplicationContext applicationContext;
        /**
         * Constructor.
         * @param cluster the cluster to migrate
         * @param node the node we are on
         * @param applicationContext current context
         */
        public DataMigrationContext(DataCluster cluster, DataNode node, ApplicationContext applicationContext) {
            super();
            this.cluster = cluster;
            this.node = node;
            this.applicationContext = applicationContext;
        }
        /**
         * The cluster we're migrating.
         * @return the cluster
         */
        public DataCluster getCluster() {
            return cluster;
        }
        /**
         * Particular node, we are running on.
         * @return the node
         */
        public DataNode getNode() {
            return node;
        }
        /**
         * @return the applicationContext
         */
        public ApplicationContext getApplicationContext() {
            return applicationContext;
        }
    }
}
