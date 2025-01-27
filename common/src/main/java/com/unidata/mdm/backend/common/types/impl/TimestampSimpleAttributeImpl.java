package com.unidata.mdm.backend.common.types.impl;

import java.time.LocalDateTime;

import com.unidata.mdm.backend.common.ConvertUtils;

/**
 * @author Mikhail Mikhailov
 * Timestamp simple attribute.
 */
public class TimestampSimpleAttributeImpl extends AbstractSimpleAttribute<LocalDateTime> {

    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected TimestampSimpleAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public TimestampSimpleAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name the name
     * @param value the value
     */
    public TimestampSimpleAttributeImpl(String name, LocalDateTime value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return DataType.TIMESTAMP;
    }

    /**
     * Fluent part for compatibility.
     * @param value the value
     * @return self
     */
    public TimestampSimpleAttributeImpl withValue(LocalDateTime value) {
        setValue(value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V narrow(NarrowType type) {
        if (type == NarrowType.ES) {
            return (V) ConvertUtils.localDateTime2Date(getValue());
        }
        return super.narrow(type);
    }
}
