package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Michael Yashin. Created on 29.05.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LookupEntityDefinition extends AbstractEntityDefinition {

    /**
     * Has some data already or not.
     */
    protected boolean hasData;
    protected String modelName;
    /**
     * Schema min 1 max 1.
     */
    protected CodeAttributeDefinition codeAttribute = new CodeAttributeDefinition();
    /**
     * Collection of alias code attributes.
     */
    protected Collection<CodeAttributeDefinition> aliasCodeAttributes = new ArrayList<>();
    /**
     * dq rules
     */
    protected List<DQRuleDefinition> dataQualityRules = new ArrayList<>();

    protected List<AttributeGroupsRO> attributeGroups = new ArrayList<>();
    /**
     * Merge settings.
     */
    private MergeSettingsRO mergeSettings;
    /**
     * Validity period.
     */
    protected PeriodBoundaryDefinition validityPeriod;
    /**
     * Ext. ID generation strategy.
     */
    protected ExternalIdGenerationStrategyRO externalIdGenerationStrategy;
    /**
     * Visible on dashboard or not.
     */
    protected boolean dashboardVisible;
    /**
     * Name of group where this entity contains
     */
    protected String groupName;

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

    public CodeAttributeDefinition getCodeAttribute() {
        return codeAttribute;
    }

    public void setCodeAttribute(CodeAttributeDefinition codeAttribute) {
        this.codeAttribute = codeAttribute;
    }

    public List<DQRuleDefinition> getDataQualityRules() {
        return dataQualityRules;
    }

    public void setDataQualityRules(List<DQRuleDefinition> dataQualities) {
        this.dataQualityRules = dataQualities;
    }

    public boolean isDashboardVisible() {
        return dashboardVisible;
    }

    public void setDashboardVisible(boolean dashboardVisible) {
        this.dashboardVisible = dashboardVisible;
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

    public Collection<CodeAttributeDefinition> getAliasCodeAttributes() {
        return aliasCodeAttributes;
    }

    public void setAliasCodeAttributes(Collection<CodeAttributeDefinition> aliasCodeAttributes) {
        this.aliasCodeAttributes = aliasCodeAttributes;
    }

    public List<AttributeGroupsRO> getAttributeGroups() {
        return attributeGroups;
    }

    public void setAttributeGroups(List<AttributeGroupsRO> attributeGroups) {
        this.attributeGroups = attributeGroups;
    }

    /**
     * @return the mergeSettings
     */
    public MergeSettingsRO getMergeSettings() {
        return mergeSettings;
    }

    /**
     * @param mergeSettings the mergeSettings to set
     */
    public void setMergeSettings(MergeSettingsRO mergeSettings) {
        this.mergeSettings = mergeSettings;
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
