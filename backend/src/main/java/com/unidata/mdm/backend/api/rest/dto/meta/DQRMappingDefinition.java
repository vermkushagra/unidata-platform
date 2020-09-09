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
import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;

/**
 * The Class DQRMappingDefinition.
 *
 * @author Michael Yashin. Created on 11.06.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DQRMappingDefinition {

    /** The attribute name. */
    protected String attributeName;

    /** The function port. */
    protected String functionPort;

    /** Constant */
    protected SimpleAttributeRO attributeConstantValue;
    /**
     * Gets the attribute name.
     *
     * @return the attribute name
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Sets the attribute name.
     *
     * @param attributeName
     *            the new attribute name
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
     * @param functionPort
     *            the functionPort to set
     */
    public void setFunctionPort(String functionPort) {
        this.functionPort = functionPort;
    }

    /**
     * @return the attributeConstantValue
     */
    public SimpleAttributeRO getAttributeConstantValue() {
        return attributeConstantValue;
    }

    /**
     * @param attributeConstantValue the attributeConstantValue to set
     */
    public void setAttributeConstantValue(SimpleAttributeRO attributeConstantValue) {
        this.attributeConstantValue = attributeConstantValue;
    }
}
