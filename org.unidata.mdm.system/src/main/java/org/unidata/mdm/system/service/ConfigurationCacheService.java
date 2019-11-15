package org.unidata.mdm.system.service;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import reactor.core.publisher.Flux;

public interface ConfigurationCacheService {

    Optional<? extends Serializable> propertyValue(String name);

    void fillCache(Map<String, Optional<? extends Serializable>> data);

    void subscribe(final Flux<Map<String, Optional<? extends Serializable>>> updates);

    Flux<Map<String, Optional<? extends Serializable>>> updates();
}
