package com.unidata.mdm.backend.service.matching.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.unidata.mdm.backend.service.matching.data.MatchingGroup;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;

public class MatchingUserSettings {

    Collection<MatchingRule> matchingRules = Collections.emptyList();
    Collection<MatchingGroup> matchingGroups = Collections.emptyList();
    Map<String,Collection<MatchingKey>> matchingKeys = Collections.emptyMap();

    public Collection<MatchingRule> getMatchingRules() {
        return matchingRules;
    }

    public void setMatchingRules(Collection<MatchingRule> matchingRules) {
        this.matchingRules = matchingRules;
    }

    public Collection<MatchingGroup> getMatchingGroups() {
        return matchingGroups;
    }

    public void setMatchingGroups(Collection<MatchingGroup> matchingGroups) {
        this.matchingGroups = matchingGroups;
    }

    public Map<String, Collection<MatchingKey>> getMatchingKeys() {
        return matchingKeys;
    }

    public void setMatchingKeys(Map<String, Collection<MatchingKey>> matchingKeys) {
        this.matchingKeys = matchingKeys;
    }
}
