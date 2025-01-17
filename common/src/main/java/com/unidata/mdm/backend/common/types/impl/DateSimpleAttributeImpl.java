package com.unidata.mdm.backend.common.types.impl;

import java.time.LocalDate;

import com.unidata.mdm.backend.common.ConvertUtils;

/**
 * @author Mikhail Mikhailov
 * Date simple attribute.
 */
public class DateSimpleAttributeImpl extends AbstractSimpleAttribute<LocalDate> {

    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected DateSimpleAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name the name
     */
    public DateSimpleAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name the name
     * @param value the value
     */
    public DateSimpleAttributeImpl(String name, LocalDate value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return DataType.DATE;
    }

    /**
     * Fluent part for compatibility.
     * @param value the value
     * @return self
     */
    public DateSimpleAttributeImpl withValue(LocalDate value) {
        setValue(value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V narrow(NarrowType type) {
        if (type == NarrowType.ES) {
            return (V) ConvertUtils.localDate2Date(getValue());
        }
        return super.narrow(type);
    }
}
