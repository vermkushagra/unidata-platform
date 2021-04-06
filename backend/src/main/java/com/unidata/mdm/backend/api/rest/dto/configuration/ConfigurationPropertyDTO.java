package com.unidata.mdm.backend.api.rest.dto.configuration;

import java.io.Serializable;

public class ConfigurationPropertyDTO<T extends Serializable> {

    private final String name;
    private final String displayName;
    private final String group;
    private final ConfigurationPropertyTypeDTO type;
    private final T value;
    private final ConfigurationPropertyMetaDTO<T> meta;

    public ConfigurationPropertyDTO(
            final String name,
            final String displayName,
            final String group,
            final ConfigurationPropertyTypeDTO type,
            final T value,
            final ConfigurationPropertyMetaDTO<T> meta
    ) {
        this.name = name;
        this.displayName = displayName;
        this.group = group;
        this.type = type;
        this.value = value;
        this.meta = meta;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGroup() {
        return group;
    }

    public ConfigurationPropertyTypeDTO getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public ConfigurationPropertyMetaDTO<T> getMeta() {
        return meta;
    }
}
