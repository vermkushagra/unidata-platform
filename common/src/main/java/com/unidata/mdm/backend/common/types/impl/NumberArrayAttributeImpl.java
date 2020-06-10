package com.unidata.mdm.backend.common.types.impl;

import java.util.List;

import com.unidata.mdm.backend.common.types.ArrayValue;

/**
 * @author Mikhail Mikhailov
 * Array of double prescision FP numbers.
 */
public class NumberArrayAttributeImpl extends AbstractArrayAttribute<Double> {
    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected NumberArrayAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public NumberArrayAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name
     * @param value
     */
    public NumberArrayAttributeImpl(String name, List<ArrayValue<Double>> value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDataType getDataType() {
        return ArrayDataType.NUMBER;
    }
}
