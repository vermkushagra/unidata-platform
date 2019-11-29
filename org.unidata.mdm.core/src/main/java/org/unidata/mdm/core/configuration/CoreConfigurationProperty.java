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
