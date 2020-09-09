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

package com.unidata.mdm.backend.api.rest.dto.cleanse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.api.rest.dto.meta.PortDefinition;

/**
 * The Class ComplexCleanseFunction.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompositeCFDefinition {

    /** The name. */
    private String name;

    /** The description. */
    private String description;

    /** The input ports. */
    private List<PortDefinition> inputPorts;

    /** The output ports. */
    private List<PortDefinition> outputPorts;

    /** The logic. */
    private CompositeCFLogic logic;

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
     *            the new name
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
     *            the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the input ports.
     *
     * @return the input ports
     */
    public List<PortDefinition> getInputPorts() {
        return inputPorts;
    }

    /**
     * Sets the input ports.
     *
     * @param inputPorts
     *            the new input ports
     */
    public void setInputPorts(List<PortDefinition> inputPorts) {
        this.inputPorts = inputPorts;
    }

    /**
     * Gets the output ports.
     *
     * @return the output ports
     */
    public List<PortDefinition> getOutputPorts() {
        return outputPorts;
    }

    /**
     * Sets the output ports.
     *
     * @param outputPorts
     *            the new output ports
     */
    public void setOutputPorts(List<PortDefinition> outputPorts) {
        this.outputPorts = outputPorts;
    }

    /**
     * Gets the logic.
     *
     * @return the logic
     */
    public CompositeCFLogic getLogic() {
        return logic;
    }

    /**
     * Sets the logic.
     *
     * @param logic
     *            the new logic
     */
    public void setLogic(CompositeCFLogic logic) {
        this.logic = logic;
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
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((inputPorts == null) ? 0 : inputPorts.hashCode());
        result = prime * result + ((logic == null) ? 0 : logic.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((outputPorts == null) ? 0 : outputPorts.hashCode());
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
        CompositeCFDefinition other = (CompositeCFDefinition) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (inputPorts == null) {
            if (other.inputPorts != null)
                return false;
        } else if (!inputPorts.equals(other.inputPorts))
            return false;
        if (logic == null) {
            if (other.logic != null)
                return false;
        } else if (!logic.equals(other.logic))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (outputPorts == null) {
            if (other.outputPorts != null)
                return false;
        } else if (!outputPorts.equals(other.outputPorts))
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
        builder.append("ComplexCleanseFunction [name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", inputPorts=");
        builder.append(inputPorts);
        builder.append(", outputPorts=");
        builder.append(outputPorts);
        builder.append(", logic=");
        builder.append(logic);
        builder.append("]");
        return builder.toString();
    }
}
