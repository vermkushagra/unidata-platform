package org.unidata.mdm.system.type.configuration;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

public enum ConfigurationPropertyType {
    STRING("String", ValueValidators.STRING_VALIDATOR, Function.identity()),
    INTEGER("Integer", ValueValidators.INT_VALIDATOR, Integer::valueOf),
    NUMBER("Number", ValueValidators.DOUBLE_VALIDATOR, Double::valueOf),
    BOOLEAN("Boolean", ValueValidators.BOOLEAN_VALIDATOR, Boolean::valueOf);

    private final String value;

    private final Predicate<Optional<String>> validator;

    private final Function<String, ? extends Serializable> deserializer;

    ConfigurationPropertyType(
            final String value,
            final Predicate<Optional<String>> validator,
            final Function<String, ? extends Serializable> deserializer
    ) {
        this.value = value;
        this.validator = validator;
        this.deserializer = deserializer;
    }

    public String value() {
        return value;
    }

    public Predicate<Optional<String>> getValidator() {
        return validator;
    }

    public Function<String, ? extends Serializable> getDeserializer() {
        return deserializer;
    }

    public static ConfigurationPropertyType fromValue(String v) {
        for (ConfigurationPropertyType c : ConfigurationPropertyType.values()) {
            if (StringUtils.equalsIgnoreCase(v, c.value())) {
                return c;
            }
        }
        return null;
    }
}
