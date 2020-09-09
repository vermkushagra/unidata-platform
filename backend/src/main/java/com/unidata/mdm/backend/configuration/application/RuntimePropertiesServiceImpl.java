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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.unidata.mdm.backend.common.configuration.application.PropertiesMetaInformationProvider;
import com.unidata.mdm.backend.common.configuration.application.RuntimePropertiesService;
import com.unidata.mdm.backend.common.dto.configuration.ConfigurationPropertyMetaDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unidata.mdm.backend.common.dto.configuration.ConfigurationPropertyDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.ValidationResult;

import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class RuntimePropertiesServiceImpl implements RuntimePropertiesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimePropertiesServiceImpl.class);

    private final List<PropertiesMetaInformationProvider> metaInformationProviders;

    private final ConfigurationCacheService configurationCacheService;

    private final DirectProcessor<Map<String, Optional<? extends Serializable>>> configurationUpdatesFromUser =
            DirectProcessor.create();

    @Autowired
    public RuntimePropertiesServiceImpl(
            final List<PropertiesMetaInformationProvider> propertiesMetaInformationProviders,
            final ConfigurationCacheService configurationCacheService,
            final List<ConfigurationUpdatesProducer> configurationUpdatesProducers,
            final List<ConfigurationUpdatesByUserConsumer> configurationUpdatesByUserConsumers,
            final List<ConfigurationUpdatesConsumer> configurationUpdatesConsumers
    ) {
        this.metaInformationProviders = propertiesMetaInformationProviders;
        this.configurationCacheService = configurationCacheService;

        configurationCacheService.fillCache(defaultValues());

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
                .<Map<String, Optional<? extends Serializable>>>merge(configurationUpdatesStreams.toArray(new Publisher[0]))
                .publishOn(Schedulers.single())
                .doOnNext(o -> {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Configuration update: {}", o);
                    }
                });
        configurationCacheService.subscribe(configurationUpdateStream);

        configurationUpdatesConsumers.forEach(consumer -> consumer.subscribe(configurationCacheService.updates()));
    }

    private Map<String, Optional<? extends Serializable>> defaultValues() {
        return metaInformationProviders.stream()
                .flatMap(p -> p.properties().stream())
                .collect(Collectors.toMap(
                        ConfigurationPropertyMetaDTO::getName,
                        p -> Optional.ofNullable(p.getDefaultValue())
                ));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ConfigurationPropertyDTO> availableProperties() {
        final Stream<ConfigurationPropertyDTO> stream = metaInformationProviders.stream()
                .flatMap(p -> p.properties().stream())
                .map(propertyInfo -> new ConfigurationPropertyDTO<>(
                        propertyInfo,
                        configurationCacheService.propertyValue(propertyInfo.getName()).orElse(null)
                ));
        return stream.collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ConfigurationPropertyDTO> getPropertiesByGroup(String groupCode) {
        final Stream<ConfigurationPropertyDTO> stream = metaInformationProviders.stream()
                .flatMap(p -> p.properties().stream())
                .filter(propertyInfo -> propertyInfo.getGroupCode().equals(groupCode))
                .map(propertyInfo -> new ConfigurationPropertyDTO<>(
                        propertyInfo,
                        configurationCacheService.propertyValue(propertyInfo.getName()).orElse(null)
                ));
        return stream.collect(Collectors.toList());
    }

    @Override
    public Optional<ConfigurationPropertyDTO> property(String name) {
        return availableProperties().stream()
                .filter(p -> p.getMeta().getName().equals(name))
                .findFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> updatePropertiesValuesFromExternalPlace(final Map<String, String> properties) {
        final Map<String, ConfigurationPropertyDTO> availableProperties = availableProperties().stream()
                .collect(Collectors.toMap(p -> p.getMeta().getName(), Function.identity()));
        final Map<String, Optional<String>> knowProperties = properties.entrySet().stream()
                .filter(entry -> availableProperties.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Optional.ofNullable(e.getValue())));
        final List<String> invalidProperties = knowProperties.entrySet().stream()
                .map(property -> {
                    if (availableProperties.get(property.getKey()).getMeta()
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
                                property.getValue().map((value) ->
                                        (Serializable) availableProperties.get(property.getKey())
                                                .getMeta().getDeserializer().apply(value)
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
