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

package com.unidata.mdm.backend.configuration.application;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.configuration.application.ValueValidators;
import com.unidata.mdm.backend.common.dto.configuration.ConfigurationPropertyTypeDTO;

public enum UnidataConfigurationProperty {

    // Unidata matching configuration
    UNIDATA_MATCHING_SEARCH_PAGE_SIZE(
            "unidata.matching.search.page.size",
            "unidata.properties.group.matching",
            ConfigurationPropertyTypeDTO.INTEGER,
            20,
            true,
            false
    ),

    UNIDATA_MATCHING_MAX_CLUSTER_SIZE(
            "unidata.matching.max.cluster.size",
            "unidata.properties.group.matching",
            ConfigurationPropertyTypeDTO.INTEGER,
            50,
            true,
            false
    ),

    // ---------- JMS ----------
    UNIDATA_NOTIFICATION_ENABLED(
            "unidata.notification.enabled",
            "unidata.notification.group",
            ConfigurationPropertyTypeDTO.BOOLEAN,
            false,
            true,
            false
    ),
    UNIDATA_NOTIFICATION_DB_BATCH_SIZE(
            "unidata.notification.db.batch.size",
            "unidata.notification.group",
            ConfigurationPropertyTypeDTO.INTEGER,
            10000,
            true,
            false
    ),
    UNIDATA_NOTIFICATION_ADD_EXTERNAL_ID_TO_ATTRIBUTES(
            "unidata.notification.add.external.id.to.attributes",
            "unidata.notification.group",
            ConfigurationPropertyTypeDTO.BOOLEAN,
            false,
            true,
            false
    ),


    // ------ Classifier ------
    CLASSIFIER_IMPORT_BATCH_SIZE(
            "unidata.classifier.import.batch.size",
            "unidata.properties.group.classifier",
            ConfigurationPropertyTypeDTO.INTEGER,
            5000,
            true,
            true
    ),

    CLASSIFIER_IMPORT_THREADS_POOL_SIZE(
            "unidata.classifier.import.threads.pool.size",
            "unidata.properties.group.classifier",
            ConfigurationPropertyTypeDTO.INTEGER,
            1,
            true,
            true
    ),

    CLASSIFIER_EXPORT_THREADS_POOL_SIZE(
            "unidata.classifier.export.threads.pool.size",
            "unidata.properties.group.classifier",
            ConfigurationPropertyTypeDTO.INTEGER,
            1,
            true,
            true
    ),
    //
    UNIDATA_DATA_AUTOMERGE_ENABLED(
            "unidata.data.automerge.enable",
            "unidata.properties.group.data",
            ConfigurationPropertyTypeDTO.BOOLEAN,
            false,
            true,
            false
    ),
    UNIDATA_DATA_USE_DEPRECATED_USEREXITS(
            "unidata.data.use.deprecated.userexits",
            "unidata.properties.group.data",
            ConfigurationPropertyTypeDTO.BOOLEAN,
            false,
            true,
            true
    ),
    UNIDATA_DATA_REFRESH_IMMEDIATE(
            "unidata.data.refresh.immediate",
            "unidata.properties.group.data",
            ConfigurationPropertyTypeDTO.BOOLEAN,
            true,
            false,
            true
    ),
    UNIDATA_DATA_ROLLBACK_THREAD_COUNT(
            "unidata.data.rollback.thread.count",
            "unidata.properties.group.data",
            ConfigurationPropertyTypeDTO.INTEGER,
            1,
            false,
            true
    ),

