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

package org.unidata.mdm.system.service.impl;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.service.ConfigurationCacheService;

import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@Service
public class LocalConfigurationCache implements ConfigurationCacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalConfigurationCache.class);

    private final ConcurrentMap<String, Optional<? extends Serializable>> storage = new ConcurrentHashMap<>();

    private final DirectProcessor<Map<String, Optional<? extends Serializable>>> processor = DirectProcessor.create();

    private final Flux<Map<String, Optional<? extends Serializable>>> updatesStream = processor
            .doOnError(throwable -> LOGGER.error("Error in updates stream from cache", throwable));

    private void upsertProperties(Map<String, Optional<? extends Serializable>> properties) {
        if (MapUtils.isNotEmpty(properties)) {
            storage.putAll(properties);
            processor.onNext(properties);
        }
    }

    @Override
    public void fillCache(Map<String, Optional<? extends Serializable>> properties) {
        if (MapUtils.isNotEmpty(properties)) {
            storage.putAll(properties);
        }
    }

    @Override
    public Optional<? extends Serializable> propertyValue(String name) {
        return Optional.ofNullable(storage.get(name)).flatMap(Function.identity());
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        updates.subscribe(this::upsertProperties);
    }

    @Override
    public Flux<Map<String, Optional<? extends Serializable>>> updates() {
        return Flux.merge(Mono.fromSupplier(() -> Collections.unmodifiableMap(storage)), updatesStream);
    }
}
