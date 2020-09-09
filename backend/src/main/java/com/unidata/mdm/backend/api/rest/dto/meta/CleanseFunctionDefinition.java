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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CompositeCFLogic;

import io.swagger.annotations.ApiModelProperty;

/**
 * The Class CleanseFunctionDefinition.
 *
 * @author Michael Yashin. Created on 20.05.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CleanseFunctionDefinition extends CleanseFunction {

    /** The input ports. */
    protected List<PortDefinition> inputPorts = new ArrayList<>();

    /** The output ports. */
    protected List<PortDefinition> outputPorts = new ArrayList<>();

    /** The logic. */
    @ApiModelProperty(value = "Описание логики, заполняется только для композитных функций")
    protected CompositeCFLogic logic;

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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CleanseFunctionDefinition [inputPorts=");
        builder.append(inputPorts);
        builder.append(", outputPorts=");
        builder.append(outputPorts);
        builder.append(", logic=");
        builder.append(logic);
        builder.append("]");
        return builder.toString();
    }
}
