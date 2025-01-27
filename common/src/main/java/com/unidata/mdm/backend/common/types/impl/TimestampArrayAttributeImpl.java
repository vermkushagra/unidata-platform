package com.unidata.mdm.backend.common.types.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.unidata.mdm.backend.common.types.ArrayValue;

/**
 * @author Mikhail Mikhailov
 * Array of local date-time values.
 */
public class TimestampArrayAttributeImpl extends AbstractArrayAttribute<LocalDateTime> {
    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected TimestampArrayAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public TimestampArrayAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name
     * @param value
     */
    public TimestampArrayAttributeImpl(String name, List<ArrayValue<LocalDateTime>> value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDataType getDataType() {
        return ArrayDataType.TIMESTAMP;
    }
}
