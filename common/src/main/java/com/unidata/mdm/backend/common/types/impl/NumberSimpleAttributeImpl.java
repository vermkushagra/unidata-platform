package com.unidata.mdm.backend.common.types.impl;

/**
 * @author Mikhail Mikhailov
 *         FP number simple attribute.
 */
public class NumberSimpleAttributeImpl extends AbstractSimpleAttribute<Double> {

    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected NumberSimpleAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     *
     * @param name
     */
    public NumberSimpleAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     *
     * @param name  the name
     * @param value the value
     */
    public NumberSimpleAttributeImpl(String name, Double value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return DataType.NUMBER;
    }

    /**
     * Fluent part for compatibility.
     *
     * @param value the value
     * @return self
     */
    public NumberSimpleAttributeImpl withValue(Double value) {
        setValue(value);
        return this;
    }
}
