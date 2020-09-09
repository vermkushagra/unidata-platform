/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
     * Lookup search attributes
     */
    private List<String> lookupEntitySearchAttributes;

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

    public List<String> getLookupEntitySearchAttributes() {
        return lookupEntitySearchAttributes;
    }

    public void setLookupEntitySearchAttributes(List<String> lookupEntitySearchAttributes) {
        this.lookupEntitySearchAttributes = lookupEntitySearchAttributes;
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
