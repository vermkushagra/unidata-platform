package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;

/**
 * @author Michael Yashin. Created on 29.05.2015.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleAttributeDefinition extends CodeAttributeDefinition {
    /**
     * Possible enum type.
     */
    protected String enumDataType;
    /**
     * Possible lookup entity type.
     */
    protected String lookupEntityType;
    /**
     * Type of code attribute values.
     */
    private SimpleDataType lookupEntityCodeAttributeType;
    /**
     * Alternative display attributes
     */
    private List<String> lookupEntityDisplayAttributes;
    /**
     * Show attr name or not.
     */
    private boolean useAttributeNameForDisplay;
    /**
     * External link template definition.
     */
    protected String linkDataType;
    /**
     * value id
     */
    private String valueId;
    /**
     * default unit id
     */
    private String defaultUnitId;
    /**
     * mask
     */
    protected String mask;
    /**
     * Attribute order.
     */
    protected int order;
    /**
     * This _STRING_ attribute supports morphological search.
     */
    protected boolean searchMorphologically;
    /**
     * @return the searchMorphologically
     */
    public boolean isSearchMorphologically() {
        return searchMorphologically;
    }
    /**
     * @param searchMorphologically the searchMorphologically to set
     */
    public void setSearchMorphologically(boolean searchMorphologically) {
        this.searchMorphologically = searchMorphologically;
    }

    @Override
    @JsonProperty(required = false)
    public SimpleDataType getSimpleDataType() {
        return super.getSimpleDataType();
    }

    @Override
    @JsonProperty(required = false)
    public void setSimpleDataType(SimpleDataType simpleDataType) {
        super.setSimpleDataType(simpleDataType);
    }

    public String getEnumDataType() {
        return enumDataType;
    }

    public void setEnumDataType(String enumDataType) {
        this.enumDataType = enumDataType;
    }

    public String getLookupEntityType() {
        return lookupEntityType;
    }

    public void setLookupEntityType(String lookupEntityType) {
        this.lookupEntityType = lookupEntityType;
    }

    /**
     * @return the lookupEntityCodeAttributeType
     */
    public SimpleDataType getLookupEntityCodeAttributeType() {
        return lookupEntityCodeAttributeType;
    }

    /**
     * @param lookupEntityCodeAttributeType the lookupEntityCodeAttributeType to set
     */
    public void setLookupEntityCodeAttributeType(SimpleDataType lookupEntityCodeAttributeType) {
        this.lookupEntityCodeAttributeType = lookupEntityCodeAttributeType;
    }

    /**
     * @return the linkDataType
     */
    public String getLinkDataType() {
        return linkDataType;
    }

    /**
     * @param linkDataType the linkDataType to set
     */
    public void setLinkDataType(String linkDataType) {
        this.linkDataType = linkDataType;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String getMask() {
        return mask;
    }

    @Override
    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getDefaultUnitId() {
        return defaultUnitId;
    }

    public void setDefaultUnitId(String defaultUnitId) {
        this.defaultUnitId = defaultUnitId;
    }

    public List<String> getLookupEntityDisplayAttributes() {
        return lookupEntityDisplayAttributes;
    }

    public void setLookupEntityDisplayAttributes(List<String> lookupEntityDisplayAttributes) {
        this.lookupEntityDisplayAttributes = lookupEntityDisplayAttributes;
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
}
