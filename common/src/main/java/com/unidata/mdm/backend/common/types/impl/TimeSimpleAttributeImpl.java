package com.unidata.mdm.backend.common.types.impl;

import java.time.LocalTime;

import com.unidata.mdm.backend.common.ConvertUtils;

/**
 * @author Mikhail Mikhailov
 * Time value.
 */
public class TimeSimpleAttributeImpl extends AbstractSimpleAttribute<LocalTime> {

    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected TimeSimpleAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public TimeSimpleAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name the name
     * @param value the value
     */
    public TimeSimpleAttributeImpl(String name, LocalTime value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return DataType.TIME;
    }

    /**
     * Fluent part for compatibility.
     * @param value the value
     * @return self
     */
    public TimeSimpleAttributeImpl withValue(LocalTime value) {
        setValue(value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V narrow(NarrowType type) {
        if (type == NarrowType.ES) {
            return (V) ConvertUtils.localTime2Date(getValue());
        }
        return super.narrow(type);
    }
}
