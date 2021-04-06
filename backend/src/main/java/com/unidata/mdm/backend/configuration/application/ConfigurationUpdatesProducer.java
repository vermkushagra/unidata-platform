package com.unidata.mdm.backend.configuration.application;

import java.io.Serializable;
import java.util.Map;

import com.google.common.base.Optional;
import org.reactivestreams.Publisher;

public interface ConfigurationUpdatesProducer {
    Publisher<Map<String, Optional<? extends Serializable>>> updates();
}
