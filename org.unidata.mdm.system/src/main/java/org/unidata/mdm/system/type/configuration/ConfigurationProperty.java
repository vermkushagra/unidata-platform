package org.unidata.mdm.system.type.configuration;

import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;

import java.io.Serializable;

public class ConfigurationProperty<T extends Serializable> {

    private final ApplicationConfigurationProperty property;

    private final T value;

    public ConfigurationProperty(
            final ApplicationConfigurationProperty property,
            final T value
    ) {
        this.property = property;
        this.value = value;
    }

    public ApplicationConfigurationProperty getProperty() {
        return property;
    }

    public T getValue() {
        return value;
    }
}
