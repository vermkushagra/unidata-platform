package com.unidata.mdm.backend.configuration.application;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import com.google.common.base.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@Order(value = 1)
public class BackendPropertiesConfigurationStorageService implements ConfigurationUpdatesProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendPropertiesConfigurationStorageService.class);

    private final Properties backedProperties;

    @Autowired
    public BackendPropertiesConfigurationStorageService(
            final @Qualifier("backendProperties") Properties backedProperties
    ) {
        this.backedProperties = backedProperties;
    }

    private Map<String, Optional<? extends Serializable>> availableProperties(final Properties backedProperties) {
        return Arrays.stream(UnidataConfigurationProperty.values())
                .filter(property -> backedProperties.containsKey(property.getKey()))
                .map(property ->
                        Pair.of(
                                property.getKey(),
                                Optional.fromNullable(backedProperties.getProperty(property.getKey()))
                                        .transform((value) -> property.getDeserializer().apply(value))
                        )
                )
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public Publisher<Map<String, Optional<? extends Serializable>>> updates() {
        return Mono.create(sink -> {
            final Map<String, Optional<? extends Serializable>> availableProperties = availableProperties(backedProperties);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Update configuration from environment. Available properties: {}", availableProperties);
            }
            sink.success(availableProperties);
        });
    }
}
