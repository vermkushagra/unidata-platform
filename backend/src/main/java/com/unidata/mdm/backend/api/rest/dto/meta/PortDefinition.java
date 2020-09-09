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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;


/**
 * The Class PortDefinition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortDefinition {

    /** The name. */
    @JsonProperty(index=1, value="name")
    private String name;

    /** The data type. */
    @JsonProperty(index=2, value="dataType")
    private SimpleDataType dataType;

    /** The required. */
    @JsonProperty(index=3, value="required")
    private boolean required;

    /** The description. */
    @JsonProperty(index=4, value="description")
    private String description;

    private CleanseFunctionPortApplicationModeRO portApplicationMode;
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
     * @param name            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the data type.
     *
     * @return the dataType
     */
    public SimpleDataType getDataType() {
        return dataType;
    }

    /**
     * Sets the data type.
     *
     * @param dataType            the dataType to set
     */
    public void setDataType(SimpleDataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Checks if is required.
     *
     * @return the required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the required.
     *
     * @param required            the required to set
     */
    public void setRequired(boolean required) {
        this.required = required;
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
     * @param description            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the upathApplicationType
     */
    public CleanseFunctionPortApplicationModeRO getPortApplicationMode() {
        return portApplicationMode;
    }

    /**
     * @param upathApplicationType the upathApplicationType to set
     */
    public void setPortApplicationMode(CleanseFunctionPortApplicationModeRO upathApplicationType) {
        this.portApplicationMode = upathApplicationType;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PortDefinition [name=");
        builder.append(name);
        builder.append(", dataType=");
        builder.append(dataType);
        builder.append(", required=");
        builder.append(required);
        builder.append(", description=");
        builder.append(description);
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
            + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result
            + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (required ? 1231 : 1237);
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
        PortDefinition other = (PortDefinition) obj;
        if (dataType != other.dataType)
            return false;
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
        if (required != other.required)
            return false;
        return true;
    }
}
