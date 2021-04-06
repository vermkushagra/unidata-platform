package com.unidata.mdm.backend.api.rest.dto.matching;

import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchingAlgorithmRO {
    private Integer id;
    private String name;
    private String description;
    private Collection<MatchingFieldRO> matchingFields = Collections.emptyList();

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

    public Collection<MatchingFieldRO> getMatchingFields() {
        return matchingFields;
    }

    public void setMatchingFields(Collection<MatchingFieldRO> matchingFields) {
        this.matchingFields = matchingFields;
    }
}
