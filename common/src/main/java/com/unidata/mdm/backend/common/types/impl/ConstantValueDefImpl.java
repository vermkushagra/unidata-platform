package com.unidata.mdm.backend.common.types.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.unidata.mdm.meta.ConstantValueDef;
import com.unidata.mdm.meta.ConstantValueType;

/**
 * @author Mikhail Mikhailov
 * Type setting extension.
 */
public class ConstantValueDefImpl extends ConstantValueDef {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 8073725919726730239L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIntValue(Long value) {
        super.setType(ConstantValueType.INTEGER);
        super.setIntValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateValue(LocalDate value) {
        super.setType(ConstantValueType.DATE);
        super.setDateValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimeValue(LocalTime value) {
        super.setType(ConstantValueType.TIME);
        super.setTimeValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimestampValue(LocalDateTime value) {
        super.setType(ConstantValueType.TIMESTAMP);
        super.setTimestampValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStringValue(String value) {
        super.setType(ConstantValueType.STRING);
        super.setStringValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNumberValue(Double value) {
        super.setType(ConstantValueType.NUMBER);
        super.setNumberValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBoolValue(Boolean value) {
        super.setType(ConstantValueType.BOOLEAN);
        super.setBoolValue(value);
    }

}
