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
