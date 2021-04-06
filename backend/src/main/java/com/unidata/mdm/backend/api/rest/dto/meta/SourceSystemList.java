package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class SourceSystemList.
 */
public class SourceSystemList {

    /** The admin system name. */
    @JsonProperty(index = 1, value = "adminSystemName")
    private String adminSystemName;

    /** The source system. */
    @JsonProperty(index = 2, value = "sourceSystem")
    private List<SourceSystemDefinition> sourceSystem;

    /**
     * Gets the admin system name.
     *
     * @return the adminSystemName
     */
    public String getAdminSystemName() {
	return adminSystemName;
    }

    /**
     * Sets the admin system name.
     *
     * @param adminSystemName
     *            the adminSystemName to set
     */
    public void setAdminSystemName(String adminSystemName) {
	this.adminSystemName = adminSystemName;
    }

    /**
     * Gets the source system.
     *
     * @return the sourceSystem
     */
    public List<SourceSystemDefinition> getSourceSystem() {
	if (sourceSystem == null) {
	    sourceSystem = new ArrayList<SourceSystemDefinition>();
	}
	return sourceSystem;
    }

    /**
     * Adds the source system.
     *
     * @param sourceSystemDefinition
     *            the source system definition
     */
    public void addSourceSystem(SourceSystemDefinition sourceSystemDefinition) {
	if (sourceSystem == null) {
	    sourceSystem = new ArrayList<SourceSystemDefinition>();

	}
	sourceSystem.add(sourceSystemDefinition);
    }

    /**
     * Sets the source system.
     *
     * @param sourceSystem
     *            the sourceSystem to set
     */
    public void setSourceSystem(List<SourceSystemDefinition> sourceSystem) {
	this.sourceSystem = sourceSystem;
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
		+ ((adminSystemName == null) ? 0 : adminSystemName.hashCode());
	result = prime * result
		+ ((sourceSystem == null) ? 0 : sourceSystem.hashCode());
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
	SourceSystemList other = (SourceSystemList) obj;
	if (adminSystemName == null) {
	    if (other.adminSystemName != null)
		return false;
	} else if (!adminSystemName.equals(other.adminSystemName))
	    return false;
	if (sourceSystem == null) {
	    if (other.sourceSystem != null)
		return false;
	} else if (!sourceSystem.equals(other.sourceSystem))
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
	builder.append("SourceSystemList [adminSystemName=");
	builder.append(adminSystemName);
	builder.append(", sourceSystem=");
	builder.append(sourceSystem);
	builder.append("]");
	return builder.toString();
    }
}