    // Version
    UNIDATA_API_VERSION("unidata.api.version", "unidata.properties.group.version", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_PLATFORM_VERSION("unidata.platform.version", "unidata.properties.group.version", ConfigurationPropertyTypeDTO.STRING),

    // Swagger
    UNIDATA_SWAGGER_BASEPATH("unidata.swagger.basepath", "unidata.properties.group.swagger", ConfigurationPropertyTypeDTO.STRING),

    // Security
    UNIDATA_SECURITY_TOKEN_TTL("unidata.security.token.ttl", "unidata.properties.group.security", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_SECURITY_TOKEN_CLEANUP("unidata.security.token.cleanup", "unidata.properties.group.security", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_SECURITY_PASSWORD_EXPIRATION("unidata.security.password.expiration", "unidata.properties.group.security", ConfigurationPropertyTypeDTO.STRING),

    // Elasticsearch
    UNIDATA_SEARCH_NODES_ADDRESSES("unidata.search.nodes.addresses", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_SEARCH_CLUSTER_NAME("unidata.search.cluster.name", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_SEARCH_INDEX_PREFIX("unidata.search.index.prefix", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_SEARCH_INDEX_RELATIONS_STRAIGHT("unidata.search.index.relations.straight", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.BOOLEAN),
    UNIDATA_SEARCH_DEFAULT_MIN_SCORE("unidata.search.default.min.score", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_SEARCH_TOTAL_COUNT_LIMIT(
            "unidata.search.total.count.limit",
            "unidata.properties.group.elasticsearch",
            ConfigurationPropertyTypeDTO.INTEGER,
            200000,
            false,
            true
    ),
    UNIDATA_SEARCH_INDEX_REFRESH_INTERVAL(
            "unidata.search.index.refresh_interval",
            "unidata.properties.group.elasticsearch",
            ConfigurationPropertyTypeDTO.INTEGER,
            1000,
            false,
            true
    ),

    UNIDATA_SEARCH_FUZZINESS("unidata.search.fuzziness", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_SEARCH_FUZZINESS_PREFIX_LENGTH("unidata.search.fuzziness.prefix.length", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_SEARCH_DISPLAY_DATE_FORMAT("unidata.search.display.date.format",
            "unidata.properties.group.elasticsearch",
            ConfigurationPropertyTypeDTO.STRING,
            "dd.MM.yyyy",
            true,
            false
    ),
    UNIDATA_SEARCH_DISPLAY_TIME_FORMAT("unidata.search.display.time.format",
            "unidata.properties.group.elasticsearch",
            ConfigurationPropertyTypeDTO.STRING,
            "HH:mm:ss",
            true,
            false
    ),
    UNIDATA_SEARCH_DISPLAY_TIMESTAMP_FORMAT(
            "unidata.search.display.timestamp.format",
            "unidata.properties.group.elasticsearch",
            ConfigurationPropertyTypeDTO.STRING,
            "dd.MM.yyyy HH:mm:ss",
            true,
            false
    ),
    UNIDATA_ELASTIC_ADMIN_ACTION_TIMEOUT(
            "unidata.elastic.admin.action.timeout",
            "unidata.properties.group.elasticsearch",
            ConfigurationPropertyTypeDTO.INTEGER,
            5000L,
            true,
            true
    ),

    // Simon perf measurement
    UNIDATA_SIMON_ENABLED(
            "unidata.simon.enabled",
            "unidata.properties.group.simon",
            ConfigurationPropertyTypeDTO.BOOLEAN,
            false,
            true,
            false
    ),

    // Validity
    UNIDATA_VALIDITY_PERIOD_START("unidata.validity.period.start", "unidata.properties.group.validity", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_VALIDITY_PERIOD_END("unidata.validity.period.end", "unidata.properties.group.validity", ConfigurationPropertyTypeDTO.STRING),

    // License
    UNIDATA_LICENSING_GPG_LICENSE_FILE("unidata.licensing.gpg.license.file", "unidata.properties.group.license", ConfigurationPropertyTypeDTO.STRING),

    // Statistic cache
    UNIDATA_STAT_CACHE_TTL("unidata.stat.cache.ttl", "unidata.properties.group.statistic.cache", ConfigurationPropertyTypeDTO.INTEGER),

    // Distributed cache
    UNIDATA_CACHE_GROUP("unidata.cache.group", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_CACHE_PASSWORD("unidata.cache.password", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_CACHE_PORT("unidata.cache.port", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_CACHE_PORT_AUTOINCREAMENT("unidata.cache.port.autoincreament", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.BOOLEAN),
    UNIDATA_CACHE_MULTICAST_ENABLED("unidata.cache.multicast.enabled", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.BOOLEAN),
    UNIDATA_CACHE_MULTICAST_GROUP("unidata.cache.multicast.group", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_CACHE_MULTICAST_PORT("unidata.cache.multicast.port", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_CACHE_MULTICAST_TTL("unidata.cache.multicast.ttl", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_CACHE_MULTICAST_TIMEOUT("unidata.cache.multicast.timeout", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_CACHE_TCP_IP_ENABLED("unidata.cache.tcp-ip.enabled", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.BOOLEAN),
    UNIDATA_CACHE_TCP_IP_MEMBERS("unidata.cache.tcp-ip.members", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.STRING),

    // Job parameters
    UNIDATA_JOB_TRIGGER_START_TIMEOUT("unidata.job.trigger.start.timeout", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_JOB_THREAD_POOL_SIZE("unidata.job.thread.pool.size", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_JOB_QUEUE_SIZE("unidata.job.queue.size", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),

    UNIDATA_JOB_REINDEX_DATA_THREAD_COUNT("unidata.job.reindex_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_JOB_REINDEX_DATA_COMMIT_INTERVAL("unidata.job.reindex_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_JOB_IMPORT_DATA_THREAD_COUNT("unidata.job.import_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_JOB_IMPORT_DATA_COMMIT_INTERVAL("unidata.job.import_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_JOB_EXPORT_DATA_THREAD_COUNT("unidata.job.export_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_JOB_EXPORT_DATA_COMMIT_INTERVAL("unidata.job.export_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),

    UNIDATA_JOB_MODIFY_DATA_THREAD_COUNT("unidata.job.batch_modify_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_JOB_MODIFY_DATA_COMMIT_INTERVAL("unidata.job.batch_modify_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),

    UNIDATA_JOB_DELETE_DATA_THREAD_COUNT("unidata.job.delete_records_job.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_JOB_DELETE_DATA_COMMIT_INTERVAL("unidata.job.delete_records_job.commit_interval", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),

    UNIDATA_JOB_MATCHING_THREAD_COUNT("unidata.job.matching.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_JOB_DUPLICATE_THREAD_COUNT("unidata.job.duplicate_job.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER),
    // Notification background job.
    NOTIFICATION_JOB_DISABLE("notification.job.disable", "unidata.properties.group.notification.job", ConfigurationPropertyTypeDTO.BOOLEAN),
    NOTIFICATION_JOB_EXECUTION_TIME("notification.job.execution.time", "unidata.properties.group.notification.job", ConfigurationPropertyTypeDTO.STRING),
    NOTIFICATION_JOB_ATTEMPT_COUNT("notification.job.attempt.count", "unidata.properties.group.notification.job", ConfigurationPropertyTypeDTO.INTEGER),

    // Clean old notification background job.
    CLEAN_NOTIFICATION_JOB_DISABLE("clean.notification.job.disable", "unidata.properties.group.clean.notification.job", ConfigurationPropertyTypeDTO.BOOLEAN),
    CLEAN_NOTIFICATION_JOB_LIFETIME_MINUTES("clean.notification.job.lifetime.minutes", "unidata.properties.group.clean.notification.job", ConfigurationPropertyTypeDTO.INTEGER),
    CLEAN_NOTIFICATION_JOB_EXECUTION_TIME("clean.notification.job.execution.time", "unidata.properties.group.clean.notification.job", ConfigurationPropertyTypeDTO.STRING),

    // Clean unused binary data background job.
    CLEAN_UNUSED_BINARY_JOB_DISABLE("clean.unused.binary.job.disable", "unidata.properties.group.clean.unused.binary.job", ConfigurationPropertyTypeDTO.BOOLEAN),
    CLEAN_UNUSED_BINARY_JOB_LIFETIME_MINUTES("clean.unused.binary.job.lifetime.minutes", "unidata.properties.group.clean.unused.binary.job", ConfigurationPropertyTypeDTO.INTEGER),
    CLEAN_UNUSED_BINARY_JOB_EXECUTION_TIME("clean.unused.binary.job.execution.time", "unidata.properties.group.clean.unused.binary.job", ConfigurationPropertyTypeDTO.STRING),

    // Calculate statistic background job.
    CALCULATE_STATISTIC_JOB_DISABLE("calculate.statistic.job.disable", "unidata.properties.group.calculate.statistic.job", ConfigurationPropertyTypeDTO.BOOLEAN),
    CALCULATE_STATISTIC_JOB_EXECUTION_TIME("calculate.statistic.job.execution.time", "unidata.properties.group.calculate.statistic.job", ConfigurationPropertyTypeDTO.STRING),

    // Invalid classification data clean job
    INVALID_CLASSIFICATION_DATA_CLEAN_JOB_DISABLE("invalid.classification.data.clean.job.enabled", "unidata.properties.group.invalid.classification.data.clean.job", ConfigurationPropertyTypeDTO.BOOLEAN),
    INVALID_CLASSIFICATION_DATA_CLEAN_EXECUTION_TIME("invalid.classification.data.clean.job.execution.time", "unidata.properties.group.invalid.classification.data.clean.job", ConfigurationPropertyTypeDTO.STRING),

    // Clean unused binary data background job.
    CLEAN_OLD_AUDIT_DATA_JOB_DISABLE("clean.old.audit.data.job.disable", "unidata.properties.group.clean.old.audit.data.job", ConfigurationPropertyTypeDTO.BOOLEAN),
    CLEAN_OLD_AUDIT_DATA_JOB_LIFETIME_MINUTES("clean.old.audit.data.job.lifetime.minutes", "unidata.properties.group.clean.old.audit.data.job", ConfigurationPropertyTypeDTO.INTEGER),
    CLEAN_OLD_AUDIT_DATA_JOB_EXECUTION_TIME("clean.old.audit.data.job.execution.time", "unidata.properties.group.clean.old.audit.data.job", ConfigurationPropertyTypeDTO.STRING),

    // Clean raw unused binary data background job.
    CLEAN_OLD_AUDIT_RAW_DATA_JOB_DISABLE("clean.old.audit.raw.data.job.disable", "unidata.properties.group.clean.old.audit.raw.data.job", ConfigurationPropertyTypeDTO.BOOLEAN),
    CLEAN_OLD_AUDIT_RAW_DATA_JOB_LIFETIME_MINUTES("clean.old.audit.raw.data.job.lifetime.minutes", "unidata.properties.group.clean.old.audit.raw.data.job", ConfigurationPropertyTypeDTO.INTEGER),
    CLEAN_OLD_AUDIT_RAW_DATA_JOB_EXECUTION_TIME("clean.old.audit.raw.data.job.execution.time", "unidata.properties.group.clean.old.audit.raw.data.job", ConfigurationPropertyTypeDTO.STRING),

    // Activiti
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_HOST("unidata.activiti.task.mailServerHost", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_PORT("unidata.activiti.task.mailServerPort", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.INTEGER),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_USE_SSL("unidata.activiti.task.mailServerUseSSL", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.BOOLEAN),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_USE_TLS("unidata.activiti.task.mailServerUseTLS", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.BOOLEAN),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_DEFAULT_FROM("unidata.activiti.task.mailServerDefaultFrom", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_USERNAME("unidata.activiti.task.mailServerUsername", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_PASSWORD("unidata.activiti.task.mailServerPassword", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_ACTIVITI_TASK_JOB_EXECUTOR_ACTIVATE("unidata.activiti.task.jobExecutorActivate", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.BOOLEAN),
    UNIDATA_ACTIVITI_TASK_ASYNC_EXECUTOR_ACTIVATE("unidata.activiti.task.asyncExecutorActivate", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.BOOLEAN),

    // Unidata audit configuration
    UNIDATA_AUDIT_EMBEDDED_URL("unidata.audit.embedded.url", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_AUDIT_EMBEDDED_USERNAME("unidata.audit.embedded.username", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_AUDIT_EMBEDDED_PASSWORD("unidata.audit.embedded.password", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.STRING),
    UNIDATA_AUDIT_EMBEDDED_MOD("unidata.audit.embedded.mode", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.STRING),

    // Default node id
    UNIDATA_NODE_ID("unidata.node.id", "unidata.properties.group.node.id", ConfigurationPropertyTypeDTO.STRING),

    JOBS_IMPORT_THREADS_POOL_SIZE(
            "unidata.jobs.import.threads.pool.size",
            "unidata.properties.group.jobs",
            ConfigurationPropertyTypeDTO.INTEGER,
            1,
            true,
            true
    ),

    JOBS_EXPORT_THREADS_POOL_SIZE(
            "unidata.jobs.export.threads.pool.size",
            "unidata.properties.group.jobs",
            ConfigurationPropertyTypeDTO.INTEGER,
            1,
            true,
            true
    ),

    IMPORT_XLSX_THREADS_POOL_SIZE(
            "unidata.import.xlsx.threads.pool.size",
            "unidata.properties.group.import.xlsx",
            ConfigurationPropertyTypeDTO.INTEGER,
            Runtime.getRuntime().availableProcessors() / 2,
            false,
            true
    ),
    IMPORT_XLSX_BATCH_SIZE(
            "unidata.import.xlsx.batch.size",
            "unidata.properties.group.import.xlsx",
            ConfigurationPropertyTypeDTO.INTEGER,
            500,
            false,
            true
    ),

    UNIDATA_BALANCER_PROTOCOL_HEADER("unidata.balancer.protocol.header", "unidata.properties.group.balancer", ConfigurationPropertyTypeDTO.STRING),

    UNIDATA_AUDIT_DISABLED("unidata.audit.disabled", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.BOOLEAN),
    UNIDATA_AUDIT_READ_EVENTS("unidata.audit.read.events", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.BOOLEAN),
    UNIDATA_AUDIT_STACKTRACE_DEPTH("unidata.audit.stacktrace.depth", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.INTEGER),
    ;
    private final String key;
    private final String groupKey;
    private final ConfigurationPropertyTypeDTO propertyType;
    private final Predicate<Optional<String>> validator;
    private final Function<String, ? extends Serializable> deserializer;
    private final Serializable defaultValue;
    private final List<Pair<? extends Serializable, String>> availableValues = new ArrayList<>();
    private final boolean required;
    private final boolean readonly;

    UnidataConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyTypeDTO propertyType
    ) {
        this(key, groupKey, propertyType, ValueValidators.ANY_VALID, propertyType.getDeserializer(), null, Collections.emptyList(), false, true);
    }

    UnidataConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyTypeDTO propertyType,
            final Serializable defaultValue,
            final boolean required,
            final boolean readonly
    ) {
        this(key, groupKey, propertyType, propertyType.getValidator(), propertyType.getDeserializer(), defaultValue, Collections.emptyList(), required, readonly);
    }

    UnidataConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyTypeDTO propertyType,
            final Predicate<Optional<String>> validator,
            final Function<String, ? extends Serializable> deserializer,
            final Serializable defaultValue,
            final List<Pair<? extends Serializable, String>> availableValues,
            final boolean required,
            final boolean readonly
    ) {
        this.key = key;
        this.groupKey = groupKey;
        this.propertyType = propertyType;
        this.validator = validator;
        this.deserializer = deserializer;
        this.defaultValue = defaultValue;
        if (!CollectionUtils.isEmpty(availableValues)) {
            this.availableValues.addAll(availableValues);
        }
        this.required = required;
        this.readonly = readonly;
    }

    public String getKey() {
        return key;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public ConfigurationPropertyTypeDTO getPropertyType() {
        return propertyType;
    }

    public Predicate<Optional<String>> getValidator() {
        return validator;
    }

    public Function<String, ? extends Serializable> getDeserializer() {
        return deserializer;
    }

    public Optional<? extends Serializable> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    public List<Pair<? extends Serializable, String>> getAvailableValues() {
        return Collections.unmodifiableList(availableValues);
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public static UnidataConfigurationProperty findByKey(final String key) {
        return Arrays.stream(UnidataConfigurationProperty.values())
                .filter(unidataConfigurationProperty -> unidataConfigurationProperty.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public static boolean exists(final String key) {
        return Arrays.stream(UnidataConfigurationProperty.values()).anyMatch(property -> property.getKey().equals(key));
    }
}
