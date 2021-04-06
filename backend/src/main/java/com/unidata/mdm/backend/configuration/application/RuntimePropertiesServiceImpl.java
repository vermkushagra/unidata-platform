package com.unidata.mdm.backend.configuration.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.unidata.mdm.backend.api.rest.dto.configuration.ConfigurationPropertyAvailableValueDTO;
import com.unidata.mdm.backend.api.rest.dto.configuration.ConfigurationPropertyDTO;
import com.unidata.mdm.backend.api.rest.dto.configuration.ConfigurationPropertyMetaDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.util.MessageUtils;

import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class RuntimePropertiesServiceImpl implements RuntimePropertiesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimePropertiesServiceImpl.class);

    private final ConfigurationCacheService configurationCacheService;

    private final DirectProcessor<Map<String, Optional<? extends Serializable>>> configurationUpdatesFromUser =
            DirectProcessor.create();

    @Autowired
    public RuntimePropertiesServiceImpl(
            final ConfigurationCacheService configurationCacheService,
            final List<ConfigurationUpdatesProducer> configurationUpdatesProducers,
            final List<ConfigurationUpdatesByUserConsumer> configurationUpdatesByUserConsumers,
            final List<ConfigurationUpdatesConsumer> configurationUpdatesConsumers
    ) {
        this.configurationCacheService = configurationCacheService;
        final List<Publisher<Map<String, Optional<? extends Serializable>>>> configurationUpdatesStreams = new ArrayList<>();
        configurationUpdatesStreams.add(configurationUpdatesFromUser);

        if (!CollectionUtils.isEmpty(configurationUpdatesProducers)) {
            configurationUpdatesStreams.addAll(
                    configurationUpdatesProducers.stream()
                            .map(ConfigurationUpdatesProducer::updates)
                            .collect(Collectors.toList())
            );
        }

        if (!CollectionUtils.isEmpty(configurationUpdatesByUserConsumers)) {
            configurationUpdatesByUserConsumers.forEach(
                    consumer -> consumer.subscribe(configurationUpdatesFromUser)
            );
        }

        @SuppressWarnings("unchecked")
        final Flux<Map<String, Optional<? extends Serializable>>> configurationUpdateStream = Flux
                .<Map<String, Optional<? extends Serializable>>>merge(
                        configurationUpdatesStreams.toArray(new Publisher[configurationUpdatesStreams.size()])
                )
                .publishOn(Schedulers.single())
                .doOnNext(o -> {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Configuration update: {}", o);
                    }
                });
        configurationCacheService.subscribe(configurationUpdateStream);

        configurationUpdatesConsumers.forEach(consumer -> consumer.subscribe(configurationCacheService.updates()));
    }

    @Override
    public Collection<ConfigurationPropertyDTO> availableProperties() {
        return Arrays.stream(UnidataConfigurationProperty.values())
                .map(property ->
                        new ConfigurationPropertyDTO<>(
                                property.getKey(),
                                MessageUtils.getMessage(property.getKey()),
                                MessageUtils.getMessage(property.getGroupKey()),
                                property.getPropertyType(),
                                configurationCacheService.propertyValue(property.getKey()).orNull(),
                                new ConfigurationPropertyMetaDTO<>(
                                        property.getDefaultValue().orNull(),
                                        property.getAvailableValues().stream()
                                                .map(value ->
                                                        new ConfigurationPropertyAvailableValueDTO<>(
                                                                value.getLeft(),
                                                                MessageUtils.getMessage(value.getRight())
                                                        )
                                                )
                                                .collect(Collectors.toList()),
                                        property.isRequired(),
                                        property.isReadonly()
                                )
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    public Collection<String> updatePropertiesValuesFromExternalPlace(final Map<String, String> properties) {
        final Map<String, Optional<String>> knowProperties = properties.entrySet().stream()
                .filter(entry -> {
                    final UnidataConfigurationProperty property =
                            UnidataConfigurationProperty.findByKey(entry.getKey());
                    return property != null && !property.isReadonly();
                })
                .map(entry -> Pair.of(entry.getKey(), Optional.fromNullable(entry.getValue())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        final List<String> invalidProperties = knowProperties.entrySet().stream()
                .map(property -> {
                    if (UnidataConfigurationProperty.findByKey(property.getKey())
                            .getValidator()
                            .test(property.getValue())) {
                        return null;
                    }
                    return property.getKey();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!invalidProperties.isEmpty()) {
            throw new ConfigurationValidationException(
                    "Invalid properties values: " + invalidProperties,
                    ExceptionId.EX_CONFIGURATION_PROPERTIES_INVALID,
                    generateErrorStringForInvalidProperties(invalidProperties)
            );
        }

        final Map<String, Optional<? extends Serializable>> toUpdate = knowProperties.entrySet().stream()
                .map(property ->
                        Pair.of(
                                property.getKey(),
                                property.getValue().transform((value) ->
                                        UnidataConfigurationProperty.findByKey(property.getKey()).getDeserializer()
                                                .apply(value)
                                )
                        )
                )
                .filter(property ->
                        !Objects.equals(property.getValue(), configurationCacheService.propertyValue(property.getKey()))
                )
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        configurationUpdatesFromUser.onNext(toUpdate);

        return Collections.emptyList();
    }

    private List<ValidationResult> generateErrorStringForInvalidProperties(List<String> invalidProperties) {
        return invalidProperties.stream()
                .map(propertyKey ->
                        new ValidationResult(
                                "Invalid property value - " + propertyKey,
                                propertyKey
                        )
                )
                .collect(Collectors.toList());
    }
}
