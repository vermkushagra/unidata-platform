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

package org.unidata.mdm.search.configuration;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Search config labeles, both dynamic and static.
 */
public final class SearchConfigurationConstants {
    /**
     * Search node(s).
     */
    public static final String SEARCH_NODES_NAME_PROPERTY = "unidata.search.nodes.addresses";
    /**
     * Search cluster property.
     */
    public static final String SEARCH_CLUSTER_NAME_PROPERTY = "unidata.search.cluster.name";
    /**
     * Index prefix property name.
     */
    public static final String SEARCH_INDEX_PREFIX_PROPERTY = "unidata.search.index.prefix";
    /**
     * Index prefix property name.
     */
    public static final String SEARCH_TOTAL_COUNT_LIMIT = "unidata.search.total.count.limit";
    /**
     * Need index relations straight side
     */
    public static final String SEARCH_INDEX_RELATIONS_STRAIGHT = "unidata.search.index.relations.straight";
    /**
     * Number of default shards property name.
     */
    public static final String SEARCH_SHARDS_NUMBER_PROPERTY = "unidata.search.shards.number";
    /**
     * Number of default shards for entity property name.
     */
    public static final String SEARCH_ENTITY_SHARDS_NUMBER_PROPERTY = "unidata.search.entity.shards.number";
    /**
     * Number of default shards for lookup property name.
     */
    public static final String SEARCH_LOOKUP_ENTITY_SHARDS_NUMBER_PROPERTY = "unidata.search.lookup.shards.number";
    /**
     * Number of default shards for system index property name.
     */
    public static final String SEARCH_SYSTEM_SHARDS_NUMBER_PROPERTY = "unidata.search.system.shards.number";
    /**
     * Number of replicas property name.
     */
    public static final String SEARCH_REPLICAS_NUMBER_PROPERTY = "unidata.search.replicas.number";
    /**
     * Number of default replicas for entity property name.
     */
    public static final String SEARCH_ENTITY_REPLICAS_NUMBER_PROPERTY = "unidata.search.entity.replicas.number";
    /**
     * Number of default replicas for lookup property name.
     */
    public static final String SEARCH_LOOKUP_ENTITY_REPLICAS_NUMBER_PROPERTY = "unidata.search.lookup.replicas.number";
    /**
     * Number of default replicas for system index property name.
     */
    public static final String SEARCH_SYSTEM_REPLICAS_NUMBER_PROPERTY = "unidata.search.system.replicas.number";
    /**
     * Number of fields per index property name.
     */
    public static final String SEARCH_FIELDS_NUMBER_PROPERTY = "unidata.search.fields.limit";
    /**
     * Admin action timeout.
     */
    public static final String SEARCH_ADMIN_ACTION_TIMEOUT = "unidata.elastic.admin.action.timeout";
    /**
     * Constructor.
     */
    private SearchConfigurationConstants() {
        super();
    }
}
