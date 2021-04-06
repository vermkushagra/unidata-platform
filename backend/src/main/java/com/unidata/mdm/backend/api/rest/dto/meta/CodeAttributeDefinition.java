package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Michael Yashin. Created on 29.05.2015.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeAttributeDefinition extends AttributeDefinition {
    /**
     * Attribute is generally displayable.
     */
    private boolean displayable;
    /**
     * The attribute is the main displayable attribute.
     */
    private boolean mainDisplayable;
    /**
     * Input mask.
     */
    private String mask;
    /**
     * Ext. ID generation strategy.
     */
    private ExternalIdGenerationStrategyRO externalIdGenerationStrategy;
    /**
     * @return the displayable
     */
    public boolean isDisplayable() {
        return displayable;
    }

    /**
     * @param displayable the displayable to set
     */
    public void setDisplayable(boolean displayable) {
        this.displayable = displayable;
    }

    /**
     * @return the mainDisplayable
     */
    public boolean isMainDisplayable() {
        return mainDisplayable;
    }

    /**
     * @param mainDisplayable the mainDisplayable to set
     */
    public void setMainDisplayable(boolean mainDisplayable) {
        this.mainDisplayable = mainDisplayable;
    }

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
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
