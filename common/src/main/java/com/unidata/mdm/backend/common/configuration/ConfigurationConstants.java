package com.unidata.mdm.backend.common.configuration;

/**
 * @author Mikhail Mikhailov
 * Configuration variable names constants.
 */
public interface ConfigurationConstants {
    /**
     * Properties bean name.
     */
    String UNIDATA_PROPERTIES_BEAN_NAME = "backendProperties";
    /**
     * Default locale property.
     */
    String DEFAULT_LOCALE_PROPERTY = "unidata.default.locale";
    /**
     * Start property bound.
     */
    String VALIDITY_PERIOD_START_PROPERTY = "unidata.validity.period.start";
    /**
     * End property bound.
     */
    String VALIDITY_PERIOD_END_PROPERTY = "unidata.validity.period.end";
    /**
     * Search node(s).
     */
    String SEARCH_NODES_NAME_PROPERTY = "unidata.search.nodes.addresses";
    /**
     * Search cluster property.
     */
    String SEARCH_CLUSTER_NAME_PROPERTY = "unidata.search.cluster.name";
    /**
     * Index prefix property name.
     */
    String SEARCH_INDEX_PREFIX_PROPERTY = "unidata.search.index.prefix";
    /**
     * Index prefix property name.
     */
    String SEARCH_TOTAL_COUNT_LIMIT = "unidata.search.total.count.limit";
    /**
     * Need index relations straight side
     */
    String SEARCH_INDEX_RELATIONS_STRAIGHT = "unidata.search.index.relations.straight";
    /**
     * Number of shards property name.
     */
    String SEARCH_SHARDS_NUMBER_PROPERTY = "unidata.search.shards.number";
    /**
     * Number of replicas property name.
     */
    String SEARCH_REPLICAS_NUMBER_PROPERTY = "unidata.search.replicas.number";
    /**
     * Number of fields per index property name.
     */
    String SEARCH_FIELDS_NUMBER_PROPERTY = "unidata.search.fields.limit";

    /**
     * If this is a smoke test stand.
     */
    String SMOKE_STAND_FLAG_PROPERTY = "unidata.smoke.stand";
    /**
     * Smoke model to use.
     */
    String SMOKE_STAND_MODEL_PROPERTY = "unidata.smoke.model";
    /**
     * Smoke classifiers to use.
     */
    String SMOKE_STAND_CLASSIFIERS_PROPERTY = "unidata.smoke.classifiers";
    /**
     * Smoke measure units to install and use.
     */
    String SMOKE_STAND_MEASURE_UNITS_PROPERTY = "unidata.smoke.measureunits";
    /**
     * Smoke match rules to install and use.
     */
    String SMOKE_STAND_MATCH_RULES_PROPERTY = "unidata.smoke.matchrules";
    /**
     * Timeout used to start new job triggered after completed one (succeeded or failed).
     */
    String JOB_TRIGGER_START_TIMEOUT_PROPERTY = "unidata.job.trigger.start.timeout";
    /**
     * Current main API version..
     */
    String API_VERSION_PROPERTY = "unidata.api.version";
    /**
     * DB clean upon startup.
     */
    String DB_CLEAN_PROPERTY = "unidata.db.clean";
    /**
     * DB migrate upon startup.
     */
    String DB_MIGRATE_PROPERTY = "unidata.db.migrate";
    /**
     * Current main platform version..
     */
    String PLATFORM_VERSION_PROPERTY = "unidata.platform.version";
    /**
     * Unidata node id property.
     */
    String UNIDATA_NODE_ID_PROPERTY = "unidata.node.id";
    /**
     * Unidata dump target format property.
     */
    String UNIDATA_DUMP_TARGET_FORMAT_PROPERTY = "unidata.dump.target.format";
    // Unidata audit configuration constants
    /**
     * Disable audit entirely.
     */
    String UNIDATA_AUDIT_DISABLED = "unidata.audit.disabled";
    /**
     * Skip read events.
     */
    String UNIDATA_AUDIT_READ_DISABLED = "unidata.audit.read.events";
    /**
     * Stacktrace reporting max. depth.
     */
    String UNIDATA_AUDIT_STACK_TRACE_DEPTH = "unidata.audit.stacktrace.depth";

    //--------------------------------------- Classifier config -------------------------------------------------

    String CLASSIFIER_IMPORT_BATCH_SIZE = "unidata.classifier.import.batch.size";
    /**
     * Size of the cache backup.
     */
    String CLASSIFIER_CACHE_BACKUP_COUNT = "unidata.classifier.model.cache.backup.count";
    /**
     * Create near cache (consuming more memory) or not.
     */
    String CLASSIFIER_CACHE_USE_NEAR = "unidata.classifier.model.cache.near";
    /**
     * Classifiers cache entry TTL.
     */
    String CLASSIFIER_CACHE_ENTRY_TTL = "unidata.classifier.model.cache.ttl";

    String REINDEX_JOB_MAPPING_BLOCK_SIZE = "unidata.job.reindex_data.mapping.block";

    String DATA_SOAP_UPSERT_MAX_ATTEMPT_COUNT = "unidata.data.soap.upsert.max.attempt.count";
}
