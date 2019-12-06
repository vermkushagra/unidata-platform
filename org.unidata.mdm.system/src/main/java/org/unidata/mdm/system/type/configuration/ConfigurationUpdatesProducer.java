package org.unidata.mdm.system.type.configuration;

import org.reactivestreams.Publisher;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface ConfigurationUpdatesProducer {
    Publisher<Map<String, Optional<? extends Serializable>>> updates(Collection<ApplicationConfigurationProperty> configurationProperties);
}
