package com.unidata.mdm.backend.api.rest.dto.security;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class SecurityLabelRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityLabelRO {

    /** The name. */
    private String name;

    /** The display name. */
    private String displayName;

    /** The description. */
    private String description;
    /** The attributes. */
    private List<SecurityLabelAttributeRO> attributes;

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
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

    /**
     * Gets the attributes.
     *
     * @return the attributes
     */
    public List<SecurityLabelAttributeRO> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new ArrayList<SecurityLabelAttributeRO>();
        }
        return attributes;
    }

    /**
     * Sets the attributes.
     *
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(List<SecurityLabelAttributeRO> attributes) {
        this.attributes = attributes;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description
     *            the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
