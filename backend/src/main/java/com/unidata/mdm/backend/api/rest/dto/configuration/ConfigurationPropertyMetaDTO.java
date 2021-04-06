package com.unidata.mdm.backend.api.rest.dto.configuration;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

public class ConfigurationPropertyMetaDTO<T extends Serializable> {

    private final T defaultValue;
    private final Set<ConfigurationPropertyAvailableValueDTO> availableValues = new LinkedHashSet<>();
    private final boolean required;
    private final boolean readonly;

    public ConfigurationPropertyMetaDTO(
            final T defaultValue,
            final Collection<ConfigurationPropertyAvailableValueDTO> availableValues,
            final boolean required,
            boolean readonly
    ) {
        this.defaultValue = defaultValue;
        if (!CollectionUtils.isEmpty(availableValues)) {
            this.availableValues.addAll(availableValues);
        }
        this.required = required;
        this.readonly = readonly;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Set<ConfigurationPropertyAvailableValueDTO> getAvailableValues() {
        return availableValues;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isReadonly() {
        return readonly;
    }
}
