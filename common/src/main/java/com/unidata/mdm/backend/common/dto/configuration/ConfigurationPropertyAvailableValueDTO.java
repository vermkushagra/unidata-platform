package com.unidata.mdm.backend.common.dto.configuration;

import java.io.Serializable;
import java.util.Objects;

public class ConfigurationPropertyAvailableValueDTO<T extends Serializable> {
    private final T value;
    private final String displayValue;

    public ConfigurationPropertyAvailableValueDTO(T value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
    }

    public T getValue() {
        return value;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationPropertyAvailableValueDTO<?> that = (ConfigurationPropertyAvailableValueDTO<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
