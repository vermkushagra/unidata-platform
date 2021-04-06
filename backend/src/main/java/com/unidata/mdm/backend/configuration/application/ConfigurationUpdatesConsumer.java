package com.unidata.mdm.backend.configuration.application;

import java.io.Serializable;
import java.util.Map;
import com.google.common.base.Optional;

import reactor.core.publisher.Flux;

public interface ConfigurationUpdatesConsumer {
    void subscribe(final Flux<Map<String, Optional<? extends Serializable>>> updates);
}
