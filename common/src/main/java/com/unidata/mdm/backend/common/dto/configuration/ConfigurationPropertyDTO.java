package com.unidata.mdm.backend.common.dto.configuration;

import java.io.Serializable;

public class ConfigurationPropertyDTO<T extends Serializable> {

    private final ConfigurationPropertyMetaDTO<T> meta;

    private final T value;

    public ConfigurationPropertyDTO(
            final ConfigurationPropertyMetaDTO<T> meta,
            final T value
    ) {
        this.meta = meta;
        this.value = value;
    }

    public ConfigurationPropertyMetaDTO<T> getMeta() {
        return meta;
    }

    public T getValue() {
        return value;
    }
}
