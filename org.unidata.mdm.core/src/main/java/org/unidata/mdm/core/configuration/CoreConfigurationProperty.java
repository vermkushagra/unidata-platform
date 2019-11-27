package org.unidata.mdm.core.configuration;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.configuration.ConfigurationPropertyType;
import org.unidata.mdm.system.type.configuration.ValueValidators;

public enum CoreConfigurationProperty implements ApplicationConfigurationProperty {

    // Unidata matching configuration
    UNIDATA_MATCHING_SEARCH_PAGE_SIZE(
            "unidata.matching.search.page.size",
            "unidata.properties.group.matching",
            ConfigurationPropertyType.INTEGER,
            20,
            true,
            false
    ),

    UNIDATA_MATCHING_MAX_CLUSTER_SIZE(
            "unidata.matching.max.cluster.size",
            "unidata.properties.group.matching",
            ConfigurationPropertyType.INTEGER,
            50,
            true,
            false
    ),

    // ---------- JMS ----------
    UNIDATA_NOTIFICATION_ENABLED(
            "unidata.notification.enabled",
            "unidata.notification.group",
            ConfigurationPropertyType.BOOLEAN,
            false,
            true,
            false
    ),
    UNIDATA_NOTIFICATION_DB_BATCH_SIZE(
            "unidata.notification.db.batch.size",
            "unidata.notification.group",
            ConfigurationPropertyType.INTEGER,
            10000,
            true,
            false
    ),
    UNIDATA_NOTIFICATION_ADD_EXTERNAL_ID_TO_ATTRIBUTES(
            "unidata.notification.add.external.id.to.attributes",
            "unidata.notification.group",
            ConfigurationPropertyType.BOOLEAN,
            false,
            true,
            false
    ),

    //
    UNIDATA_DATA_AUTOMERGE_ENABLED(
            "unidata.data.automerge.enable",
            "unidata.properties.group.data",
            ConfigurationPropertyType.BOOLEAN,
            false,
            true,
            false
    ),
    UNIDATA_DATA_USE_DEPRECATED_USEREXITS(
            "unidata.data.use.deprecated.userexits",
            "unidata.properties.group.data",
            ConfigurationPropertyType.BOOLEAN,
            false,
            true,
            true
    ),
    UNIDATA_DATA_REFRESH_IMMEDIATE(
            "unidata.data.refresh.immediate",
            "unidata.properties.group.data",
            ConfigurationPropertyType.BOOLEAN,
            true,
            false,
            true
    ),
    UNIDATA_DATA_ROLLBACK_THREAD_COUNT(
            "unidata.data.rollback.thread.count",
            "unidata.properties.group.data",
            ConfigurationPropertyType.INTEGER,
            1,
            false,
            true
    ),

    // Version
    UNIDATA_API_VERSION("unidata.api.version", "unidata.properties.group.version", ConfigurationPropertyType.STRING),
    UNIDATA_PLATFORM_VERSION("unidata.platform.version", "unidata.properties.group.version", ConfigurationPropertyType.STRING),

    // Swagger
    UNIDATA_SWAGGER_BASEPATH("unidata.swagger.basepath", "unidata.properties.group.swagger", ConfigurationPropertyType.STRING),

    // Security
    UNIDATA_SECURITY_TOKEN_TTL("unidata.security.token.ttl", "unidata.properties.group.security", ConfigurationPropertyType.STRING),
    UNIDATA_SECURITY_TOKEN_CLEANUP("unidata.security.token.cleanup", "unidata.properties.group.security", ConfigurationPropertyType.STRING),

    // Simon perf measurement
    UNIDATA_SIMON_ENABLED(
            "unidata.simon.enabled",
            "unidata.properties.group.simon",
            ConfigurationPropertyType.BOOLEAN,
            false,
            true,
            false
    ),

    // Validity
    UNIDATA_VALIDITY_PERIOD_START("unidata.validity.period.start", "unidata.properties.group.validity", ConfigurationPropertyType.STRING),
    UNIDATA_VALIDITY_PERIOD_END("unidata.validity.period.end", "unidata.properties.group.validity", ConfigurationPropertyType.STRING),

