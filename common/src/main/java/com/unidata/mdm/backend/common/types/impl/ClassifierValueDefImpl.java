package com.unidata.mdm.backend.common.types.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.unidata.mdm.classifier.ClassifierValueDef;
import com.unidata.mdm.classifier.ClassifierValueType;

/**
 * @author Mikhail Mikhailov
 * Type setting extension.
 */
public class ClassifierValueDefImpl extends ClassifierValueDef {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5720962938161845468L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIntValue(Long value) {
        super.setType(ClassifierValueType.INTEGER);
        super.setIntValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateValue(LocalDate value) {
        super.setType(ClassifierValueType.DATE);
        super.setDateValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimeValue(LocalTime value) {
        super.setType(ClassifierValueType.TIME);
        super.setTimeValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimestampValue(LocalDateTime value) {
        super.setType(ClassifierValueType.TIMESTAMP);
        super.setTimestampValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStringValue(String value) {
        super.setType(ClassifierValueType.STRING);
        super.setStringValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNumberValue(Double value) {
        super.setType(ClassifierValueType.NUMBER);
        super.setNumberValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBoolValue(Boolean value) {
        super.setType(ClassifierValueType.BOOLEAN);
        super.setBoolValue(value);
    }

}
