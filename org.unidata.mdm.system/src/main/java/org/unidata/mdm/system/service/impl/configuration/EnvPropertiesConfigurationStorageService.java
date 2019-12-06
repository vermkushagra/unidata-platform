package org.unidata.mdm.system.service.impl.configuration;

import org.apache.commons.lang3.tuple.Pair;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesProducer;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Order(1)
public class EnvPropertiesConfigurationStorageService implements ConfigurationUpdatesProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvPropertiesConfigurationStorageService.class);

    private final Environment environment;

    public EnvPropertiesConfigurationStorageService(final Environment environment) {
        this.environment = environment;
    }

    private Map<String, Optional<? extends Serializable>> availableProperties(
            final Collection<ApplicationConfigurationProperty> configurationProperties
    ) {
        return configurationProperties.stream()
                .map(property ->
                        Pair.of(
                                property.getKey(),
                                Optional.ofNullable(environment.getProperty(property.getKey()))
                                        .map(value -> property.getDeserializer().apply(value))
                        )
                )
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public Publisher<Map<String, Optional<? extends Serializable>>> updates(
            final Collection<ApplicationConfigurationProperty> configurationProperties
    ) {
        return Mono.create(sink -> {
            final Map<String, Optional<? extends Serializable>> availableProperties = availableProperties(configurationProperties);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Update configuration from environment. Available properties: {}", availableProperties);
            }
            sink.success(availableProperties);
        });
    }
}