    // License
    //UN-10099 UNIDATA_LICENSING_GPG_LICENSE_FILE("unidata.licensing.gpg.license.file", "unidata.properties.group.license", ConfigurationPropertyTypeDTO.STRING),

    // Statistic cache
    UNIDATA_STAT_CACHE_TTL("unidata.stat.cache.ttl", "unidata.properties.group.statistic.cache", ConfigurationPropertyType.INTEGER),

    // Distributed cache
    UNIDATA_CACHE_GROUP("unidata.cache.group", "unidata.properties.group.distributed.cache", ConfigurationPropertyType.STRING),
    UNIDATA_CACHE_PASSWORD("unidata.cache.password", "unidata.properties.group.distributed.cache", ConfigurationPropertyType.STRING),
    UNIDATA_CACHE_PORT("unidata.cache.port", "unidata.properties.group.distributed.cache", ConfigurationPropertyType.INTEGER),
    UNIDATA_CACHE_PORT_AUTOINCREAMENT("unidata.cache.port.autoincrement", "unidata.properties.group.distributed.cache", ConfigurationPropertyType.BOOLEAN),
    UNIDATA_CACHE_MULTICAST_ENABLED("unidata.cache.multicast.enabled", "unidata.properties.group.distributed.cache", ConfigurationPropertyType.BOOLEAN),
    UNIDATA_CACHE_MULTICAST_GROUP("unidata.cache.multicast.group", "unidata.properties.group.distributed.cache", ConfigurationPropertyType.STRING),
    UNIDATA_CACHE_MULTICAST_PORT("unidata.cache.multicast.port", "unidata.properties.group.distributed.cache", ConfigurationPropertyType.INTEGER),
    UNIDATA_CACHE_MULTICAST_TTL("unidata.cache.multicast.ttl", "unidata.properties.group.distributed.cache", ConfigurationPropertyType.INTEGER),
    UNIDATA_CACHE_MULTICAST_TIMEOUT("unidata.cache.multicast.timeout", "unidata.properties.group.distributed.cache", ConfigurationPropertyType.INTEGER),
    UNIDATA_CACHE_TCP_IP_ENABLED("unidata.cache.tcp-ip.enabled", "unidata.properties.group.distributed.cache", ConfigurationPropertyType.BOOLEAN),
    UNIDATA_CACHE_TCP_IP_MEMBERS("unidata.cache.tcp-ip.members", "unidata.properties.group.distributed.cache", ConfigurationPropertyType.STRING),

