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
