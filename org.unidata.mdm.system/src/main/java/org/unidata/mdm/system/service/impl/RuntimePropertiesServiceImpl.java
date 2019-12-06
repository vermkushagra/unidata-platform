package org.unidata.mdm.system.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.exception.PlatformValidationException;
import org.unidata.mdm.system.exception.SystemExceptionIds;
import org.unidata.mdm.system.exception.ValidationResult;
import org.unidata.mdm.system.service.ConfigurationCacheService;
import org.unidata.mdm.system.service.RuntimePropertiesService;
import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.configuration.ConfigurationProperty;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesByUserConsumer;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesConsumer;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesProducer;
import reactor.core.Disposable;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RuntimePropertiesServiceImpl implements RuntimePropertiesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimePropertiesServiceImpl.class);

    public static final String NULL_VALUE_PLACE_HOLDER = "@_null_";

    private final List<ApplicationConfigurationProperty> configurationProperties = new ArrayList<>();

    private final ConfigurationCacheService configurationCacheService;

    private final DirectProcessor<Map<String, Optional<? extends Serializable>>> configurationUpdatesFromUser =
            DirectProcessor.create();

    private List<ConfigurationUpdatesProducer> configurationUpdatesProducers;

    private List<ConfigurationUpdatesByUserConsumer> configurationUpdatesByUserConsumers;

    private final AtomicBoolean init = new AtomicBoolean(false);

    public RuntimePropertiesServiceImpl(
            final ConfigurationCacheService configurationCacheService,
            final List<ConfigurationUpdatesProducer> configurationUpdatesProducers,
            final List<ConfigurationUpdatesByUserConsumer> configurationUpdatesByUserConsumers
    ) {
        this.configurationCacheService = configurationCacheService;
        this.configurationUpdatesProducers =  configurationUpdatesProducers;
        this.configurationUpdatesByUserConsumers =  configurationUpdatesByUserConsumers;
    }

    private void init() {
        if (!init.compareAndSet(false, true)) {
            return;
        }
        final List<Publisher<Map<String, Optional<? extends Serializable>>>> configurationUpdatesStreams = new ArrayList<>();
        configurationUpdatesStreams.add(configurationUpdatesFromUser);

        if (!CollectionUtils.isEmpty(configurationUpdatesProducers)) {
            configurationUpdatesStreams.addAll(
                    configurationUpdatesProducers.stream()
                            .map(p -> p.updates(configurationProperties))
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
    }

    @Override
    public void addConfigurationProperties(final Collection<ApplicationConfigurationProperty> configurationProperties) {
        if (CollectionUtils.isNotEmpty(configurationProperties)) {
            this.configurationProperties.addAll(
                    configurationProperties
            );
            configurationCacheService.fillCache(
                    configurationProperties.stream()
                            .collect(
                                    Collectors.toMap(
                                            ApplicationConfigurationProperty::getKey,
                                            ApplicationConfigurationProperty::getDefaultValue
                                    )
                            )
            );
            init();
        }
    }

    @Override
    public void subscribeToConfigurationUpdates(ConfigurationUpdatesConsumer configurationUpdatesConsumer) {
        configurationUpdatesConsumer.subscribe(configurationCacheService.updates());
    }

    @Override
    public Collection<ConfigurationProperty> availableProperties() {
        return configurationProperties.stream()
                .map(p -> new ConfigurationProperty<>(
                        p,
                        configurationCacheService.propertyValue(p.getKey()).orElse(null)
                )).collect(Collectors.toList());
    }

    @Override
    public Collection<ConfigurationProperty> getPropertiesByGroup(final String groupCode) {
        return availableProperties().stream()
                .filter(p -> groupCode.equals(p.getProperty().getGroupKey()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ConfigurationProperty> property(final String name) {
        return availableProperties().stream()
                .filter(p -> name.equals(p.getProperty().getKey()))
                .findFirst();
    }

    @Override
    public Collection<String> updatePropertiesValues(final Map<String, String> properties) {
        final Map<String, ApplicationConfigurationProperty> updatableProperties = configurationProperties.stream()
                .filter(p -> !p.isReadonly())
                .collect(Collectors.toMap(ApplicationConfigurationProperty::getKey, Function.identity()));
        final Map<String, Optional<String>> knowPropertiesValues = properties.entrySet().stream()
                .filter(e -> updatableProperties.containsKey(e.getKey()))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> Optional.ofNullable(
                                        NULL_VALUE_PLACE_HOLDER.equals(e.getValue()) ? null : e.getValue()
                                )
                        )
                );
        validateProperties(updatableProperties, knowPropertiesValues);

        final Map<String, Optional<? extends Serializable>> toUpdate = knowPropertiesValues.entrySet().stream()
                .map(p ->
                        Pair.of(
                                p.getKey(),
                                p.getValue().map(value ->
                                        (Serializable) updatableProperties.get(p.getKey()).getDeserializer()
                                                .apply(value)
                                )
                        )
                )
                .filter(p -> !Objects.equals(p.getValue(), configurationCacheService.propertyValue(p.getKey())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        configurationUpdatesFromUser.onNext(toUpdate);

        return Collections.emptyList();
    }

    private void validateProperties(Map<String, ApplicationConfigurationProperty> updatableProperties, Map<String, Optional<String>> knowProperties) {
        final List<String> invalidProperties = knowProperties.entrySet().stream()
                .map(e -> updatableProperties.get(e.getKey()).getValidator().test(e.getValue()) ? null : e.getKey())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!invalidProperties.isEmpty()) {
            throw new PlatformValidationException(
                    "Invalid properties values: " + invalidProperties,
                    SystemExceptionIds.EX_CONFIGURATION_PROPERTIES_INVALID,
                    generateErrorStringForInvalidProperties(invalidProperties)
            );
        }
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
