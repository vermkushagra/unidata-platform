package org.unidata.mdm.search.configuration;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.configuration.ConfigurationPropertyType;
import org.unidata.mdm.system.type.configuration.ValueValidators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Alexander Malyshev
 */
public enum SearchConfigurationProperty implements ApplicationConfigurationProperty {

    // Elasticsearch
    UNIDATA_SEARCH_NODES_ADDRESSES("unidata.search.nodes.addresses", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.STRING),
    UNIDATA_SEARCH_CLUSTER_NAME("unidata.search.cluster.name", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.STRING),
    UNIDATA_SEARCH_INDEX_PREFIX("unidata.search.index.prefix", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.STRING),
    UNIDATA_SEARCH_DEFAULT_SHARDS_NUMBER("unidata.search.shards.number", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),
    UNIDATA_SEARCH_DEFAULT_REPLICAS_NUMBER("unidata.search.replicas.number", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),
    UNIDATA_SEARCH_DEFAULT_ENTITY_SHARDS_NUMBER("unidata.search.entity.shards.number", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),
    UNIDATA_SEARCH_DEFAULT_ENTITY_REPLICAS_NUMBER("unidata.search.entity.replicas.number", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),
    UNIDATA_SEARCH_DEFAULT_LOOKUP_SHARDS_NUMBER("unidata.search.lookup.shards.number", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),
    UNIDATA_SEARCH_DEFAULT_LOOKUP_REPLICAS_NUMBER("unidata.search.lookup.replicas.number", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),
    UNIDATA_SEARCH_DEFAULT_SYSTEM_SHARDS_NUMBER("unidata.search.system.shards.number", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),
    UNIDATA_SEARCH_DEFAULT_SYSTEM_REPLICAS_NUMBER("unidata.search.system.replicas.number", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),
    UNIDATA_SEARCH_DEFAULT_MATCHING_SHARDS_NUMBER("unidata.search.matching.shards.number", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),
    UNIDATA_SEARCH_DEFAULT_MATCHING_REPLICAS_NUMBER("unidata.search.matching.replicas.number", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),


    UNIDATA_SEARCH_INDEX_RELATIONS_STRAIGHT("unidata.search.index.relations.straight", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.BOOLEAN),
    UNIDATA_SEARCH_DEFAULT_MIN_SCORE("unidata.search.default.min.score", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),
    UNIDATA_SEARCH_TOTAL_COUNT_LIMIT(
            "unidata.search.total.count.limit",
            Constants.ELASTICSEARCH_PROPERTIES_GROUP,
            ConfigurationPropertyType.INTEGER,
            200000,
            false,
            true
    ),
    UNIDATA_SEARCH_INDEX_REFRESH_INTERVAL(
            "unidata.search.index.refresh_interval",
            Constants.ELASTICSEARCH_PROPERTIES_GROUP,
            ConfigurationPropertyType.INTEGER,
            1000,
            false,
            true
    ),

    UNIDATA_SEARCH_FUZZINESS("unidata.search.fuzziness", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.STRING),
    UNIDATA_SEARCH_FUZZINESS_PREFIX_LENGTH("unidata.search.fuzziness.prefix.length", Constants.ELASTICSEARCH_PROPERTIES_GROUP, ConfigurationPropertyType.INTEGER),
    UNIDATA_SEARCH_DISPLAY_DATE_FORMAT("unidata.search.display.date.format",
            Constants.ELASTICSEARCH_PROPERTIES_GROUP,
            ConfigurationPropertyType.STRING,
            "dd.MM.yyyy",
            true,
            false
    ),
    UNIDATA_SEARCH_DISPLAY_TIME_FORMAT("unidata.search.display.time.format",
            Constants.ELASTICSEARCH_PROPERTIES_GROUP,
            ConfigurationPropertyType.STRING,
            "HH:mm:ss",
            true,
            false
    ),
    UNIDATA_SEARCH_DISPLAY_TIMESTAMP_FORMAT(
            "unidata.search.display.timestamp.format",
            Constants.ELASTICSEARCH_PROPERTIES_GROUP,
            ConfigurationPropertyType.STRING,
            "dd.MM.yyyy HH:mm:ss",
            true,
            false
    ),
    UNIDATA_ELASTIC_ADMIN_ACTION_TIMEOUT(
            "unidata.elastic.admin.action.timeout",
            Constants.ELASTICSEARCH_PROPERTIES_GROUP,
            ConfigurationPropertyType.INTEGER,
            5000,
            true,
            true
    );

    private final String key;
    private final String groupKey;
    private final ConfigurationPropertyType propertyType;
    private final Predicate<Optional<String>> validator;
    private final Function<String, ? extends Serializable> deserializer;
    private final Serializable defaultValue;
    private final List<Pair<? extends Serializable, String>> availableValues = new ArrayList<>();
    private final boolean required;
    private final boolean readonly;

    SearchConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyType propertyType
    ) {
        this(key, groupKey, propertyType, ValueValidators.ANY_VALID, propertyType.getDeserializer(), null, Collections.emptyList(), false, true);
    }

    SearchConfigurationProperty(
            final String key,
            final String groupKey,
            final ConfigurationPropertyType propertyType,
            final Serializable defaultValue,
            final boolean required,
            final boolean readonly
    ) {
        this(key, groupKey, propertyType, propertyType.getValidator(), propertyType.getDeserializer(), defaultValue, Collections.emptyList(), required, readonly);
    }

    SearchConfigurationProperty(
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

    public static class Constants {
        private Constants() { }

        static final String ELASTICSEARCH_PROPERTIES_GROUP = "unidata.properties.group.elasticsearch";
    }
}
