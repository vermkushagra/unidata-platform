package com.unidata.mdm.backend.api.rest.dto.matching;

import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchingGroupRO {

    private Integer id;

    private String name;

    private String description;

    private String entityName;

    private boolean autoMerge;

    private Collection<Integer> ruleIds = Collections.emptyList();

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

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Collection<Integer> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(Collection<Integer> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public boolean isAutoMerge() {
        return autoMerge;
    }

    public void setAutoMerge(boolean autoMerge) {
        this.autoMerge = autoMerge;
    }
}
