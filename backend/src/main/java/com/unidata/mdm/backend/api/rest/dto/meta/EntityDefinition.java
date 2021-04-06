package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Michael Yashin. Created on 25.05.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityDefinition extends NestedEntityDefinition {
    /**
     * Has some data already or not.
     */
    protected boolean hasData;
    protected String modelName;
    /**
     * Relations if any.
     */
    protected List<RelationDefinition> relations = new ArrayList<>();
    /**
     * Merge settings.
     */
    protected MergeSettingsRO mergeSettings;
    /**
     * Attribute groups
     */
    protected List<AttributeGroupsRO> attributeGroups = new ArrayList<>();
    /**
     * Relation groups
     */
    protected List<RelationGroupsRO> relationGroups = new ArrayList<>();
    /**
     * Is visible on statistic dashboard.
     */
    protected boolean isDashboardVisible;
    /**
     * Name of group where this entity contains
     */
    protected String groupName;
    /**
     * Data qualities.
     */
    protected List<DQRuleDefinition> dataQualityRules = new ArrayList<>();
    /**
     * Customized for this register validity period.
     */
    protected PeriodBoundaryDefinition validityPeriod;
    /**
     * Ext. ID generation strategy.
     */
    protected ExternalIdGenerationStrategyRO externalIdGenerationStrategy;
    /**
     * @return the hasData
     */
    public boolean isHasData() {
        return hasData;
    }

    /**
     * @param hasData the hasData to set
     */
    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public List<RelationDefinition> getRelations() {
        return relations;
    }

    public void setRelations(List<RelationDefinition> relations) {
        this.relations = relations;
    }

    public MergeSettingsRO getMergeSettings() {
        return mergeSettings;
    }

    public void setMergeSettings(MergeSettingsRO mergeSettings) {
        this.mergeSettings = mergeSettings;
    }

    public List<DQRuleDefinition> getDataQualityRules() {
        return dataQualityRules;
    }

    public void setDataQualityRules(List<DQRuleDefinition> dataQualities) {
        this.dataQualityRules = dataQualities;
    }

    public boolean isDashboardVisible() {
        return isDashboardVisible;
    }

    public void setDashboardVisible(boolean isDashboardVisible) {
        this.isDashboardVisible = isDashboardVisible;
    }

	public PeriodBoundaryDefinition getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(PeriodBoundaryDefinition validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<AttributeGroupsRO> getAttributeGroups() {
        return attributeGroups;
    }

    public void setAttributeGroups(List<AttributeGroupsRO> attributeGroups) {
        this.attributeGroups = attributeGroups;
    }

    public List<RelationGroupsRO> getRelationGroups() {
        return relationGroups;
    }

    public void setRelationGroups(List<RelationGroupsRO> relationGroups) {
        this.relationGroups = relationGroups;
    }

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
    /**
     * @return the externalIdGenerationStrategy
     */
    public ExternalIdGenerationStrategyRO getExternalIdGenerationStrategy() {
        return externalIdGenerationStrategy;
    }

    /**
     * @param externalIdGenerationStrategy the externalIdGenerationStrategy to set
     */
    public void setExternalIdGenerationStrategy(ExternalIdGenerationStrategyRO externalIdGenerationStrategy) {
        this.externalIdGenerationStrategy = externalIdGenerationStrategy;
    }
}
