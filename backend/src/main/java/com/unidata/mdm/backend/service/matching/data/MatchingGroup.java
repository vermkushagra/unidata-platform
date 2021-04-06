package com.unidata.mdm.backend.service.matching.data;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.ValidationResult;

public class MatchingGroup {
    /**
     * Business limitation of rule name.
     */
    private static final int GROUP_NAME_MAX_LENGTH = 255;
    private Integer id;

    private String name;

    private String description;

    private String entityName;

    private boolean autoMerge;

    private boolean active;

    private Collection<MatchingRule> rules;

    private Collection<Integer> rulesIds;
    /**
     * Support XML import/export case.
     */
    private Collection<String> rulesNames;

    private Function<Integer, MatchingRule> fetchFunction;

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

    public boolean isAutoMerge() {
        return autoMerge;
    }

    public void setAutoMerge(boolean autoMerge) {
        this.autoMerge = autoMerge;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Collection<Integer> getRulesIds() {
        return rulesIds;
    }

    public void setRulesIds(Collection<Integer> rulesIds) {
        this.rulesIds = rulesIds;
    }

	/**
     * @return the rulesNames
     */
    public Collection<String> getRulesNames() {
        return rulesNames;
    }

    /**
     * @param rulesNames the rulesNames to set
     */
    public void setRulesNames(Collection<String> rulesNames) {
        this.rulesNames = rulesNames;
    }

    public Collection<MatchingRule> getRules() {
		if (rules == null) {
			if (getRulesIds() == null || fetchFunction == null) {
				return null;
			}
			rules = getRulesIds().stream().parallel().map(fetchFunction).collect(toList());
		}
		return rules;
	}

    public MatchingGroup addFetchFunction(@Nonnull Function<Integer, MatchingRule> fetchFunction) {
        this.fetchFunction = fetchFunction;
        return this;
    }

    /**
     * Throw an Exception in case when something wrong with inner state of group
     */
    public  Collection<ValidationResult> checkCompleteness() {
        Collection<ValidationResult> validationErrors = new ArrayList<>(4);
        String name = isBlank(getName()) ? "Не известно" : getName();
        String entityName = isBlank(getEntityName()) ? "Не известно" : getEntityName();
        if (isBlank(getName())) {
            ValidationResult validation = new ValidationResult(
                    "Matching group [{}] in entity [{}] can not be define without a name",
                    ExceptionId.EX_MATCHING_GROUP_INCORRECT_BLANK_NAME.getCode(), name, entityName);
            validationErrors.add(validation);
        } else if (getName().length() > GROUP_NAME_MAX_LENGTH) {
            ValidationResult validation = new ValidationResult(
                    "Matching group [{}] in entity [{}] can not be define with name which length more then [{}]",
                    ExceptionId.EX_MATCHING_GROUP_INCORRECT_LONG_NAME.getCode(), name, entityName, GROUP_NAME_MAX_LENGTH);
            validationErrors.add(validation);
        }
        if (isBlank(getEntityName())) {
            ValidationResult validation = new ValidationResult(
                    "Matching group [{}] in entity [{}] can not be define  without an entity",
                    ExceptionId.EX_MATCHING_GROUP_INCORRECT_BLANK_ENTITY.getCode(), name, entityName);
            validationErrors.add(validation);
        }
        return validationErrors;
    }

    public Set<String> getMatchingFieldNames(){
        Set<String> result = new HashSet<>();
        getRules().forEach(matchingRule ->
                matchingRule.getMatchingAlgorithms().forEach(matchingAlgorithm -> {
                    result.addAll(matchingAlgorithm.getMatchingFields()
                            .stream()
                            .map(MatchingField::getAttrName)
                            .collect(Collectors.toList()));
                }));
        return result;
    }

    public MatchingGroup copy(){
        MatchingGroup copy = new MatchingGroup();
        if(rules != null){
            copy.rules = new ArrayList<>(this.rules);
        }
        if(rulesIds != null){
            copy.rulesIds = new ArrayList<>(this.rulesIds);
        }
        if(rulesNames != null){
            copy.rulesNames = new ArrayList<>(this.rulesNames);
        }
        copy.id = this.id;
        copy.active = this.active;
        copy.autoMerge = this.autoMerge;
        copy.name = this.name;
        copy.description = this.description;
        copy.entityName = this.entityName;
        copy.fetchFunction = this.fetchFunction;
        return copy;
    }


}
