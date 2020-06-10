package com.unidata.mdm.backend.common.cleanse;

/**
 * @author Mikhail Mikhailov
 * Cleanse function configuration top level class.
 */
public abstract class CleanseFunctionConfiguration {
    /**
     * Name of the function.
     */
    protected String functionName;
    /**
     * Description.
     */
    protected String description;
    /**
     * Constructor.
     */
    public CleanseFunctionConfiguration() {
        super();
    }
    /**
     * @return the functionName
     */
    public String getFunctionName() {
        return functionName;
    }
    /**
     * @param functionName the functionName to set
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Cleanse function type.
     * @return
     */
    public abstract CleanseFunctionType getType();
}
