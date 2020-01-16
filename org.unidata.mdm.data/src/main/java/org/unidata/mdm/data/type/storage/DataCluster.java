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

package org.unidata.mdm.data.type.storage;

/**
 * @author Mikhail Mikhailov
 * Just a container for nodes, shards metadata.
 */
public class DataCluster {

    private final int id;

    private final String name;

    private final int distributionFactor;

    private final DataNode[] nodes;

    private final DataShard[] shards;

    private final boolean hasData;

    private final boolean initialized;

    private final int version;

    /**
     * Constructor.
     */
    private DataCluster(DataClusterBuilder builder) {
        super();
        this.id = builder.id;
        this.distributionFactor = builder.distributionFactor;
        this.name = builder.name;
        this.nodes = builder.nodes;
        this.shards = builder.shards;
        this.initialized = builder.initialized;
        this.hasData = builder.hasData;
        this.version = builder.version;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the distributionFactor
     */
    public int getDistributionFactor() {
        return distributionFactor;
    }

    /**
     * @return the nodes
     */
    public DataNode[] getNodes() {
        return nodes;
    }

    /**
     * @return the shards
     */
    public DataShard[] getShards() {
        return shards;
    }

    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return the hasData
     */
    public boolean hasData() {
        return hasData;
    }

    public static DataClusterBuilder builder() {
        return new DataClusterBuilder();
    }

    public static class DataClusterBuilder {

        private int id;

        private String name;

        private int distributionFactor;

        private DataNode[] nodes;

        private DataShard[] shards;

        private boolean hasData;

        private boolean initialized;

        private int version;

        private DataClusterBuilder() {
            super();
        }
        /**
         * @param id the id to set
         */
        public DataClusterBuilder id(int id) {
            this.id = id;
            return this;
        }
        /**
         * @param name the name to set
         */
        public DataClusterBuilder name(String name) {
            this.name = name;
            return this;
        }
        /**
         * @param distributionFactor the distributionFactor to set
         */
        public DataClusterBuilder distributionFactor(int distributionFactor) {
            this.distributionFactor = distributionFactor;
            return this;
        }
        /**
         * @param nodes the nodes to set
         */
        public DataClusterBuilder nodes(DataNode[] nodes) {
            this.nodes = nodes;
            return this;
        }
        /**
         * @param shards the shards to set
         */
        public DataClusterBuilder shards(DataShard[] shards) {
            this.shards = shards;
            return this;
        }
        /**
         * @param hasData the hasData to set
         */
        public DataClusterBuilder hasData(boolean hasData) {
            this.hasData = hasData;
            return this;
        }
        /**
         * @param initialized the initialized to set
         */
        public DataClusterBuilder initialized(boolean initialized) {
            this.initialized = initialized;
            return this;
        }
        /**
         * @param version the version to set
         */
        public DataClusterBuilder version(int version) {
            this.version = version;
            return this;
        }
        /**
         * The builder method.
         * @return cluster instance
         */
        public DataCluster build() {
            return new DataCluster(this);
        }
    }
}
