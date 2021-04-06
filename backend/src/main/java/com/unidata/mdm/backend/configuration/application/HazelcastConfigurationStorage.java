package com.unidata.mdm.backend.configuration.application;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.base.Optional;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.impl.MapListenerAdapter;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

@Service
@Order(value = 3)
public class HazelcastConfigurationStorage
        implements ConfigurationUpdatesByUserConsumer, ConfigurationUpdatesProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastConfigurationStorage.class);

    private static final String CONFIGURATION_MAP_NAME = "configuration";

    private final HazelcastInstance hazelcastInstance;

    private final DirectProcessor<Map<String, Optional<? extends Serializable>>> processor = DirectProcessor.create();

    @Autowired
    public HazelcastConfigurationStorage(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        hazelcastInstance.getMap(CONFIGURATION_MAP_NAME).addEntryListener(
                new MapListenerAdapter<String, Optional<? extends Serializable>>() {
                    @Override
                    public void onEntryEvent(EntryEvent<String, Optional<? extends Serializable>> event) {
                        if (event.getMember().localMember()) {
                            return;
                        }

                        final Map<String, Optional<? extends Serializable>> updatedProperty =
                                Collections.singletonMap(event.getKey(), event.getValue());
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
                hazelcastInstance.getMap(CONFIGURATION_MAP_NAME).putAll(values);
        updates.subscribe(ReactiveUtils.errorLogAndSkipConsumer(updatesConsumer, LOGGER));
    }

    @Override
    public Publisher<Map<String, Optional<? extends Serializable>>> updates() {
        return processor;
    }
}