    // Job parameters
    UNIDATA_JOB_TRIGGER_START_TIMEOUT("unidata.job.trigger.start.timeout", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_THREAD_POOL_SIZE("unidata.job.thread.pool.size", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_QUEUE_SIZE("unidata.job.queue.size", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),

    UNIDATA_JOB_REINDEX_DATA_THREAD_COUNT("unidata.job.reindex_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_REINDEX_DATA_PARTITION_THREAD_COUNT("unidata.job.reindex_data.partition.thread_count", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_REINDEX_DATA_COMMIT_INTERVAL("unidata.job.reindex_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_IMPORT_DATA_THREAD_COUNT("unidata.job.import_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_IMPORT_DATA_COMMIT_INTERVAL("unidata.job.import_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_EXPORT_DATA_THREAD_COUNT("unidata.job.export_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_EXPORT_DATA_COMMIT_INTERVAL("unidata.job.export_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),

    UNIDATA_JOB_MODIFY_DATA_THREAD_COUNT("unidata.job.batch_modify_data.thread_count", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_MODIFY_DATA_COMMIT_INTERVAL("unidata.job.batch_modify_data.commit_interval", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_MODIFY_DATA_AUDIT_LEVEL("unidata.job.batch_modify_data.audit_level", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),

    UNIDATA_JOB_DELETE_DATA_THREAD_COUNT("unidata.job.delete_records_job.thread_count", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_DELETE_DATA_COMMIT_INTERVAL("unidata.job.delete_records_job.commit_interval", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_DELETE_DATA_AUDIT_LEVEL("unidata.job.delete_records_job.audit_level", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),

    UNIDATA_JOB_DELETE_RELATIONS_THREAD_COUNT("unidata.job.delete_relations_job.thread_count", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_DELETE_RELATIONS_COMMIT_INTERVAL("unidata.job.delete_relations_job.commit_interval", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_DELETE_RELATIONS_AUDIT_LEVEL("unidata.job.delete_relations_job.audit_level", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),

    UNIDATA_JOB_RESTORE_RECORDS_THREAD_COUNT("unidata.job.restore_records_job.thread_count", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_RESTORE_RECORDS_COMMIT_INTERVAL("unidata.job.restore_records_job.commit_interval", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_RESTORE_RECORDS_AUDIT_LEVEL("unidata.job.restore_records_job.audit_level", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),

    UNIDATA_JOB_RESTORE_PERIODS_THREAD_COUNT("unidata.job.restore_record_periods_job.thread_count", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_RESTORE_PERIODS_COMMIT_INTERVAL("unidata.job.restore_record_periods_job.commit_interval", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_RESTORE_PERIODS_AUDIT_LEVEL("unidata.job.restore_record_periods_job.audit_level", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),

    UNIDATA_JOB_MATCHING_THREAD_COUNT("unidata.job.matching.thread_count", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    UNIDATA_JOB_DUPLICATE_THREAD_COUNT("unidata.job.duplicate_job.thread_count", "unidata.properties.group.job", ConfigurationPropertyType.INTEGER),
    // Notification background job.
    NOTIFICATION_JOB_DISABLE("notification.job.disable", "unidata.properties.group.notification.job", ConfigurationPropertyType.BOOLEAN),
    NOTIFICATION_JOB_EXECUTION_TIME("notification.job.execution.time", "unidata.properties.group.notification.job", ConfigurationPropertyType.STRING),
    NOTIFICATION_JOB_ATTEMPT_COUNT("notification.job.attempt.count", "unidata.properties.group.notification.job", ConfigurationPropertyType.INTEGER),

    // Clean old notification background job.
    CLEAN_NOTIFICATION_JOB_DISABLE("clean.notification.job.disable", "unidata.properties.group.clean.notification.job", ConfigurationPropertyType.BOOLEAN),
    CLEAN_NOTIFICATION_JOB_LIFETIME_MINUTES("clean.notification.job.lifetime.minutes", "unidata.properties.group.clean.notification.job", ConfigurationPropertyType.INTEGER),
    CLEAN_NOTIFICATION_JOB_EXECUTION_TIME("clean.notification.job.execution.time", "unidata.properties.group.clean.notification.job", ConfigurationPropertyType.STRING),

    // Clean unused binary data background job.
    CLEAN_UNUSED_BINARY_JOB_DISABLE("clean.unused.binary.job.disable", "unidata.properties.group.clean.unused.binary.job", ConfigurationPropertyType.BOOLEAN),
    CLEAN_UNUSED_BINARY_JOB_LIFETIME_MINUTES("clean.unused.binary.job.lifetime.minutes", "unidata.properties.group.clean.unused.binary.job", ConfigurationPropertyType.INTEGER),
    CLEAN_UNUSED_BINARY_JOB_EXECUTION_TIME("clean.unused.binary.job.execution.time", "unidata.properties.group.clean.unused.binary.job", ConfigurationPropertyType.STRING),

    // Calculate statistic background job.
    CALCULATE_STATISTIC_JOB_DISABLE("calculate.statistic.job.disable", "unidata.properties.group.calculate.statistic.job", ConfigurationPropertyType.BOOLEAN),
    CALCULATE_STATISTIC_JOB_EXECUTION_TIME("calculate.statistic.job.execution.time", "unidata.properties.group.calculate.statistic.job", ConfigurationPropertyType.STRING),

    // Invalid classification data clean job
    INVALID_CLASSIFICATION_DATA_CLEAN_JOB_DISABLE("invalid.classification.data.clean.job.enabled", "unidata.properties.group.invalid.classification.data.clean.job", ConfigurationPropertyType.BOOLEAN),
    INVALID_CLASSIFICATION_DATA_CLEAN_EXECUTION_TIME("invalid.classification.data.clean.job.execution.time", "unidata.properties.group.invalid.classification.data.clean.job", ConfigurationPropertyType.STRING),

    // Clean unused binary data background job.
    CLEAN_OLD_AUDIT_DATA_JOB_DISABLE("clean.old.audit.data.job.disable", "unidata.properties.group.clean.old.audit.data.job", ConfigurationPropertyType.BOOLEAN),
    CLEAN_OLD_AUDIT_DATA_JOB_LIFETIME_MINUTES("clean.old.audit.data.job.lifetime.minutes", "unidata.properties.group.clean.old.audit.data.job", ConfigurationPropertyType.INTEGER),
    CLEAN_OLD_AUDIT_DATA_JOB_EXECUTION_TIME("clean.old.audit.data.job.execution.time", "unidata.properties.group.clean.old.audit.data.job", ConfigurationPropertyType.STRING),

    // Clean raw unused binary data background job.
    CLEAN_OLD_AUDIT_RAW_DATA_JOB_DISABLE("clean.old.audit.raw.data.job.disable", "unidata.properties.group.clean.old.audit.raw.data.job", ConfigurationPropertyType.BOOLEAN),
    CLEAN_OLD_AUDIT_RAW_DATA_JOB_LIFETIME_MINUTES("clean.old.audit.raw.data.job.lifetime.minutes", "unidata.properties.group.clean.old.audit.raw.data.job", ConfigurationPropertyType.INTEGER),
    CLEAN_OLD_AUDIT_RAW_DATA_JOB_EXECUTION_TIME("clean.old.audit.raw.data.job.execution.time", "unidata.properties.group.clean.old.audit.raw.data.job", ConfigurationPropertyType.STRING),

    // Activiti
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_HOST("unidata.activiti.task.mailServerHost", "unidata.properties.group.activiti", ConfigurationPropertyType.STRING),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_PORT("unidata.activiti.task.mailServerPort", "unidata.properties.group.activiti", ConfigurationPropertyType.INTEGER),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_USE_SSL("unidata.activiti.task.mailServerUseSSL", "unidata.properties.group.activiti", ConfigurationPropertyType.BOOLEAN),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_USE_TLS("unidata.activiti.task.mailServerUseTLS", "unidata.properties.group.activiti", ConfigurationPropertyType.BOOLEAN),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_DEFAULT_FROM("unidata.activiti.task.mailServerDefaultFrom", "unidata.properties.group.activiti", ConfigurationPropertyType.STRING),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_USERNAME("unidata.activiti.task.mailServerUsername", "unidata.properties.group.activiti", ConfigurationPropertyType.STRING),
    UNIDATA_ACTIVITI_TASK_MAIL_SERVER_PASSWORD("unidata.activiti.task.mailServerPassword", "unidata.properties.group.activiti", ConfigurationPropertyType.STRING),
    UNIDATA_ACTIVITI_TASK_JOB_EXECUTOR_ACTIVATE("unidata.activiti.task.jobExecutorActivate", "unidata.properties.group.activiti", ConfigurationPropertyType.BOOLEAN),
    UNIDATA_ACTIVITI_TASK_ASYNC_EXECUTOR_ACTIVATE("unidata.activiti.task.asyncExecutorActivate", "unidata.properties.group.activiti", ConfigurationPropertyType.BOOLEAN),

    // Unidata audit configuration
    UNIDATA_AUDIT_EMBEDDED_URL("unidata.audit.embedded.url", "unidata.properties.group.audit", ConfigurationPropertyType.STRING),
    UNIDATA_AUDIT_EMBEDDED_USERNAME("unidata.audit.embedded.username", "unidata.properties.group.audit", ConfigurationPropertyType.STRING),
    UNIDATA_AUDIT_EMBEDDED_PASSWORD("unidata.audit.embedded.password", "unidata.properties.group.audit", ConfigurationPropertyType.STRING),
    UNIDATA_AUDIT_EMBEDDED_MOD("unidata.audit.embedded.mode", "unidata.properties.group.audit", ConfigurationPropertyType.STRING),

    // Default node id
    UNIDATA_NODE_ID("unidata.node.id", "unidata.properties.group.node.id", ConfigurationPropertyType.STRING),

    IMPORT_XLSX_BATCH_SIZE(
            "unidata.import.xlsx.batch.size",
            "unidata.properties.group.import.xlsx",
            ConfigurationPropertyType.INTEGER,
            500,
            false,
            true
    ),

    // Unidata rare task executor
    ASYNC_RARE_TASKS_EXECUTOR_THREADS_POOL_SIZE(
            "unidata.async.rare.tasks.executor.threads.pool.size",
            "unidata.async.rare.tasks.executor",
            ConfigurationPropertyType.INTEGER,
            5,
            true,
            false
    ),

    UNIDATA_BALANCER_PROTOCOL_HEADER("unidata.balancer.protocol.header", "unidata.properties.group.balancer", ConfigurationPropertyType.STRING),

    // AUDIT
    UNIDATA_AUDIT_ENABLED(
            Constants.UNIDATA_AUDIT_ENABLED_KEY,
            "unidata.properties.group.audit",
            ConfigurationPropertyType.BOOLEAN,
            false,
            true,
            false
    ),
    UNIDATA_AUDIT_READ_EVENTS("unidata.audit.read.events", "unidata.properties.group.audit", ConfigurationPropertyType.BOOLEAN),
    UNIDATA_AUDIT_STACKTRACE_DEPTH("unidata.audit.stacktrace.depth", "unidata.properties.group.audit", ConfigurationPropertyType.INTEGER),
    UNIDATA_AUDIT_WRITER_POOL_SIZE(
            Constants.UNIDATA_AUDIT_WRITER_POOL_SIZE_KEY,
            "unidata.properties.group.audit",
            ConfigurationPropertyType.INTEGER,
            5,
            true,
            false
    ),
    UNIDATA_AUDIT_ENABLED_STORAGES(
            Constants.UNIDATA_AUDIT_ENABLED_STORAGES_KEY,
            "unidata.properties.group.audit",
            ConfigurationPropertyType.STRING,
            "es,db",
            true,
            false
    ),
    ;
    private final String key;
    private final String groupKey;
    private final ConfigurationPropertyType propertyType;
    private final Predicate<Optional<String>> validator;
    private final Function<String, ? extends Serializable> deserializer;
    private final Serializable defaultValue;
    private final List<Pair<? extends Serializable, String>> availableValues = new ArrayList<>();
    private final boolean required;
    private final boolean readonly;

    CoreConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyType propertyType
    ) {
        this(key, groupKey, propertyType, ValueValidators.ANY_VALID, propertyType.getDeserializer(), null, Collections.emptyList(), false, true);
    }

    CoreConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyType propertyType,
            final Serializable defaultValue,
            final boolean required,
            final boolean readonly
    ) {
        this(key, groupKey, propertyType, propertyType.getValidator(), propertyType.getDeserializer(), defaultValue, Collections.emptyList(), required, readonly);
    }

    CoreConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyType propertyType,
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

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getGroupKey() {
        return groupKey;
    }

    @Override
    public ConfigurationPropertyType getPropertyType() {
        return propertyType;
    }

    @Override
    public Predicate<Optional<String>> getValidator() {
        return validator;
    }

    @Override
    public Function<String, ? extends Serializable> getDeserializer() {
        return deserializer;
    }

    @Override
    public Optional<? extends Serializable> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    @Override
    public List<Pair<? extends Serializable, String>> getAvailableValues() {
        return Collections.unmodifiableList(availableValues);
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    public static CoreConfigurationProperty findByKey(final String key) {
        return Arrays.stream(CoreConfigurationProperty.values())
                .filter(unidataConfigurationProperty -> unidataConfigurationProperty.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public static boolean exists(final String key) {
        return Arrays.stream(CoreConfigurationProperty.values()).anyMatch(property -> property.getKey().equals(key));
    }

    public static final class Constants {
        private Constants() { }
        public static final String UNIDATA_AUDIT_ENABLED_KEY = "unidata.audit.enabled";
        public static final String UNIDATA_AUDIT_WRITER_POOL_SIZE_KEY = "unidata.audit.writer.pool.size";
        public static final String UNIDATA_AUDIT_ENABLED_STORAGES_KEY = "unidata.audit.enabled.storages";
    }
}
