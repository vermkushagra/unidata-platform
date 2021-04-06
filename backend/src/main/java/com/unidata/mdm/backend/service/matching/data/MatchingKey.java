package com.unidata.mdm.backend.service.matching.data;

public class MatchingKey {
    private String matchingRuleName;
    private String entityName;
    private String matchingGroupName;

    public String getMatchingRuleName() {
        return matchingRuleName;
    }

    public MatchingKey setMatchingRuleName(String matchingRuleName) {
        this.matchingRuleName = matchingRuleName;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    public MatchingKey setEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public String getMatchingGroupName() {
        return matchingGroupName;
    }

    public MatchingKey setMatchingGroupName(String matchingGroupName) {
        this.matchingGroupName = matchingGroupName;
        return this;
    }
}
