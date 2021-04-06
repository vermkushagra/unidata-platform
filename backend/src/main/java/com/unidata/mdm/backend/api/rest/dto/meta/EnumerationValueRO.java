package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class EnumerationValue.
 */
public class EnumerationValueRO {
    /** The code. */
    @JsonProperty(index = 1, value = "name")
    private String name;
    /** The description. */
    @JsonProperty(index = 2, value = "displayName")
    private String displayName;


    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the code.
     *
     * @param code            the code to set
     */
    public void setName(String code) {
	this.name = code;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDisplayName() {
	return displayName;
    }

    /**
     * Sets the description.
     *
     * @param description            the description to set
     */
    public void setDisplayName(String description) {
	this.displayName = description;
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
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result
		+ ((displayName == null) ? 0 : displayName.hashCode());
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
	EnumerationValueRO other = (EnumerationValueRO) obj;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (displayName == null) {
	    if (other.displayName != null)
		return false;
	} else if (!displayName.equals(other.displayName))
	    return false;
	return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("EnumerationValue [code=");
	builder.append(name);
	builder.append(", description=");
	builder.append(displayName);
	builder.append("]");
	return builder.toString();
    }
}
