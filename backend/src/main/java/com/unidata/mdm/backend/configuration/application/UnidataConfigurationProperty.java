package com.unidata.mdm.backend.configuration.application;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.base.Optional;
import com.unidata.mdm.backend.api.rest.dto.configuration.ConfigurationPropertyTypeDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

public enum UnidataConfigurationProperty {

    // Unidata matching configuration
    UNIDATA_MATCHING_SEARCH_PAGE_SIZE(
            "unidata.matching.search.page.size",
            "unidata.properties.group.matching",
            ConfigurationPropertyTypeDTO.INTEGER,
            ValueValidators.INT_VALIDATOR,
            Integer::valueOf,
            20,
            true,
            false
    ),
    UNIDATA_MATCHING_MAX_CLUSTER_SIZE(
            "unidata.matching.max.cluster.size",
            "unidata.properties.group.matching",
            ConfigurationPropertyTypeDTO.INTEGER,
            ValueValidators.INT_VALIDATOR,
            Integer::valueOf,
            50,
            true,
            false
    ),

    // ---------- JMS ----------
    UNIDATA_NOTIFICATION_ENABLED(
            "unidata.notification.enabled",
            "unidata.notification.group",
            ConfigurationPropertyTypeDTO.BOOLEAN,
            ValueValidators.BOOLEAN_VALIDATOR,
            Boolean::valueOf,
            false,
            true
    ),
    UNIDATA_NOTIFICATION_ADD_EXTERNAL_ID_TO_ATTRIBUTES(
            "unidata.notification.add.external.id.to.attributes",
            "unidata.notification.group",
            ConfigurationPropertyTypeDTO.BOOLEAN,
            ValueValidators.BOOLEAN_VALIDATOR,
            Boolean::valueOf,
            false,
            true
    ),


    // ------ Classifier ------
    CLASSIFIER_IMPORT_BATCH_SIZE(
            "unidata.classifier.import.batch.size",
            "unidata.properties.group.classifier",
            ConfigurationPropertyTypeDTO.INTEGER,
            ValueValidators.INT_VALIDATOR,
            Integer::valueOf,
            5000,
            true,
            true
    ),

    CLASSIFIER_IMPORT_THREADS_POOL_SIZE(
            "unidata.classifier.import.threads.pool.size",
            "unidata.properties.group.classifier",
            ConfigurationPropertyTypeDTO.INTEGER,
            ValueValidators.INT_VALIDATOR,
            Integer::valueOf,
            1,
            true,
            true
    ),

    CLASSIFIER_EXPORT_THREADS_POOL_SIZE(
            "unidata.classifier.export.threads.pool.size",
            "unidata.properties.group.classifier",
            ConfigurationPropertyTypeDTO.INTEGER,
            ValueValidators.INT_VALIDATOR,
            Integer::valueOf,
            1,
            true,
            true
    ),
    //
    UNIDATA_DATA_AUTOMERGE_ENABLED(
            "unidata.data.automerge.enable",
            "unidata.properties.group.data",
            ConfigurationPropertyTypeDTO.BOOLEAN,
            ValueValidators.BOOLEAN_VALIDATOR,
            Boolean::valueOf,
            false,
            true,
            false
    ),

