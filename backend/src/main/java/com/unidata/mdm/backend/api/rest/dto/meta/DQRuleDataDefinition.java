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

/**
 * The Class DQRuleDataDefinition.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DQRuleDataDefinition {
    
    /** The attribute name. */
    private String attributeName;
    
    /** The function port. */
    private String functionPort;
    
    /**
     * Gets the attribute name.
     *
     * @return the attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }
    
    /**
     * Sets the attribute name.
     *
     * @param attributeName the attributeName to set
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
    
    /**
     * Gets the function port.
     *
     * @return the functionPort
     */
    public String getFunctionPort() {
        return functionPort;
    }
    
    /**
     * Sets the function port.
     *
     * @param functionPort the functionPort to set
     */
    public void setFunctionPort(String functionPort) {
        this.functionPort = functionPort;
    }
}
