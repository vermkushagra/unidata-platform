package com.unidata.mdm.backend.common.dto.configuration;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.collections.CollectionUtils;

public class ConfigurationPropertyMetaDTO<T extends Serializable> {

    private final String name;
    private final Supplier<String> displayName;
    private final String groupCode;
    private final Supplier<String> group;
    private final ConfigurationPropertyTypeDTO type;
    private final T defaultValue;
    private final Set<ConfigurationPropertyAvailableValueDTO> availableValues = new LinkedHashSet<>();
    private final boolean required;
    private final boolean readonly;

    private final Predicate<Optional<String>> validator;
    private final Function<String, ? extends Serializable> deserializer;

    public ConfigurationPropertyMetaDTO(
            final String name,
            final Supplier<String> displayName,
            final String groupCode,
            final Supplier<String> group,
            final ConfigurationPropertyTypeDTO type,
            final T defaultValue,
            final Collection<ConfigurationPropertyAvailableValueDTO> availableValues,
            final boolean required,
            final boolean readonly,
            final Predicate<Optional<String>> validator,
            final Function<String, ? extends Serializable> deserializer
    ) {
        this.defaultValue = defaultValue;
        this.name = name;
        this.groupCode = groupCode;
        this.displayName = displayName;
        this.group = group;
        this.type = type;
        if (!CollectionUtils.isEmpty(availableValues)) {
            this.availableValues.addAll(availableValues);
        }
        this.required = required;
        this.readonly = readonly;
        this.validator = validator;
        this.deserializer = deserializer;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public String getGroup() {
        return group.get();
    }

    public ConfigurationPropertyTypeDTO getType() {
        return type;
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

    public Predicate<Optional<String>> getValidator() {
        return validator;
    }

    public Function<String, ? extends Serializable> getDeserializer() {
        return deserializer;
    }

    public String getGroupCode() {
        return groupCode;
    }
}
