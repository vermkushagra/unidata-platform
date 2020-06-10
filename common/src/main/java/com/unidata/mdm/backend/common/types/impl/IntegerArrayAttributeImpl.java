package com.unidata.mdm.backend.common.types.impl;

import java.util.List;

import com.unidata.mdm.backend.common.types.ArrayValue;

/**
 * @author Mikhail Mikhailov
 * Array of long integer numbers.
 */
public class IntegerArrayAttributeImpl extends AbstractArrayAttribute<Long> {
    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected IntegerArrayAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public IntegerArrayAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name
     * @param value
     */
    public IntegerArrayAttributeImpl(String name, List<ArrayValue<Long>> value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDataType getDataType() {
        return ArrayDataType.INTEGER;
    }
}
