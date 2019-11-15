package org.unidata.mdm.search.type.mapping.impl;

/**
 * @author Mikhail Mikhailov on Oct 9, 2019
 */
public abstract class AbstractValueMappingField<X extends AbstractValueMappingField<X>> extends AbstractMappingField<X> {
    /**
     * Index as doc value.
     */
    private boolean docValue;
    /**
     * The 'null' value.
     */
    private Object defaultValue;
    /**
     * Constructor.
     * @param name
     */
    public AbstractValueMappingField(String name) {
        super(name);
    }
    /**
     * @return the docValue
     */
    public boolean isDocValue() {
        return docValue;
    }
    /**
     * @param docValue the docValue to set
     */
    public void setDocValue(boolean docValue) {
        this.docValue = docValue;
    }
    /**
     * @return the defaultValue
     */
    public Object getDefaultValue() {
        return defaultValue;
    }
    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    /**
     * Sets field's storage to doc value.
     * @param docValue the flag
     * @return self
     */
    public X withDocValue(boolean docValue) {
        setDocValue(docValue);
        return self();
    }
    /**
     * @param defaultValue the defaultValue to set
     */
    public X withDefaultValue(Object defaultValue) {
        setDefaultValue(defaultValue);
        return self();
    }
}
