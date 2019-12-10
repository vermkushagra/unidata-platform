package org.unidata.mdm.system.service.impl.configuration;

import org.apache.commons.lang3.SerializationUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.util.ReactiveUtils;
import org.unidata.mdm.system.dao.ConfigurationDAO;
import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesByUserConsumer;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesProducer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Order(2)
public class DatabaseConfigurationStorageService
        implements ConfigurationUpdatesProducer, ConfigurationUpdatesByUserConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfigurationStorageService.class);

    private final ConfigurationDAO configurationDAO;

    public DatabaseConfigurationStorageService(final ConfigurationDAO configurationDAO) {
        this.configurationDAO = configurationDAO;
    }

    private Map<String, Optional<? extends Serializable>> availableProperties() {
        return configurationDAO.fetchAllProperties().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> SerializationUtils.<com.google.common.base.Optional<? extends Serializable>>deserialize(e.getValue())
                                .toJavaUtil()
                ));
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        updates.map(values ->
                values.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> SerializationUtils.serialize(
                                        com.google.common.base.Optional.fromJavaUtil(e.getValue())
                                )
                        ))
        ).subscribe(ReactiveUtils.errorLogAndSkipConsumer(configurationDAO::save, LOGGER));
    }

    @Override
    public Publisher<Map<String, Optional<? extends Serializable>>> updates(
            final Collection<ApplicationConfigurationProperty> configurationProperties
    ) {
        return Mono.create(sink -> {
            final Map<String, Optional<? extends Serializable>> availableProperties = availableProperties();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Update configuration from database. Available properties: {}", availableProperties);
            }
            sink.success(availableProperties);
        });
    }
}