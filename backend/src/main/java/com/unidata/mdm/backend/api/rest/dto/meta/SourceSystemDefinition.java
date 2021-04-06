package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import org.apache.commons.collections.CollectionUtils;

/**
 * The Class SourceSystemDefinition.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SourceSystemDefinition {

    /** The name. */
    @JsonProperty(index = 1, value = "name")
    private String name;

    /** The description. */
    @JsonProperty(index = 2, value = "description")
    private String description;
    /** The description. */
    @JsonProperty(index = 3, value = "weight")
    private int weight;

    @JsonProperty(index = 4, value = "customProperties")
    private final List<CustomPropertyDefinition> customProperties = new ArrayList<>();

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
     *            the description to set
     */
    public void setDescription(String description) {
	this.description = description;
    }

    /**
     * Gets the weight.
     *
     * @return the weight
     */
    public int getWeight() {
	return weight;
    }

    /**
     * Sets the weight.
     *
     * @param weight
     *            the weight to set
     */
    public void setWeight(int weight) {
	this.weight = weight;
    }

    public List<CustomPropertyDefinition> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(final Collection<CustomPropertyDefinition> customProperties) {
        if (CollectionUtils.isEmpty(customProperties)) {
            return;
        }
        this.customProperties.clear();
        this.customProperties.addAll(customProperties);
    }

    public void addCustomProperties(final Collection<CustomPropertyDefinition> customProperties) {
        if (CollectionUtils.isEmpty(customProperties)) {
            return;
        }
        this.customProperties.addAll(customProperties);
    }

    /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("SourceSystemDefinition [name=");
	builder.append(name);
	builder.append(", description=");
	builder.append(description);
	builder.append(", weight=");
	builder.append(weight);
	builder.append("]");
	return builder.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((description == null) ? 0 : description.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + weight;
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	SourceSystemDefinition other = (SourceSystemDefinition) obj;
	if (description == null) {
	    if (other.description != null)
		return false;
	} else if (!description.equals(other.description))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (weight != other.weight)
	    return false;
	return true;
    }

}
