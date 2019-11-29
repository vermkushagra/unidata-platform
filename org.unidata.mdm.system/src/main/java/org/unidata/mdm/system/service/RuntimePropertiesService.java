package org.unidata.mdm.system.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.configuration.ConfigurationProperty;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesConsumer;

public interface RuntimePropertiesService {

    void addConfigurationProperties(Collection<ApplicationConfigurationProperty> configurationProperties);

    void subscribeToConfigurationUpdates(ConfigurationUpdatesConsumer configurationUpdatesConsumer);

    <T extends Serializable> Collection<ConfigurationProperty<T>> availableProperties();

    <T extends Serializable> Collection<ConfigurationProperty<T>> getPropertiesByGroup(String groupName);

    <T extends Serializable> Optional<ConfigurationProperty<T>> property(String name);

    Collection<String> updatePropertiesValues(Map<String, String> properties);
}
