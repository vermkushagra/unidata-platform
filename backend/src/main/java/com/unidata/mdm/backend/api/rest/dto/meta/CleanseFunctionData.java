package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;


/**
 * The Class CleanseFunctionData.
 */
public class CleanseFunctionData {
    
    /** The name. */
    @JsonProperty(index=1, value="functionName")
    private String functionName;
    
    /** The simple attributes. */
    @JsonProperty(index=2, value="simpleAttributes")
    private List<SimpleAttributeRO> simpleAttributes;


    /** The simple attributes. */
    @JsonProperty(index=3, value="resultCode")
    private String resultCode;

    /** The simple attributes. */
    @JsonProperty(index=4, value="errorMessage")
    private String errorMessage;


    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getFunctionName() {
        return functionName;
    }
    
    /**
     * Sets the name.
     *
     * @param name the name to set
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    
    /**
     * Gets the simple attributes.
     *
     * @return the simpleAttributes
     */
    public List<SimpleAttributeRO> getSimpleAttributes() {
        return simpleAttributes;
    }
    
    /**
     * Sets the simple attributes.
     *
     * @param simpleAttributes the simpleAttributes to set
     */
    public void setSimpleAttributes(List<SimpleAttributeRO> simpleAttributes) {
        this.simpleAttributes = simpleAttributes;
    }

    /**
     * Get error message.
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set error message
     * @param errorMessage error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get result code
     * @return result code
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * Set result code
     * @param resultCode result code
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /* (non-Javadoc)
             * @see java.lang.Object#toString()
             */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("CleanseFunctionData [name=");
	builder.append(functionName);
	builder.append(", simpleAttributes=");
	builder.append(simpleAttributes);
	builder.append("]");
	return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((functionName == null) ? 0 : functionName.hashCode());
	result = prime
		* result
		+ ((simpleAttributes == null) ? 0 : simpleAttributes.hashCode());
	return result;
    }
    /* (non-Javadoc)
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
	CleanseFunctionData other = (CleanseFunctionData) obj;
	if (functionName == null) {
	    if (other.functionName != null)
		return false;
	} else if (!functionName.equals(other.functionName))
	    return false;
	if (simpleAttributes == null) {
	    if (other.simpleAttributes != null)
		return false;
	} else if (!simpleAttributes.equals(other.simpleAttributes))
	    return false;
	return true;
    }
}
