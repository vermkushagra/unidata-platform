package com.unidata.mdm.backend.api.rest.dto.matching;

import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchingRuleRO extends BaseMatchingRuleRO {
    //not null not empty!
    private Collection<MatchingAlgorithmRO> matchingAlgorithms = Collections.emptyList();

    public Collection<MatchingAlgorithmRO> getMatchingAlgorithms() {
        return matchingAlgorithms;
    }

    public void setMatchingAlgorithms(Collection<MatchingAlgorithmRO> matchingAlgorithms) {
        this.matchingAlgorithms = matchingAlgorithms;
    }
}
