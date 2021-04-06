package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.api.rest.dto.ArrayDataType;

/**
 * @author Mikhail Mikhailov
 * Array type definition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrayAttributeDefinitionRO extends AbstractAttributeDefinition {
    /**
     * Attribute is generally searchable.
     */
    private boolean searchable = false;
    /**
     * Can be null or not.
     */
    private boolean nullable = true;
    /**
     * Mask.
     */
    private String mask;
    /**
     * Attribute order.
     */
    private int order;
    /**
     * Type of array values.
     */
    private ArrayDataType arrayDataType;
    /**
     * Possible lookup entity type.
     */
    private String lookupEntityType;
    /**
     * Type of code attribute values.
     */
    private ArrayDataType lookupEntityCodeAttributeType;
    /**
     * Alternative display attributes
     */
    private List<String> lookupEntityDisplayAttributes;
    /**
     * Show attr name or not.
     */
    private boolean useAttributeNameForDisplay;
    /**
     * Exchange separator.
     */
    private String exchangeSeparator;
    /**
     * This _STRING_ attribute supports morphological search.
     */
    protected boolean searchMorphologically;
    /**
     * Constructor.
     */
    public ArrayAttributeDefinitionRO() {
        super();
    }
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
    /**
     * @return the searchable
     */
    public boolean isSearchable() {
        return searchable;
    }
    /**
     * @param searchable the searchable to set
     */
    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }
    /**
     * @return the nullable
     */
    public boolean isNullable() {
        return nullable;
    }
    /**
     * @param nullable the nullable to set
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
    /**
     * @return the mask
     */
    public String getMask() {
        return mask;
    }
    /**
     * @param mask the mask to set
     */
    public void setMask(String mask) {
        this.mask = mask;
    }
    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }
    /**
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }
    /**
     * @return the arrayDataType
     */
    public ArrayDataType getArrayDataType() {
        return arrayDataType;
    }
    /**
     * @param arrayDataType the arrayDataType to set
     */
    public void setArrayDataType(ArrayDataType arrayDataType) {
        this.arrayDataType = arrayDataType;
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
    public ArrayDataType getLookupEntityCodeAttributeType() {
        return lookupEntityCodeAttributeType;
    }
    /**
     * @param lookupEntityCodeAttributeType the lookupEntityCodeAttributeType to set
     */
    public void setLookupEntityCodeAttributeType(ArrayDataType lookupEntityCodeAttributeType) {
        this.lookupEntityCodeAttributeType = lookupEntityCodeAttributeType;
    }
    /**
     * @return the exchangeSeparator
     */
    public String getExchangeSeparator() {
        return exchangeSeparator;
    }
    /**
     * @param exchangeSeparator the exchangeSeparator to set
     */
    public void setExchangeSeparator(String exchangeSeparator) {
        this.exchangeSeparator = exchangeSeparator;
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
