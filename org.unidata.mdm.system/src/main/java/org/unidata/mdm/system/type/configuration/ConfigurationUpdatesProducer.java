package org.unidata.mdm.system.type.configuration;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import org.reactivestreams.Publisher;

public interface ConfigurationUpdatesProducer {
    Publisher<Map<String, Optional<? extends Serializable>>> updates();
}
