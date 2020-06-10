package com.unidata.mdm.backend.common.cleanse;

/**
 * @author Mikhail Mikhailov
 * Single cleanse function configuration.
 */
public class SingleCleanseFunctionConfiguration extends CleanseFunctionConfiguration {
    /**
     * Java class.
     */
    private String javaClass;
    /**
     * Constructor.
     */
    public SingleCleanseFunctionConfiguration() {
        super();
    }
    /**
     * @return the javaClass
     */
    public String getJavaClass() {
        return javaClass;
    }
    /**
     * @param javaClass the javaClass to set
     */
    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CleanseFunctionType getType() {
        return CleanseFunctionType.SINGLE;
    }
    /**
     * Fluent setter.
     * @param functionName the function name
     * @return self
     */
    public SingleCleanseFunctionConfiguration withFunctionName(String functionName) {
        super.setFunctionName(functionName);
        return this;
    }
    /**
     * Fluent setter.
     * @param description the description
     * @return self
     */
    public SingleCleanseFunctionConfiguration withDescription(String description) {
        super.setDescription(description);
        return this;
    }
    /**
     * Fluent setter.
     * @param javaClass the javaClass
     * @return self
     */
    public SingleCleanseFunctionConfiguration withJavaClass(String javaClass) {
        setJavaClass(javaClass);
        return this;
    }
}
