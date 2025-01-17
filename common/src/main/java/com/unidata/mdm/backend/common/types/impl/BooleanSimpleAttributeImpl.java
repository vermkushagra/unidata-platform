package com.unidata.mdm.backend.common.types.impl;

/**
 * @author Mikhail Mikhailov
 * Boolean simple attribute.
 */
public class BooleanSimpleAttributeImpl extends AbstractSimpleAttribute<Boolean> {

    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected BooleanSimpleAttributeImpl() {
        super();
    }
     /**
     * Constructor.
     * @param name the name
     */
    public BooleanSimpleAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name the name
     * @param value the value
     */
    public BooleanSimpleAttributeImpl(String name, Boolean value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    /**
     * Fluent part for compatibility.
     * @param value the value
     * @return self
     */
    public BooleanSimpleAttributeImpl withValue(Boolean value) {
        setValue(value);
        return this;
    }
}
