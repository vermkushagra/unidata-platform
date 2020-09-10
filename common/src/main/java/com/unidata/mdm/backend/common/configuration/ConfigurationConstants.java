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
     * Simon enabled property name.
     */
    String SIMON_STATISTIC_ENABLED_PROPERTY = "unidata.simon.enabled";
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
}
