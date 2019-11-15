package org.unidata.mdm.system.type.configuration;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import reactor.core.publisher.Flux;

public interface ConfigurationUpdatesByUserConsumer {
    void subscribe(final Flux<Map<String, Optional<? extends Serializable>>> updates);
}
