package com.unidata.mdm.backend.common.dto;

public class CustomPropertyDefinition {
    /**
     * Custom property name.
     */
    private String name;

    /**
     * Custom property value.
     */
    private String value;

    public CustomPropertyDefinition() {
    }

    public CustomPropertyDefinition(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
