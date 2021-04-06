package com.unidata.mdm.backend.api.rest.dto.matching;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchingFieldRO {
    private Integer id;
    private String name;
    private String description;
    private boolean constantField = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isConstantField() {
        return constantField;
    }

    public void setConstantField(boolean constantField) {
        this.constantField = constantField;
    }
}
