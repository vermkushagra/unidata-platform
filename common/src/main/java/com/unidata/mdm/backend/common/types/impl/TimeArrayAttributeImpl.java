package com.unidata.mdm.backend.common.types.impl;

import java.time.LocalTime;
import java.util.List;

import com.unidata.mdm.backend.common.types.ArrayValue;

/**
 * @author Mikhail Mikhailov
 * Array of local time values.
 */
public class TimeArrayAttributeImpl extends AbstractArrayAttribute<LocalTime> {
    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected TimeArrayAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public TimeArrayAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name
     * @param value
     */
    public TimeArrayAttributeImpl(String name, List<ArrayValue<LocalTime>> value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDataType getDataType() {
        return ArrayDataType.TIME;
    }
}
