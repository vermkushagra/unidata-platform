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

package org.unidata.mdm.search.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.mapping.Mapping;
import org.unidata.mdm.system.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov on Oct 7, 2019
 * A context type for creating indexes and their mappings.
 */
public class MappingRequestContext extends CommonRequestContext implements TypedSearchContext {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = 8819233250828339429L;
    /**
     * Query type name.
     */
    private final String entity;
    /**
     * The storage id to use. Overrides the system one.
     */
    private final String storageId;
    /**
     * The number of primary shards.
     */
    private final int shards;
    /**
     * The number of replicas.
     */
    private final int replicas;
    /**
     * The number of fields.
     */
    private final int fields;
    /**
     * Mappings.
     */
    private final transient List<Mapping> mappings;
    /**
     * Constructor.
     * @param b
     */
    private MappingRequestContext(MappingRequestContextBuilder b) {
        super(b);
        this.entity = b.entity;
        this.storageId = b.storageId;
        this.shards = b.shards;
        this.replicas = b.replicas;
        this.fields = b.fields;
        this.mappings = b.mappings;

        setFlag(SearchContextFlags.FLAG_INDEX_FORCE_CREATE, b.forceCreate);
        setFlag(SearchContextFlags.FLAG_INDEX_DROP, b.drop);
        setFlag(SearchContextFlags.FLAG_INDEX_WHITESPACE_TOKENIZE, b.whitespace);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStorageId() {
        return storageId;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntity() {
        return entity;
    }
    /**
     * @return the shards
     */
    public int getShards() {
        return shards;
    }
    /**
     * @return the replicas
     */
    public int getReplicas() {
        return replicas;
    }
    /**
     * @return the fields
     */
    public int getFields() {
        return fields;
    }
    /**
     * Gets the collected types.
     * @return types
     */
    public Collection<IndexType> getTypes() {
        return CollectionUtils.isEmpty(this.mappings) ? Collections.emptyList() : mappings.stream().map(Mapping::getIndexType).collect(Collectors.toList());
    }
    /**
     * Gets the mappings.
     * @return mappings
     */
    public Collection<Mapping> getMappings() {
        return CollectionUtils.isEmpty(this.mappings) ? Collections.emptyList() : mappings;
    }
    /**
     * Force index creation or not, if it already exists.
     * @return true if so, false otherwise
     */
    public boolean forceCreate() {
        return getFlag(SearchContextFlags.FLAG_INDEX_FORCE_CREATE);
    }
    /**
     * Drop indicator.
     * @return true if so, false otherwise
     */
    public boolean drop() {
        return getFlag(SearchContextFlags.FLAG_INDEX_DROP);
    }
    /**
     * Create indicator.
     * @return true if so, false otherwise
     */
    public boolean create() {
        return !getFlag(SearchContextFlags.FLAG_INDEX_DROP);
    }
    /**
     * WS tokenize flag.
     * @return true if so, false otherwise
     */
    public boolean whitespace() {
        return getFlag(SearchContextFlags.FLAG_INDEX_WHITESPACE_TOKENIZE);
    }
    /**
     * Gets a builder.
     * @return builder
     */
    public static MappingRequestContextBuilder builder() {
        return new MappingRequestContextBuilder();
    }
    /**
     * Request builder.
     * @author Mikhail Mikhailov on Oct 7, 2019
     */
    public static class MappingRequestContextBuilder extends CommonRequestContextBuilder<MappingRequestContextBuilder> {
        /**
         * Query type name.
         */
        private String entity;
        /**
         * The storage id to use. Overrides the system one.
         */
        private String storageId;
        /**
         * Force (re-)create index, even if it already exists.
         */
        private boolean forceCreate;
        /**
         * Drop action.
         */
        private boolean drop;
        /**
         * Special WS tokenization.
         */
        private boolean whitespace;
        /**
         * The number of primary shards.
         */
        private int shards;
        /**
         * The number of replicas.
         */
        private int replicas;
        /**
         * The number of fields.
         */
        private int fields;
        /**
         * Mappings.
         */
        private List<Mapping> mappings;
        /**
         * Constructor.
         */
        private MappingRequestContextBuilder() {
            super();
        }
        /**
         * @param entityName - entity name
         * @return self
         */
        public MappingRequestContextBuilder entity(String entityName) {
            this.entity = entityName;
            return self();
        }
        /**
         * Overrides default storage id.
         *
         * @param storageId the storage id to use
         * @return self
         */
        public MappingRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return self();
        }
        /**
         * Force (re-)create index, even if it already exists.
         *
         * @param storageId the storage id to use
         * @return self
         */
        public MappingRequestContextBuilder forceCreate(boolean force) {
            this.forceCreate = force;
            return self();
        }
        /**
         * Drop index / mapping if exists.
         *
         * @param drop the flag
         * @return self
         */
        public MappingRequestContextBuilder drop(boolean drop) {
            this.drop = drop;
            return self();
        }
        /**
         * Special WS tokenization.
         *
         * @param whitespace the flag
         * @return self
         */
        public MappingRequestContextBuilder whitespace(boolean whitespace) {
            this.whitespace = whitespace;
            return self();
        }
        /**
         * Number of primary.
         *
         * @param shards the number of primary
         * @return self
         */
        public MappingRequestContextBuilder shards(int shards) {
            this.shards = shards;
            return self();
        }
        /**
         * Number of replicas.
         *
         * @param replicas the number of replicas
         * @return self
         */
        public MappingRequestContextBuilder replicas(int replicas) {
            this.replicas = replicas;
            return self();
        }
        /**
         * Number of fields in an index.
         *
         * @param fields the number of fields
         * @return self
         */
        public MappingRequestContextBuilder fields(int fields) {
            this.fields = fields;
            return self();
        }
        /**
         * Puts some mappings.
         *
         * @param mappings the mappings
         * @return self
         */
        public MappingRequestContextBuilder mappings(Mapping... mappings) {

            for (int i = 0; mappings != null && i < mappings.length; i++) {

                if (this.mappings == null) {
                    this.mappings = new ArrayList<>(mappings.length);
                }

                this.mappings.add(mappings[i]);
            }

            return self();
        }
        /**
         * Puts a mapping.
         *
         * @param mapping the mapping
         * @return self
         */
        public MappingRequestContextBuilder mapping(Mapping mapping) {

            if (this.mappings == null) {
                this.mappings = new ArrayList<>();
            }

            this.mappings.add(mapping);
            return self();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public MappingRequestContext build() {
            return new MappingRequestContext(this);
        }
    }
}
