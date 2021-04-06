package com.unidata.mdm.backend.configuration.application;

import java.io.Serializable;
import java.util.Map;
import com.google.common.base.Optional;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.dao.ConfigurationDAO;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Order(value = 2)
public class DatabaseConfigurationStorageService
        implements ConfigurationUpdatesProducer, ConfigurationUpdatesByUserConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfigurationStorageService.class);

    private final ConfigurationDAO configurationDAO;

    @Autowired
    public DatabaseConfigurationStorageService(final ConfigurationDAO configurationDAO) {
        this.configurationDAO = configurationDAO;
    }

    private Map<String, Optional<? extends Serializable>> availableProperties() {
        return configurationDAO.fetchAllProperties().entrySet().stream()
                .filter(entry -> UnidataConfigurationProperty.exists(entry.getKey()))
                .map(entry ->
                        Pair.of(
                                entry.getKey(),
                                SerializationUtils.<Optional<? extends Serializable>>deserialize(entry.getValue())
                        )
                )
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        updates
                .map(values ->
                        values.entrySet().stream()
                                .map(entry ->
                                        Pair.of(
                                                entry.getKey(),
                                                SerializationUtils.serialize(entry.getValue()))
                                )
                                .collect(Collectors.toMap(Pair::getKey, Pair::getValue))
                )
                .subscribe(ReactiveUtils.errorLogAndSkipConsumer(configurationDAO::save, LOGGER));
    }

    @Override
    public Publisher<Map<String, Optional<? extends Serializable>>> updates() {
        return Mono.create(sink -> {
            final Map<String, Optional<? extends Serializable>> availableProperties = availableProperties();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Update configuration from database. Available properties: {}", availableProperties);
            }
            sink.success(availableProperties);
        });
    }
}
