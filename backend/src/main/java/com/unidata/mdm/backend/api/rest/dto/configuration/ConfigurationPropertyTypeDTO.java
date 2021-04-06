package com.unidata.mdm.backend.api.rest.dto.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum ConfigurationPropertyTypeDTO {
    STRING("String"),
    INTEGER("Integer"),
    NUMBER("Number"),
    BOOLEAN("Boolean");

    private final String value;

    ConfigurationPropertyTypeDTO(String v) {
        value = v;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static ConfigurationPropertyTypeDTO fromValue(String v) {
        for (ConfigurationPropertyTypeDTO c : ConfigurationPropertyTypeDTO.values()) {
            if (StringUtils.equalsIgnoreCase(v, c.value())) {
                return c;
            }
        }
        return null;
    }
}
