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
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    public Publisher<Map<String, Optional<? extends Serializable>>> updates() {
        return processor;
    }
}