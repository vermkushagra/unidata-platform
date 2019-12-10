package org.unidata.mdm.system.service.impl.configuration;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesByUserConsumer;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesProducer;
import org.unidata.mdm.system.util.ReactiveUtils;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.impl.MapListenerAdapter;

import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

@Service
@Order(3)
public class HazelcastConfigurationStorage
        implements ConfigurationUpdatesProducer, ConfigurationUpdatesByUserConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastConfigurationStorage.class);

    private static final String CONFIGURATION_MAP_NAME = "configuration";

    private final HazelcastInstance hazelcastInstance;

    private final DirectProcessor<Map<String, Optional<? extends Serializable>>> processor = DirectProcessor.create();

    public HazelcastConfigurationStorage(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        hazelcastInstance.getMap(CONFIGURATION_MAP_NAME).addEntryListener(
                new MapListenerAdapter<String, com.google.common.base.Optional<? extends Serializable>>() {
                    @Override
                    public void onEntryEvent(EntryEvent<String, com.google.common.base.Optional<? extends Serializable>> event) {
                        if (event.getMember().localMember()) {
                            return;
                        }

                        final Map<String, Optional<? extends Serializable>> updatedProperty =
                                Collections.singletonMap(
                                        event.getKey(),
                                        com.google.common.base.Optional.toJavaUtil(event.getValue())
                                );
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Update configuration from Hazelcast. Available properties: {}", updatedProperty);
                        }
                        processor.onNext(updatedProperty);
                    }
                },
                true
        );
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final Consumer<Map<String, Optional<? extends Serializable>>> updatesConsumer = values ->
                hazelcastInstance.getMap(CONFIGURATION_MAP_NAME)
                        .putAll(values.entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        e -> com.google.common.base.Optional.fromJavaUtil(e.getValue()))
                                )
                        );
        updates.subscribe(ReactiveUtils.errorLogAndSkipConsumer(updatesConsumer, LOGGER));
    }

    @Override
    public Publisher<Map<String, Optional<? extends Serializable>>> updates(
            final Collection<ApplicationConfigurationProperty> configurationProperties
    ) {
        return processor;
    }
}