    // Version
    UNIDATA_API_VERSION("unidata.api.version", "unidata.properties.group.version", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_PLATFORM_VERSION("unidata.platform.version", "unidata.properties.group.version", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Swagger
    UNIDATA_SWAGGER_BASEPATH("unidata.swagger.basepath", "unidata.properties.group.swagger", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Security
    UNIDATA_SECURITY_TOKEN_TTL("unidata.security.token.ttl", "unidata.properties.group.security", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_SECURITY_TOKEN_CLEANUP("unidata.security.token.cleanup", "unidata.properties.group.security", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_SECURITY_PASSWORD_EXPIRATION("unidata.security.password.expiration", "unidata.properties.group.security", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Elasticsearch
    UNIDATA_SEARCH_NODES_ADDRESSES("unidata.search.nodes.addresses", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_SEARCH_CLUSTER_NAME("unidata.search.cluster.name", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_SEARCH_INDEX_PREFIX("unidata.search.index.prefix", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_SEARCH_INDEX_RELATIONS_STRAIGHT("unidata.search.index.relations.straight", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    UNIDATA_SEARCH_DEFAULT_MIN_SCORE("unidata.search.default.min.score", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_SEARCH_FUZZINESS("unidata.search.fuzziness", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_SEARCH_FUZZINESS_PREFIX_LENGTH("unidata.search.fuzziness.prefix.length", "unidata.properties.group.elasticsearch", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_SEARCH_DISPLAY_DATE_FORMAT("unidata.search.display.date.format",
            "unidata.properties.group.elasticsearch",
            ConfigurationPropertyTypeDTO.STRING,
            ValueValidators.STRING_VALIDATOR,
            (v) -> v,
            "dd.MM.yyyy",
            true,
            false),
    UNIDATA_SEARCH_DISPLAY_TIME_FORMAT("unidata.search.display.time.format",
            "unidata.properties.group.elasticsearch",
            ConfigurationPropertyTypeDTO.STRING,
            ValueValidators.STRING_VALIDATOR,
            (v) -> v,
            "HH:mm:ss",
            true,
            false),
    UNIDATA_SEARCH_DISPLAY_TIMESTAMP_FORMAT("unidata.search.display.timestamp.format",
            "unidata.properties.group.elasticsearch",
            ConfigurationPropertyTypeDTO.STRING,
            ValueValidators.STRING_VALIDATOR,
            (v) -> v,
            "dd.MM.yyyy HH:mm:ss",
            true,
            false
    ),
    UNIDATA_ELASTIC_ADMIN_ACTION_TIMEOUT(
            "unidata.elastic.admin.action.timeout",
            "unidata.properties.group.elasticsearch",
            ConfigurationPropertyTypeDTO.INTEGER,
            ValueValidators.INT_VALIDATOR,
            (v) -> v,
            5000L,
            true,
            true
    ),

    // Simon perf measurement
    UNIDATA_SIMON_ENABLED("unidata.simon.enabled", "unidata.properties.group.simon", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),

    // Validity
    UNIDATA_VALIDITY_PERIOD_START("unidata.validity.period.start", "unidata.properties.group.validity", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_VALIDITY_PERIOD_END("unidata.validity.period.end", "unidata.properties.group.validity", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // License
    UNIDATA_LICENSING_GPG_LICENSE_FILE("unidata.licensing.gpg.license.file", "unidata.properties.group.license", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Statistic cache
    UNIDATA_STAT_CACHE_TTL("unidata.stat.cache.ttl", "unidata.properties.group.statistic.cache", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),

    // Distributed cache
    UNIDATA_CACHE_GROUP("unidata.cache.group", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_CACHE_PASSWORD("unidata.cache.password", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_CACHE_PORT("unidata.cache.port", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.INTEGER, (v) -> v),
    UNIDATA_CACHE_PORT_AUTOINCREAMENT("unidata.cache.port.autoincreament", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    UNIDATA_CACHE_MULTICAST_ENABLED("unidata.cache.multicast.enabled", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    UNIDATA_CACHE_MULTICAST_GROUP("unidata.cache.multicast.group", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_CACHE_MULTICAST_PORT("unidata.cache.multicast.port", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_CACHE_MULTICAST_TTL("unidata.cache.multicast.ttl", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_CACHE_MULTICAST_TIMEOUT("unidata.cache.multicast.timeout", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_CACHE_TCP_IP_ENABLED("unidata.cache.tcp-ip.enabled", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    UNIDATA_CACHE_TCP_IP_MEMBERS("unidata.cache.tcp-ip.members", "unidata.properties.group.distributed.cache", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Job parameters
    UNIDATA_JOB_TRIGGER_START_TIMEOUT("unidata.job.trigger.start.timeout", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_JOB_THREAD_POOL_SIZE("unidata.job.thread.pool.size", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_JOB_QUEUE_SIZE("unidata.job.queue.size", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_JOB_REINDEX_REMOTE_THREAD_COUNT("unidata.job.reindex_remote.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_JOB_IMPORT_RECORDS_THREAD_COUNT("unidata.job.import_records.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_JOB_EXPORT_RECORDS_THREAD_COUNT("unidata.job.export_records.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),

    UNIDATA_JOB_REINDEX_DATA_THREAD_COUNT("unidata.job.reindex_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_JOB_REINDEX_DATA_COMMIT_INTERVAL("unidata.job.reindex_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_JOB_IMPORT_DATA_THREAD_COUNT("unidata.job.import_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_JOB_IMPORT_DATA_COMMIT_INTERVAL("unidata.job.import_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_JOB_EXPORT_DATA_THREAD_COUNT("unidata.job.export_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_JOB_EXPORT_DATA_COMMIT_INTERVAL("unidata.job.export_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),

    UNIDATA_JOB_MODIFY_DATA_THREAD_COUNT("unidata.job.batch_modify_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_JOB_MODIFY_DATA_COMMIT_INTERVAL("unidata.job.batch_modify_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),

    UNIDATA_JOB_DUPLICATE_DATA_THREAD_COUNT("unidata.job.duplicate_job.thread_count", "unidata.properties.group.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    // Notification background job.
    NOTIFICATION_JOB_DISABLE("notification.job.disable", "unidata.properties.group.notification.job", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    NOTIFICATION_JOB_EXECUTION_TIME("notification.job.execution.time", "unidata.properties.group.notification.job", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    NOTIFICATION_JOB_ATTEMPT_COUNT("notification.job.attempt.count", "unidata.properties.group.notification.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),

    // Clean old notification background job.
    CLEAN_NOTIFICATION_JOB_DISABLE("clean.notification.job.disable", "unidata.properties.group.clean.notification.job", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    CLEAN_NOTIFICATION_JOB_LIFETIME_MINUTES("clean.notification.job.lifetime.minutes", "unidata.properties.group.clean.notification.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    CLEAN_NOTIFICATION_JOB_EXECUTION_TIME("clean.notification.job.execution.time", "unidata.properties.group.clean.notification.job", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Clean unused binary data background job.
    CLEAN_UNUSED_BINARY_JOB_DISABLE("clean.unused.binary.job.disable", "unidata.properties.group.clean.unused.binary.job", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    CLEAN_UNUSED_BINARY_JOB_LIFETIME_MINUTES("clean.unused.binary.job.lifetime.minutes", "unidata.properties.group.clean.unused.binary.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    CLEAN_UNUSED_BINARY_JOB_EXECUTION_TIME("clean.unused.binary.job.execution.time", "unidata.properties.group.clean.unused.binary.job", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Calculate statistic background job.
    CALCULATE_STATISTIC_JOB_DISABLE("calculate.statistic.job.disable", "unidata.properties.group.calculate.statistic.job", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    CALCULATE_STATISTIC_JOB_EXECUTION_TIME("calculate.statistic.job.execution.time", "unidata.properties.group.calculate.statistic.job", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Invalid classification data clean job
    INVALID_CLASSIFICATION_DATA_CLEAN_JOB_DISABLE("invalid.classification.data.clean.job.enabled", "unidata.properties.group.invalid.classification.data.clean.job", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    INVALID_CLASSIFICATION_DATA_CLEAN_EXECUTION_TIME("invalid.classification.data.clean.job.execution.time", "unidata.properties.group.invalid.classification.data.clean.job", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Clean unused binary data background job.
    CLEAN_OLD_AUDIT_DATA_JOB_DISABLE("clean.old.audit.data.job.disable", "unidata.properties.group.clean.old.audit.data.job", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    CLEAN_OLD_AUDIT_DATA_JOB_LIFETIME_MINUTES("clean.old.audit.data.job.lifetime.minutes", "unidata.properties.group.clean.old.audit.data.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    CLEAN_OLD_AUDIT_DATA_JOB_EXECUTION_TIME("clean.old.audit.data.job.execution.time", "unidata.properties.group.clean.old.audit.data.job", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Clean raw unused binary data background job.
    CLEAN_OLD_AUDIT_RAW_DATA_JOB_DISABLE("clean.old.audit.raw.data.job.disable", "unidata.properties.group.clean.old.audit.raw.data.job", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    CLEAN_OLD_AUDIT_RAW_DATA_JOB_LIFETIME_MINUTES("clean.old.audit.raw.data.job.lifetime.minutes", "unidata.properties.group.clean.old.audit.raw.data.job", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    CLEAN_OLD_AUDIT_RAW_DATA_JOB_EXECUTION_TIME("clean.old.audit.raw.data.job.execution.time", "unidata.properties.group.clean.old.audit.raw.data.job", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Activiti
    UNIDATA_ACTIVITI_TASK_MAIL_NOTIFICATION_ENABLED("unidata.activiti.task.mail.notification.enabled", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_HOST("unidata.activiti.task.mailServerHost", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_PORT("unidata.activiti.task.mailServerPort", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.INTEGER, Integer::valueOf),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_USE_SSL("unidata.activiti.task.mailServerUseSSL", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_USE_TLS("unidata.activiti.task.mailServerUseTLS", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_DEFAULT_FROM("unidata.activiti.task.mailServerDefaultFrom", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_USERNAME("unidata.activiti.task.mailServerUsername", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_PASSWORD("unidata.activiti.task.mailServerPassword", "unidata.properties.group.activiti", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Unidata audit configuration
    UNIDATA_AUDIT_TTL_ENABLED("unidata.audit.ttl.enabled", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.BOOLEAN, Boolean::valueOf),
    UNIDATA_AUDIT_TTL_VALUE("unidata.audit.ttl.value", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_AUDIT_EMBEDDED_URL("unidata.audit.embedded.url", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_AUDIT_EMBEDDED_USERNAME("unidata.audit.embedded.username", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_AUDIT_EMBEDDED_PASSWORD("unidata.audit.embedded.password", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
    UNIDATA_AUDIT_EMBEDDED_MOD("unidata.audit.embedded.mode", "unidata.properties.group.audit", ConfigurationPropertyTypeDTO.STRING, (v) -> v),

    // Default node id
    UNIDATA_NODE_ID("unidata.node.id", "unidata.properties.group.node.id", ConfigurationPropertyTypeDTO.STRING, (v) -> v),
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
            final ConfigurationPropertyTypeDTO propertyType,
            final Function<String, ? extends Serializable> deserializer
    ) {
        this(key, groupKey, propertyType, null, deserializer, null, Collections.emptyList(), false, true);
    }


    UnidataConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyTypeDTO propertyType,
            final Predicate<Optional<String>> validator,
            final Function<String, ? extends Serializable> deserializer
    ) {
        this(key, groupKey, propertyType, validator, deserializer, null, Collections.emptyList(), false, false);
    }

    UnidataConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyTypeDTO propertyType,
            final Predicate<Optional<String>> validator,
            final Function<String, ? extends Serializable> deserializer,
            final boolean required
    ) {
        this(key, groupKey, propertyType, validator, deserializer, null, Collections.emptyList(), required, false);
    }

    UnidataConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyTypeDTO propertyType,
            final Predicate<Optional<String>> validator,
            final Function<String, ? extends Serializable> deserializer,
            final Serializable defaultValue,
            final boolean required
    ) {
        this(key, groupKey, propertyType, validator, deserializer, defaultValue, Collections.emptyList(), required, false);
    }

    UnidataConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyTypeDTO propertyType,
            final Predicate<Optional<String>> validator,
            final Function<String, ? extends Serializable> deserializer,
            final Serializable defaultValue,
            final boolean required,
            final boolean readonly
    ) {
        this(key, groupKey, propertyType, validator, deserializer, defaultValue, Collections.emptyList(), required, readonly);
    }

    UnidataConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyTypeDTO propertyType,
            final Predicate<Optional<String>> validator,
            final Function<String, ? extends Serializable> deserializer,
            final List<Pair<? extends Serializable, String>> availableValues
    ) {
        this(key, groupKey, propertyType, validator, deserializer, null, availableValues, false, false);
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
        return Optional.fromNullable(defaultValue);
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
