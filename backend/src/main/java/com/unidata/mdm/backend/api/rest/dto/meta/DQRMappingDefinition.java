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
