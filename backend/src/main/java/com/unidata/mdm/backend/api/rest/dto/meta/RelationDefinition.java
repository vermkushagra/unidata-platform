package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;

/**
 * The Class RelationDefinition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationDefinition {

    /** The rel name. */
    private String name;

    /** The display name. */
    private String displayName;
    /** The simple attributes. */
    private List<SimpleAttributeDefinition> simpleAttributes;

    /** The from entity. */
    private String fromEntity;

    /** The to entity. */
    private String toEntity;

    /** The rel type. */
    private RelType relType;

    /** The required. */
    private boolean required;

    /** The to entity default display attributes. */
    private List<String> toEntityDefaultDisplayAttributes;
    private final List<CustomPropertyDefinition> customProperties = new ArrayList<>();
    /** Show attr name or not. */
    private boolean useAttributeNameForDisplay;

    /**
     * Ext. ID generation strategy.
     */
    protected ExternalIdGenerationStrategyRO externalIdGenerationStrategy;

    /**
     * Has some data already or not.
     */
    protected boolean hasData;

    /**
     * Gets the simple attributes.
     *
     * @return the simple attributes
     */
    public List<SimpleAttributeDefinition> getSimpleAttributes() {
        return simpleAttributes;
    }

    /**
     * Sets the simple attributes.
     *
     * @param simpleAttributes
     *            the new simple attributes
     */
    public void setSimpleAttributes(List<SimpleAttributeDefinition> simpleAttributes) {
        this.simpleAttributes = simpleAttributes;
    }

    /**
     * Gets the from entity.
     *
     * @return the from entity
     */
    public String getFromEntity() {
        return fromEntity;
    }

    /**
     * Sets the from entity.
     *
     * @param fromEntity
     *            the new from entity
     */
    public void setFromEntity(String fromEntity) {
        this.fromEntity = fromEntity;
    }

    /**
     * Gets the to entity.
     *
     * @return the to entity
     */
    public String getToEntity() {
        return toEntity;
    }

    /**
     * Sets the to entity.
     *
     * @param toEntity
     *            the new to entity
     */
    public void setToEntity(String toEntity) {
        this.toEntity = toEntity;
    }

    /**
     * Gets the rel type.
     *
     * @return the rel type
     */
    public RelType getRelType() {
        return relType;
    }

    /**
     * Sets the rel type.
     *
     * @param relType
     *            the new rel type
     */
    public void setRelType(RelType relType) {
        this.relType = relType;
    }

    /**
     * Checks if is required.
     *
     * @return true, if is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the required.
     *
     * @param required
     *            the new required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Gets the to entity default display attributes.
     *
     * @return the to entity default display attributes
     */
    public List<String> getToEntityDefaultDisplayAttributes() {
        return toEntityDefaultDisplayAttributes;
    }

    /**
     * Sets the to entity default display attributes.
     *
     * @param toEntityDefaultDisplayAttributes
     *            the new to entity default display attributes
     */
    public void setToEntityDefaultDisplayAttributes(List<String> toEntityDefaultDisplayAttributes) {
        this.toEntityDefaultDisplayAttributes = toEntityDefaultDisplayAttributes;
    }

    /**
     * Gets the rel name.
     *
     * @return the rel name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the rel name.
     *
     * @param relName
     *            the new rel name
     */
    public void setName(String relName) {
        this.name = relName;
    }

    /**
     * Gets the display name.
     *
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName
     *            the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public List<CustomPropertyDefinition> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(final Collection<CustomPropertyDefinition> customProperties) {
        if (CollectionUtils.isEmpty(customProperties)) {
            return;
        }
        this.customProperties.addAll(customProperties);
    }
    /**
     * @return the useAttributeNameForDisplay
     */
    public boolean isUseAttributeNameForDisplay() {
        return useAttributeNameForDisplay;
    }

    /**
     * @param useAttributeNameForDisplay the useAttributeNameForDisplay to set
     */
    public void setUseAttributeNameForDisplay(boolean useAttributeNameForDisplay) {
        this.useAttributeNameForDisplay = useAttributeNameForDisplay;
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
